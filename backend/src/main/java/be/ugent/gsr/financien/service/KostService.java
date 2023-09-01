package be.ugent.gsr.financien.service;

import be.ugent.gsr.financien.domain.Gebruiker;
import be.ugent.gsr.financien.domain.Kost;
import be.ugent.gsr.financien.domain.SubBudgetPost;
import be.ugent.gsr.financien.model.KostDTO;
import be.ugent.gsr.financien.model.KostStatus;
import be.ugent.gsr.financien.model.KostType;
import be.ugent.gsr.financien.repos.GebruikerRepository;
import be.ugent.gsr.financien.repos.KostRepository;
import be.ugent.gsr.financien.repos.SubBudgetPostRepository;
import be.ugent.gsr.financien.util.NotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class KostService {

    private final KostRepository kostRepository;
    private final SubBudgetPostRepository subBudgetPostRepository;
    private final GebruikerRepository gebruikerRepository;
    private final PDFService pdfService;
    private final MailService mailService;

    @Value("${mail.dsv.mailadress}")
    private String dsvMailadress;

    public KostService(final KostRepository kostRepository,
                       final SubBudgetPostRepository subBudgetPostRepository,
                       final GebruikerRepository gebruikerRepository,
                       final PDFService pdfService,
                       final MailService mailService
    ) {
        this.kostRepository = kostRepository;
        this.subBudgetPostRepository = subBudgetPostRepository;
        this.gebruikerRepository = gebruikerRepository;
        this.pdfService = pdfService;
        this.mailService = mailService;
    }

    public List<KostDTO> findAll(Gebruiker gebruiker, Integer jaartal) {
        List<SubBudgetPost> visibleSubBudgetPosts = gebruiker.visibleBudgetPosts(subBudgetPostRepository, jaartal);
        // All the subBudgetPosts die de gebruiker kan zien
        final List<Kost> kosts = kostRepository.findAll().stream().filter(kost -> visibleSubBudgetPosts.contains(kost.getSubBudgetPost())).toList();
        return kosts.stream()
                .map(kost -> mapToDTO(kost, new KostDTO()))
                .toList();

    }

    public KostDTO get(final Integer id) {
        return kostRepository.findById(id)
                .map(kost -> mapToDTO(kost, new KostDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public ResponseEntity<Integer> create(final KostDTO kostDTO, Gebruiker gebruiker, Integer boekjaar) {
        if (gebruiker.visibleBudgetPosts(subBudgetPostRepository, boekjaar).stream().map(SubBudgetPost::getId).toList().contains(kostDTO.getSubBudgetPost())) {
            final Kost kost = new Kost();
            mapToEntity(kostDTO, kost);
            Integer newId = kostRepository.save(kost).getId();
            return new ResponseEntity<>(newId, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(-1, HttpStatus.FORBIDDEN);
        }
    }

    public ResponseEntity<Integer> update(final Integer id, final KostDTO kostDTO, Gebruiker gebruiker) {
        final Kost kost = kostRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        if (gebruiker.visibleBudgetPosts(subBudgetPostRepository, kost.getSubBudgetPost().getBudgetPost().getBoekjaar().getJaartal()).stream().map(SubBudgetPost::getId).toList().contains(kostDTO.getSubBudgetPost())) {
            if (kostDTO.getStatus() == KostStatus.ONDERTEKEND) {
                return new ResponseEntity<>(id, HttpStatus.FORBIDDEN);
            }
            mapToEntity(kostDTO, kost);
            kostRepository.save(kost);
            return ResponseEntity.ok(id);
        } else {
            return new ResponseEntity<>(-1, HttpStatus.FORBIDDEN);
        }
    }

    public void delete(final Integer id) {
        kostRepository.deleteById(id);
    }

    private KostDTO mapToDTO(final Kost kost, final KostDTO kostDTO) {
        kostDTO.setId(kost.getId());
        kostDTO.setHoeveelheid(kost.getHoeveelheid());
        kostDTO.setUitleg(kost.getUitleg());
        kostDTO.setType(kost.getType());
        kostDTO.setCostDatum(kost.getCostDatum());
        kostDTO.setIngediendDatum(kost.getIngediendDatum());
        kostDTO.setProcessedDatum(kost.getProcessedDatum());
        kostDTO.setRecipient(kost.getRecipient());
        kostDTO.setRemarks(kost.getRemarks());
        kostDTO.setStatus(kost.getStatus());
        kostDTO.setSubBudgetPost(kost.getSubBudgetPost() == null ? null : kost.getSubBudgetPost().getId());
        kostDTO.setUser(kost.getGebruiker() == null ? null : kost.getGebruiker().getId());
        return kostDTO;
    }

    private Kost mapToEntity(final KostDTO kostDTO, final Kost kost) {
        kost.setHoeveelheid(kostDTO.getHoeveelheid());
        kost.setUitleg(kostDTO.getUitleg());
        kost.setType(kostDTO.getType());
        kost.setCostDatum(kostDTO.getCostDatum());
        kost.setIngediendDatum(kostDTO.getIngediendDatum());
        kost.setProcessedDatum(kostDTO.getProcessedDatum());
        kost.setRecipient(kostDTO.getRecipient());
        kost.setRemarks(kostDTO.getRemarks());
        kost.setStatus(kostDTO.getStatus());
        final SubBudgetPost subBudgetPost = kostDTO.getSubBudgetPost() == null ? null : subBudgetPostRepository.findById(kostDTO.getSubBudgetPost())
                .orElseThrow(() -> new NotFoundException("subBudgetPost not found"));
        kost.setSubBudgetPost(subBudgetPost);
        final Gebruiker user = kostDTO.getUser() == null ? null : gebruikerRepository.findById(kostDTO.getUser())
                .orElseThrow(() -> new NotFoundException("user not found"));
        kost.setGebruiker(user);
        return kost;
    }

    public ResponseEntity<Integer> onderteken(Integer id, KostDTO kostDTO) {
        final Kost kost = kostRepository.findById(id).orElseThrow(NotFoundException::new);
        if (kost.getStatus() != KostStatus.GOEDGEKEURD) {
            return new ResponseEntity<>(id, HttpStatus.FORBIDDEN);
        } else if (kostDTO.getStatus() != KostStatus.ONDERTEKEND) {
            return new ResponseEntity<>(id, HttpStatus.BAD_REQUEST);
        }
        mapToEntity(kostDTO, kost);
        kostRepository.save(kost);

        //Als Kost van goedgekeurd naar Ondertekend gaat, mail sturen naar DSV met alle gegevens.
        if (kost.getType() == KostType.ONKOST) {
            mailService.sendMailWithAttachment(
                    dsvMailadress,
                    "Onkost GSR Budgetpost " + kost.getSubBudgetPost().getBudgetPost().getDsvCode(), // Todo misschien aanpassn
                    "Hierbij een onkost van de GSR op Budgetpost " + kost.getSubBudgetPost().getBudgetPost().getDsvCode(),
                    pdfService.fillOnkostNota(kost)
            );
        } else { // Factuur
            mailService.sendMailWithAttachment(
                    dsvMailadress,
                    "Onkost GSR Budgetpost " + kost.getSubBudgetPost().getBudgetPost().getDsvCode(), // Todo misschien aanpassn
                    "Hierbij een factuur van de GSR op Budgetpost " + kost.getSubBudgetPost().getBudgetPost().getDsvCode(),
                    pdfService.makeFactuurPDf(kost)
            );
        }
        return ResponseEntity.ok(id);
    }

    public ResponseEntity<Integer> afhandelen(Integer id, KostDTO kostDTO) {
        final Kost kost = kostRepository.findById(id).orElseThrow(NotFoundException::new);
        if (kost.getStatus() != KostStatus.INGEDIEND) {
            return new ResponseEntity<>(id, HttpStatus.BAD_REQUEST);
        }
        mapToEntity(kostDTO, kost);
        kostRepository.save(kost);
        return ResponseEntity.ok(id);
    }
}
