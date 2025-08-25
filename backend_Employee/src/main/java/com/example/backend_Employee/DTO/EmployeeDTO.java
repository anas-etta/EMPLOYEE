package com.example.backend_Employee.DTO;

public class EmployeeDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String immatriculation;

    public EmployeeDTO() {}

    public EmployeeDTO(Long id, String firstName, String lastName, String email, String immatriculation) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.immatriculation = immatriculation;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getImmatriculation() { return immatriculation; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }
}