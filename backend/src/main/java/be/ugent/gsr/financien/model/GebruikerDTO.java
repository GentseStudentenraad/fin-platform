package be.ugent.gsr.financien.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class GebruikerDTO extends AbstractAuditDTO{

    private Integer id;

    @NotNull
    @Size(max = 255)
    private String naam;

    @NotNull
    @Size(max = 255)
    private String email;

    @NotNull
    @Size(max = 255)
    private String wachtwoord;

    @NotNull
    private GebruikerType type;

    private List<RolDTO> rol;

    private List<OrganisatieDTO> organisatie;

}
