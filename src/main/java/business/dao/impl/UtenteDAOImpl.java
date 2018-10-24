/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.dao.impl;

import business.Utente;
import business.dao.intf.UtenteDAO;
import business.dao.data.Database;
import business.dao.data.DataLayerException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author lorenzo
 */
public class UtenteDAOImpl implements UtenteDAO {

    private static final String GET_UTENTE = "SELECT * FROM Utente WHERE token=?";

    private static final String GET_CREDENZIALI = "SELECT * FROM Utente WHERE username=? AND password=?";

    private static final String SET_NUOVO_UTENTE = "INSERT INTO Utente(username,password,email,tipologia) VALUES(?,?,?,?)";

    private static final String GET_USERNAME_ESISTENTE = "SELECT Utente.username FROM Utente WHERE Utente.username=?";

    private static final String GET_EMAIL_ESISTENTE = "SELECT Utente.email FROM Utente WHERE Utente.email=?";

    private static final String UPDATE_TOKEN = "UPDATE utente SET utente.token=? WHERE utente.username=?";

    private static final String DELETE_UTENTE = "DELETE FROM utente WHERE utente.idUtente=?";


    /**
     * Elimina utente dal db. Restituisce l'utente eliminato oppure null
     * @param utente
     * @return utente
     * @throws DataLayerException
     */
    @Override
    public Utente deleteUtente(Utente utente) throws DataLayerException{
        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(DELETE_UTENTE)) {
                ps.setLong(1, utente.getId());

                if(ps.executeUpdate() != 0)
                    return utente;
            }
        }catch (SQLException ex){
            throw new DataLayerException("DATA DELETE UTENTE",ex);
        }

        return null;
    }

    /**
     * Get credential of user
     *
     * @param utente
     * @return Utente or null if user exist
     * @throws DataLayerException
     */
    @Override
    public Utente getCredenziali(Utente utente) throws DataLayerException {

        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(GET_CREDENZIALI)) {
                //Prepare statement
                ps.setString(1, utente.getUsername());
                ps.setString(2, utente.getPassword());
                try (ResultSet rset = ps.executeQuery()) {

                    if (rset.next()) {
                        utente = new Utente(
                                rset.getLong("idUtente"),
                                rset.getString("username"),
                                rset.getString("email"),
                                rset.getString("tipologia"),
                                rset.getString("token")
                                //TODO add expire and create
                        );

                        return utente;
                    }
                }

            }
        } catch (SQLException ex) {
            throw new DataLayerException("CREDENZIALI UTENTE", ex);
        }

        return null;
    }

    /**
     * Seach utente by token, if not exist return null
     *
     * @param token
     * @return Utente || null
     * @throws DataLayerException
     */
    @Override
    public Utente getUtente(String token) throws DataLayerException {

        Utente utente;
        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(GET_UTENTE)) {
                //Prepare statement
                ps.setString(1, token);
                try (ResultSet rset = ps.executeQuery()) {

                    if (rset.next()) {
                        utente = new Utente(
                                rset.getLong("idUtente"),
                                rset.getString("username"),
                                rset.getString("email"),
                                rset.getString("tipologia"),
                                rset.getString("token")
                                //TODO add expire and create
                        );

                        return utente;
                    }
                }

            }
        } catch (SQLException ex) {
            throw new DataLayerException("DATA TOKEN UTENTE", ex);
        }

        return null;
    }



    @Override
    public int setToken(Utente utente) throws DataLayerException {

        int result = -1;
        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(UPDATE_TOKEN)) {

                ps.setString(1, utente.getToken());
                ps.setString(2, utente.getUsername());

                result = ps.executeUpdate();

            }
        } catch (SQLException ex) {
            throw new DataLayerException("DATA UPDATE TOKEN", ex);
        }

        return result;
    }

    @Override
    public Utente nuovoUtente(Utente utente) throws DataLayerException {

        System.out.println("ute" + utente);

        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(SET_NUOVO_UTENTE, Statement.RETURN_GENERATED_KEYS)) {
                //Prepare statement
                ps.setString(1, utente.getUsername());
                ps.setString(2, utente.getPassword());
                ps.setString(3, utente.getEmail());
                ps.setString(4, utente.getTipo());

                ps.executeUpdate();

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        utente.setId(generatedKeys.getLong(1));
                    } else {
                        throw new DataLayerException("RECUPERO UTENTE");
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataLayerException("NUOVO UTENTE", ex);
        }

        return utente;
    }

    @Override
    public boolean getUsernameEsistente(String username) throws DataLayerException {

        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(GET_USERNAME_ESISTENTE)) {
                //Prepare statement
                ps.setString(1, username);

                try (ResultSet rset = ps.executeQuery()) {
                    if (rset.next()) {
                        return true;
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataLayerException("USERNAME UTENTE", ex);
        }

        return false;
    }

    @Override
    public boolean getEmailEsistente(String email) throws DataLayerException {

        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(GET_EMAIL_ESISTENTE)) {
                //Prepare statement
                ps.setString(1, email);

                try (ResultSet rset = ps.executeQuery()) {
                    if (rset.next()) {
                        return true;
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataLayerException("EMAIL UTENTE", ex);
        }

        return false;
    }
}
