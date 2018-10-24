package controller;

import business.Annuncio;
import business.Studente;
import business.Tirocinio;
import business.Utente;
import business.dao.data.DataLayerException;
import business.dao.intf.TirocinioDAO;
import javax.inject.Inject;
import java.io.*;
import java.util.Set;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import util.iTextPDF;


public class ControllerTirocinio {


    @Inject
    TirocinioDAO tirocinioDAO;

    @Inject
    iTextPDF iTextPDF;

    public ByteArrayOutputStream downloadProgetto(Utente utente, long idAnnuncio, long idStudente) throws DataLayerException {

        Studente studente = new Studente(idStudente);
        Annuncio annuncio = new Annuncio(idAnnuncio);
        Tirocinio tirocinio = new Tirocinio(studente,annuncio);

        tirocinio = tirocinioDAO.getTirocinio(tirocinio);



        //check azienda e utente

        //edit pdf
        return  iTextPDF.createPDF(tirocinio);

    }
}
