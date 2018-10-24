/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.dao.impl;

import business.*;
import business.dao.intf.AnnuncioDAO;
import business.dao.data.Database;
import business.dao.data.DataLayerException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;

/**
 *
 * @author lorenzo
 */
public class AnnuncioDAOImpl implements AnnuncioDAO {



    private static final String GET_ALL_ANNUNCI = "SELECT * FROM annuncio JOIN azienda ON annuncio.Azienda_idAzienda=azienda.idAzienda";

    private static final String GET_ANNUNCI_RANGE_TO_END = "SELECT * FROM annuncio JOIN azienda ON annuncio.Azienda_idAzienda=azienda.idAzienda LIMIT ?,18446744073709551615";

    private static final String GET_ANNUNCI_RANGE = "SELECT * FROM annuncio JOIN azienda ON annuncio.Azienda_idAzienda=azienda.idAzienda LIMIT ?,?";

    private static final String GET_ANNUNCI_OF_AZIENDA = "SELECT * FROM annuncio JOIN azienda ON annuncio.Azienda_idAzienda=azienda.idAzienda WHERE annuncio.Azienda_idAzienda=?";

    private static final String PUBLISHED_BY_AZIENDA = "SELECT * FROM annuncio JOIN azienda ON annuncio.Azienda_idAzienda=azienda.idAzienda WHERE annuncio.Azienda_idAzienda=? AND annuncio.idAnnuncio = ?";

    private static final String GET_ANNUNCIO_BY_ID = "SELECT * FROM annuncio JOIN azienda ON annuncio.Azienda_idAzienda=azienda.idAzienda  WHERE annuncio.idAnnuncio = ?";

    private static final String GET_ANNUNCI_AZIENDA_BY_STATO = "SELECT * FROM annuncio JOIN azienda ON annuncio.Azienda_idAzienda=azienda.idAzienda WHERE annuncio.Azienda_idAzienda=? AND annuncio.stato=? LIMIT ?,4";

    private static final String GET_ANNUNCI_STATO = "SELECT * FROM annuncio JOIN azienda ON annuncio.Azienda_idAzienda=azienda.idAzienda WHERE annuncio.stato=? LIMIT ?,4";

    private static final String SAVE_ANNUNCIO = "INSERT INTO annuncio (titolo, corpo, dataAvvio, dataTermine, modalita, sussidio, settore, Azienda_idAzienda,nomeDocente,cognomeDocente,emailDocente,nomeReferente,cognomeReferente,emailReferente,telefonoReferente,stato) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'ATTIVO')";

    private static final String UPDATE_STATO = "UPDATE Annuncio SET Stato=? WHERE idAnnuncio=?";
    
    private static final String GET_ANNUNCI_SEARCH = "SELECT *, MATCH(Corpo) AGAINST(?) AS Rate FROM Annuncio JOIN azienda ON annuncio.Azienda_idAzienda=azienda.idAzienda WHERE MATCH(Corpo) AGAINST(?)  AND Annuncio.Stato='ATTIVO' ORDER BY RATE DESC LIMIT ?,4";
    
    private static final String COUNT_ANNUNCI="SELECT COUNT(*) AS Number FROM Annuncio";
    
