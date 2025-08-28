package com.example.batch.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "employees")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("firstName")
    @Column(name = "first_name")
    private String firstName;

    @JsonProperty("lastName")
    @Column(name = "last_name")
    private String lastName;

    @JsonProperty("email")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @JsonProperty("immatriculation")
    @Column(name = "immatriculation", unique = true, nullable = false, length = 6)
    private String immatriculation;

    public Employee() {}

    public Employee(String firstName, String lastName, String email, String immatriculation) {
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