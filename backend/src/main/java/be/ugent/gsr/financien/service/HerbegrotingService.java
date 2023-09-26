package be.ugent.gsr.financien.service;

import be.ugent.gsr.financien.domain.Boekjaar;
import be.ugent.gsr.financien.domain.BudgetPost;
import be.ugent.gsr.financien.domain.Herbegroting;
import be.ugent.gsr.financien.domain.SubBudgetPost;
import be.ugent.gsr.financien.model.HerbegrotingDTO;
import be.ugent.gsr.financien.model.HerbegrotingResponseDTO;
import be.ugent.gsr.financien.repos.BoekjaarRepository;
import be.ugent.gsr.financien.repos.BudgetPostRepository;
import be.ugent.gsr.financien.repos.HerbegrotingRepository;
import be.ugent.gsr.financien.repos.SubBudgetPostRepository;
import be.ugent.gsr.financien.util.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;


@Service
public class HerbegrotingService {

    private final HerbegrotingRepository herbegrotingRepository;
    private final BoekjaarRepository boekjaarRepository;
    private final SubBudgetPostRepository subBudgetPostRepository;
    private final BudgetPostRepository budgetPostRepository;

    public HerbegrotingService(final HerbegrotingRepository herbegrotingRepository,
                               final BoekjaarRepository boekjaarRepository,
                               final SubBudgetPostRepository subBudgetPostRepository,
                               final BudgetPostRepository budgetPostRepository) {
        this.herbegrotingRepository = herbegrotingRepository;
        this.boekjaarRepository = boekjaarRepository;
        this.subBudgetPostRepository = subBudgetPostRepository;
        this.budgetPostRepository = budgetPostRepository;
    }

    public List<HerbegrotingResponseDTO> findAll(Integer boekjaar) {
        final List<Herbegroting> herbegrotingen = herbegrotingRepository.getHerbegrotingByBoekjaar_Id(boekjaar);
        return herbegrotingen.stream()
                .map(herbegroting -> mapToDTO(herbegroting, new HerbegrotingResponseDTO()))
                .toList();
    }

    public HerbegrotingResponseDTO get(final Integer id) {
        return herbegrotingRepository.findById(id)
                .map(herbegroting -> mapToDTO(herbegroting, new HerbegrotingResponseDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public ResponseEntity<Integer> create(final HerbegrotingDTO herbegrotingDTO) {

        if (herbegrotingDTO.getFromPost() == null || herbegrotingDTO.getToPost() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (herbegrotingDTO.isForSubPosts()) {
            SubBudgetPost from = subBudgetPostRepository.findById(herbegrotingDTO.getFromPost()).orElseThrow(EntityNotFoundException::new);
            SubBudgetPost to = subBudgetPostRepository.findById(herbegrotingDTO.getToPost()).orElseThrow(EntityNotFoundException::new);

            if (from.getBudget().compareTo(herbegrotingDTO.getHoeveelheid()) < 0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            from.setBudget(from.getBudget().subtract(herbegrotingDTO.getHoeveelheid()));
            to.setBudget(to.getBudget().add(herbegrotingDTO.getHoeveelheid()));
            subBudgetPostRepository.save(to);
            subBudgetPostRepository.save(from);

            from.getBudgetPost().setBudget(from.getBudgetPost().getBudget().subtract(herbegrotingDTO.getHoeveelheid()));
            to.getBudgetPost().setBudget(to.getBudgetPost().getBudget().add(herbegrotingDTO.getHoeveelheid()));
            budgetPostRepository.save(from.getBudgetPost());
            budgetPostRepository.save(to.getBudgetPost());

        } else {
            BudgetPost from = budgetPostRepository.findById(herbegrotingDTO.getFromPost()).orElseThrow(EntityNotFoundException::new);
            BudgetPost to = budgetPostRepository.findById(herbegrotingDTO.getToPost()).orElseThrow(EntityNotFoundException::new);

            if (from.getBudget().compareTo(herbegrotingDTO.getHoeveelheid()) < 0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            from.setBudget(from.getBudget().subtract(herbegrotingDTO.getHoeveelheid()));
            to.setBudget(to.getBudget().add(herbegrotingDTO.getHoeveelheid()));
            budgetPostRepository.save(from);
            budgetPostRepository.save(to);
        }

        final Herbegroting herbegroting = new Herbegroting();
        mapToEntity(herbegrotingDTO, herbegroting);

        return new ResponseEntity<>(herbegrotingRepository.save(herbegroting).getId(), HttpStatus.CREATED);
    }

    public void update(final Integer id, final HerbegrotingDTO herbegrotingDTO) {
        final Herbegroting herbegroting = herbegrotingRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(herbegrotingDTO, herbegroting);
        herbegrotingRepository.save(herbegroting);
    }

    public void delete(final Integer id) {
        herbegrotingRepository.deleteById(id);
    }

    private HerbegrotingResponseDTO mapToDTO(final Herbegroting herbegroting,
                                             final HerbegrotingResponseDTO herbegrotingDTO) {
        herbegrotingDTO.setId(herbegroting.getId());
        herbegrotingDTO.setHoeveelheid(herbegroting.getHoeveelheid());
        herbegrotingDTO.setDatum(herbegroting.getDatum());
        herbegrotingDTO.setBoekjaar(herbegroting.getBoekjaar() == null ? null : herbegroting.getBoekjaar().getId());
        herbegrotingDTO.setFromPost(herbegroting.getFrom());
        herbegrotingDTO.setToPost(herbegroting.getTo());
        return herbegrotingDTO;
    }

    private Herbegroting mapToEntity(final HerbegrotingDTO herbegrotingDTO,
                                     final Herbegroting herbegroting) {
        herbegroting.setHoeveelheid(herbegrotingDTO.getHoeveelheid());
        herbegroting.setDatum(OffsetDateTime.now());
        final Boekjaar boekjaar = herbegrotingDTO.getBoekjaar() == null ? null : boekjaarRepository.findById(herbegrotingDTO.getBoekjaar())
                .orElseThrow(() -> new NotFoundException("boekjaar not found"));
        herbegroting.setBoekjaar(boekjaar);
        if (herbegrotingDTO.isForSubPosts()){
            final SubBudgetPost fromSubBudgetPost = subBudgetPostRepository.findById(herbegrotingDTO.getFromPost())
                    .orElseThrow(() -> new NotFoundException("fromSubBudgetPost not found"));
            herbegroting.setFrom(fromSubBudgetPost.getNaam());

            final SubBudgetPost toSubBudgetPost = subBudgetPostRepository.findById(herbegrotingDTO.getToPost())
                    .orElseThrow(() -> new NotFoundException("toSubBudgetPost not found"));
            herbegroting.setTo(toSubBudgetPost.getNaam());
        } else {
            final BudgetPost fromSubBudgetPost = budgetPostRepository.findById(herbegrotingDTO.getFromPost())
                    .orElseThrow(() -> new NotFoundException("fromBudgetPost not found"));
            herbegroting.setFrom(fromSubBudgetPost.getDsvCode());

            final BudgetPost toSubBudgetPost = budgetPostRepository.findById(herbegrotingDTO.getToPost())
                    .orElseThrow(() -> new NotFoundException("toBudgetPost not found"));
            herbegroting.setTo(toSubBudgetPost.getDsvCode());
        }
        return herbegroting;
    }

}
