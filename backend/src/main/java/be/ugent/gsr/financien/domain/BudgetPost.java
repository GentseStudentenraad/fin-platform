package be.ugent.gsr.financien.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetPost extends AbstractAuditableEntity {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "primary_sequence",
            sequenceName = "primary_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence"
    )
    private Integer id;

    @Column(nullable = false)
    private String naam;

    @Column(nullable = false)
    private String dsvCode;

    @Column(columnDefinition = "text")
    private String beschrijving;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boekjaar_id", nullable = false)
    private Boekjaar boekjaar;

    @OneToMany(mappedBy = "budgetPost")
    private Set<SubBudgetPost> subBudgetPosten;


    public BigDecimal getBudget() {
        return this.subBudgetPosten.stream().map(SubBudgetPost::getBudget).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getBudgetOver() {
        return this.subBudgetPosten.stream().map(SubBudgetPost::getVerbruiktBudget).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
