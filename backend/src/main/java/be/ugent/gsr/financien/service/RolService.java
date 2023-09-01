package be.ugent.gsr.financien.service;

import be.ugent.gsr.financien.domain.Rol;
import be.ugent.gsr.financien.domain.SubBudgetPost;
import be.ugent.gsr.financien.model.RolDTO;
import be.ugent.gsr.financien.repos.GebruikerRepository;
import be.ugent.gsr.financien.repos.OrganisatieRepository;
import be.ugent.gsr.financien.repos.RolRepository;
import be.ugent.gsr.financien.repos.SubBudgetPostRepository;
import be.ugent.gsr.financien.util.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;


@Service
@Transactional
public class RolService {

    private final RolRepository rolRepository;
    private final SubBudgetPostRepository subBudgetPostRepository;
    private final GebruikerRepository gebruikerRepository;
    private final OrganisatieRepository organisatieRepository;

    public RolService(final RolRepository rolRepository,
                      final SubBudgetPostRepository subBudgetPostRepository,
                      final GebruikerRepository gebruikerRepository,
                      final OrganisatieRepository organisatieRepository) {
        this.rolRepository = rolRepository;
        this.subBudgetPostRepository = subBudgetPostRepository;
        this.gebruikerRepository = gebruikerRepository;
        this.organisatieRepository = organisatieRepository;
    }

    public List<RolDTO> findAll() {
        final List<Rol> rols = rolRepository.findAll(Sort.by("id"));
        return rols.stream()
                .map(rol -> mapToDTO(rol, new RolDTO()))
                .toList();
    }

    public RolDTO get(final Integer id) {
        return rolRepository.findById(id)
                .map(rol -> mapToDTO(rol, new RolDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final RolDTO rolDTO) {
        final Rol rol = new Rol();
        mapToEntity(rolDTO, rol);
        return rolRepository.save(rol).getId();
    }

    public void update(final Integer id, final RolDTO rolDTO) {
        final Rol rol = rolRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(rolDTO, rol);
        rolRepository.save(rol);
    }

    public void delete(final Integer id) {
        final Rol rol = rolRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        gebruikerRepository.findAllByRol(rol)
                .forEach(gebruiker -> gebruiker.getRol().remove(rol));
        organisatieRepository.findAllByRol(rol)
                .forEach(organisatie -> organisatie.getRol().remove(rol));
        rolRepository.delete(rol);
    }

    public RolDTO mapToDTO(final Rol rol, final RolDTO rolDTO) {
        rolDTO.setId(rol.getId());
        rolDTO.setNaam(rol.getNaam());
        rolDTO.setSubBudgetPost(rol.getSubBudgetPost().stream()
                .map(subBudgetPost -> subBudgetPost.getId())
                .toList());
        return rolDTO;
    }

    public Rol mapToEntity(final RolDTO rolDTO, final Rol rol) {
        rol.setNaam(rolDTO.getNaam());
        final List<SubBudgetPost> subBudgetPost = subBudgetPostRepository.findAllById(
                rolDTO.getSubBudgetPost() == null ? Collections.emptyList() : rolDTO.getSubBudgetPost());
        if (subBudgetPost.size() != (rolDTO.getSubBudgetPost() == null ? 0 : rolDTO.getSubBudgetPost().size())) {
            throw new NotFoundException("one of subBudgetPost not found");
        }
        rol.setSubBudgetPost(new HashSet<>(subBudgetPost));
        return rol;
    }

}
