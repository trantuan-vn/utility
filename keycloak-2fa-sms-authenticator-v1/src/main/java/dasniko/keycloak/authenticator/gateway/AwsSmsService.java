package dasniko.keycloak.authenticator.gateway;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
public class AwsSmsService implements SmsService {

    private final SnsClient sns;
    private final String senderId;

    AwsSmsService(Map<String, String> config) {
        // Lấy thông tin từ config
        String accessKey = config.get("accessKey");
        String secretKey = config.get("secretKey");
        String region = config.get("region");
        senderId = config.get("senderId");

        // Kiểm tra null và ném ngoại lệ nếu thiếu thông tin
        if (accessKey == null || accessKey.trim().isEmpty()) {
            throw new IllegalArgumentException("AWS Access Key is missing or empty in configuration");
        }
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalArgumentException("AWS Secret Key is missing or empty in configuration");
        }
        if (region == null || region.trim().isEmpty()) {
            throw new IllegalArgumentException("AWS Region is missing or empty in configuration");
        }

        // Khởi tạo SnsClient với credentials và region từ config
        this.sns = SnsClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.of(region))
                .build();
    }

    @Override
    public void send(String phoneNumber, String message) {
        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        messageAttributes.put("AWS.SNS.SMS.SenderID",
                MessageAttributeValue.builder().stringValue(senderId).dataType("String").build());
        messageAttributes.put("AWS.SNS.SMS.SMSType",
                MessageAttributeValue.builder().stringValue("Transactional").dataType("String").build());

        sns.publish(builder -> builder
                .message(message)
                .phoneNumber(phoneNumber)
                .messageAttributes(messageAttributes));
    }
}