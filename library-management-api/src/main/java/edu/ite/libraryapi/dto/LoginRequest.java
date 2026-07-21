package edu.ite.libraryapi.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank
    private String role;

    private String username;

    private Integer studentId;

    @NotBlank
    private String password;

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
