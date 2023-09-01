package be.ugent.gsr.financien.service;

import be.ugent.gsr.financien.domain.Gebruiker;
import be.ugent.gsr.financien.model.GebruikerType;
import be.ugent.gsr.financien.model.JwtAuthenticationResponse;
import be.ugent.gsr.financien.model.LoginDAO;
import be.ugent.gsr.financien.model.RegistreerDAO;
import be.ugent.gsr.financien.repos.GebruikerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final GebruikerRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationResponse registreer(RegistreerDAO request) {
        var user = Gebruiker.builder().naam(request.getNaam())
                .email(request.getEmail()).wachtwoord(passwordEncoder.encode(request.getWachtwoord()))
                .type(GebruikerType.OTHER).telNr(request.getTelNr()).build();
        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    public JwtAuthenticationResponse login(LoginDAO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }
}