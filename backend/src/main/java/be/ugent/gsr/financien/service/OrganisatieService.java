package be.ugent.gsr.financien.service;

import be.ugent.gsr.financien.domain.Gebruiker;
import be.ugent.gsr.financien.domain.Organisatie;
import be.ugent.gsr.financien.domain.Rol;
import be.ugent.gsr.financien.model.GebruikerType;
import be.ugent.gsr.financien.model.OrganisatieDTO;
import be.ugent.gsr.financien.repos.GebruikerRepository;
import be.ugent.gsr.financien.repos.OrganisatieRepository;
import be.ugent.gsr.financien.repos.RolRepository;
import be.ugent.gsr.financien.util.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class OrganisatieService {

    private final OrganisatieRepository organisatieRepository;
    private final RolRepository rolRepository;
    private final GebruikerRepository gebruikerRepository;

    public OrganisatieService(final OrganisatieRepository organisatieRepository,
                              final RolRepository rolRepository, final GebruikerRepository gebruikerRepository) {
        this.organisatieRepository = organisatieRepository;
        this.rolRepository = rolRepository;
        this.gebruikerRepository = gebruikerRepository;
    }

    public List<OrganisatieDTO> findAll(Gebruiker gebruiker) {
        final List<Organisatie> organisaties;
        if (gebruiker.getType() == GebruikerType.OTHER) {
            organisaties = organisatieRepository.findAllByOrganisatieUsersContaining(gebruiker);
        } else {
            organisaties = organisatieRepository.findAll();
        }
        return organisaties.stream()
                .map(organisatie -> mapToDTO(organisatie, new OrganisatieDTO()))
                .toList();
    }

    public ResponseEntity<OrganisatieDTO> get(final Integer id, Gebruiker gebruiker) {
        final Organisatie organisatie = organisatieRepository.findById(id).orElseThrow(NotFoundException::new);
        if (gebruiker.getType() == GebruikerType.OTHER) {
            if (organisatie.getOrganisatieUsers().contains(gebruiker)){
                return ResponseEntity.ok(mapToDTO(organisatie, new OrganisatieDTO()));
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } else {
            return ResponseEntity.ok(mapToDTO(organisatie, new OrganisatieDTO()));
        }
    }

    public Integer create(final OrganisatieDTO organisatieDTO) {
        final Organisatie organisatie = new Organisatie();
        mapToEntity(organisatieDTO, organisatie);
        return organisatieRepository.save(organisatie).getId();
    }

    public void update(final Integer id, final OrganisatieDTO organisatieDTO) {
        final Organisatie organisatie = organisatieRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(organisatieDTO, organisatie);
        organisatieRepository.save(organisatie);
    }

    public void delete(final Integer id) {
        final Organisatie organisatie = organisatieRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        gebruikerRepository.findAllByOrganisatie(organisatie)
                .forEach(gebruiker -> gebruiker.getOrganisatie().remove(organisatie));
        organisatieRepository.delete(organisatie);
    }

    public OrganisatieDTO mapToDTO(final Organisatie organisatie,
                                    final OrganisatieDTO organisatieDTO) {
        organisatieDTO.setId(organisatie.getId());
        organisatieDTO.setNaam(organisatie.getNaam());
        organisatieDTO.setIsRecognized(organisatie.getIsRecognized());
        organisatieDTO.setRekeningnummer(organisatie.getRekeningnummer());
        organisatieDTO.setAdres(organisatie.getAdres());
        organisatieDTO.setRol(organisatie.getRol().stream()
                .map(rol -> rol.getId())
                .toList());
        return organisatieDTO;
    }

    public Organisatie mapToEntity(final OrganisatieDTO organisatieDTO,
                                    final Organisatie organisatie) {
        organisatie.setNaam(organisatieDTO.getNaam());
        organisatie.setIsRecognized(organisatieDTO.getIsRecognized());
        organisatie.setRekeningnummer(organisatieDTO.getRekeningnummer());
        organisatie.setAdres(organisatieDTO.getAdres());
        final List<Rol> rol = rolRepository.findAllById(
                organisatieDTO.getRol() == null ? Collections.emptyList() : organisatieDTO.getRol());
        if (rol.size() != (organisatieDTO.getRol() == null ? 0 : organisatieDTO.getRol().size())) {
            throw new NotFoundException("one of rol not found");
        }
        organisatie.setRol(rol.stream().collect(Collectors.toSet()));
        return organisatie;
    }

}
