package com.smartconsultor.keycloak;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserSessionModel;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class DeviceEventListenerProvider implements EventListenerProvider {

    private static final long THIRTY_DAYS_IN_SECONDS = 30L * 24 * 60 * 60;
    private final KeycloakSession session;

    public DeviceEventListenerProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void onEvent(Event event) {
        if (event.getType() == EventType.LOGIN || event.getType() == EventType.IDENTITY_PROVIDER_LOGIN) {
            RealmModel realm = session.realms().getRealm(event.getRealmId());
            UserModel user = session.users().getUserById(realm, event.getUserId());
            UserSessionModel userSession = session.sessions().getUserSession(realm, event.getSessionId());

            if (userSession == null) return;

            String deviceInfo = event.getDetails().get("user_agent") + " - " + event.getIpAddress();
            long currentTime = Instant.now().getEpochSecond();

            // Cập nhật thời gian đăng nhập trong USER_ATTRIBUTE
            Map<String, Long> devices = getDevicesFromAttributes(user);
            devices.put(deviceInfo, currentTime);

            // Xóa thiết bị không hoạt động trong 30 ngày
            devices.entrySet().removeIf(entry -> (currentTime - entry.getValue()) > THIRTY_DAYS_IN_SECONDS);

            if (devices.isEmpty()) {
                user.removeAttribute("devices");
            } else {
                user.setAttribute("devices", new ArrayList<>(serializeDevices(devices)));
            }

            // Lưu thiết bị active vào UserSession
            userSession.setNote("active_device", deviceInfo);
            System.out.println("Device logged in: " + deviceInfo);
        } else if (event.getType() == EventType.LOGOUT) {
            RealmModel realm = session.realms().getRealm(event.getRealmId());
            UserSessionModel userSession = session.sessions().getUserSession(realm, event.getSessionId());
            if (userSession != null) {
                String deviceInfo = userSession.getNote("active_device");
                if (deviceInfo != null) {
                    System.out.println("Device logged out: " + deviceInfo);
                }
            }
        }
    }

    private Map<String, Long> getDevicesFromAttributes(UserModel user) {
        List<String> deviceList = Optional.ofNullable(user.getAttributes().get("devices")).orElse(List.of());
        Map<String, Long> devices = new HashMap<>();
        for (String deviceEntry : deviceList) {
            String[] parts = deviceEntry.split("::");
            if (parts.length == 2) {
                devices.put(parts[0], Long.parseLong(parts[1]));
            }
        }
        return devices;
    }

    private List<String> serializeDevices(Map<String, Long> devices) {
        return devices.entrySet().stream()
            .map(entry -> entry.getKey() + "::" + entry.getValue())
            .collect(Collectors.toList());
    }

    @Override
    public void close() {}
    @Override
    public void onEvent(AdminEvent arg0, boolean arg1) {
        throw new UnsupportedOperationException("Unimplemented method 'onEvent'");
    }
}