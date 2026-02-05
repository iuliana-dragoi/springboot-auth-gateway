package com.example.auth.gateway.auth;

import java.util.List;

public record AuthenticatedUser(String username, List<String> roles, String authProvider) {}
