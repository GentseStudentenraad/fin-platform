package be.ugent.gsr.financien.service;

import be.ugent.gsr.financien.domain.Bankgegevens;
import be.ugent.gsr.financien.domain.Gebruiker;
import be.ugent.gsr.financien.domain.Nota;
import be.ugent.gsr.financien.domain.Organisatie;
import be.ugent.gsr.financien.domain.SubBudgetPost;
import be.ugent.gsr.financien.model.NotaDTO;
import be.ugent.gsr.financien.model.NotaStatus;
import be.ugent.gsr.financien.model.NotaType;
import be.ugent.gsr.financien.repos.BankgegevensRepository;
import be.ugent.gsr.financien.repos.DocumentRepository;
import be.ugent.gsr.financien.repos.GebruikerRepository;
import be.ugent.gsr.financien.repos.KostRepository;
import be.ugent.gsr.financien.repos.NotaRepository;
import be.ugent.gsr.financien.repos.OrganisatieRepository;
import be.ugent.gsr.financien.repos.SubBudgetPostRepository;
import be.ugent.gsr.financien.util.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class NotaService {

    private final NotaRepository notaRepository;
    private final SubBudgetPostRepository subBudgetPostRepository;
    private final GebruikerRepository gebruikerRepository;
    private final PDFService pdfService;
    private final MailService mailService;
    private final OrganisatieRepository organisatieRepository;
    private final BankgegevensRepository bankgegevensRepository;
    private final KostRepository kostRepository;
    private final DocumentRepository documentRepository;

    @Value("${mail.dsv.mailadress}")
    private String dsvMailadress;

    public NotaService(final NotaRepository notaRepository,
                       final SubBudgetPostRepository subBudgetPostRepository,
                       final GebruikerRepository gebruikerRepository,
                       final PDFService pdfService,
                       final MailService mailService,
                       final OrganisatieRepository organisatieRepository,
                       final BankgegevensRepository bankgegevensRepository,
                       final KostRepository kostRepository,
                       final DocumentRepository documentRepository

    ) {
        this.notaRepository = notaRepository;
        this.subBudgetPostRepository = subBudgetPostRepository;
        this.gebruikerRepository = gebruikerRepository;
        this.pdfService = pdfService;
        this.mailService = mailService;
        this.organisatieRepository = organisatieRepository;
        this.bankgegevensRepository = bankgegevensRepository;
        this.kostRepository = kostRepository;
        this.documentRepository = documentRepository;
    }

    public Page<NotaDTO> findAll(Pageable pageable, Gebruiker gebruiker, Integer jaartal) {
        List<SubBudgetPost> visibleSubBudgetPosts = gebruiker.visibleBudgetPosts(subBudgetPostRepository, jaartal);
        // All the subBudgetPosts die de gebruiker kan zien
        final Page<Nota> kosts = notaRepository.findNotasBySubBudgetPostIn(visibleSubBudgetPosts, pageable);
        return kosts.map(nota -> mapToDTO(nota, new NotaDTO()));
    }

    public NotaDTO get(final Integer id) {
        return notaRepository.findById(id)
                .map(nota -> mapToDTO(nota, new NotaDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public ResponseEntity<Integer> create(final NotaDTO notaDTO, Gebruiker gebruiker, Integer boekjaar) {
        if (gebruiker.visibleBudgetPosts(subBudgetPostRepository, boekjaar).stream().map(SubBudgetPost::getId).toList().contains(notaDTO.getSubBudgetPost())) {
            final Nota nota = new Nota();
            mapToEntity(notaDTO, nota);
            Integer newId = notaRepository.save(nota).getId();
            return new ResponseEntity<>(newId, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(-1, HttpStatus.FORBIDDEN);
        }
    }

    public ResponseEntity<Integer> update(final Integer id, final NotaDTO notaDTO, Gebruiker gebruiker) {
        final Nota nota = notaRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        if (gebruiker.visibleBudgetPosts(subBudgetPostRepository, nota.getSubBudgetPost().getBudgetPost().getBoekjaar().getJaartal()).stream().map(SubBudgetPost::getId).toList().contains(notaDTO.getSubBudgetPost())) {
            if (notaDTO.getStatus() == NotaStatus.ONDERTEKEND) {
                return new ResponseEntity<>(id, HttpStatus.FORBIDDEN);
            }
            mapToEntity(notaDTO, nota);
            notaRepository.save(nota);
            return ResponseEntity.ok(id);
        } else {
            return new ResponseEntity<>(-1, HttpStatus.FORBIDDEN);
        }
    }

    public void delete(final Integer id) {
        notaRepository.deleteById(id);
    }

    private NotaDTO mapToDTO(final Nota nota, final NotaDTO notaDTO) {
        notaDTO.setId(nota.getId());
        notaDTO.setUitleg(nota.getUitleg());
        notaDTO.setType(nota.getType());
        notaDTO.setIngediendDatum(nota.getIngediendDatum());
        notaDTO.setProcessedDatum(nota.getProcessedDatum());
        notaDTO.setRemarks(nota.getRemarks());
        notaDTO.setStatus(nota.getStatus());
        notaDTO.setSubBudgetPost(nota.getSubBudgetPost() == null ? null : nota.getSubBudgetPost().getId());
        notaDTO.setGebruiker(nota.getGebruiker() == null ? null : nota.getGebruiker().getId());
        return notaDTO;
    }

    private Nota mapToEntity(final NotaDTO notaDTO, final Nota nota) {
        if (notaDTO.getSubBudgetPost() != null) {
            SubBudgetPost subBudgetPost = subBudgetPostRepository.findById(notaDTO.getSubBudgetPost())
                    .orElseThrow(() -> new EntityNotFoundException("SubBudgetPost not found"));
            nota.setSubBudgetPost(subBudgetPost);
        }

        if (notaDTO.getGebruiker() != null) {
            Gebruiker gebruiker = gebruikerRepository.findById(notaDTO.getGebruiker())
                    .orElseThrow(() -> new EntityNotFoundException("Gebruiker not found"));
            nota.setGebruiker(gebruiker);
        }

        if (notaDTO.getOrganisatie() != null) {
            Organisatie organisatie = organisatieRepository.findById(notaDTO.getOrganisatie())
                    .orElseThrow(() -> new EntityNotFoundException("Organisatie not found"));
            nota.setOrganisatie(organisatie);
        }

        if (notaDTO.getBankgegevens() != null) {
            Bankgegevens bankgegevens = bankgegevensRepository.findById(notaDTO.getBankgegevens())
                    .orElseThrow(() -> new EntityNotFoundException("Bankgegevens not found"));
            nota.setBankgegevens(bankgegevens);
        }

        if (notaDTO.getKosten() != null) {
            nota.setKosten(notaDTO.getKosten().stream().map(kostId -> kostRepository.findById(kostId)
                    .orElseThrow(() -> new EntityNotFoundException("Kost not found"))).collect(Collectors.toList()));
        }

        if (notaDTO.getBewijzen() != null) {
            nota.setBewijzen(notaDTO.getBewijzen().stream().map(bewijsId -> documentRepository.findById(bewijsId)
                    .orElseThrow(() -> new EntityNotFoundException("Document not found"))).collect(Collectors.toList()));
        }
        notaRepository.save(nota);
        return nota;
    }

    public ResponseEntity<Integer> onderteken(Integer id, NotaDTO notaDTO) {
        final Nota nota = notaRepository.findById(id).orElseThrow(NotFoundException::new);
        if (nota.getStatus() != NotaStatus.GOEDGEKEURD) {
            return new ResponseEntity<>(id, HttpStatus.FORBIDDEN);
        } else if (notaDTO.getStatus() != NotaStatus.ONDERTEKEND) {
            return new ResponseEntity<>(id, HttpStatus.BAD_REQUEST);
        }
        nota.setStatus(NotaStatus.ONDERTEKEND);
        nota.getSubBudgetPost().setBudget(nota.getSubBudgetPost().getBudget().subtract(nota.totaalBedrag()));
        subBudgetPostRepository.save(nota.getSubBudgetPost());
        notaRepository.save(nota);

        //Als nota van goedgekeurd naar Ondertekend gaat, mail sturen naar DSV met alle gegevens.
        if (nota.getType() == NotaType.ONKOST) {
            mailService.sendMailWithAttachment(
                    dsvMailadress,
                    "Onkost GSR Budgetpost " + nota.getSubBudgetPost().getBudgetPost().getDsvCode(), // Todo aanpassn
                    "Hierbij een onkost van de GSR op Budgetpost " + nota.getSubBudgetPost().getBudgetPost().getDsvCode(),
                    pdfService.fillOnkostNota(nota)
            );
        } else if (nota.getType() == NotaType.FACTUUR) { // Factuur
            mailService.sendMailWithAttachment(
                    dsvMailadress,
                    "Onkost GSR Budgetpost " + nota.getSubBudgetPost().getBudgetPost().getDsvCode(), // Todo aanpassn
                    "Hierbij een factuur van de GSR op Budgetpost " + nota.getSubBudgetPost().getBudgetPost().getDsvCode(),
                    pdfService.makeFactuurPDf(nota)
            );
        } else { // Platform only
            mailService.sendMail(
                    null,
                    "Platform only onkost " + nota.getSubBudgetPost().getBudgetPost().getDsvCode(), // Todo aanpassen
                    "Hierbij een copy van een platform only onkost op de budgetpost: " +
                            nota.getSubBudgetPost().getBudgetPost().getDsvCode() +
                            " en Subbudgetpost: " +
                            nota.getSubBudgetPost().getNaam()
            );
        }
        return ResponseEntity.ok(id);
    }

    public ResponseEntity<Integer> goedkeuren(Integer id) {
        return afhandelen(id, NotaStatus.GOEDGEKEURD);
    }


    public ResponseEntity<Integer> afkeuren(Integer id) {
        return afhandelen(id, NotaStatus.AFGEKEURD);

    }

    private ResponseEntity<Integer> afhandelen(Integer id, NotaStatus status) {
        final Nota nota = notaRepository.findById(id).orElseThrow(NotFoundException::new);
        if (nota.getStatus() != NotaStatus.INGEDIEND) {
            return new ResponseEntity<>(id, HttpStatus.BAD_REQUEST);
        }
        nota.setStatus(status);
        notaRepository.save(nota);
        return ResponseEntity.ok(id);
    }
}
