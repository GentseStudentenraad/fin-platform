package be.ugent.gsr.financien.service;

import be.ugent.gsr.financien.domain.Bankgegevens;
import be.ugent.gsr.financien.model.BankgegevensDTO;
import be.ugent.gsr.financien.repos.BankgegevensRepository;
import be.ugent.gsr.financien.repos.GebruikerRepository;
import be.ugent.gsr.financien.repos.OrganisatieRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BankgegevensService {

    private final BankgegevensRepository bankgegevensRepository;
    private final GebruikerRepository gebruikersRepository;
    private final OrganisatieRepository organisatieRepository;

    public BankgegevensService(
            BankgegevensRepository bankgegevensRepository,
            GebruikerRepository gebruikersRepository,
            OrganisatieRepository organisatieRepository
    ) {
        this.bankgegevensRepository = bankgegevensRepository;
        this.gebruikersRepository = gebruikersRepository;
        this.organisatieRepository = organisatieRepository;
    }

    public List<Bankgegevens> getAll() {
        return bankgegevensRepository.findAll();
    }

    public Bankgegevens getById(Integer id) {
        return bankgegevensRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Bankgegevens create(BankgegevensDTO bankgegevensDTO) {
        Bankgegevens bankgegevens = new Bankgegevens();
        mapToEntity(bankgegevensDTO, bankgegevens);
        return bankgegevensRepository.save(bankgegevens);
    }

    public Bankgegevens update(Integer id, BankgegevensDTO bankgegevensDTO) {
        Optional<Bankgegevens> optionalBankgegevens = bankgegevensRepository.findById(id);
        if (optionalBankgegevens.isPresent()) {
            Bankgegevens bankgegevens = optionalBankgegevens.get();
            mapToEntity(bankgegevensDTO, bankgegevens);
            return bankgegevensRepository.save(bankgegevens);
        }
        return null;
    }

    public boolean delete(Integer id) {
        Optional<Bankgegevens> bankgegevensOptional = bankgegevensRepository.findById(id);
        if (bankgegevensOptional.isPresent()) {
            if (bankgegevensOptional.get().getNotas().isEmpty()) {
                bankgegevensRepository.deleteById(id);
                return true;
            }
            return false;
        }
        return false;
    }

    private BankgegevensDTO mapToDTO(final Bankgegevens bankgegevens) {
        return new BankgegevensDTO(bankgegevens);
    }

    private Bankgegevens mapToEntity(final BankgegevensDTO bankgegevensDTO, final Bankgegevens bankgegevens){
        if (bankgegevensDTO.getNaam() != null)
            bankgegevens.setNaam(bankgegevens.getNaam());

        if (bankgegevensDTO.getIban() != null)
            bankgegevens.setIban(bankgegevens.getIban());

        if (bankgegevensDTO.getBic() != null)
            bankgegevens.setBic(bankgegevens.getBic());

        if (bankgegevensDTO.getRekeningnummer() != null)
            bankgegevens.setRekeningnummer(bankgegevens.getRekeningnummer());

        if (bankgegevensDTO.getKaarthouder() != null)
            bankgegevens.setKaarthouder(bankgegevens.getKaarthouder());

        if (bankgegevensDTO.getGebruikerId() != null)
            bankgegevens.setGebruiker(gebruikersRepository.findById(bankgegevensDTO.getGebruikerId())
                    .orElseThrow(EntityNotFoundException::new));

        if (bankgegevensDTO.getOrganisatieId() != null)
            bankgegevens.setOrganisatie(organisatieRepository.findById(bankgegevensDTO.getOrganisatieId())
                    .orElseThrow(EntityNotFoundException::new));

        return bankgegevens;
    }

}
