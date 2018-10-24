/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.dao.intf;

import business.dao.data.DataLayerException;
import business.Richiesta;
import business.Studente;

/**
 *
 * @author lorenzo
 */
public interface RichiestaDAO {
    
    public int saveRichiesta(Richiesta richiesta) throws DataLayerException;
    
    public int deleteRichiesta(Richiesta richiesta) throws DataLayerException;
    
    public Richiesta getRichiestaStudente(Richiesta richiesta) throws DataLayerException;
    
}
