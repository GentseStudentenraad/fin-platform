package be.ugent.gsr.financien.rest;

import be.ugent.gsr.financien.domain.Gebruiker;
import be.ugent.gsr.financien.model.NotaDTO;
import be.ugent.gsr.financien.service.NotaService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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


@RestController
@RequestMapping(value = "/api/notas", produces = MediaType.APPLICATION_JSON_VALUE)
public class NotaResource {

    private final NotaService notaService;

    public NotaResource(final NotaService notaService) {
        this.notaService = notaService;
    }

    @GetMapping("/boekjaar/{boekjaarID}")
    public ResponseEntity<Page<NotaDTO>> getAllNotas(@PathVariable(name = "boekjaarID") final Integer boekjaar, Pageable pageable) {
        Gebruiker gebruiker = (Gebruiker) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(notaService.findAll(pageable, gebruiker, boekjaar));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotaDTO> getNota(@PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(notaService.get(id));
    }

    @PostMapping("/boekjaar/{boekjaarID}")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createNota(@RequestBody @Valid final NotaDTO notaDTO, @PathVariable(name = "boekjaarID") final Integer boekjaar) {
        Gebruiker gebruiker = (Gebruiker) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return notaService.create(notaDTO, gebruiker, boekjaar);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateNota(@PathVariable(name = "id") final Integer id,
                                              @RequestBody @Valid final NotaDTO notaDTO) {
        Gebruiker gebruiker = (Gebruiker) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return notaService.update(id, notaDTO, gebruiker);
    }

}
