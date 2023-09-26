package be.ugent.gsr.financien.repos;

import be.ugent.gsr.financien.domain.Gebruiker;
import be.ugent.gsr.financien.domain.Organisatie;
import be.ugent.gsr.financien.domain.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OrganisatieRepository extends JpaRepository<Organisatie, Integer> {

    List<Organisatie> findAllByRol(Rol rol);

    List<Organisatie> findAllByOrganisatieUsersContaining(Gebruiker gebruiker);

}
