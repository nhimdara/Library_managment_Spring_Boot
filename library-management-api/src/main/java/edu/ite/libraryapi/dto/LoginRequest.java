package edu.ite.libraryapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credentials for an admin or student")
public class LoginRequest {
    @NotBlank
    @Schema(description = "Account type", allowableValues = {"ADMIN", "STUDENT"}, example = "ADMIN")
    private String role;

    @Schema(description = "Required when role is ADMIN", example = "admin")
    private String username;

    @Schema(description = "Required when role is STUDENT", example = "1")
    private Integer studentId;

    @NotBlank
    @Schema(example = "12345678")
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
