package be.ugent.gsr.financien.model;

import be.ugent.gsr.financien.domain.BudgetPost;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
public class SubBudgetPostDTO extends AbstractAuditableDTO {

    private Integer id;

    @NotNull
    @Size(max = 255)
    private String naam;

    @NotNull
    @Digits(integer = 12, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "77.08")
    private BigDecimal budget;

    @NotNull
    @Digits(integer = 12, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "53.08")
    private BigDecimal verbruiktBudget = BigDecimal.valueOf(0.0); //default 0 used

    private String beschrijving;

    @NotNull
    private Boolean allowCosts;

    @NotNull
    private Integer budgetPost;

}
