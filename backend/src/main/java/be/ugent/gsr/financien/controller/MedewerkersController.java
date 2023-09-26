package be.ugent.gsr.financien.controller;


import be.ugent.gsr.financien.service.NotaService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Deze Controller is voor alle endpoints die enkel voor de beheerders of medewerkers zijn.
 */

@RestController
@RequestMapping("/api")
@PreAuthorize("hasAnyRole('MEDEWERKER', 'BEHEERDER')")
public class MedewerkersController {

    private final NotaService notaService;

    public MedewerkersController(final NotaService notaService) {
        this.notaService = notaService;
    }

    @PutMapping("/notas/{id}/goedkeuren")
    public ResponseEntity<Integer> goedkeurenNota(@PathVariable(name = "id") final Integer id) {
        return notaService.goedkeuren(id);
    }

    @PutMapping("/notas/{id}/afkeuren")
    public ResponseEntity<Integer> afkeurenNota(@PathVariable(name = "id") final Integer id) {
        return notaService.afkeuren(id);
    }

    @DeleteMapping("/notas/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteNota(@PathVariable(name = "id") final Integer id) {
        notaService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
