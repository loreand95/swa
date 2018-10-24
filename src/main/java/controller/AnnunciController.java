package controller;

import business.Annuncio;
import business.dao.data.DataLayerException;
import business.dao.impl.AnnuncioDAOImpl;
import business.dao.intf.AnnuncioDAO;

public class AnnunciController {

    private AnnuncioDAO annuncioDAO;

    public AnnunciController(){
        this.annuncioDAO= new AnnuncioDAOImpl();
    }

    public int countAnnunci() throws DataLayerException {
        return  annuncioDAO.countAnnunci();
    }
}
