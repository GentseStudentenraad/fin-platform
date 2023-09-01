package be.ugent.gsr.financien.rest;

import be.ugent.gsr.financien.model.GebruikerDTO;
import be.ugent.gsr.financien.service.GebruikerService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping(value = "/api/gebruikers", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasAnyRole('VOORZITTER', 'BEHEERDER')")
public class GebruikerResource {

    private final GebruikerService gebruikerService;

    public GebruikerResource(final GebruikerService gebruikerService) {
        this.gebruikerService = gebruikerService;
    }

    @GetMapping
    public ResponseEntity<List<GebruikerDTO>> getAllGebruikers() {
        return ResponseEntity.ok(gebruikerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GebruikerDTO> getGebruiker(@PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(gebruikerService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createGebruiker(
            @RequestBody @Valid final GebruikerDTO gebruikerDTO) {
        final Integer createdId = gebruikerService.create(gebruikerDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateGebruiker(@PathVariable(name = "id") final Integer id,
                                                   @RequestBody @Valid final GebruikerDTO gebruikerDTO) {
        gebruikerService.update(id, gebruikerDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteGebruiker(@PathVariable(name = "id") final Integer id) {
        gebruikerService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
