package com.smartconsultor.keycloak;

import org.apache.pulsar.client.api.CompressionType;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.TypedMessageBuilder;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.events.Event;
import org.keycloak.models.*;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CustomDeviceAuthenticator implements Authenticator {
    private final KeycloakSession session;
    private PulsarClient client = null;
    private Producer<byte[]> producer=null;
  
    public CustomDeviceAuthenticator(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        UserModel user = context.getUser();
        Event event = context.getEvent().getEvent();
        String deviceInfo = event.getDetails().get("user_agent") + " - " + event.getIpAddress();  

        // Lấy danh sách thiết bị từ USER_ATTRIBUTE
        Map<String, Long> devices = getDevicesFromAttributes(user);
        if (devices != null){
            // Kiểm tra thiết bị mới
            if (!devices.containsKey(deviceInfo)) {
                // Có thiết bị cũ đang hoạt động -> Gửi OTP tới thiết bị cũ
                String otp = generateOTP();
                sendOtpToOldDevice(user.getUsername(), otp);
                context.getAuthenticationSession().setAuthNote("expected_otp", otp);
                context.getAuthenticationSession().setAuthNote("new_device", deviceInfo);
                context.challenge(createOtpForm(context));
            } else {
                // Thiết bị đã biết -> Tiếp tục đăng nhập
                context.success();
            }
        }
        else {
            // Không có thiết bị cũ -> Chuyển sang MFA khác (TOTP/SMS)
            context.getAuthenticationSession().setAuthNote("fallback_mfa", "true");
            context.success(); // Chuyển bước tiếp theo trong flow            
        }
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String userInputOtp = formData.getFirst("otp");
        String expectedOtp = context.getAuthenticationSession().getAuthNote("expected_otp");

        if (userInputOtp != null && userInputOtp.equals(expectedOtp)) {
            // Xác thực thành công -> Cập nhật thiết bị vào USER_ATTRIBUTE
            UserModel user = context.getUser();
            String newDevice = context.getAuthenticationSession().getAuthNote("new_device");
            long currentTime = System.currentTimeMillis() / 1000;
            Map<String, Long> devices = getDevicesFromAttributes(user);
            devices.put(newDevice, currentTime);
            user.setAttribute("devices", new ArrayList<>(serializeDevices(devices)));            
            context.success();
        } else {
            context.failure(AuthenticationFlowError.INVALID_CREDENTIALS, 
                context.form().setError("Invalid OTP").createForm("otp-form.ftl"));
        }
    }

    private Response createOtpForm(AuthenticationFlowContext context) {
        MultivaluedHashMap<String, String> formData = new MultivaluedHashMap<>();
        formData.add("message", "An OTP has been sent to your old device. Please enter it below.");
        return context.form().setFormData(formData).createForm("otp-form.ftl");
    }

    private String generateOTP() {
        return String.format("%06d", new Random().nextInt(999999));
    }
    
    private void sendOtpToOldDevice(String username, String otp) {
        if (producer == null || client == null) {
            System.out.println("Pulsar producer chưa được khởi tạo hoặc mất kết nối, đang thử reconnect...");
            try {
                restartPulsarClient();
                restartProducer();
            } catch (Exception e) {
                System.out.println("Lỗi khi thử reconnect Pulsar: " + e.getMessage());
                return;
            }
        }
    
        try {
            TypedMessageBuilder<byte[]> message = producer.newMessage()
                .key(username)
                .value(otp.getBytes());
    
            MessageId msgId = message.send();
            System.out.println("Đã gửi OTP " + otp + " cho user " + username + " với MessageId " + msgId);
        } catch (Exception e) {
            System.out.println("Lỗi khi gửi OTP qua Pulsar: " + e.getMessage());
            try {
                restartPulsarClient();
                restartProducer();
            } catch (Exception ex) {
                System.out.println("Thử reconnect thất bại: " + ex.getMessage());
            }
        }
    }
    
    private Map<String, Long> getDevicesFromAttributes(UserModel user) {
        List<String> deviceList = user.getAttributes().get("devices");
    
        // Trả về null nếu không có thuộc tính "devices" hoặc danh sách rỗng
        if (deviceList == null || deviceList.isEmpty()) {
            return null;
        }
    
        Map<String, Long> devices = new HashMap<>();
        for (String deviceEntry : deviceList) {
            String[] parts = deviceEntry.split("::");
            if (parts.length == 2) {
                try {
                    devices.put(parts[0], Long.parseLong(parts[1]));
                } catch (NumberFormatException e) {
                    // Bỏ qua entry nếu giá trị timestamp không hợp lệ
                }
            }
        }
    
        return devices.isEmpty() ? null : devices;
    }
    

    private List<String> serializeDevices(Map<String, Long> devices) {
        return devices.entrySet().stream()
            .map(entry -> entry.getKey() + "::" + entry.getValue())
            .collect(Collectors.toList());
    }
    @Override
    public boolean requiresUser() { return true; }
    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) { return true; }
    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {}
    @Override
    public void close() {}
    
    private void restartPulsarClient() throws Exception {
        if (client != null) {
            client.close();
        }
    
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream("application.properties");
        properties.load(input);
    
        int retries = 3; // Số lần thử kết nối lại
        while (retries > 0) {
            try {
                client = PulsarClient.builder()
                    .serviceUrl(properties.getProperty("pulsar.url"))
                    .connectionsPerBroker(Integer.parseInt(properties.getProperty("pulsar.connectionsPerBroker", "1")))
                    .ioThreads(Integer.parseInt(properties.getProperty("pulsar.numIoThreads", "2")))
                    .listenerThreads(Integer.parseInt(properties.getProperty("pulsar.numListenerThreads", "1")))
                    .enableTcpNoDelay(true)
                    .operationTimeout(Integer.parseInt(properties.getProperty("pulsar.operationTimeout", "30")), TimeUnit.SECONDS)
                    .keepAliveInterval(Integer.parseInt(properties.getProperty("pulsar.keepAliveInterval", "30")), TimeUnit.SECONDS)
                    .build();
                System.out.println("Kết nối lại Pulsar thành công!");
                return;
            } catch (Exception e) {
                retries--;
                System.out.println("Lỗi kết nối Pulsar, thử lại... (" + retries + " lần còn lại)");
                Thread.sleep(2000); // Chờ 2 giây trước khi thử lại
            }
        }
        throw new Exception("Không thể kết nối lại Pulsar sau nhiều lần thử!");
    }
    

    private void restartProducer() throws Exception {
        if (client == null) {
            System.out.println("Không thể tạo producer, Pulsar client chưa kết nối.");
            return;
        }
    
        if (producer != null) {
            producer.close();
        }
    
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream("application.properties");
        properties.load(input);
    
        producer = client.newProducer()
            .topic(properties.getProperty("pulsar.producer.topic", "otp"))
            .sendTimeout(Integer.parseInt(properties.getProperty("pulsar.producer.sendTimeout", "0")), TimeUnit.SECONDS)
            .compressionType(CompressionType.valueOf(properties.getProperty("pulsar.producer.compressionType", "LZ4")))
            .batchingMaxMessages(Integer.parseInt(properties.getProperty("pulsar.producer.batchingMaxMessagesPerBatch", "100")))
            .batchingMaxPublishDelay(Integer.parseInt(properties.getProperty("pulsar.producer.batchingMaxPublishDelay", "10")), TimeUnit.MILLISECONDS)
            .create();
    
        System.out.println("Producer Pulsar đã được khởi tạo lại.");
    }
    
}

