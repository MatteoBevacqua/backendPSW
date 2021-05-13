package reservations.journey_planner.demo.services;

import org.keycloak.*;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.*;

import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reservations.journey_planner.demo.entities.Passenger;
import reservations.journey_planner.demo.exceptions.EmailAlreadyInUseException;
import reservations.journey_planner.demo.repositories.PassengerRepository;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import reservations.journey_planner.demo.requestPOJOs.PassengerDTO;


import javax.ws.rs.core.Response;
import java.util.Collections;

@Service
public class PassengerService {
    @Autowired
    PassengerRepository passengerRepository;
    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${key_conf.clientid}")
    private String clientId;
    @Value("${key_conf.adminusername}")
    private String adminUsername;
    @Value("${key_conf.adminpassword}")
    private String adminPassword;
    private Keycloak keycloak;

    @EventListener(ApplicationReadyEvent.class)
    public void establishConnectionToAuthServer() {
        keycloak = KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .grantType(OAuth2Constants.PASSWORD)
                .realm(realm)
                .clientId(clientId)
                .username(adminUsername)
                .password(adminPassword).build();
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public Passenger addUser(PassengerDTO passengerDTO) {
        if (!passengerDTO.isValid())
            throw new RuntimeException("Missing fields");
        if ((passengerRepository.findPassengerByEmail(passengerDTO.getEmail())) != null)
            throw new EmailAlreadyInUseException();
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();
        RolesResource roles = realmResource.roles();
        UserRepresentation user = new UserRepresentation();
        user.setUsername(passengerDTO.getUsername());
        user.setFirstName(passengerDTO.getFirstName());
        user.setLastName(passengerDTO.getLastName());
        user.setEmail(passengerDTO.getEmail());
        user.setEnabled(true);
     // non funziona
     //   user.setClientRoles(Collections.singletonMap("spring-boot", Collections.singletonList("passenger")));
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(passengerDTO.getPassword());
        user.setCredentials(Collections.singletonList(passwordCred));
        Response response = usersResource.create(user);
        if (response.getStatus() != 201)
            throw new RuntimeException("Unexpected error");
        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        Passenger toAdd = passengerDTO.asPassenger();
        toAdd.setId(userId);
        assignRoleToUser(userId);
        return passengerRepository.save(toAdd);
    }

    private void assignRoleToUser(String userId) {
        UsersResource usersResource = keycloak.realm(realm).users();
        UserResource userResource = usersResource.get(userId);
        ClientRepresentation clientRepresentation = keycloak.realm(realm).clients().findByClientId("spring-boot").get(0);
        ClientResource clientResource = keycloak.realm(realm).clients().get(clientRepresentation.getId());
        RoleRepresentation roleRepresentation = clientResource.roles().get("passenger").toRepresentation();
        userResource.roles().clientLevel(clientRepresentation.getId()).add(Collections.singletonList(roleRepresentation));
    }


}