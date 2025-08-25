package com.example.backend_Employee.Service;

import com.example.backend_Employee.DTO.KeycloakUserDTO;
import java.util.List;
import java.util.Map;

public interface KeycloakAdminService {
    List<Map<String, Object>> getUsers();
    void createUser(KeycloakUserDTO userDTO);
    void deleteUser(String id);

    boolean emailExists(String email); // <--- Add this
}