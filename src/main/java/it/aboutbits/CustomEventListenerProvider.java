package it.aboutbits;

import java.util.Set;

import org.jboss.logging.Logger;
import org.keycloak.models.UserCredentialManager;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.UserModel;

public class CustomEventListenerProvider implements EventListenerProvider {

    private static final Logger log = Logger.getLogger(CustomEventListenerProvider.class);

    private final KeycloakSession session;
    private final RealmProvider model;

    public CustomEventListenerProvider(KeycloakSession session) {
        this.session = session;
        this.model = session.realms();
    }

    @Override
    public void onEvent(Event event) {

        if (EventType.LOGOUT.equals(event.getType()) || EventType.UPDATE_PASSWORD.equals(event.getType())) {
            
            RealmModel realm = this.model.getRealm(event.getRealmId());
            UserModel newRegisteredUser = this.session.users().getUserById(event.getUserId(), realm);
            UserCredentialManager a = this.session.userCredentialManager();
            String username = newRegisteredUser.getUsername();
            // Set<String> requiredActions = newRegisteredUser.getRequiredActions();
            // String configotp = "is not configured OTP.";
            // for(String element:requiredActions) {
            //     if("CONFIGURE_TOTP".equals(element)) {
            //         configotp = "is configured OTP.";
            //         break;
            //     }
            // }
            
            String fullname;
            if("".equals(newRegisteredUser.getFirstName()) || "".equals(newRegisteredUser.getLastName()) || newRegisteredUser.getFirstName() == null || newRegisteredUser.getLastName() == null) {
                fullname = null;
            }
            else {
                fullname = newRegisteredUser.getFirstName() + ' ' + newRegisteredUser.getLastName();
            }
            String configotp = "is not configured OTP.";
            if(a.isConfiguredFor(realm, newRegisteredUser, "otp")) {
                configotp = "is configured OTP.";
            }
            String email = newRegisteredUser.getEmail();
            if("".equals(email)) {
                email = null;
            }
            log.info("Usename: "+ username + ", Email: " + email + ", Fullname: " + fullname + ", " + configotp);
        }

    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {

    }

    @Override
    public void close() {

    }
}
