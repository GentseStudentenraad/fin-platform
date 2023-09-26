package be.ugent.gsr.financien.domain;

import be.ugent.gsr.financien.model.BankgegevensDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.util.Set;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bankgegevens extends AbstractAuditableEntity {

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

    @Column(nullable = true)
    private String naam;

    @Column(nullable = false)
    private String iban;

    @Column(nullable = false)
    private String bic;

    @Column(nullable = false)
    private String rekeningnummer;

    @Column(nullable = false)
    private String kaarthouder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gebruiker_id")
    private Gebruiker gebruiker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisatie_id")
    private Organisatie organisatie;

    @OneToMany(mappedBy = "bankgegevens")
    private Set<Nota> notas;


}
