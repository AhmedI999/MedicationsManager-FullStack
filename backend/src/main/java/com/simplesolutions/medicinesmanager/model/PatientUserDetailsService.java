package com.simplesolutions.medicinesmanager.model;

import com.simplesolutions.medicinesmanager.service.patient.PatientDao;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientUserDetailsService implements UserDetailsService {

    private final PatientDao patientDao;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return patientDao.selectPatientByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("patient with email [%s] not found".formatted(username)));
    }
}
