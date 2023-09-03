package be.ugent.gsr.financien.service;

import be.ugent.gsr.financien.domain.Document;
import be.ugent.gsr.financien.domain.Kost;
import be.ugent.gsr.financien.domain.Organisatie;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("pdfService")
public class PDFService {

    Map<String, String> dsv_budgetpostMapping = new HashMap<>() {{
        put("DSV0402", "Participatie - DSV0402");
        put("DSV0407", "Onderwijsprojecten GSR - DSV0407");
        put("DSV0411", "Student KickOff - DSV0411");
        put("DSV04018", "Ondersteuning SoRa-OWR-IR - DSV04018");
        put("DSV04021", "Langlopende sponsorwerking - DSV04021");
        put("DSV04025", "Community-vorming - DSV04025");
        put("DSV0404", "Cultour - DSV0404");
        put("DSV0409", "Ondersteuning GSR - DSV0409");
        put("DSV04017", "Facultaire Ondersteuning - DSV04017");
        put("DSV04019", "Werking Therminal - DSV04019");
        put("DSV04023", "Inhoudelijke Projectwerking - DSV04023");
    }};

    @Value("${onkost.nota.pdf}")
    private String onkostNotaTemplate;

    public ByteArrayResource fillOnkostNota(Kost kost) {

        InputStream input = null;
        try {
            input = new FileInputStream(onkostNotaTemplate);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        //Load editable pdf file
        try (PDDocument pdfDoc = PDDocument.load(input)) {
            Organisatie organisatie = kost.getOrganisatie();

            PDDocumentCatalog docCatalog = pdfDoc.getDocumentCatalog();
            PDAcroForm acroForm = docCatalog.getAcroForm();

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PDField naamFilled = acroForm.getField("Naam_filled");
            naamFilled.setValue(organisatie.getNaam());

            PDField adresFilled = acroForm.getField("Adres_filled");
            adresFilled.setValue(organisatie.getAdres());

            PDField rekeningnummerFilled = acroForm.getField("Rekeningnummer_filled");
            rekeningnummerFilled.setValue(organisatie.getRekeningnummer());

            PDField bic = acroForm.getField("Text1.3.9");
            bic.setValue(organisatie.getBic());

            PDField totaleBedrag = acroForm.getField("Totale Bedrag");
            totaleBedrag.setValue(kost.getHoeveelheid().toString());

            PDField motivatieFilled = acroForm.getField("Motivatie_filled");
            motivatieFilled.setValue(kost.getUitleg());

            PDField datumFilled = acroForm.getField("Datum_filled");
            datumFilled.setValue(kost.getIngediendDatum().toString());

            PDField dsvPost = acroForm.getField("DSV post");
            dsvPost.setValue(dsv_budgetpostMapping.getOrDefault(kost.getSubBudgetPost().getBudgetPost().getDsvCode(), "extra1"));

            PDField orgNaam = acroForm.getField("Naam Studentenraad");
            orgNaam.setValue(organisatie.getNaam());

            PDField orgNaam2 = acroForm.getField("VolledigeNaam_filled");
            orgNaam2.setValue(organisatie.getNaam());

            PDField rekeningnummer = acroForm.getField("Rekeningnummer_filled");
            rekeningnummer.setValue(organisatie.getRekeningnummer());

            PDField adres = acroForm.getField("Adres_filled");
            adres.setValue(organisatie.getAdres());

            PDField tel_nr_verantwoordelijke = acroForm.getField("Telefoonnummer verantwoordelijke");
            tel_nr_verantwoordelijke.setValue(kost.getGebruiker().getTelNr());

            PDField datum_kost = acroForm.getField("Datum1");
            datum_kost.setValue(kost.getCostDatum().toString());

            PDField uitleg_kost = acroForm.getField("Voorwerp van terugbetaling 1");
            uitleg_kost.setValue(kost.getUitleg());

            PDField bedrag_kost = acroForm.getField("Bedrag1");
            bedrag_kost.setValue(kost.getHoeveelheid().toString());

            PDField bedrag_totaal = acroForm.getField("Totale Bedrag");
            bedrag_totaal.setValue(kost.getHoeveelheid().toString()); // Zelfde als bedrag 1 want we vullen 1 kost in per document.

            PDField naam_indiener = acroForm.getField("VolledigeNaam_filled");
            naam_indiener.setValue(kost.getGebruiker().getNaam());

            //TODO add handtekening voorzitter. Is dit nodig? want hoe doet DSA dat? "fieldname: 'Signature2.1'"
            //TODO add handtekening aanvrager. Is dit nodig? want hoe doet DSA dat? "fieldname: 'Signature1'"
            //TODO add handtekening aanvrager. Is dit nodig? want hoe doet DSA dat? "fieldname: 'Signature2.O'"

            PDField datum_ondertekend_voorzitter = acroForm.getField("Text53");
            datum_ondertekend_voorzitter.setValue(LocalDate.now().toString());

            /*make the final document uneditable*/
            acroForm.flatten();

            PDFMergerUtility merger = new PDFMergerUtility();
            merger.appendDocument(pdfDoc, pdfDoc);

            /* Voor elk document in de bewijzen voeg het achteraan toe aan de PDF */
            for (byte[] toAdd : kost.getBewijzen().stream().map(Document::getPdfData).toList()) {
                InputStream kostPDFStream = new ByteArrayInputStream(toAdd);
                merger.appendDocument(pdfDoc, PDDocument.load(kostPDFStream));
            }

            merger.mergeDocuments(null);
            pdfDoc.save(out);

            // How to use this service and the email service:

            return new ByteArrayResource(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ByteArrayResource makeFactuurPDf(Kost kost) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            List<PDDocument> pdfs = kost.getBewijzen().stream()
                    .map(Document::getPdfData)
                    .map(ByteArrayInputStream::new).map(input -> {
                        try {
                            return PDDocument.load(input);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).toList();

            PDFMergerUtility merger = new PDFMergerUtility();
            merger.appendDocument(pdfs.get(0), pdfs.get(0));

            /* Voor elk document in de bewijzen voeg het toe aan de PDF */
            for (int i = 1; i < pdfs.size(); i++) {
                merger.appendDocument(pdfs.get(0), pdfs.get(i));
            }

            merger.mergeDocuments(null);
            pdfs.get(0).save(out);

            // How to use this service and the email service:

            return new ByteArrayResource(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
