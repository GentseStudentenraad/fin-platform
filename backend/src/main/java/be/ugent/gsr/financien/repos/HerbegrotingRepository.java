package be.ugent.gsr.financien.repos;

import be.ugent.gsr.financien.domain.Herbegroting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface HerbegrotingRepository extends JpaRepository<Herbegroting, Integer> {

    List<Herbegroting> getHerbegrotingByBoekjaar_Id(Integer boekjaar);
}
