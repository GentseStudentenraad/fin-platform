package be.ugent.gsr.financien.domain;

import be.ugent.gsr.financien.model.KostStatus;
import be.ugent.gsr.financien.model.KostType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Kost extends AbstractAuditableEntity {

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

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal hoeveelheid;

    @Column(nullable = false, columnDefinition = "text")
    private String uitleg;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private KostType type;

    @Column(nullable = false)
    private LocalDate costDatum;

    @Column
    private OffsetDateTime ingediendDatum;

    @Column
    private LocalDate processedDatum;

    @Column(nullable = false)
    private String recipient;

    @Column(columnDefinition = "text")
    private String remarks;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private KostStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_budget_post_id", nullable = false)
    private SubBudgetPost subBudgetPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Gebruiker gebruiker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private Organisatie organisatie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bankgegevens_id")
    private Bankgegevens bankgegevens;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "kost")
    private List<Document> bewijzen;
    // TODO moet er hiervoor een apparte endpoint die "add document" is? of elke keer als een kost aangepast wordt met een nieuw document wordt het aan de lijst toegevoegd. En dan de endpoint van de documenten zelf gebruiken voor verwijderen

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(nullable = false)
    private String lastModifiedBy;

}
