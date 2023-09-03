package be.ugent.gsr.financien.service;

import be.ugent.gsr.financien.domain.Boekjaar;
import be.ugent.gsr.financien.domain.BudgetPost;
import be.ugent.gsr.financien.domain.Herbegroting;
import be.ugent.gsr.financien.domain.SubBudgetPost;
import be.ugent.gsr.financien.model.HerbegrotingDTO;
import be.ugent.gsr.financien.repos.BoekjaarRepository;
import be.ugent.gsr.financien.repos.HerbegrotingRepository;
import be.ugent.gsr.financien.repos.SubBudgetPostRepository;
import be.ugent.gsr.financien.util.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class HerbegrotingService {

    private final HerbegrotingRepository herbegrotingRepository;
    private final BoekjaarRepository boekjaarRepository;
    private final SubBudgetPostRepository subBudgetPostRepository;

    public HerbegrotingService(final HerbegrotingRepository herbegrotingRepository,
                               final BoekjaarRepository boekjaarRepository,
                               final SubBudgetPostRepository subBudgetPostRepository) {
        this.herbegrotingRepository = herbegrotingRepository;
        this.boekjaarRepository = boekjaarRepository;
        this.subBudgetPostRepository = subBudgetPostRepository;
    }

    public List<HerbegrotingDTO> findAll(Integer boekjaar) {
        final List<Herbegroting> herbegrotingen = herbegrotingRepository.getHerbegrotingByBoekjaar_Id(boekjaar);
        return herbegrotingen.stream()
                .map(herbegroting -> mapToDTO(herbegroting, new HerbegrotingDTO()))
                .toList();
    }

    public HerbegrotingDTO get(final Integer id) {
        return herbegrotingRepository.findById(id)
                .map(herbegroting -> mapToDTO(herbegroting, new HerbegrotingDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public ResponseEntity<Integer> create(final HerbegrotingDTO herbegrotingDTO) {
        final Herbegroting herbegroting = new Herbegroting();
        mapToEntity(herbegrotingDTO, herbegroting);

        SubBudgetPost from = herbegroting.getFromSubBudgetPost();
        SubBudgetPost to = herbegroting.getToSubBudgetPost();

        if (from.getBudget().compareTo(herbegroting.getHoeveelheid()) < 0){
            herbegrotingRepository.delete(herbegroting);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        from.setBudget(from.getBudget().subtract(herbegroting.getHoeveelheid()));
        to.setBudget(to.getBudget().add(herbegroting.getHoeveelheid()));

        subBudgetPostRepository.save(from);
        subBudgetPostRepository.save(to);
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

    private HerbegrotingDTO mapToDTO(final Herbegroting herbegroting,
                                     final HerbegrotingDTO herbegrotingDTO) {
        herbegrotingDTO.setId(herbegroting.getId());
        herbegrotingDTO.setHoeveelheid(herbegroting.getHoeveelheid());
        herbegrotingDTO.setDatum(herbegroting.getDatum());
        herbegrotingDTO.setBoekjaar(herbegroting.getBoekjaar() == null ? null : herbegroting.getBoekjaar().getId());
        herbegrotingDTO.setFromSubBudgetPost(herbegroting.getFromSubBudgetPost() == null ? null : herbegroting.getFromSubBudgetPost().getId());
        herbegrotingDTO.setToSubBudgetPost(herbegroting.getToSubBudgetPost() == null ? null : herbegroting.getToSubBudgetPost().getId());
        return herbegrotingDTO;
    }

    private Herbegroting mapToEntity(final HerbegrotingDTO herbegrotingDTO,
                                     final Herbegroting herbegroting) {
        herbegroting.setHoeveelheid(herbegrotingDTO.getHoeveelheid());
        herbegroting.setDatum(herbegrotingDTO.getDatum());
        final Boekjaar boekjaar = herbegrotingDTO.getBoekjaar() == null ? null : boekjaarRepository.findById(herbegrotingDTO.getBoekjaar())
                .orElseThrow(() -> new NotFoundException("boekjaar not found"));
        herbegroting.setBoekjaar(boekjaar);
        final SubBudgetPost fromSubBudgetPost = herbegrotingDTO.getFromSubBudgetPost() == null ? null : subBudgetPostRepository.findById(herbegrotingDTO.getFromSubBudgetPost())
                .orElseThrow(() -> new NotFoundException("fromSubBudgetPost not found"));
        herbegroting.setFromSubBudgetPost(fromSubBudgetPost);
        final SubBudgetPost toSubBudgetPost = herbegrotingDTO.getToSubBudgetPost() == null ? null : subBudgetPostRepository.findById(herbegrotingDTO.getToSubBudgetPost())
                .orElseThrow(() -> new NotFoundException("toSubBudgetPost not found"));
        herbegroting.setToSubBudgetPost(toSubBudgetPost);
        return herbegroting;
    }

}
