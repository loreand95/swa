package util;

import business.Annuncio;
import business.Studente;
import business.Tirocinio;
import business.dao.data.DataLayerException;
import business.dao.intf.TirocinioDAO;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.IOException;
import java.util.Set;


import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Set;

public class iTextPDF {

    public iTextPDF(){}

    @Inject
    TirocinioDAO tirocinioDAO;

    /**
     * Crea
     * @param tirocinio
     * @return
     * @throws DataLayerException
     */
    public ByteArrayOutputStream createPDF(Tirocinio tirocinio) throws DataLayerException {

        InputStream is = tirocinioDAO.downloadProgettoBase();
        System.out.println("IS:"+is);

        try {
            PdfReader reader = new PdfReader(is, null);
            // We create an OutputStream for the new PDF
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // Now we create the PDF
            PdfStamper stamper = new PdfStamper(reader, baos);
            // We alter the fields of the existing PDF

            AcroFields fields = stamper.getAcroFields();
            fields.setGenerateAppearances(true);

            Set<String> parameters = fields.getFields().keySet();

            // Fill field
            fields.setField("nomeTirocinante", tirocinio.getStudente().getNome());
            //fields.setField("nato", tirocinio.getStudente().getCittaNascita());
            fields.setField("residenza", tirocinio.getStudente().getCittaResidenza());
            fields.setField("telefono", tirocinio.getStudente().getTelefono());


            stamper.setFormFlattening(true);
            stamper.close();
            reader.close();


            return baos;

        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }

    return null;
    }
}