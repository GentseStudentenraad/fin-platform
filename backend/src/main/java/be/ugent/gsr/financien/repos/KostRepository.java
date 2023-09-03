package be.ugent.gsr.financien.repos;

import be.ugent.gsr.financien.domain.Kost;
import be.ugent.gsr.financien.domain.SubBudgetPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface KostRepository extends JpaRepository<Kost, Integer> {

    Page<Kost> findKostsBySubBudgetPostIn(List<SubBudgetPost> subBudgetPosts, Pageable pageable);

}
