/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.dao.impl;

import business.*;
import business.dao.data.Database;
import business.dao.data.DataLayerException;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import business.dao.intf.TirocinioDAO;

/**
 *
 * @author lorenzo
 */
public class TirocinioDAOImpl implements TirocinioDAO {

    private static final String GET_INFO_TIROCINIO = "SELECT Tirocinio.dataInizio, Tirocinio.dataFine, Tirocinio.Resoconto_idResoconto,Resoconto.nome,Resoconto.valutazione,Azienda.ragSociale, Azienda.indirizzoSede, Azienda.citta, Azienda.nomeResponsabile, Azienda.cognomeResponsabile, Azienda.emailResponsabile, Azienda.telResponsabile, Annuncio.nomeDocente, Annuncio.cognomeDocente, Annuncio.emailDocente FROM Tirocinio JOIN Annuncio ON Tirocinio.Annuncio_idAnnuncio = Annuncio.idAnnuncio LEFT JOIN Resoconto ON Tirocinio.Resoconto_idResoconto = Resoconto.idResoconto JOIN Azienda ON Annuncio.Azienda_idAzienda = Azienda.idAzienda WHERE Tirocinio.Studente_idStudente=?";

    private  static final String DOWNLOAD_PROGETTO = "SELECT File FROM Resoconto WHERE idResoconto=0";

    private  static final String GET_TIROCINIO = "SELECT * FROM Tirocinio JOIN Studente on Studente_idStudente=idStudente JOIN Annuncio ON Annuncio_idAnnuncio=idAnnuncio JOIN Azienda ON Azienda_idAzienda=idAzienda WHERE Annuncio_idAnnuncio=? AND Studente_idStudente=?";

    private static final String SET_VALUTAZIONE = "UPDATE Resoconto SET valutazione=? WHERE idResoconto=?";

    private static final String DOWNLOAD_RESOCONTO = "SELECT Resoconto.file FROM Resoconto WHERE idResoconto=?";

    private static final String UPLOAD_RESOCONTO = "UPDATE Resoconto SET Nome= ?, File=?, Estensione=?, Peso=? WHERE idResoconto=?;";

    private static final String SET_NUOVO_TIROCINIO = "INSERT INTO Tirocinio (Annuncio_idAnnuncio, Studente_idStudente, dataInizio, dataFine, crediti)\n"
            + "VALUES (?,?,?,?,?)";

    @Override
    public List<Tirocinio> getTirocini(long idStudente) throws DataLayerException {
        List<Tirocinio> tirocini = new ArrayList();
        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(GET_INFO_TIROCINIO)) {
                ps.setLong(1, idStudente);
                try (ResultSet rset = ps.executeQuery()) {

                    while (rset.next()) {
                        Resoconto resocontoAnnuncio = new Resoconto(rset.getInt("Tirocinio.Resoconto_idResoconto"), rset.getString("Resoconto.nome"), rset.getInt("Resoconto.valutazione"));
                        Docente docenteAnnuncio = new Docente(rset.getString("Annuncio.nomeDocente"), rset.getString("Annuncio.cognomeDocente"), rset.getString("Annuncio.emailDocente"));
                        Azienda aziendaAnnuncio = new Azienda(rset.getString("ragSociale"), rset.getString("indirizzoSede"), rset.getString("citta"), rset.getString("nomeResponsabile"), rset.getString("cognomeResponsabile"), rset.getString("emailResponsabile"), rset.getString("telResponsabile"));
                        Annuncio annuncio = new Annuncio(aziendaAnnuncio, docenteAnnuncio);
                        tirocini.add(new Tirocinio(resocontoAnnuncio, annuncio, rset.getDate("Tirocinio.dataInizio"), rset.getDate("Tirocinio.dataFine")));
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataLayerException("GET TIROCINI", ex);
        }

        return tirocini;
    }

    @Override
    public int setValutazione(int valutazione, long idResoconto) throws DataLayerException {

        int result = -1;
        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(SET_VALUTAZIONE)) {

                //Prepare query
                ps.setInt(1, valutazione);
                ps.setLong(2, idResoconto);

                result = ps.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataLayerException("SAVE VALUTAZIONE", ex);
        }
        return result;
    }

