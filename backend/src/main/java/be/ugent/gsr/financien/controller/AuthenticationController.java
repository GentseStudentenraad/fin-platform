package be.ugent.gsr.financien.controller;

import be.ugent.gsr.financien.domain.Gebruiker;
import be.ugent.gsr.financien.model.GebruikerDTO;
import be.ugent.gsr.financien.model.JwtAuthenticationResponse;
import be.ugent.gsr.financien.model.LoginDTO;
import be.ugent.gsr.financien.model.RegistreerDTO;
import be.ugent.gsr.financien.service.AuthenticationService;
import be.ugent.gsr.financien.service.GebruikerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final GebruikerService gebruikerService;

    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponse> signup(@RequestBody RegistreerDTO request) {
        return ResponseEntity.ok(authenticationService.registreer(request));
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> signin(@RequestBody LoginDTO request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @GetMapping("/user")
    public ResponseEntity<GebruikerDTO> getGebruiker() {
        Gebruiker gebruiker = (Gebruiker) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(gebruikerService.mapToDTO(gebruiker, new GebruikerDTO()));
    }

}