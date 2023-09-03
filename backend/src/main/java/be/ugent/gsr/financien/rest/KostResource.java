package be.ugent.gsr.financien.rest;

import be.ugent.gsr.financien.domain.Gebruiker;
import be.ugent.gsr.financien.model.KostDTO;
import be.ugent.gsr.financien.service.KostService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "/api/kosten", produces = MediaType.APPLICATION_JSON_VALUE)
public class KostResource {

    private final KostService kostService;

    public KostResource(final KostService kostService) {
        this.kostService = kostService;
    }

    @GetMapping("/boekjaar/{boekjaarID}")
    public ResponseEntity<Page<KostDTO>> getAllKosts(@PathVariable(name = "boekjaarID") final Integer boekjaar, Pageable pageable) {
        Gebruiker gebruiker = (Gebruiker) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(kostService.findAll(pageable,gebruiker, boekjaar));
    }

    @GetMapping("/{id}")
    public ResponseEntity<KostDTO> getKost(@PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(kostService.get(id));
    }

    @PostMapping("/boekjaar/{boekjaarID}")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createKost(@RequestBody @Valid final KostDTO kostDTO, @PathVariable(name = "boekjaarID") final Integer boekjaar) {
        Gebruiker gebruiker = (Gebruiker) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return kostService.create(kostDTO, gebruiker, boekjaar);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateKost(@PathVariable(name = "id") final Integer id,
                                              @RequestBody @Valid final KostDTO kostDTO) {
        Gebruiker gebruiker = (Gebruiker) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return kostService.update(id, kostDTO, gebruiker);
    }

}
