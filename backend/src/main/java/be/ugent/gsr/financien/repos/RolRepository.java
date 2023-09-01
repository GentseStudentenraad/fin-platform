package be.ugent.gsr.financien.repos;

import be.ugent.gsr.financien.domain.Rol;
import be.ugent.gsr.financien.domain.SubBudgetPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface RolRepository extends JpaRepository<Rol, Integer> {

    List<Rol> findAllBySubBudgetPost(SubBudgetPost subBudgetPost);

}
