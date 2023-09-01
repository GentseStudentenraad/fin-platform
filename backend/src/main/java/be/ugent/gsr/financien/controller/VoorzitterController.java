package be.ugent.gsr.financien.controller;

import be.ugent.gsr.financien.model.KostDTO;
import be.ugent.gsr.financien.service.KostService;
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
@PreAuthorize("hasRole('VOORZITTER')")
public class VoorzitterController {


    private final KostService kostService;

    public VoorzitterController(final KostService kostService) {
        this.kostService = kostService;
    }

    /* Al de endpoints die met kosten te maken hebben. */

    @PutMapping("/kosten/{id}/ondertekenen")
    public ResponseEntity<Integer> ondertekenKost(@PathVariable(name = "id") final Integer id,
                                                  @RequestBody @Valid final KostDTO kostDTO) {
        return kostService.onderteken(id, kostDTO);
    }


}
