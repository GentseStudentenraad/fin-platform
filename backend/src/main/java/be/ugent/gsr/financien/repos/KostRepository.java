package be.ugent.gsr.financien.repos;

import be.ugent.gsr.financien.domain.Kost;
import org.springframework.data.jpa.repository.JpaRepository;


public interface KostRepository extends JpaRepository<Kost, Integer> {
}
