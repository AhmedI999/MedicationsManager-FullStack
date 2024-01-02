package com.simplesolutions.medicinesmanager.security.auth;

public record AuthenticationRequest(
        String email,
        String password

) {}
