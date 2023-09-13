package be.ugent.gsr.financien.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BoekjaarDTO extends AbstractAuditDTO{

    private Integer id;

    @NotNull
    private Integer jaartal;

    //default value is false. enkel automatisch ingevuld als nieuw boekjaar aangemaakt wordt
    private Boolean afgerond = false;

}
