package presentation;

import business.Richiesta;
import business.Utente;
import business.dao.data.DataLayerException;
import controller.ControllerAuthentication;
import controller.ControllerAzienda;
import controller.ControllerStudente;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.Date;

public class RESTsub_candidati {

    @Inject
    RESTsub_progettoformativo resTsub_progettoformativo;

    @Inject
    ControllerAuthentication controllerAuthentication;

    @Inject
    ControllerStudente controllerStudente;

    @Inject
    ControllerAzienda controllerAzienda;

    /**
     * Inserisce la candidatura per il tirocinio {ID} dello studente autenticato nel contesto della sessione {SID}.
     * Se la sessione non corrisponde a uno studente, genera un errore un errore 403 - Forbidden. Il payload dovrà
     * contenere tutti i dati necessari alla definizione della candidatura, come risulta dalla specifica del progetto
     * Internship tutor.
     *
     * POST - URI: rest/auth/offerte/{id}/candidati
     *
     * @param authcookie
     * @param idAnnuncio
     * @param richiesta
     * @return response
     */
    @POST
    @Path("candidati")
    @Produces(MediaType.APPLICATION_JSON)
    public Response invioCandidatura(@CookieParam("sid") Cookie authcookie, @PathParam("id") long idAnnuncio, Richiesta richiesta) {

        try {
            if (authcookie != null) {//utente loggato
                Utente utente = controllerAuthentication.getUtente(authcookie.getValue());

                if (utente.getTipo().equals("ST")) {

                    int result = controllerStudente.sendRichiesta(utente.getId(), idAnnuncio, richiesta);

                    if (result == 1) {
                        return Response.ok().build();
                    } else {
                        return Response.serverError().build();
                    }

                } else {
                    return Response.status(403).entity("ERROR-TYPE-USER").build();
                }
            } else {
                return Response.status(403).entity("ERROR-SESSION").build();
            }
        } catch (DataLayerException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    /**
     * Consente all’azienda autenticata nel contesto della sessione {SID} di respingere la candidatura {IDc} per il
     * tirocinio {IDt}. Se la sessione non corrisponde a un’azienda, o se l’offerta di tirocinio non è stata pubblicata
     * dall’azienda in sessione, genera un errore un errore 403 – Forbidden o 400 – Bad Request.
     *
     * DELETE - URI: rest/auth/offerte/{id}/candidati/{idc: [0-9]+}
     *
     * @return response
     */
    @DELETE
    @Path("candidati/{idc: [0-9]+}")
    public Response rifiutaCandidatura(@CookieParam("sid") Cookie authcookie, @PathParam("id") long idAnnuncio, @PathParam("idc") long idCandidato) {

        try {
            if (authcookie != null) {//utente loggato
                Utente utente = controllerAuthentication.getUtente(authcookie.getValue());

                //check autenticazione - solo l'azienda può rifiutare un candidato
                if (utente.getTipo().equals("AZ")) {

                    int result = controllerAzienda.rifiutaCandidatura(utente, idCandidato, idAnnuncio);

                    switch (result) {
                        case 1:
                            return Response.noContent().build();

                        case 403:
                            return Response.status(403).entity("ERROR-AZIENDA-NOT-AUTHORIZED").build();

                        default:
                            return Response.serverError().entity("UNKNOWN-ERROR").build();
                    }

                } else {
                    return Response.status(403).entity("ERROR-TYPE-USER").build();
                }
            } else {
                return Response.status(403).entity("ERROR-SESSION").build();
            }
        } catch (DataLayerException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }


    }

    /**
     * Consente all’azienda autenticata nel contesto della sessione {SID} di accettare la candidatura {IDc} per il
     * tirocinio {IDt}. Il payload dovrà contenere tutti i dati necessari alla finalizzazione del progetto formativo,
     * come risulta dalla specifica del progetto Internship tutor. Se la sessione non corrisponde a un’azienda, o se
     * l’offerta di tirocinio non è stata pubblicata dall’azienda in sessione, genera un errore un errore 403 - Forbidden.
     *
     * PUT - URI: rest/auth/offerte/{IDt}/candidati/{idc: [0-9]+}
     *
     * @param authcookie
     * @param idAnnuncio
     * @param idCandidato
     * @return response
     */
    @PUT
    @Path("candidati/{idc: [0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response approvazioneCandidatura(@CookieParam("sid") Cookie authcookie, @PathParam("id") long idAnnuncio, @PathParam("idc") long idCandidato) {

        try {
            if (authcookie != null) {//utente loggato
                Utente utente = controllerAuthentication.getUtente(authcookie.getValue());

                //check autenticazione - l'approvazione può essere svolta solo dall'azienda
                if (utente.getTipo().equals("AZ")) {

                    int result = controllerAzienda.approvaCandidatura(utente, idCandidato, idAnnuncio);

                    if (result == 1) {
                        return Response.ok().build();
                    } else if (result == 403) {
                        return Response.status(403).entity("PERMISSION DENIED").build();
                    }else {
                        return Response.serverError().build();
                    }

                } else {
                    return Response.status(403).entity("ERROR-TYPE-USER").build();
                }
            } else {
                return Response.status(403).entity("ERROR-SESSION").build();
            }
        } catch (DataLayerException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }


    /**
     * Subrequest per il progetto formativo
     *
     * @return RESTsub_progettoformativo
     */
    @Path("candidati/{idc: [0-9]+}/progetto-formativo")
    public RESTsub_progettoformativo progettoformativo(){
        return resTsub_progettoformativo;
    }

}
