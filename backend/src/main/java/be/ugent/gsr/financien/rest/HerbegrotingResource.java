package be.ugent.gsr.financien.rest;

import be.ugent.gsr.financien.model.HerbegrotingDTO;
import be.ugent.gsr.financien.model.HerbegrotingResponseDTO;
import be.ugent.gsr.financien.service.HerbegrotingService;
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
@RequestMapping(value = "/api/herbegrotingen", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasAnyRole('VOORZITTER', 'BEHEERDER')")
public class HerbegrotingResource {

    private final HerbegrotingService herbegrotingService;

    public HerbegrotingResource(final HerbegrotingService herbegrotingService) {
        this.herbegrotingService = herbegrotingService;
    }

    @GetMapping("/boekjaar/{boekjaarID}")
    public ResponseEntity<List<HerbegrotingResponseDTO>> getAllHerbegrotings(@PathVariable("boekjaarID") Integer boekjaar) {
        return ResponseEntity.ok(herbegrotingService.findAll(boekjaar));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HerbegrotingResponseDTO> getHerbegroting(
            @PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(herbegrotingService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createHerbegroting(
            @RequestBody @Valid final HerbegrotingDTO herbegrotingDTO) {
        return herbegrotingService.create(herbegrotingDTO);
    }

    /**
     * Een herbegroting zou niet mogen aangepast worden.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateHerbegroting(
            @PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final HerbegrotingDTO herbegrotingDTO) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "403")
    public ResponseEntity<Void> deleteHerbegroting(@PathVariable(name = "id") final Integer id) {
        // Forbidden om een herbegroting te verwijderen. Herbegrotingen zijn er om een geschiedenis bij te houden.
        //herbegrotingService.delete(id);
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

}
