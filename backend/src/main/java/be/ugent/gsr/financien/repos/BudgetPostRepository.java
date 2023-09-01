package be.ugent.gsr.financien.repos;

import be.ugent.gsr.financien.domain.BudgetPost;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BudgetPostRepository extends JpaRepository<BudgetPost, Integer> {
}
