package com.smartconsultor.keycloak;

import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory; 

public class DeviceEventListenerProviderFactory implements EventListenerProviderFactory {
    // Constructor không tham số bắt buộc để tránh lỗi
    public DeviceEventListenerProviderFactory() {}

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new DeviceEventListenerProvider(session);
    }

    @Override
    public void init(org.keycloak.Config.Scope config) {}

    @Override
    public void postInit(KeycloakSessionFactory factory) {}

    @Override
    public void close() {}

    @Override
    public String getId() {
        return "device-listener";
    }
}
