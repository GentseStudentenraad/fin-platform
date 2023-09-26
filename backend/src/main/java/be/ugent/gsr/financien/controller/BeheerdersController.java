package be.ugent.gsr.financien.controller;

import be.ugent.gsr.financien.model.NotaDTO;
import be.ugent.gsr.financien.service.NotaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Deze Controller is voor alle endpoints die enkel voor de voorzitter zijn.
 */

@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('BEHEERDER')")
public class BeheerdersController {


    private final NotaService notaService;

    public BeheerdersController(final NotaService notaService) {
        this.notaService = notaService;
    }

    /* Al de endpoints die met notas te maken hebben. */

    @PutMapping("/notas/{id}/ondertekenen")
    public ResponseEntity<Integer> ondertekennota(@PathVariable(name = "id") final Integer id,
                                                  @RequestBody @Valid final NotaDTO notaDTO) {
        return notaService.onderteken(id, notaDTO);
    }

    //TODO endpoint om een bepaalde user een gebruiker en/of beheerder te maken.

}
