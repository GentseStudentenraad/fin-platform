package be.ugent.gsr.financien.repos;

import be.ugent.gsr.financien.domain.Gebruiker;
import be.ugent.gsr.financien.domain.Organisatie;
import be.ugent.gsr.financien.domain.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface GebruikerRepository extends JpaRepository<Gebruiker, Integer> {

    List<Gebruiker> findAllByOrganisatie(Organisatie organisatie);

    List<Gebruiker> findAllByRol(Rol rol);

    Optional<Gebruiker> findByNaam(String naam);

    Optional<Gebruiker> findByEmail(String email);
}
