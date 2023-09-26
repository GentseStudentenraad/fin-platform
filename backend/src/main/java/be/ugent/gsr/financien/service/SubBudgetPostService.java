package be.ugent.gsr.financien.service;

import be.ugent.gsr.financien.domain.BudgetPost;
import be.ugent.gsr.financien.domain.Gebruiker;
import be.ugent.gsr.financien.domain.SubBudgetPost;
import be.ugent.gsr.financien.model.SubBudgetPostDTO;
import be.ugent.gsr.financien.repos.BudgetPostRepository;
import be.ugent.gsr.financien.repos.RolRepository;
import be.ugent.gsr.financien.repos.SubBudgetPostRepository;
import be.ugent.gsr.financien.util.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Transactional
public class SubBudgetPostService {

    private final SubBudgetPostRepository subBudgetPostRepository;
    private final BudgetPostRepository budgetPostRepository;
    private final RolRepository rolRepository;

    public SubBudgetPostService(final SubBudgetPostRepository subBudgetPostRepository,
                                final BudgetPostRepository budgetPostRepository, final RolRepository rolRepository) {
        this.subBudgetPostRepository = subBudgetPostRepository;
        this.budgetPostRepository = budgetPostRepository;
        this.rolRepository = rolRepository;
    }

    public List<SubBudgetPostDTO> findAll(Gebruiker gebruiker, Integer boekjaar) {
        final List<SubBudgetPost> subBudgetPosts = gebruiker.visibleBudgetPosts(subBudgetPostRepository, boekjaar);
        return subBudgetPosts.stream()
                .map(subBudgetPost -> mapToDTO(subBudgetPost, new SubBudgetPostDTO()))
                .toList();
    }

    public ResponseEntity<SubBudgetPostDTO> get(final Integer id, Gebruiker gebruiker) {
        SubBudgetPost subBudgetPost = subBudgetPostRepository.findById(id).orElseThrow(NotFoundException::new);
        List<SubBudgetPost> accessible = gebruiker.visibleBudgetPosts(subBudgetPostRepository, subBudgetPost.getBudgetPost().getBoekjaar().getJaartal());

        if (accessible.stream().map(SubBudgetPost::getId).toList().contains(subBudgetPost.getId())) {
            return ResponseEntity.ok(mapToDTO(subBudgetPost, new SubBudgetPostDTO()));
        } else {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }

    public Integer create(final SubBudgetPostDTO subBudgetPostDTO) {
        final SubBudgetPost subBudgetPost = new SubBudgetPost();
        mapToEntity(subBudgetPostDTO, subBudgetPost);
        return subBudgetPostRepository.save(subBudgetPost).getId();
    }

    public void update(final Integer id, final SubBudgetPostDTO subBudgetPostDTO) {
        final SubBudgetPost subBudgetPost = subBudgetPostRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(subBudgetPostDTO, subBudgetPost);
        subBudgetPostRepository.save(subBudgetPost);
    }

    public void delete(final Integer id) {
        final SubBudgetPost subBudgetPost = subBudgetPostRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        rolRepository.findAllBySubBudgetPost(subBudgetPost)
                .forEach(rol -> rol.getSubBudgetPost().remove(subBudgetPost));
        subBudgetPostRepository.delete(subBudgetPost);
    }

    private SubBudgetPostDTO mapToDTO(final SubBudgetPost subBudgetPost,
                                      final SubBudgetPostDTO subBudgetPostDTO) {
        subBudgetPostDTO.setId(subBudgetPost.getId());
        subBudgetPostDTO.setNaam(subBudgetPost.getNaam());
        subBudgetPostDTO.setBudget(subBudgetPost.getBudget());
        subBudgetPostDTO.setVerbruiktBudget(subBudgetPost.getVerbruiktBudget());
        subBudgetPostDTO.setBeschrijving(subBudgetPost.getBeschrijving());
        subBudgetPostDTO.setAllowCosts(subBudgetPost.getAllowCosts());
        subBudgetPostDTO.setBudgetPost(subBudgetPost.getBudgetPost() == null ? null : subBudgetPost.getBudgetPost().getId());
        return subBudgetPostDTO;
    }

    private SubBudgetPost mapToEntity(final SubBudgetPostDTO subBudgetPostDTO,
                                      final SubBudgetPost subBudgetPost) {
        subBudgetPost.setNaam(subBudgetPostDTO.getNaam());
        subBudgetPost.setBudget(subBudgetPostDTO.getBudget());
        // Het verbruikt budget wordt enkel aangepast als een nieuwe nota doorgestuurd is naar DSV.
        //subBudgetPost.setVerbruiktBudget(subBudgetPostDTO.getVerbruiktBudget());
        subBudgetPost.setBeschrijving(subBudgetPostDTO.getBeschrijving());
        subBudgetPost.setAllowCosts(subBudgetPostDTO.getAllowCosts());
        final BudgetPost budgetPost = subBudgetPostDTO.getBudgetPost() == null ? null : budgetPostRepository.findById(subBudgetPostDTO.getBudgetPost())
                .orElseThrow(() -> new NotFoundException("budgetPost not found"));
        subBudgetPost.setBudgetPost(budgetPost);
        return subBudgetPost;
    }

}
