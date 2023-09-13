package be.ugent.gsr.financien.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
public class HerbegrotingDTO extends AbstractAuditDTO {

    private Integer id;

    @NotNull
    @Digits(integer = 12, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "72.08")
    private BigDecimal hoeveelheid;

    @NotNull
    private LocalDate datum;

    @NotNull
    private Integer boekjaar;

    @NotNull
    private Integer fromSubBudgetPost;

    @NotNull
    private Integer toSubBudgetPost;

}
