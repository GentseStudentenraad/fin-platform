package be.ugent.gsr.financien.model;

import be.ugent.gsr.financien.domain.Document;
import be.ugent.gsr.financien.domain.Kost;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class KostDTO extends AbstractAuditableDTO {
    private final Integer id;

    @NotNull
    @Digits(integer = 12, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "53.08")
    private final BigDecimal bedrag;

    private final String uitleg;


    private final LocalDate kostDatum;

    private final List<Integer> bewijzenIds;

    @NotNull
    private Integer notaId;

    // Constructor to initialize from Kost entity
    public KostDTO(Kost kost) {
        super(kost);
        this.id = kost.getId();
        this.bedrag = kost.getBedrag();
        this.uitleg = kost.getUitleg();
        this.kostDatum = kost.getKostDatum();

        // Assuming Document entity has an getId() method
        this.bewijzenIds = kost.getBewijzen().stream().map(Document::getId).collect(Collectors.toList());

        if (kost.getNota() != null) {
            this.notaId = kost.getNota().getId();
        }
    }

}
