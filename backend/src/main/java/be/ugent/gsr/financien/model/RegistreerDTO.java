package be.ugent.gsr.financien.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistreerDTO {
    private String Naam;
    private String email;
    private String wachtwoord;
    private String telNr;
}