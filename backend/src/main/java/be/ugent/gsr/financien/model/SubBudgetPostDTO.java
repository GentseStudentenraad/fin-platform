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
public class SubBudgetPostDTO extends AbstractAuditDTO {

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

    /**
    * Functie om een basis lege SubBudgetPost aan te maken die het volledig nog te verdelen budget heeft van deze budgetPost.
     * TODO Willen we echt een "te verdelen" subbudgetpost?
     *  Het gaat vrij ambetant worden om elke keer als een subbudgetpost aangemaakt wordt alle subbudgetposten van die budgetpost te overlopen om dan de "te verdelen" budget met x te verminderen.
     *  Gwn de budgetpost een Budget geven gaat veel gemakkelijker zijn
    * */
    public static SubBudgetPostDTO basicBudgetPostDTO(BudgetPost budgetPost, BigDecimal budget) {
        SubBudgetPostDTO res = new SubBudgetPostDTO();
        res.setNaam("Te Verdelen");
        res.setBudget(budget);
        res.setVerbruiktBudget(BigDecimal.ZERO);
        res.setBeschrijving("Nog niet verdeeld budget van deze DSV post.");
        res.setAllowCosts(Boolean.FALSE);
        res.setBudgetPost(budgetPost.getId());
        return res;
    }

}
