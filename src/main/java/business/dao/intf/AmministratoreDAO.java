/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.dao.intf;

import business.dao.data.DataLayerException;
import business.Azienda;
import business.Convenzione;
import java.util.List;

/**
 *
 * @author lorenzo
 */
public interface AmministratoreDAO {
    public List<Azienda> getListaAziende(String tipologia, int page) throws DataLayerException;
    public int setConvenzione(Convenzione convenzione, Azienda azienda) throws DataLayerException;
}
