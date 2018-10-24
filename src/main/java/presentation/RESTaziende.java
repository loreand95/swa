package presentation;


import business.Annuncio;
import business.Azienda;
import business.AziendaDetail;
import business.Utente;
import business.dao.data.DataLayerException;
import business.dao.intf.AnnuncioDAO;
import business.dao.intf.AziendaDAO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import business.dao.intf.UtenteDAO;
import com.fasterxml.jackson.annotation.JsonFormat;
import controller.ControllerAuthentication;
import controller.ControllerAzienda;


@Path("aziende")
public class RESTaziende {


    @Inject
    AziendaDAO aziendaDAO;

    @Inject
    ControllerAuthentication controllerAuthentication;

    @Inject
    ControllerAzienda controllerAzienda;

    /**
     * Restituisce una lista contenente i nomi di tutte le aziende presenti
     * nel sistema e la URI necessaria per leggerne i dettagli.
     *
     * GET - URI: /rest/aziende
     *
     * @return [{nome:"azienda srl", url:"http://internships.it/aziende/1"}]
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAziende(@Context UriInfo context) {

        try {

            List<HashMap<String, String>> list = controllerAzienda.getAllAziende(context);
            if (list != null) {
                return Response.ok(list).build();
            } else {
                return Response.serverError().entity("UNKNOWN ERROR").build();
            }
        } catch (DataLayerException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    /**
     * Registra una nuova azienda. Il payload dovrà essere una struttura contente tutti i dati richiesti in fase di registrazione,
     * come risulta dalla specifica del progetto Internship tutor, inclusi username e password dell’utenza corrispondente.
     * In output, come da standard, ci sarà la URL necessaria a leggere i dati dell’azienda appena inserita (credenziali escluse!)
     *
     * POST - URI: /rest/aziende
     *
     * @param c
     * @param azienda
     * @return URI Azienda
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertAzienda(@Context UriInfo c, Azienda azienda) {

        try {
            //retunr id nuova azienda
            long id = controllerAzienda.registrazioneAzienda(azienda);

            if (id != -1) {
                //Creazione URI nuova azienda
                URI u = c.getBaseUriBuilder()
                        .path(RESTaziende.class)
                        .path(RESTaziende.class, "getAziendaById")
                        .build(id);

                return Response.created(u).build();
            } else {
                return Response.serverError().build();
            }

        } catch (DataLayerException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    /**
     * Restituisce tutti i dettagli relativi all’azienda identificata da {ID}. Il formato di output potrà
     * essere lo stesso usato per l’inserimento (POST) sulla URL aziende
     *
     * GET - URI: /rest/aziende/{id: [0-9]+}
     *
     * @param idAzienda
     * @return azienda
     */
    @GET
    @Path("{id: [0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAziendaById(@PathParam("id") long idAzienda) {
        try {
            Azienda azienda = controllerAzienda.getAzienda(idAzienda);
            return Response.ok(azienda).build();
        } catch (DataLayerException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    /**
     * Modifica le informazioni relative ai dati dell’azienda identificata da {ID}. Il payload potrà essere lo stesso usato per
     * l’inserimento (POST) sulla URL aziende. L’operazione viene eseguita nel contesto della sessione con token {SID} e, in
     * base al tipo di utente associato, permetterà di aggiornare solo un insieme specifico di dettagli: l’azienda potrà
     * modificare tutti i dati inizialmente inseriti, tranne le credenziali di accesso, l’amministratore solo il flag di
     * “abilitazione alla pubblicazione”, mentre gli studenti riceveranno un errore 403 - Forbidden.
     *
     * PUT - URI: /rest/aziende/{id: [0-9]+}
     *
     * @param authcookie
     * @param idAzienda
     * @param azienda
     * @return Update || Forbidden
     */
    @PUT
    @Path("{id: [0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAzienda(@CookieParam("sid") Cookie authcookie, @PathParam("id") long idAzienda, Azienda azienda) {

        try {

            if (authcookie != null) {//utente loggato
                //get id azienda
                azienda.setId(idAzienda);

                Utente utente = controllerAuthentication.getUtente(authcookie.getValue());

                switch (utente.getTipo()) {

                    case "AZ":
                        //azienda piò modificare solo il proprio account
                        if(utente.getId()!=idAzienda) return Response.status(403).entity("Permission Denied").build();
                        //update azienda
                        return Response.noContent().build();

                    case "ST":
                        //not autorizzato
                        return Response.status(403).build();

                    case "AM":
                        //update attivazione
                        controllerAzienda.updateActivation(azienda);
                        return Response.noContent().build();

                    default:
                        return Response.status(403).entity("Error type session").build();
                }
            } else { //utetnte non loggato
                return Response.status(403).entity("No active session").build();
            }
        } catch (DataLayerException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    /**
     * Restituisce tutti i dettagli relativi all’azienda identificata da {ID}. Il formato di output potrà
     * essere lo stesso usato per l’inserimento (POST) sulla URL aziende
     *
     * GET - URI: /rest/aziende/{id: [0-9]+/offerte}
     *
     * @param idAzienda
     * @return azienda
     */
    @GET
    @Path("{id: [0-9]+}/offerte")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOfferteByIdAzienda(@PathParam("id") long idAzienda) {
        try {

            List<Annuncio> listAnnunci = controllerAzienda.getAnnunci(idAzienda);

            return Response.ok(listAnnunci).build();
        } catch (DataLayerException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
}
