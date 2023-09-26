package be.ugent.gsr.financien.domain;

import be.ugent.gsr.financien.model.NotaDTO;
import be.ugent.gsr.financien.model.NotaStatus;
import be.ugent.gsr.financien.model.NotaType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.time.OffsetDateTime;
import java.util.List;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Nota extends AbstractAuditableEntity {

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

    @Column(nullable = false, columnDefinition = "text")
    private String uitleg;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotaType type;

    @Column
    private OffsetDateTime ingediendDatum;

    @Column
    private OffsetDateTime processedDatum;

    @Column(columnDefinition = "text")
    private String remarks;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotaStatus status;

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
    @JoinColumn(name = "bankgegevens_id", nullable = true)
    private Bankgegevens bankgegevens; // De bankgegevens zijn enkel van belang indien de NotaType "onkost" is. Anders zijn er geen bankgegevens nodig.

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "nota")
    private List<Kost> kosten;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Document> bewijzen;


    public Nota(NotaDTO notaDTO) {
        super(notaDTO);
        this.setId(notaDTO.getId());
        this.setUitleg(notaDTO.getUitleg());
        this.setType(notaDTO.getType());
        this.setIngediendDatum(OffsetDateTime.now());
        this.setProcessedDatum(notaDTO.getProcessedDatum());
        this.setRemarks(notaDTO.getRemarks());
        this.setStatus(notaDTO.getStatus());
    }
}
