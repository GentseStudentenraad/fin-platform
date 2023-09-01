package be.ugent.gsr.financien.rest;

import be.ugent.gsr.financien.model.OrganisatieDTO;
import be.ugent.gsr.financien.service.OrganisatieService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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


@RestController
@RequestMapping(value = "/api/organisaties", produces = MediaType.APPLICATION_JSON_VALUE)
// TODO juiste auth rollen toevoegen. Je mag enkel organisaties zien waar je aan gekoppeld bent. Of voorzitter/beheerder zijn.
public class OrganisatieResource {

    private final OrganisatieService organisatieService;

    public OrganisatieResource(final OrganisatieService organisatieService) {
        this.organisatieService = organisatieService;
    }

    @GetMapping
    public ResponseEntity<List<OrganisatieDTO>> getAllOrganisaties() {
        return ResponseEntity.ok(organisatieService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganisatieDTO> getOrganisatie(
            @PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(organisatieService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createOrganisatie(
            @RequestBody @Valid final OrganisatieDTO organisatieDTO) {
        final Integer createdId = organisatieService.create(organisatieDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateOrganisatie(@PathVariable(name = "id") final Integer id,
                                                     @RequestBody @Valid final OrganisatieDTO organisatieDTO) {
        organisatieService.update(id, organisatieDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteOrganisatie(@PathVariable(name = "id") final Integer id) {
        organisatieService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
