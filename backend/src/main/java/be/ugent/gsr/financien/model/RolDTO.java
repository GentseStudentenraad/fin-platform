package be.ugent.gsr.financien.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class RolDTO extends AbstractAuditDTO {

    private Integer id;

    @NotNull
    @Size(max = 255)
    private String naam;

    private List<Integer> subBudgetPost;

}
