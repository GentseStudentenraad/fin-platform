package be.ugent.gsr.financien.repos;

import be.ugent.gsr.financien.domain.Gebruiker;
import be.ugent.gsr.financien.domain.SubBudgetPost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface SubBudgetPostRepository extends JpaRepository<SubBudgetPost, Integer> {

}
