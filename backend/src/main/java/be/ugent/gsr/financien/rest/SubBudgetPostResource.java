package be.ugent.gsr.financien.rest;

import be.ugent.gsr.financien.domain.Gebruiker;
import be.ugent.gsr.financien.model.SubBudgetPostDTO;
import be.ugent.gsr.financien.service.SubBudgetPostService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
@RequestMapping(value = "/api/subBudgetPosten", produces = MediaType.APPLICATION_JSON_VALUE)
//TODO enkel subbudgetposten kunnen opvragen waar je toegang toe hebt.
public class SubBudgetPostResource {

    private final SubBudgetPostService subBudgetPostService;

    public SubBudgetPostResource(final SubBudgetPostService subBudgetPostService) {
        this.subBudgetPostService = subBudgetPostService;
    }

    @GetMapping("/boekjaar/{boekjaarID}")
    public ResponseEntity<List<SubBudgetPostDTO>> getAllSubBudgetPosts(
            @PathVariable(name = "boekjaarID") final Integer boekjaar) {
        Gebruiker gebruiker = (Gebruiker) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(subBudgetPostService.findAll(gebruiker, boekjaar));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubBudgetPostDTO> getSubBudgetPost(
            @PathVariable(name = "id") final Integer id) {
        Gebruiker gebruiker = (Gebruiker) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return subBudgetPostService.get(id, gebruiker);
    }

    /**
    * Enkel de Beheerders mogen een subbudget post aanmaken
    * */
    @PostMapping
    @ApiResponse(responseCode = "201")
    @PreAuthorize("hasAnyRole('VOORZITTER', 'BEHEERDER')")
    public ResponseEntity<Integer> createSubBudgetPost(
            @RequestBody @Valid final SubBudgetPostDTO subBudgetPostDTO) {
        final Integer createdId = subBudgetPostService.create(subBudgetPostDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    /**
     * Enkel de Beheerders mogen een subbudget post aanpassen
     * */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('VOORZITTER', 'BEHEERDER')")
    public ResponseEntity<Integer> updateSubBudgetPost(@PathVariable(name = "id") final Integer id,
                                                       @RequestBody @Valid final SubBudgetPostDTO subBudgetPostDTO) {
        subBudgetPostService.update(id, subBudgetPostDTO);
        return ResponseEntity.ok(id);
    }

    /**
     * Enkel de Beheerders mogen een subbudget post verwijderen
     * */
    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    @PreAuthorize("hasAnyRole('VOORZITTER', 'BEHEERDER')")
    public ResponseEntity<Void> deleteSubBudgetPost(@PathVariable(name = "id") final Integer id) {
        subBudgetPostService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
