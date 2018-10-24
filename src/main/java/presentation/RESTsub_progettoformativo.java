package presentation;

import business.Utente;
import business.dao.data.DataLayerException;
import controller.ControllerAuthentication;
import controller.ControllerAzienda;
import controller.ControllerStudente;
import controller.ControllerTirocinio;
import javax.inject.Inject;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;

import java.io.*;

public class RESTsub_progettoformativo {

    @Inject
    ControllerAuthentication controllerAuthentication;

    @Inject
    ControllerStudente controllerStudente;

    @Inject
    ControllerAzienda controllerAzienda;

    @Inject
    ControllerTirocinio controllerTirocinio;

    /**
     * Consente all’azienda o allo studente autenticati nel contesto della sessione {SID} di scaricare il progetto formativo relativo alla candidatura
     * {IDc} per il tirocinio {IDt}. Se la sessione non corrisponde all’azienda che offre il tirocinio specificato o allo studente che ha presentato la
     * relativa candidatura, che deve essere stata approvata, genera un errore un errore 403 - Forbidden.
     *
     * @param authcookie
     * @param idAnnuncio
     * @param idCandidato
     * @return
     */
    @GET
    public Response downloadProgetto(@CookieParam("sid") Cookie authcookie, @PathParam("id") long idAnnuncio, @PathParam("idc") long idCandidato) {

        try {
            if (authcookie != null) {//utente loggato
                Utente utente = controllerAuthentication.getUtente(authcookie.getValue());


                boolean autorizzato=false;

                switch (utente.getTipo()) {
                    case "AZ":
                        //l'azienda è autorizzata solo se proprietaria dell'annuncio
                        autorizzato = controllerAzienda.publishedByCompany(utente,idAnnuncio);
                        break;

                    case "ST":
                        //studente autorizzato solo se è candidato
                        if(utente.getId()==idCandidato) autorizzato=true;
                        break;

                    default:
                        return Response.status(403).entity("ERROR-TYPE-USER").build();
                }


                //check autenticazione
                if (autorizzato) {


                    ByteArrayOutputStream os = controllerTirocinio.downloadProgetto(utente, idCandidato, idAnnuncio);

                    if (os == null) {
                        return Response.serverError().entity("EMPTY FILE").build();
                    }

                    Response.ResponseBuilder responseBuilder = Response.ok(os.toByteArray());

                    responseBuilder.type("application/pdf");
                    responseBuilder.header("Content-Disposition", "filename=ProgettoFormativo.pdf");
                    return responseBuilder.build();


                } else {
                    return Response.status(403).entity("DENIED").build();
                }
            } else {
                return Response.status(403).entity("ERROR-SESSION").build();
            }
        } catch (DataLayerException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
}
