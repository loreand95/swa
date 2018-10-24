/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.dao.impl;

import business.dao.data.DataLayerException;
import business.dao.data.Database;
import business.dao.intf.AmministratoreDAO;
import business.Azienda;
import business.Convenzione;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author lorenzo
 */
public class AmministratoreDAOImpl implements AmministratoreDAO {

    private static final String AZIENDE = "SELECT * FROM Azienda WHERE Azienda.stato=? LIMIT ?,?";

    private static final String UPLOAD_CONVENZIONE = "INSERT INTO convenzione (nome, file, estensione, peso) VALUES(?,?,?,?)";

    private static final String UPDATE_CONVEZIONE = "UPDATE Azienda SET Convenzione_idConvenzione=?, Stato='CONVENZIONATA'  WHERE idAzienda=?;";

    private final int NUMBER_ELEMENT = 4;

    @Override
    public int setConvenzione(Convenzione convenzione, Azienda azienda) throws DataLayerException {
        int result = 0;
        long idConvenzione;

        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(UPLOAD_CONVENZIONE, Statement.RETURN_GENERATED_KEYS)) {


                ps.setString(1, convenzione.getNome());
                ps.setBlob(2, convenzione.getFile());
                ps.setString(3, convenzione.getEstensione());
                ps.setLong(4, convenzione.getPeso());

                if (ps.executeUpdate() == 1) {
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.first();
                        idConvenzione = keys.getLong(1);
                        try (PreparedStatement ps1 = connection.prepareStatement(UPDATE_CONVEZIONE)) {
                            ps1.setLong(1, idConvenzione);
                            ps1.setLong(2, azienda.getId());

                            result = ps1.executeUpdate();
                        }
                    }
                } else {
                    //remove last insert
                    //TODO
                }
            }
        } catch (SQLException e) {
            throw new DataLayerException("DATA ERROR UPLOAD", e);
        }
        return result;
    }

    @Override
    public List<Azienda> getListaAziende(String tipologia, int page) throws DataLayerException {


        List<Azienda> listaAziende = new ArrayList();

        try (Connection connection = Database.getDatasource().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(AZIENDE)) {

                ps.setString(1, tipologia);
                ps.setInt(2, page * NUMBER_ELEMENT);
                ps.setInt(3, NUMBER_ELEMENT);

                try (ResultSet rset = ps.executeQuery()) {

                    while (rset.next()) {
                        listaAziende.add(new Azienda(rset.getInt("idAzienda"), rset.getString("ragSociale")));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataLayerException("DATA ERROR GET AZIENDA", e);
        }
        return listaAziende;
    }

}
