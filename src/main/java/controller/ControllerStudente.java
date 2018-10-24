package controller;

import business.*;
import business.dao.data.DataLayerException;
import business.dao.intf.AziendaDAO;
import business.dao.intf.RichiestaDAO;
import business.dao.intf.StudenteDAO;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;


public class ControllerStudente {

    @Inject
    AziendaDAO aziendaDAO;

    @Inject
    StudenteDAO studenteDAO;

    @Inject
    RichiestaDAO richiestaDAO;


    /**
     * Restituisce info sullo studente solo se è un tirocinante dell'azienda, altrimenti null
     *
     * @param studente
     * @return studente
     */
    public Studente getStudenteByAzienda(Utente utente, Studente studente) throws DataLayerException {

        //storico tirocinanti azienda
        List<Studente> listaTirocinanti = aziendaDAO.getAllTirocinanti(utente);

        for(Studente tirocinante : listaTirocinanti) {
            if (tirocinante.getId() == studente.getId()) {
                return tirocinante;
            }
        }
        return null;
    }

    /**
     * Controlla che l'utente sia autorizzato e restituisce info sullo studente richiesto. Altrimenti restituisce null
     *
     * @param utente
     * @param studente
     * @return studente
     * @throws DataLayerException
     */
    public Studente getStudenteByStudente(Utente utente, Studente studente) throws DataLayerException{

        //Lo studente può vedere solo il prorpio profilo
        if(utente.getId() == studente.getId()) {

            return studenteDAO.getStudente(studente.getId());
        }

        return null;

    }

    /**
     * Restituisce info su qualsiasi studente richiesto. Se la richiesta non proviene da un Admin restituisce null
     *
     * @param utente
     * @param studente
     * @return studente
     * @throws DataLayerException
     */
    public Studente getStudenteByAdmin(Utente utente, Studente studente) throws DataLayerException{
        if(utente.getTipo().equals("AM")) {
            return studenteDAO.getStudente(studente.getId());
        }
        return null;
    }

    /**
     * Crea un nuovo utente nel db
     * @param studente
     * @return utente
     * @throws DataLayerException
     */
    public Utente insertStudente(Studente studente) throws DataLayerException{
        return studenteDAO.setRegistrazioneStudente(studente);
    }

    /**
     * Salva la candidatura dell'utente nel db. Restituisce:
     *  1 -> OK
     *  1062 -> Candidatura precedentemente inviata
     *  altro -> Errore
     *
     * @param idUtente
     * @param idAnnuncio

     * @return valore numerico
     * @throws DataLayerException
     */
    public int sendRichiesta(long idUtente, long idAnnuncio,Richiesta richiesta) throws DataLayerException{
        richiesta.setStudente(new Studente(idUtente));
        richiesta.setAnnuncio(new Annuncio(idAnnuncio));


        return  richiestaDAO.saveRichiesta(richiesta);

    }
}
