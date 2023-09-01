package be.ugent.gsr.financien.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
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
public class SubBudgetPost {

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

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal budget;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal verbruiktBudget;

    @Column(columnDefinition = "text")
    private String beschrijving;

    @Column(nullable = false)
    private Boolean allowCosts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_post_id", nullable = false)
    private BudgetPost budgetPost;

    @OneToMany(mappedBy = "subBudgetPost")
    private Set<Kost> kosten;

    @ManyToMany(mappedBy = "subBudgetPost")
    private Set<Rol> roles;

    @OneToMany(mappedBy = "fromSubBudgetPost")
    private Set<Herbegroting> fromSubBudgetPostHerbegrotings;

    @OneToMany(mappedBy = "toSubBudgetPost")
    private Set<Herbegroting> toSubBudgetPostHerbegrotings;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

}
