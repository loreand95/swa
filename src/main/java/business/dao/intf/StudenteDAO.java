/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.dao.intf;

import business.Utente;
import business.dao.data.DataLayerException;
import business.Studente;

/**
 *
 * @author lorenzo
 */
public interface StudenteDAO {


    public Utente setRegistrazioneStudente(Studente studente) throws DataLayerException;

    public Studente getStudente(long id) throws DataLayerException;

}
