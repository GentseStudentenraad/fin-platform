package be.ugent.gsr.financien.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
public class BudgetPostDTO extends AbstractAuditDTO {

    private Integer id;

    @NotNull
    @Size(max = 255)
    private String naam;

    private String beschrijving;

    @NotNull
    private Integer boekjaar;

    @NotNull
    private BigDecimal budget;

}
