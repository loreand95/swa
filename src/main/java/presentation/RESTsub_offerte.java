package presentation;

import business.Annuncio;
import business.Utente;
import business.dao.data.DataLayerException;
import controller.ControllerAuthentication;
import controller.ControllerOfferta;
import javax.inject.Inject;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class RESTsub_offerte {

    @Inject
    ControllerOfferta controllerOfferta;

    @Inject
    ControllerAuthentication controllerAuthentication;

    @Inject
    RESTsub_candidati RESTsub_candidati;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOfferte(@CookieParam("sid") Cookie authcookie) {
        try {
            if (authcookie != null) {//utente loggato
                Utente utente = controllerAuthentication.getUtente(authcookie.getValue());

                List<Annuncio> listAnnunci = controllerOfferta.getAnnuncioOfAzienda(utente);

                return Response.ok(listAnnunci).build();

            } else { //utetnte non loggato
                return Response.status(403).build();
            }

        } catch (DataLayerException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @Path("{id: [0-9]+}")
    public RESTsub_candidati subCandidati(){ return RESTsub_candidati; }
}
