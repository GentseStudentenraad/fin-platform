package be.ugent.gsr.financien.repos;

import be.ugent.gsr.financien.domain.Bankgegevens;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankgegevensRepository extends JpaRepository<Bankgegevens, Integer> {

}
