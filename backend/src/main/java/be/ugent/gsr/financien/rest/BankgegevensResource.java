package be.ugent.gsr.financien.rest;

import be.ugent.gsr.financien.domain.Bankgegevens;
import be.ugent.gsr.financien.model.BankgegevensDTO;
import be.ugent.gsr.financien.service.BankgegevensService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bankgegevens")
public class BankgegevensResource {

    @Autowired
    private BankgegevensService bankgegevensService;

    @GetMapping
    public ResponseEntity<List<BankgegevensDTO>> getAll() {
        List<Bankgegevens> bankgegevensList = bankgegevensService.getAll();
        List<BankgegevensDTO> dtoList = bankgegevensList.stream()
                .map(BankgegevensDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankgegevensDTO> getById(@PathVariable Integer id) {
        Bankgegevens bankgegevens = bankgegevensService.getById(id);
        if (bankgegevens == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new BankgegevensDTO(bankgegevens));
    }

    @PostMapping
    public ResponseEntity<BankgegevensDTO> create(@RequestBody BankgegevensDTO bankgegevensDTO) {
        Bankgegevens created = bankgegevensService.create(bankgegevensDTO);
        return ResponseEntity.ok(new BankgegevensDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BankgegevensDTO> update(@PathVariable Integer id, @RequestBody BankgegevensDTO bankgegevensDTO) {
        Bankgegevens updated = bankgegevensService.update(id, bankgegevensDTO);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new BankgegevensDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean deleted = bankgegevensService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }
}
