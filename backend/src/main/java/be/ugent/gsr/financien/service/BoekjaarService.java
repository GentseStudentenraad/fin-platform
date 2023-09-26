package be.ugent.gsr.financien.service;

import be.ugent.gsr.financien.domain.Boekjaar;
import be.ugent.gsr.financien.model.BoekjaarDTO;
import be.ugent.gsr.financien.repos.BoekjaarRepository;
import be.ugent.gsr.financien.util.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BoekjaarService {

    private final BoekjaarRepository boekjaarRepository;

    public BoekjaarService(final BoekjaarRepository boekjaarRepository) {
        this.boekjaarRepository = boekjaarRepository;
    }

    public List<BoekjaarDTO> findAll() {
        final List<Boekjaar> boekjaren = boekjaarRepository.findAll(Sort.by("id"));
        return boekjaren.stream()
                .map(boekjaar -> mapToDTO(boekjaar, new BoekjaarDTO()))
                .toList();
    }

    public BoekjaarDTO get(final Integer id) {
        return boekjaarRepository.findById(id)
                .map(boekjaar -> mapToDTO(boekjaar, new BoekjaarDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final BoekjaarDTO boekjaarDTO) {
        final Boekjaar boekjaar = new Boekjaar();
        mapToEntity(boekjaarDTO, boekjaar);
        return boekjaarRepository.save(boekjaar).getId();
    }

    public void delete(final Integer id) {
        boekjaarRepository.deleteById(id);
    }

    private BoekjaarDTO mapToDTO(final Boekjaar boekjaar, final BoekjaarDTO boekjaarDTO) {
        boekjaarDTO.setId(boekjaar.getId());
        boekjaarDTO.setJaartal(boekjaar.getJaartal());
        boekjaarDTO.setAfgerond(boekjaar.getAfgerond());
        return boekjaarDTO;
    }

    private Boekjaar mapToEntity(final BoekjaarDTO boekjaarDTO, final Boekjaar boekjaar) {
        if (boekjaarDTO.getJaartal() != null)
            boekjaar.setJaartal(boekjaarDTO.getJaartal());
        if (boekjaarDTO.getAfgerond() != null)
            boekjaar.setAfgerond(boekjaarDTO.getAfgerond());
        return boekjaar;
    }

    public void afronden(final Integer id) {
        final Boekjaar boekjaar = boekjaarRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        boekjaar.setAfgerond(true);
        boekjaarRepository.save(boekjaar);
    }
}
