package presentation;

import business.Utente;
import business.dao.data.DataLayerException;
import controller.ControllerAuthentication;


import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.CookieParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;


@Path("auth")
public class RESTauth {

    @Inject
    RESTsub_studenti RESTsub_studenti;

    @Inject
    RESTsub_offerte RESTsub_offerte;

    @Inject
    ControllerAuthentication controllerAuthentication;

    /**
     * Prende in input un oggetto contenente username e password di un utente e
     * restituisce un oggetto contenente il relativo token di sessione/autenticazione.
     *
     * POST
     * URI: /rest/auth
     *
     * @param utente
     * @return token
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(Utente utente) {
        try {

            String token = controllerAuthentication.getToken(utente);

            if(token!=null){
                //restituiamo il token come testo della risposta e anche come cookie
                NewCookie authcookie = new NewCookie("sid", token);
                return Response.ok(token).cookie(authcookie).build();
            }

            //credenziali errate
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid username or password").build();

        } catch (DataLayerException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    /**
     * Chiude la sessione relativa al token di sessione invalidandolo
     *
     * DELETE
     * URI: /rest/auth
     *
     * @param authcookie
     * @return Response= "Logout successful" || "No activate session"
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@CookieParam("sid") Cookie authcookie) {
        if (authcookie != null) {

            //TODO eliminare cookie dalla base di dati

            NewCookie resetauthcookie = new NewCookie(authcookie, null, 0, false);
            //resettiamo il cookie sul client
            return Response.ok("Logout successful").cookie(resetauthcookie).build();
        } else {
            return Response.ok("No active session").build();
        }
    }

    /**
     * URI: rest/auth/studenti
     * @return studente
     */
    @Path("studenti")
    public RESTsub_studenti getStudente(){
        return RESTsub_studenti;
    }

    /**
     * URI: rest/auth/offerte
     * @return annunci
     */
    @Path("offerte")
    public RESTsub_offerte getOfferte(){
        return RESTsub_offerte;
    }

}
