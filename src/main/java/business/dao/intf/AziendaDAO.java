/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.dao.intf;

import business.*;
import business.dao.data.DataLayerException;

import java.io.InputStream;
import java.util.List;

/**
 *
 * @author lorenzo
 */
public interface AziendaDAO {

    public List<Studente> getAllTirocinanti(Utente utente) throws DataLayerException;

    public int updateAzienda(Azienda azienda) throws DataLayerException;

    public int updateActivation(Azienda azienda) throws DataLayerException;
    
    public Azienda getAzienda(Azienda azienda) throws DataLayerException;

    public List<Azienda> getAziende() throws DataLayerException;
    
    public int removeAzienda(Azienda azienda) throws DataLayerException;
    
    public List<Azienda> getAllAziendeConvenzionate() throws DataLayerException;

    public List<Richiesta> getRichieste(long id) throws DataLayerException;
    
    public List<Tirocinio> getTirocini(long id) throws DataLayerException;

    public InputStream getConvenzione(Azienda azienda) throws DataLayerException;

    public boolean isConvenzionata(long id) throws DataLayerException;

    public String getStato(long id) throws DataLayerException;

    public Azienda getApprovazione(long id) throws DataLayerException;

    public Resoconto setConcludiTirocinio(Tirocinio tirocinio) throws DataLayerException;

    public long setRegistrazioneAzienda(Azienda azienda) throws DataLayerException;

    public int updateStato(Azienda azienda, String stato) throws DataLayerException;

    public void removeTirocinio(Tirocinio tirocinio) throws DataLayerException;

    public int setNuovoTirocinio(Tirocinio tirocinio) throws DataLayerException;
    
    public InputStream getModuloConvenzione() throws DataLayerException; 

}
