package be.ugent.gsr.financien.repos;

import be.ugent.gsr.financien.domain.Nota;
import be.ugent.gsr.financien.domain.SubBudgetPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NotaRepository extends JpaRepository<Nota, Integer> {

    Page<Nota> findNotasBySubBudgetPostIn(List<SubBudgetPost> subBudgetPosts, Pageable pageable);

}
