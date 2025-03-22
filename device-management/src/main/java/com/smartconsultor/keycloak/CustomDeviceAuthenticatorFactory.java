package com.smartconsultor.keycloak;

import java.util.Collections;
import java.util.List;

import org.keycloak.Config.Scope;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;


public class CustomDeviceAuthenticatorFactory  implements AuthenticatorFactory {
    // Constructor không tham số bắt buộc để tránh lỗi
    public CustomDeviceAuthenticatorFactory() {}

    @Override
    public String getId() { return "custom-device-authenticator"; }
    
    @Override
    public Authenticator create(KeycloakSession session) 
    { 
        return new CustomDeviceAuthenticator(session); 
    }
    @Override
    public String getDisplayType() { return "Custom Device Verification"; }
    
    @Override
    public String getHelpText() { return "Verify new devices using old devices or fallback to MFA"; }
    
    @Override
    public List<ProviderConfigProperty> getConfigProperties() { return Collections.emptyList(); }
    
    @Override
    public boolean isConfigurable() { return false; }
    
    @Override
    public boolean isUserSetupAllowed() { return false; }
    
    @Override
    public void init(Scope config) {}
    @Override
    public void postInit(KeycloakSessionFactory factory) {}
    @Override
    public void close() {}
    @Override
    public String getReferenceCategory() {
        return null;
    }
    @Override
    public Requirement[] getRequirementChoices() {
        return new Requirement[]{ 
            Requirement.REQUIRED,
            Requirement.ALTERNATIVE,
            Requirement.DISABLED,
            Requirement.CONDITIONAL
        };    
    }
}
