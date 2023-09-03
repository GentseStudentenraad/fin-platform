package be.ugent.gsr.financien.service;

import be.ugent.gsr.financien.domain.Boekjaar;
import be.ugent.gsr.financien.domain.BudgetPost;
import be.ugent.gsr.financien.model.BudgetPostDTO;
import be.ugent.gsr.financien.model.SubBudgetPostDTO;
import be.ugent.gsr.financien.repos.BoekjaarRepository;
import be.ugent.gsr.financien.repos.BudgetPostRepository;
import be.ugent.gsr.financien.repos.SubBudgetPostRepository;
import be.ugent.gsr.financien.util.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
public class BudgetPostService {

    private final BudgetPostRepository budgetPostRepository;
    private final BoekjaarRepository boekjaarRepository;

    private final SubBudgetPostService subBudgetPostService;

    public BudgetPostService(final BudgetPostRepository budgetPostRepository,
                             final BoekjaarRepository boekjaarRepository,
                             final SubBudgetPostService subBudgetPostService) {
        this.budgetPostRepository = budgetPostRepository;
        this.boekjaarRepository = boekjaarRepository;
        this.subBudgetPostService = subBudgetPostService;
    }

    public List<BudgetPostDTO> findAll() {
        final List<BudgetPost> budgetPosten = budgetPostRepository.findAll(Sort.by("id"));
        return budgetPosten.stream()
                .map(budgetPost -> mapToDTO(budgetPost, new BudgetPostDTO()))
                .toList();
    }

    public BudgetPostDTO get(final Integer id) {
        return budgetPostRepository.findById(id)
                .map(budgetPost -> mapToDTO(budgetPost, new BudgetPostDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final BudgetPostDTO budgetPostDTO) {
        final BudgetPost budgetPost = new BudgetPost();
        mapToEntity(budgetPostDTO, budgetPost);
        subBudgetPostService.create(SubBudgetPostDTO.basicBudgetPostDTO(budgetPost, budgetPostDTO.getBudget()));
        return budgetPostRepository.save(budgetPost).getId();
    }

    public void update(final Integer id, final BudgetPostDTO budgetPostDTO) {
        final BudgetPost budgetPost = budgetPostRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(budgetPostDTO, budgetPost);
        budgetPostRepository.save(budgetPost);
    }

    public void delete(final Integer id) {
        budgetPostRepository.deleteById(id);
    }

    private BudgetPostDTO mapToDTO(final BudgetPost budgetPost, final BudgetPostDTO budgetPostDTO) {
        budgetPostDTO.setId(budgetPost.getId());
        budgetPostDTO.setNaam(budgetPost.getNaam());
        budgetPostDTO.setBeschrijving(budgetPost.getBeschrijving());
        budgetPostDTO.setBoekjaar(budgetPost.getBoekjaar() == null ? null : budgetPost.getBoekjaar().getId());
        budgetPostDTO.setBudget(budgetPost.getBudget());
        return budgetPostDTO;
    }

    private BudgetPost mapToEntity(final BudgetPostDTO budgetPostDTO, final BudgetPost budgetPost) {
        budgetPost.setNaam(budgetPostDTO.getNaam());
        budgetPost.setBeschrijving(budgetPostDTO.getBeschrijving());
        final Boekjaar boekjaar = budgetPostDTO.getBoekjaar() == null ? null : boekjaarRepository.findById(budgetPostDTO.getBoekjaar())
                .orElseThrow(() -> new NotFoundException("boekjaar not found"));
        budgetPost.setBoekjaar(boekjaar);
        return budgetPost;
    }

    public List<BudgetPostDTO> findAllByBoekjaarId(Integer boekjaarId) {
        return budgetPostRepository.findAll()
                .stream().filter(b -> Objects.equals(b.getBoekjaar().getId(), boekjaarId))
                .map(budgetPost -> mapToDTO(budgetPost, new BudgetPostDTO()))
                .toList();
    }

    public Integer createInBoekjaar(BudgetPostDTO budgetPostDTO, Integer boekjaarId) {
        budgetPostDTO.setBoekjaar(boekjaarId);
        return create(budgetPostDTO);
    }
}
