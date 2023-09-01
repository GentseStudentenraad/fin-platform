package be.ugent.gsr.financien.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;


@Getter
@Setter
public class KostDTO { //TODO add pdf data

    private Integer id;

    @NotNull
    @Digits(integer = 12, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "72.08")
    private BigDecimal hoeveelheid;

    @NotNull
    private String uitleg;

    @NotNull
    private KostType type;

    @NotNull
    private LocalDate costDatum;

    private OffsetDateTime ingediendDatum;

    private LocalDate processedDatum;

    @NotNull
    @Size(max = 255)
    private String recipient;

    private String remarks;

    @NotNull
    private KostStatus status;

    @NotNull
    private Integer subBudgetPost;

    @NotNull
    private Integer user;

}
