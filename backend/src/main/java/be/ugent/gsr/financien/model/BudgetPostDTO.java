package be.ugent.gsr.financien.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BudgetPostDTO {

    private Integer id;

    @NotNull
    @Size(max = 255)
    private String naam;

    private String beschrijving;

    @NotNull
    private Integer boekjaar;

}
