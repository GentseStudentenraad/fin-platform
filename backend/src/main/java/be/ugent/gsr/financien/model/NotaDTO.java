package be.ugent.gsr.financien.model;

import be.ugent.gsr.financien.domain.Document;
import be.ugent.gsr.financien.domain.Kost;
import be.ugent.gsr.financien.domain.Nota;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotaDTO extends AbstractAuditableDTO {

    private Integer id;

    @NotNull
    private String uitleg;

    @NotNull
    private NotaType type;

    private OffsetDateTime ingediendDatum;

    private OffsetDateTime processedDatum;

    private String remarks;

    @NotNull
    private NotaStatus status;

    @NotNull
    private Integer subBudgetPost;

    @NotNull
    private Integer gebruiker;

    @NotNull
    private Integer organisatie;

    @NotNull
    private Integer bankgegevens;

    @NotNull
    private List<Integer> kosten;

    @NotNull
    private List<Integer> bewijzen;


    public NotaDTO(Nota nota) {
        super(nota);
        this.id = nota.getId();
        this.uitleg = nota.getUitleg();
        this.type = nota.getType();
        this.ingediendDatum = nota.getIngediendDatum();
        this.processedDatum = nota.getProcessedDatum();
        this.remarks = nota.getRemarks();
        this.status = nota.getStatus();

        // Foreign keys
        this.subBudgetPost = nota.getSubBudgetPost().getId();
        this.gebruiker = nota.getGebruiker() != null ? nota.getGebruiker().getId() : null;
        this.organisatie = nota.getOrganisatie() != null ? nota.getOrganisatie().getId() : null;
        this.bankgegevens = nota.getBankgegevens().getId();

        // For the List<Kost> and List<Document>, assuming you want to just map their IDs
        if (nota.getKosten() != null) {
            this.kosten = nota.getKosten().stream().map(Kost::getId).collect(Collectors.toList());
        }
        if (nota.getBewijzen() != null) {
            this.bewijzen = nota.getBewijzen().stream().map(Document::getId).collect(Collectors.toList());
        }
    }
}
