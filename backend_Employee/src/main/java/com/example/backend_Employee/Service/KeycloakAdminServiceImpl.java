package com.example.backend_Employee.Service;

import com.example.backend_Employee.DTO.KeycloakUserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
public class KeycloakAdminServiceImpl implements KeycloakAdminService {

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin.username}")
    private String adminUsername;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    private final RestTemplate restTemplate = new RestTemplate();

    private String getAdminAccessToken() {
        String url = keycloakServerUrl + "/realms/master/protocol/openid-connect/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", "admin-cli");
        params.add("username", adminUsername);
        params.add("password", adminPassword);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
        return (String) response.getBody().get("access_token");
    }

    @Override
    public List<Map<String, Object>> getUsers() {
        String token = getAdminAccessToken();
        String url = keycloakServerUrl + "/admin/realms/" + realm + "/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> req = new HttpEntity<>(headers);
        ResponseEntity<List> resp = restTemplate.exchange(url, HttpMethod.GET, req, List.class);

        // Return only username, id, and roles
        List<Map<String, Object>> users = new ArrayList<>();
        for (Object userObj : resp.getBody()) {
            Map<String, Object> userMap = (Map<String, Object>) userObj;
            Map<String, Object> userSummary = new HashMap<>();
            userSummary.put("id", userMap.get("id"));
            userSummary.put("username", userMap.get("username"));
            userSummary.put("email", userMap.get("email"));
            userSummary.put("firstName", userMap.get("firstName"));
            userSummary.put("lastName", userMap.get("lastName"));

            // Get user roles
            String userId = (String) userMap.get("id");
            String roleUrl = keycloakServerUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm";
            ResponseEntity<List> roleResp = restTemplate.exchange(roleUrl, HttpMethod.GET, req, List.class);
            List roles = roleResp.getBody();
            if (!roles.isEmpty()) {
                userSummary.put("role", ((Map) roles.get(0)).get("name"));
            } else {
                userSummary.put("role", "");
            }
            users.add(userSummary);
        }
        return users;
    }

    @Override
    public void createUser(KeycloakUserDTO userDTO) {
        String token = getAdminAccessToken();
        String url = keycloakServerUrl + "/admin/realms/" + realm + "/users";

        // Build user payload
        Map<String, Object> user = new HashMap<>();
        user.put("username", userDTO.getUsername());
        user.put("enabled", true);
        user.put("email", userDTO.getEmail());
        user.put("firstName", userDTO.getFirstName());
        user.put("lastName", userDTO.getLastName());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(user, headers);
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, request, Void.class);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            // Get user id
            String getUserUrl = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("username", userDTO.getUsername()).toUriString();
            HttpEntity<Void> getUserRequest = new HttpEntity<>(headers);
            ResponseEntity<List> userResp = restTemplate.exchange(getUserUrl, HttpMethod.GET, getUserRequest, List.class);
            String userId = (String) ((Map) userResp.getBody().get(0)).get("id");

            // Set password
            if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                String setPasswordUrl = keycloakServerUrl + "/admin/realms/" + realm + "/users/" + userId + "/reset-password";
                Map<String, Object> passwordPayload = new HashMap<>();
                passwordPayload.put("type", "password");
                passwordPayload.put("value", userDTO.getPassword());
                passwordPayload.put("temporary", false);

                HttpEntity<Map<String, Object>> passwordRequest = new HttpEntity<>(passwordPayload, headers);
                restTemplate.exchange(setPasswordUrl, HttpMethod.PUT, passwordRequest, Void.class);
            }

            // Get role id
            String getRoleUrl = keycloakServerUrl + "/admin/realms/" + realm + "/roles/" + userDTO.getRole();
            ResponseEntity<Map> roleResp = restTemplate.exchange(getRoleUrl, HttpMethod.GET, getUserRequest, Map.class);
            Map role = roleResp.getBody();

            // Assign role
            String assignRoleUrl = keycloakServerUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm";
            HttpEntity<List<Map>> assignRequest = new HttpEntity<>(List.of(role), headers);
            restTemplate.exchange(assignRoleUrl, HttpMethod.POST, assignRequest, Void.class);
        } else {
            throw new RuntimeException("Erreur lors de la création de l'utilisateur");
        }
    }

    @Override
    public void deleteUser(String id) {
        String token = getAdminAccessToken();
        String url = keycloakServerUrl + "/admin/realms/" + realm + "/users/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class);
    }
    @Override
    public boolean emailExists(String email) {
        String token = getAdminAccessToken();
        String url = keycloakServerUrl + "/admin/realms/" + realm + "/users?email=" + email;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> req = new HttpEntity<>(headers);
        ResponseEntity<List> resp = restTemplate.exchange(url, HttpMethod.GET, req, List.class);

        List users = resp.getBody();
        return users != null && !users.isEmpty();
    }
}