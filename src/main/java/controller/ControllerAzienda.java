package controller;

import business.*;
import business.dao.data.DataLayerException;
import business.dao.intf.AnnuncioDAO;
import business.dao.intf.AziendaDAO;
import business.dao.intf.RichiestaDAO;
import business.dao.intf.TirocinioDAO;
import presentation.RESTaziende;
import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ControllerAzienda {

    @Inject
    AziendaDAO aziendaDAO;

    @Inject
    AnnuncioDAO annuncioDAO;

    @Inject
    RichiestaDAO richiestaDAO;

    @Inject
    TirocinioDAO tirocinioDAO;


    /**
     * Restituisce tutte le aziende nel formato: [{nome:"azienda srl", url:"http://internships.it/aziende/1"}]
     * @param context
     * @return lista aziende
     * @throws DataLayerException
     */
    public List<HashMap<String, String>> getAllAziende(UriInfo context) throws DataLayerException{

        List<Azienda> listaAziende = aziendaDAO.getAziende();
        List<HashMap<String, String>> listaJSON = new LinkedList<>();

        for (Azienda azienda : listaAziende) {
            HashMap<String, String> hashMap = new HashMap<>();

            // Get nome azienda
            hashMap.put("nome", azienda.getRagioneSociale());

            // URI Azienda
            URI u = context.getBaseUriBuilder()
                    .path(RESTaziende.class)
                    .path("/" + azienda.getId())
                    .build();
            hashMap.put("url", u.toString());

            // Inserimento nella lista
            listaJSON.add(hashMap);
        }

        return listaJSON;
    }

    /**
     * Abilitazione azienda
     *
     * @param azienda
     * @throws DataLayerException
     */
    public void updateActivation(Azienda azienda) throws DataLayerException {
        int result = aziendaDAO.updateActivation(azienda);
        if (result != 0) {
            throw new DataLayerException("ERROR:" + result);
        }
    }

    /**
     * Aggiornamento valori azienda
     *
     * @param azienda
     * @throws DataLayerException
     */
    public void updateAzienda(Azienda azienda) throws DataLayerException {
        int result = aziendaDAO.updateAzienda(azienda);
        if (result != 0) {
            throw new DataLayerException("ERROR:" + result);
        }
    }

    /**
     * Registra nuova azienda
     * @param azienda
     * @return id nuova azienda
     * @throws DataLayerException
     */
    public long registrazioneAzienda(Azienda azienda) throws DataLayerException{
        return  aziendaDAO.setRegistrazioneAzienda(azienda);
    }

    public Azienda getAzienda(long idAzienda) throws DataLayerException{
       return aziendaDAO.getAzienda(new Azienda(idAzienda));
    }

    /**
     * Restituisce gli annunci di un azienda
     * @param idAzienda
     * @return lista annunci
     * @throws DataLayerException
     */
    public List<Annuncio> getAnnunci(long idAzienda) throws DataLayerException {
        return annuncioDAO.getAnnunci(new Utente(idAzienda));
    }

    /**
     * Approva candidatura studente, 1 OK, 403 FORBIDDEN
     *
     * @param utente
     * @param idCandidato
     * @param idAnnucio
     * @return int
     * @throws DataLayerException
     */
    public int approvaCandidatura(Utente utente, long idCandidato, long idAnnucio) throws DataLayerException{

        Studente studente = new Studente(idCandidato);
        Annuncio annuncio = new Annuncio(idAnnucio);
        Richiesta richiesta = new Richiesta(annuncio,studente);

        //check annuncio appartenente all'azienda
        boolean publishedByCompany = annuncioDAO.publishedByCompany(utente,annuncio);

        if(publishedByCompany){

            //get richiesta from db
            richiesta = richiestaDAO.getRichiestaStudente(richiesta);


            if(richiesta!=null){//richiesta esistente

                if(tirocinioDAO.setNuovoTirocinio(richiesta)==1){
                    return  richiestaDAO.deleteRichiesta(richiesta);
                }else {
                    throw new DataLayerException("NO INSERT TIROCINIO");
                }

            }//else 403
        }

        return 403;
    }

    /**
     * Rifiuta candidatura studente
     *
     * @param utente
     * @param idCandidato
     * @param idAnnucio
     * @return int
     * @throws DataLayerException
     */
    public int rifiutaCandidatura(Utente utente, long idCandidato, long idAnnucio) throws DataLayerException{
        //check annuncio appartenente all'azienda
        boolean publishedByCompany = annuncioDAO.publishedByCompany(utente,new Annuncio(idAnnucio));

        if(publishedByCompany){
            Richiesta richiesta = new Richiesta(new Annuncio(idAnnucio),new Studente(idCandidato));
            return  richiestaDAO.deleteRichiesta(richiesta);
        }

        return 403;
    }

    /**
     * Dato un utente azienda ed un annuncio ritorna vero se l'annuncio appartiene all'azienda
     *
     * @param utente
     * @param idAnnucio
     * @return boolean
     * @throws DataLayerException
     */
    public boolean publishedByCompany(Utente utente,long idAnnucio) throws DataLayerException{
        return annuncioDAO.publishedByCompany(utente,new Annuncio(idAnnucio));
    }

}
