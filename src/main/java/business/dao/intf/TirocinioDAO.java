/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.dao.intf;

import business.Richiesta;
import business.dao.data.DataLayerException;
import business.Annuncio;
import business.Resoconto;
import business.Tirocinio;
import java.io.InputStream;
import java.util.List;

/**
 *
 * @author lorenzo
 */
public interface TirocinioDAO {

    public List<Tirocinio> getTirocini(long idTirocinante) throws DataLayerException;

    public int setValutazione(int valutazione, long idResoconto) throws DataLayerException;

    public InputStream downloadResoconto(Resoconto resoconto) throws DataLayerException;

    public int uploadResoconto(Resoconto resoconto) throws DataLayerException;

    public int setNuovoTirocinio(Richiesta richiesta) throws DataLayerException;

    public InputStream downloadProgettoBase() throws DataLayerException;

    public Tirocinio getTirocinio(Tirocinio tirocinio) throws DataLayerException;

}
