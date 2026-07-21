package edu.ite.libraryapi.dto;

public class LoginResponse {
    private final Integer id;
    private final String name;
    private final String username;
    private final String role;

    public LoginResponse(Integer id, String name, String username, String role) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.role = role;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
}
