package be.ugent.gsr.financien.service;

import be.ugent.gsr.financien.domain.Gebruiker;
import be.ugent.gsr.financien.domain.Organisatie;
import be.ugent.gsr.financien.domain.Rol;
import be.ugent.gsr.financien.model.GebruikerDTO;
import be.ugent.gsr.financien.model.OrganisatieDTO;
import be.ugent.gsr.financien.model.RolDTO;
import be.ugent.gsr.financien.repos.GebruikerRepository;
import be.ugent.gsr.financien.repos.OrganisatieRepository;
import be.ugent.gsr.financien.repos.RolRepository;
import be.ugent.gsr.financien.util.NotFoundException;
import jakarta.transaction.Transactional;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class GebruikerService implements UserDetailsService {

    private final GebruikerRepository gebruikerRepository;
    private final RolRepository rolRepository;
    private final OrganisatieRepository organisatieRepository;
    private final RolService rolService;
    private final OrganisatieService organisatieService;

    public GebruikerService(final GebruikerRepository gebruikerRepository,
                            final RolRepository rolRepository,
                            final OrganisatieRepository organisatieRepository, RolService rolService, OrganisatieService organisatieService) {
        this.gebruikerRepository = gebruikerRepository;
        this.rolRepository = rolRepository;
        this.organisatieRepository = organisatieRepository;
        this.rolService = rolService;
        this.organisatieService = organisatieService;
    }

    public List<GebruikerDTO> findAll() {
        final List<Gebruiker> gebruikers = gebruikerRepository.findAll(Sort.by("id"));
        return gebruikers.stream()
                .map(gebruiker -> mapToDTO(gebruiker, new GebruikerDTO()))
                .toList();
    }

    public GebruikerDTO get(final Integer id) {
        return gebruikerRepository.findById(id)
                .map(gebruiker -> mapToDTO(gebruiker, new GebruikerDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final GebruikerDTO gebruikerDTO) {
        final Gebruiker gebruiker = new Gebruiker();
        mapToEntity(gebruikerDTO, gebruiker);
        return gebruikerRepository.save(gebruiker).getId();
    }

    public void update(final Integer id, final GebruikerDTO gebruikerDTO) {
        final Gebruiker gebruiker = gebruikerRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(gebruikerDTO, gebruiker);
        gebruikerRepository.save(gebruiker);
    }

    public void delete(final Integer id) {
        gebruikerRepository.deleteById(id);
    }

    public GebruikerDTO mapToDTO(final Gebruiker gebruiker, final GebruikerDTO gebruikerDTO) {
        gebruikerDTO.setId(gebruiker.getId());
        gebruikerDTO.setNaam(gebruiker.getNaam());
        gebruikerDTO.setEmail(gebruiker.getEmail());
        gebruikerDTO.setWachtwoord(gebruiker.getWachtwoord());
        gebruikerDTO.setType(gebruiker.getType());
        gebruikerDTO.setRol(gebruiker.getRol().stream()
                .map(rol -> rolService.mapToDTO(rol, new RolDTO()))
                .toList());
        gebruikerDTO.setOrganisatie(gebruiker.getOrganisatie().stream()
                .map(organisatie -> organisatieService.mapToDTO(organisatie, new OrganisatieDTO()))
                .toList());
        return gebruikerDTO;
    }

    private Gebruiker mapToEntity(final GebruikerDTO gebruikerDTO, final Gebruiker gebruiker) {
        gebruiker.setNaam(gebruikerDTO.getNaam());
        gebruiker.setEmail(gebruikerDTO.getEmail());
        gebruiker.setWachtwoord(gebruikerDTO.getWachtwoord());
        gebruiker.setType(gebruikerDTO.getType());
        final List<Rol> rol = rolRepository.findAllById(
                gebruikerDTO.getRol() == null ? Collections.emptyList() : gebruikerDTO.getRol().stream().map(RolDTO::getId).toList());
        if (rol.size() != (gebruikerDTO.getRol() == null ? 0 : gebruikerDTO.getRol().size())) {
            throw new NotFoundException("one of rol not found");
        }
        gebruiker.setRol(new HashSet<>(rol));
        final List<Organisatie> organisatie = organisatieRepository.findAllById(
                gebruikerDTO.getOrganisatie() == null ? Collections.emptyList() : gebruikerDTO.getOrganisatie().stream().map(OrganisatieDTO::getId).toList());
        if (organisatie.size() != (gebruikerDTO.getOrganisatie() == null ? 0 : gebruikerDTO.getOrganisatie().size())) {
            throw new NotFoundException("one of organisatie not found");
        }
        gebruiker.setOrganisatie(new HashSet<>(organisatie));
        return gebruiker;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return gebruikerRepository.findByNaam(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
