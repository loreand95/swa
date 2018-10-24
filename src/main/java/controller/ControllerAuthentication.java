package controller;

import business.Azienda;
import business.dao.data.DataLayerException;
import business.dao.impl.UtenteDAOImpl;
import business.dao.intf.UtenteDAO;
import business.Utente;

import javax.inject.Inject;
import javax.ws.rs.core.NewCookie;
import java.util.UUID;
import java.util.logging.Logger;

public class ControllerAuthentication {

    @Inject
    UtenteDAO utenteDAO;

    /**
     * check credenziali sul database,
     * se le credenziali sono corrette restituisce il token dell'utente
     * altrimenti restiuisce null
     *
     * @param utente
     * @return token utente or null
     * @throws DataLayerException
     */
    public String getToken(Utente utente) throws DataLayerException {
        String token;

        //check credenziali
        utente = utenteDAO.getCredenziali(utente);

        if (utente != null) { //credenziali esatte

            //TODO add expire and create time

            if (utente.getToken()==null || utente.getToken().isEmpty()) {//utente senza cookie
                //il cookie viene generato
                token = UUID.randomUUID().toString();
                //store on db
                utente.setToken(token);
                utenteDAO.setToken(utente);
            }
            return utente.getToken();
        }

        return null; //credenziali errate
    }

    public Utente getUtente(String authcookie) throws DataLayerException {

        if(!authcookie.isEmpty()){
                return  utenteDAO.getUtente(authcookie);
        }
        throw  new DataLayerException("Cookie is empty");
    }

}
