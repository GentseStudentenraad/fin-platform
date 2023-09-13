package be.ugent.gsr.financien.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class OrganisatieDTO extends AbstractAuditDTO{

    private Integer id;

    @NotNull
    @Size(max = 255)
    private String naam;

    @NotNull
    @JsonProperty("isRecognized")
    private Boolean isRecognized;

    @NotNull
    @Size(max = 255)
    private String rekeningnummer;

    @NotNull
    private String adres;

    private List<Integer> rol;

}
