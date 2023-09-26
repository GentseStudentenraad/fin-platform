package be.ugent.gsr.financien.model;

import be.ugent.gsr.financien.domain.Bankgegevens;
import be.ugent.gsr.financien.domain.Nota;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.validator.routines.IBANValidator;
import org.apache.commons.validator.routines.checkdigit.CheckDigit;
import org.apache.commons.validator.routines.checkdigit.IBANCheckDigit;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class BankgegevensDTO extends AbstractAuditableDTO {
    private final Integer id;
    private final String naam;
    private final String iban;
    private final String bic;
    private final String rekeningnummer;
    private final String kaarthouder;
    private final Set<Integer> notaIds;

    private Integer gebruikerId;
    private Integer organisatieId;

    // Constructor to initialize from Bankgegevens entity
    public BankgegevensDTO(Bankgegevens bankgegevens) {
        super(bankgegevens);
        this.id = bankgegevens.getId();
        this.naam = bankgegevens.getNaam();
        this.iban = bankgegevens.getIban();
        this.bic = bankgegevens.getBic();
        this.rekeningnummer = bankgegevens.getRekeningnummer();
        this.kaarthouder = bankgegevens.getKaarthouder();

        if (bankgegevens.getGebruiker() != null) {
            this.gebruikerId = bankgegevens.getGebruiker().getId();
        }

        if (bankgegevens.getOrganisatie() != null) {
            this.organisatieId = bankgegevens.getOrganisatie().getId();
        }

        this.notaIds = bankgegevens.getNotas().stream().map(Nota::getId).collect(Collectors.toSet());

    }

    // Validation methods for IBAN and BIC
    private boolean validateIban() {
        IBANValidator ibanValidator = IBANValidator.DEFAULT_IBAN_VALIDATOR;
        CheckDigit ibanCheckDigit = IBANCheckDigit.IBAN_CHECK_DIGIT;
        return ibanValidator.isValid(this.iban) && ibanCheckDigit.isValid(this.iban);
    }

    private boolean validateBic() {
        Pattern bicPattern = Pattern.compile("^[A-Za-z]{6}[A-Za-z0-9]{2}([A-Za-z0-9]{3})?$");
        return bicPattern.matcher(this.bic).matches();
    }

}
