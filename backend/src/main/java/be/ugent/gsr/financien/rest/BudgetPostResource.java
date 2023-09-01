package be.ugent.gsr.financien.rest;

import be.ugent.gsr.financien.model.BudgetPostDTO;
import be.ugent.gsr.financien.service.BudgetPostService;
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
@RequestMapping(value = "/api/budgetPosten", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasAnyRole('VOORZITTER', 'BEHEERDER')")
public class BudgetPostResource {

    private final BudgetPostService budgetPostService;

    public BudgetPostResource(final BudgetPostService budgetPostService) {
        this.budgetPostService = budgetPostService;
    }

    @GetMapping
    public ResponseEntity<List<BudgetPostDTO>> getAllBudgetPosts() {
        return ResponseEntity.ok(budgetPostService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetPostDTO> getBudgetPost(
            @PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(budgetPostService.get(id));
    }

    @GetMapping("/boekjaar/{boekjaarID}")
    public ResponseEntity<List<BudgetPostDTO>> getAllBudgetPosts(
            @PathVariable(name = "boekjaarID") final Integer boekjaarId) {
        return ResponseEntity.ok(budgetPostService.findAllByBoekjaarId(boekjaarId));
    }

    @PostMapping("/boekjaar/{boekjaarID}")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createBudgetPostBoekjaar(
            @RequestBody @Valid final BudgetPostDTO budgetPostDTO,
            @PathVariable(name = "boekjaarID") final Integer boekjaarId) {
        final Integer createdId = budgetPostService.createInBoekjaar(budgetPostDTO, boekjaarId);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createBudgetPost(
            @RequestBody @Valid final BudgetPostDTO budgetPostDTO) {
        final Integer createdId = budgetPostService.create(budgetPostDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateBudgetPost(@PathVariable(name = "id") final Integer id,
                                                    @RequestBody @Valid final BudgetPostDTO budgetPostDTO) {
        budgetPostService.update(id, budgetPostDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteBudgetPost(@PathVariable(name = "id") final Integer id) {
        budgetPostService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