    private static final String COUNT_ANNUNCI_SEARCH="SELECT COUNT(*) AS Number FROM Annuncio WHERE MATCH(Corpo) AGAINST(?)  AND Annuncio.Stato='ATTIVO'";
    
    
    @Override
    public int countAnnunciSearch(String campoRicerca) throws DataLayerException{
    
        int result = 0;
        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(COUNT_ANNUNCI_SEARCH)) {
                ps.setString(1, campoRicerca);
                try (ResultSet rset = ps.executeQuery()) {

                    if (rset.next()) {
                        result =  rset.getInt("Number");
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataLayerException("COUNT ANNUNCIO SEARCH", ex);
        }
        return result;
    }
    
    @Override
    public int countAnnunci() throws DataLayerException{
    
        int result = 0;
        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(COUNT_ANNUNCI)) {
                try (ResultSet rset = ps.executeQuery()) {

                    if (rset.next()) {
                        result =  rset.getInt("Number");
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataLayerException("COUNT ANNUNCIO", ex);
        }
        return result;
    }
    
    @Override
    public Annuncio getAnnuncioById(long id) throws DataLayerException {

        Annuncio annuncio = null;

        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(GET_ANNUNCIO_BY_ID)) {
                ps.setLong(1, id);
                try (ResultSet rset = ps.executeQuery()) {

                    if (rset.next()) {
                        Referente referenteAnnuncio = new Referente(rset.getString("nomeReferente"), rset.getString("cognomeReferente"), rset.getString("telefonoReferente"));
                        Docente docenteAnnuncio = new Docente(rset.getString("nomeDocente"), rset.getString("cognomeDocente"), rset.getString("emailDocente"));
                        Azienda aziendaAnnuncio = new Azienda(
                                rset.getString("ragSociale"),
                                rset.getString("nomeResponsabile"),
                                rset.getString("cognomeResponsabile"),
                                rset.getString("emailResponsabile"),
                                rset.getString("telResponsabile"),
                                rset.getString("indirizzoSede"),
                                rset.getString("pIVA"),
                                rset.getString("citta"),
                                rset.getString("cap"),
                                rset.getString("provincia")
                        );
                        annuncio = new Annuncio(rset.getInt("idAnnuncio"), rset.getString("titolo"), rset.getString("corpo"), rset.getDate("dataAvvio").toLocalDate(), rset.getDate("dataTermine").toLocalDate(), rset.getString("modalita"), rset.getString("settore"), rset.getString("sussidio"), aziendaAnnuncio, docenteAnnuncio, referenteAnnuncio);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataLayerException("GET ANNUNCIO", ex);
        }

        return annuncio;
    }
   
    @Override
    public Annuncio getAnnuncio(Annuncio annuncio) throws DataLayerException {

        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(GET_ANNUNCIO_BY_ID)) {
                ps.setLong(1, annuncio.getId());
                try (ResultSet rset = ps.executeQuery()) {

                    if (rset.next()) {
                        Referente referenteAnnuncio = new Referente(rset.getString("nomeReferente"), rset.getString("cognomeReferente"), rset.getString("telefonoReferente"));
                        Docente docenteAnnuncio = new Docente(rset.getString("nomeDocente"), rset.getString("cognomeDocente"), rset.getString("emailDocente"));
                        Azienda aziendaAnnuncio = new Azienda(
                                rset.getString("ragSociale"),
                                rset.getString("nomeResponsabile"),
                                rset.getString("cognomeResponsabile"),
                                rset.getString("emailResponsabile"),
                                rset.getString("telResponsabile"),
                                rset.getString("indirizzoSede"),
                                rset.getString("pIVA"),
                                rset.getString("citta"),
                                rset.getString("cap"),
                                rset.getString("provincia")
                        );
                        annuncio = new Annuncio(rset.getInt("idAnnuncio"), rset.getString("titolo"), rset.getString("corpo"), rset.getDate("dataAvvio").toLocalDate(), rset.getDate("dataTermine").toLocalDate(), rset.getString("modalita"), rset.getString("settore"), rset.getString("sussidio"), aziendaAnnuncio, docenteAnnuncio, referenteAnnuncio);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataLayerException("GET ANNUNCIO", ex);
        }

        return annuncio;
    }

    @Override
    public List<Annuncio> getAnnunci(long idAzienda, int valuePage, String stato) throws DataLayerException {

        List<Annuncio> annunci = new ArrayList();

        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(GET_ANNUNCI_AZIENDA_BY_STATO)) {
                ps.setLong(1, idAzienda);
                ps.setString(2, stato);
                ps.setInt(3, valuePage);
                try (ResultSet rset = ps.executeQuery()) {
                    while (rset.next()) {
                        annunci.add(
                                new Annuncio(rset.getInt("idAnnuncio"),
                                rset.getString("titolo"), rset.getString("corpo"),
                                rset.getDate("dataAvvio").toLocalDate(),
                                rset.getDate("dataTermine").toLocalDate(),
                                rset.getString("modalita"),
                                rset.getString("settore"),
                                rset.getString("sussidio"),
                                new Azienda(rset.getInt("idAzienda")))
                        );
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataLayerException("GET ANNUNCI FROM AZIENDA", ex);
        }

        return annunci;
    }

    @Override
    public List<Annuncio> getAnnunci(Utente utente) throws DataLayerException {

        List<Annuncio> annunci = new ArrayList();

        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(GET_ANNUNCI_OF_AZIENDA)) {
                ps.setLong(1, utente.getId());
                try (ResultSet rset = ps.executeQuery()) {
                    annunci=getAnnunciImpl(rset);
                }
            }
        } catch (SQLException ex) {
            throw new DataLayerException("GET ANNUNCI OF AZIENDA", ex);
        }

        return annunci;
    }

    @Override
    public List<Annuncio> getAnnunci(int valuePage, String stato) throws DataLayerException {

        List<Annuncio> annunci = new ArrayList();
        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(GET_ANNUNCI_STATO)) {

                ps.setString(1, stato);
                ps.setInt(2, valuePage);

                try (ResultSet rset = ps.executeQuery()) {
                    while (rset.next()) {
                        annunci.add(new Annuncio(rset.getInt("idAnnuncio"),
                                rset.getString("titolo"),
                                rset.getString("corpo"),
                                rset.getDate("dataAvvio").toLocalDate(),
                                rset.getDate("dataTermine").toLocalDate(),
                                rset.getString("modalita"),
                                rset.getString("settore"),
                                rset.getString("sussidio"),
                                new Azienda(rset.getInt("idAzienda")))
                        );
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataLayerException("GET ANNUNCI BY PAGE", ex);
        }

        return annunci;
    }

    @Override
    public List<Annuncio> getAnnunci(int first, int last) throws DataLayerException {

        List<Annuncio> annunci = new ArrayList();

        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(GET_ANNUNCI_RANGE)) {

                //Includiamo primo ed ultimo elesmento
                first--; //la prima riga SQL inzia da 0
                int nextItems = last-first; //Esempio= first: 3, last: 5 -> LIMIT 2,3

                ps.setInt(1, first);
                ps.setInt(2, nextItems);

                try (ResultSet rset = ps.executeQuery()) {
                    annunci= getAnnunciImpl(rset);
                }
            }
        } catch (SQLException ex) {
            throw new DataLayerException("GET ANNUNCI BY RANGE", ex);
        }

        return annunci;
    }

    @Override
    public List<Annuncio> getAnnunci(int first) throws DataLayerException {

        List<Annuncio> annunci = new ArrayList();

        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(GET_ANNUNCI_RANGE_TO_END)) {

                //Includiamo primo ed ultimo elesmento
                first--; //la prima riga SQL inzia da 0

                ps.setInt(1, first);

                try (ResultSet rset = ps.executeQuery()) {
                    annunci= getAnnunciImpl(rset);
                }
            }
        } catch (SQLException ex) {
            throw new DataLayerException("GET ANNUNCI BY RANGE TO END", ex);
        }

        return annunci;
    }

