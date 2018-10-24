package controller;

import business.Annuncio;
import business.Utente;
import business.dao.data.DataLayerException;
import business.dao.intf.AnnuncioDAO;

import javax.inject.Inject;
import javax.swing.text.html.HTMLDocument;
import java.util.Iterator;
import java.util.List;

public class ControllerOfferta {

    @Inject
    AnnuncioDAO annuncioDAO;

    /**
     * Restituisce un annuncio dal ID
     * @param idAnnuncio
     * @return annuncio
     * @throws DataLayerException
     */
    public Annuncio getAnnuncioById(long idAnnuncio) throws DataLayerException {
        return annuncioDAO.getAnnuncioById(idAnnuncio);
    }

    /**
     * Restituisce tutti gli annunci dell'azienda
     * @param utente
     * @return
     * @throws DataLayerException
     */
    public List<Annuncio> getAnnuncioOfAzienda(Utente utente) throws DataLayerException{
        return annuncioDAO.getAnnunci(utente);
    }

    /**
     *Restituisce la lista di annunci, eventualmente filtrata per citta, i-esimo elemento inziale e finale
     * @return List<Annuncio>
     * @throws DataLayerException
     */
    public List<Annuncio> getAllAnnunciFilter(String cityFilter, int n, int m) throws DataLayerException{

        List<Annuncio> listAnnunci;

        if(n !=0 ){
            if(m >= n){
                // restituisce gli annunci da n a m
                listAnnunci = annuncioDAO.getAnnunci(n,m);
            }else{
                // restituisce gli annunci da n fino all'ultimo
                listAnnunci = annuncioDAO.getAnnunci(n);
            }

        }else{
            //restituisce tutti gli annunci
            listAnnunci = annuncioDAO.getAnnunci();
        }


        //Filter list
        if(cityFilter!=null && !cityFilter.isEmpty()){
            int index;

            Iterator<Annuncio> iterator = listAnnunci.iterator();
            while (iterator.hasNext()){
                Annuncio annuncio = iterator.next();

                //rimozione annunci che non hanno la stessa citta
                if (!annuncio.getAzienda().getCitta().equalsIgnoreCase(cityFilter)){
                    iterator.remove();
                }
            }

        }
        return listAnnunci;
    }
}