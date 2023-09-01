package be.ugent.gsr.financien.rest;

import be.ugent.gsr.financien.model.BoekjaarDTO;
import be.ugent.gsr.financien.service.BoekjaarService;
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
@RequestMapping(value = "/api/boekjaren", produces = MediaType.APPLICATION_JSON_VALUE)
public class BoekjaarResource {

    private final BoekjaarService boekjaarService;

    public BoekjaarResource(final BoekjaarService boekjaarService) {
        this.boekjaarService = boekjaarService;
    }

    @GetMapping
    public ResponseEntity<List<BoekjaarDTO>> getAllBoekjaren() {
        return ResponseEntity.ok(boekjaarService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoekjaarDTO> getBoekjaar(@PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(boekjaarService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    @PreAuthorize("hasAnyRole('VOORZITTER', 'BEHEERDER')")
    public ResponseEntity<Integer> createBoekjaar(
            @RequestBody @Valid final BoekjaarDTO boekjaarDTO) {
        final Integer createdId = boekjaarService.create(boekjaarDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    /*
     * Enkel "afgerond" mag geupdate worden
     * */
    @PutMapping("/{id}/afronden")
    @PreAuthorize("hasAnyRole('VOORZITTER', 'BEHEERDER')")
    public ResponseEntity<Integer> updateBoekjaar(@PathVariable(name = "id") final Integer id) {
        boekjaarService.afronden(id);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "403")
    public ResponseEntity<Void> deleteBoekjaar(@PathVariable(name = "id") final Integer id) {
        //forbidden om een boekjaar te verwijderen
        //boekjaarService.delete(id);
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

}
