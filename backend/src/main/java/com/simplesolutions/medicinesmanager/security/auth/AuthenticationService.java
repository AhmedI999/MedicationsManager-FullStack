package com.simplesolutions.medicinesmanager.security.auth;

import com.simplesolutions.medicinesmanager.dto.patientdto.PatientDTOMapper;
import com.simplesolutions.medicinesmanager.dto.patientdto.PatientResponseDTO;
import com.simplesolutions.medicinesmanager.model.Patient;
import com.simplesolutions.medicinesmanager.security.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final PatientDTOMapper patientDTOMapper;
    private final JWTUtil jwtUtil;

    public String login (AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password())
        );
        Patient principal = ( Patient ) authentication.getPrincipal();
        PatientResponseDTO patientDto = patientDTOMapper.apply(principal);
        return jwtUtil.issueToken(patientDto.email(), patientDto.roles());
    }



}