    @Override
    public InputStream downloadResoconto(Resoconto resoconto) throws DataLayerException {
        InputStream inputStream = null;

        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(DOWNLOAD_RESOCONTO)) {
                //Prepare query
                ps.setLong(1, resoconto.getId());

                try (ResultSet rset = ps.executeQuery()) {
                    if (rset.next()) {
                        Blob blob = rset.getBlob("File");
                        inputStream = blob.getBinaryStream();
                    }
                }
            }

        } catch (SQLException e) {
            throw new DataLayerException("DOWNLOAD RESOCONTO", e);
        }

        return inputStream;
    }

    @Override
    public InputStream downloadProgettoBase() throws DataLayerException {
        InputStream inputStream = null;

        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(DOWNLOAD_PROGETTO)) {

                try (ResultSet rset = ps.executeQuery()) {
                    if (rset.next()) {
                        Blob blob = rset.getBlob("file");
                        inputStream = blob.getBinaryStream();
                    }
                }
            }

        } catch (SQLException e) {
            throw new DataLayerException("DOWNLOAD RESOCONTO", e);
        }

        return inputStream;
    }

    @Override
    public Tirocinio getTirocinio(Tirocinio tirocinio) throws DataLayerException {
        InputStream inputStream = null;

        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(GET_TIROCINIO)) {
                //Prepare query

                ps.setLong(1, tirocinio.getStudente().getId());
                ps.setLong(2, tirocinio.getAnnuncio().getId());

                System.out.println("QUERY"+ps.toString());
                try (ResultSet rset = ps.executeQuery()) {
                    if (rset.next()) {

                        Studente s = new Studente(rset.getLong("Studente.idStudente"));


                        s.setNome(rset.getString("Studente.nome"));
                        s.setCognome(rset.getString("Studente.cognome"));
                        s.setCodFiscale(rset.getString("Studente.codFiscale"));
                        s.setTelefono(rset.getString("Studente.telefono"));
                        s.setCittaResidenza(rset.getString("Studente.citta_residenza"));

                        Annuncio a = new Annuncio(rset.getLong("Annuncio.idAnnuncio"));

                        tirocinio.setStudente(s);
                        tirocinio.setAnnuncio(a);

                    }
                }
            }

        } catch (SQLException e) {
            throw new DataLayerException("DOWNLOAD RESOCONTO", e);
        }

        return tirocinio;
    }

    @Override
    public int uploadResoconto(Resoconto resoconto) throws DataLayerException {

        int result = -1;
        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(UPLOAD_RESOCONTO)) {

                //Prepare query
                ps.setString(1, resoconto.getNome());
                ps.setBlob(2, resoconto.getFile());
                ps.setString(3, resoconto.getEstensione());
                ps.setLong(4, resoconto.getPeso());
                ps.setLong(5, resoconto.getId());

                result = ps.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataLayerException("UPLOAD RESOCONTO", ex);
        }

        return result;
    }

    /**
     * Rimnuovi il tirocinio sostenuto dallo studente presso l'azienda
     *
     * @param richiesta
     * @return
     * @throws business.dao.data.DataLayerException
     */
    @Override
    public int setNuovoTirocinio(Richiesta richiesta) throws DataLayerException {
        int result = -1;
        System.out.println(richiesta.toString());
        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(SET_NUOVO_TIROCINIO)) {
                ps.setLong(1, richiesta.getAnnuncio().getId());
                ps.setLong(2, richiesta.getStudente().getId());
                ps.setDate(3, new java.sql.Date(richiesta.getDataInizio().getTime()));
                ps.setDate(4, new java.sql.Date(richiesta.getDataFine().getTime()));
                ps.setInt(5, richiesta.getCrediti());
                result = ps.executeUpdate();

            }
        } catch (SQLException ex) {
            throw new DataLayerException("ERRORE NUOVO TIROCINIO", ex);
        }
        return result;
    }

}
