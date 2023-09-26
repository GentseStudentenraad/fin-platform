package be.ugent.gsr.financien.repos;

import be.ugent.gsr.financien.domain.SubBudgetPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;


public interface SubBudgetPostRepository extends JpaRepository<SubBudgetPost, Integer> {

    @Query("SELECT sb FROM SubBudgetPost sb WHERE sb.budgetPost.boekjaar.jaartal = :jaar")
    List<SubBudgetPost> findAllByBoekjaarJaartal(@Param("jaar") Integer jaar);

    @Query("SELECT sb FROM SubBudgetPost sb WHERE sb.budgetPost.boekjaar.jaartal = :jaar AND sb.id IN :subBudgetPostIds")
    List<SubBudgetPost> findAllByBoekjaarJaartalAndRoles(@Param("jaar") Integer jaar, @Param("subBudgetPostIds") Set<Integer> subBudgetPostIds);

    @Query("SELECT sb FROM SubBudgetPost sb " +
            "JOIN sb.budgetPost bp " +
            "JOIN bp.boekjaar bj " +
            "WHERE bj.jaartal = :jaartal AND sb.id IN (" +
            "SELECT sbp.id FROM Rol r " +
            "JOIN r.subBudgetPost sbp " +
            "JOIN sbp.budgetPost bp " +
            "JOIN bp.boekjaar bj " +
            "WHERE bj.jaartal = :jaartal AND (r.id IN (" +
            "SELECT r.id FROM Gebruiker g " +
            "JOIN g.rollen r WHERE g.id = :gebruikerId" +
            ") OR r.id IN (" +
            "SELECT r.id FROM Organisatie o " +
            "JOIN o.rollen r " +
            "JOIN o.organisatieUsers g WHERE g.id = :gebruikerId" +
            ")))")
    List<SubBudgetPost> findAllAccessibleByGebruikerAndYear(@Param("gebruikerId") Integer gebruikerId, @Param("jaartal") Integer jaartal);





}
