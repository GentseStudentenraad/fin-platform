package be.ugent.gsr.financien.controller;


import be.ugent.gsr.financien.model.KostDTO;
import be.ugent.gsr.financien.service.KostService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Deze Controller is voor alle endpoints die enkel voor de beheerders zijn.
 */

@RestController
@RequestMapping("/api")
@PreAuthorize("hasAnyRole('VOORZITTER', 'BEHEERDER')")
public class BeheerdersController {

    private final KostService kostService;

    public BeheerdersController(final KostService kostService) {
        this.kostService = kostService;
    }

    @PutMapping("/kosten/{id}/goedkeuren")
    public ResponseEntity<Integer> goedkeurenKost(@PathVariable(name = "id") final Integer id,
                                                  @RequestBody @Valid final KostDTO kostDTO) {
        return kostService.afhandelen(id, kostDTO);
    }

    @PutMapping("/kosten/{id}/afkeuren")
    public ResponseEntity<Integer> afkeurenKost(@PathVariable(name = "id") final Integer id,
                                                  @RequestBody @Valid final KostDTO kostDTO) {
        return kostService.afhandelen(id, kostDTO);
    }

    @DeleteMapping("/kosten/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteKost(@PathVariable(name = "id") final Integer id) {
        kostService.delete(id);
        return ResponseEntity.noContent().build();
    }



}