    @Override
    public List<Annuncio> getAnnunci() throws DataLayerException {

        List<Annuncio> annunci = new ArrayList();

        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(GET_ALL_ANNUNCI)) {

                try (ResultSet rset = ps.executeQuery()) {
                    annunci= getAnnunciImpl(rset);
                }
            }
        } catch (SQLException ex) {
            throw new DataLayerException("GET ALL ANNUNCI ", ex);
        }

        return annunci;
    }

    //List of annunci
    private List<Annuncio> getAnnunciImpl( ResultSet rset) throws DataLayerException {
        List<Annuncio> annunci = new ArrayList();

        try{
        while (rset.next()) {
            annunci.add(new Annuncio(rset.getInt("idAnnuncio"),
                    rset.getString("titolo"),
                    rset.getString("corpo"),
                    rset.getDate("dataAvvio").toLocalDate(),
                    rset.getDate("dataTermine").toLocalDate(),
                    rset.getString("modalita"),
                    rset.getString("settore"),
                    rset.getString("sussidio"),
                    new Azienda(
                            rset.getLong("idAzienda"),
                            rset.getString("nomeRap"),
                            rset.getString("cognomeRap"),
                            rset.getString("telResponsabile"),
                            rset.getString("nomeResponsabile"),
                            rset.getString("cognomeResponsabile"),
                            rset.getString("emailResponsabile"),
                            rset.getString("ragSociale"),
                            rset.getString("indirizzoSede"),
                            rset.getString("pIVA"),
                            rset.getString("foro"),
                            rset.getString("cap"),
                            rset.getString("citta"),
                            rset.getString("provincia")),
                    new Docente(rset.getString("nomeDocente"),rset.getString("cognomeDocente"),rset.getString("emailDocente")),
                    new Referente(rset.getString("nomeReferente"),rset.getString("cognomeReferente"),rset.getString("emailReferente"),rset.getString("telefonoReferente"))
                    )
            );
        }
        }catch (SQLException ex){
            throw new DataLayerException("GET ANNUNCI BY RANGE IMPL", ex);
        }

        return annunci;
    }


    @Override
    public List<Annuncio> getAnnunciSearch(int valuePage, String campoRicerca) throws DataLayerException{

        List<Annuncio> annunci = new ArrayList();
        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(GET_ANNUNCI_SEARCH)) {

                ps.setString(1, campoRicerca);
                ps.setString(2, campoRicerca);
                ps.setInt(3, valuePage);

                try (ResultSet rset = ps.executeQuery()) {
                    while (rset.next()) {
                        annunci.add(new Annuncio(rset.getInt("idAnnuncio"),
                                rset.getString("titolo"),
                                rset.getString("corpo"),
                                rset.getDate("dataAvvio").toLocalDate(),
                                rset.getDate("dataTermine").toLocalDate(),
                                rset.getString("modalita"),
                                rset.getString("settore"),
                                rset.getString("sussidio"),
                                new Azienda(
                                        rset.getLong("idAzienda"),
                                        rset.getString("nomeRap"),
                                        rset.getString("cognomeRap"),
                                        rset.getString("telResponsabile"),
                                        rset.getString("nomeResponsabile"),
                                        rset.getString("cognomeResponsabile"),
                                        rset.getString("emailResponsabile"),
                                        rset.getString("ragSociale"),
                                        rset.getString("indirizzoSede"),
                                        rset.getString("pIVA"),
                                        rset.getString("foro"),
                                        rset.getString("cap"),
                                        rset.getString("citta"),
                                        rset.getString("provincia")
                                )
                             )
                        );
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataLayerException("GET ANNUNCI BY SEARCH", ex);
        }

        return annunci;
    }
    
    @Override
    public int saveAnnuncio(Annuncio annuncio) throws DataLayerException {
        int result = -1;
        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(SAVE_ANNUNCIO)) {

                ps.setString(1, annuncio.getTitolo());
                ps.setString(2, annuncio.getCorpo());
                ps.setDate(3, java.sql.Date.valueOf(annuncio.getDataAvvio()));
                ps.setDate(4, java.sql.Date.valueOf(annuncio.getDataTermine()));
                ps.setString(5, annuncio.getModalita());
                ps.setString(6, annuncio.getSussidio());
                ps.setString(7, annuncio.getSettore());
                ps.setLong(8, annuncio.getAzienda().getId());
                ps.setString(9, annuncio.getDocente().getNome());
                ps.setString(10, annuncio.getDocente().getCognome());
                ps.setString(11, annuncio.getDocente().getEmail());
                ps.setString(12, annuncio.getReferente().getNome());
                ps.setString(13, annuncio.getReferente().getCognome());
                ps.setString(14, annuncio.getReferente().getEmail());
                ps.setString(15, annuncio.getReferente().getTelefono());

                result = ps.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataLayerException("SAVE ANNUNCIO", ex);
        }
        return result;
    }

    @Override
    public int updateStato(Annuncio annuncio) throws DataLayerException{
    
        int result = -1;
        
        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(UPDATE_STATO)) {

                //Prepare query
                ps.setString(1, annuncio.getStato());
                ps.setLong(2, annuncio.getId());

                result = ps.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataLayerException("UPDATE STATO ANNUNCIO", ex);
        }
        
        return result;
    }

    @Override
    public boolean publishedByCompany(Utente utente, Annuncio annuncio) throws DataLayerException{
            try (Connection connection = Database.getDatasource().getConnection()) {
                try (PreparedStatement ps = connection.prepareStatement(PUBLISHED_BY_AZIENDA)) {

                    ps.setLong(1, utente.getId());
                    ps.setLong(2, annuncio.getId());

                    try (ResultSet rset = ps.executeQuery()) {
                        if(rset.next())return true;
                    }
                }
            } catch (SQLException ex) {
                throw new DataLayerException("PUBLISHED BY AZIENDA", ex);
            }
            return false;
    }
}
