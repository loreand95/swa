/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.dao.intf;

import business.dao.data.DataLayerException;
import business.Utente;

/**
 *
 * @author lorenzo
 */
public interface UtenteDAO {

    public Utente deleteUtente(Utente utente) throws DataLayerException;

    public Utente getCredenziali(Utente utente) throws DataLayerException;

    public Utente getUtente(String token) throws DataLayerException;

    public Utente nuovoUtente(Utente utente) throws DataLayerException;

    public boolean getEmailEsistente(String email) throws DataLayerException;

    public boolean getUsernameEsistente(String username) throws DataLayerException;

    public int setToken(Utente utente) throws DataLayerException;
}
