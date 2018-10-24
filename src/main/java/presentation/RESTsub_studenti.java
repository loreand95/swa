package presentation;

import business.Studente;
import business.Utente;
import business.dao.data.DataLayerException;
import controller.ControllerAuthentication;
import controller.ControllerStudente;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RESTsub_studenti {

    @Inject
    ControllerAuthentication controllerAuthentication;

    @Inject
    ControllerStudente controllerStudente;

    /**
     *
     * Restituisce tutti i dettagli relativi allo studente indetificato da {ID}. Il formato di output potrà essere lo stesso usato
     * per l’inserimento (POST) sulla URL studenti. L’operazione viene eseguita nel contesto della sessione con token {SID} e, in base
     * al tipo di utente associato, permetterà di leggere solo un insieme specifico di dettagli: le aziende potranno leggere tutti i
     * dati dello studente solo nel caso in cui risulti attivo un tirocinio presso di loro, mentre l’amministratore e l’utente stesso
     * potranno vedere tutti i dati. Negli altri casi, verrà generato un errore 403 - Forbidden.
     *
     * GET - URI: rest/auth/studenti/{id: [0-9]+}
     *
     * @param authcookie
     * @param idStudente
     * @return studente
     */
    @GET
    @Path("{id: [0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStudentInfo(@CookieParam("sid") Cookie authcookie, @PathParam("id") long idStudente) {
        Studente studente = new Studente(idStudente);

        try {
            if (authcookie != null) {//utente loggato

                Utente utente = controllerAuthentication.getUtente(authcookie.getValue());

                switch (utente.getTipo()) {
                    case "AZ":

                        studente = controllerStudente.getStudenteByAzienda(utente, studente);
                        if (studente != null) {
                            return Response.ok(studente).build();
                        } else {
                            return Response.status(403).entity("ID-TIROCINANTE-ERRATO").build();
                        }

                    case "ST":
                        studente = controllerStudente.getStudenteByStudente(utente, studente);
                        if (studente != null) {
                            return Response.ok(studente).build();
                        } else {
                            return Response.status(403).entity("ID-STUDENTE-ERRATO").build();
                        }

                    case "AM":
                        studente = controllerStudente.getStudenteByAdmin(utente, studente);
                        return Response.ok(studente).build();


                    default:
                        studente = controllerStudente.getStudenteByStudente(utente, studente);
                        return Response.status(403).entity("ERRORE-SESSIONE").build();
                }
            } else { //utetnte non loggato
                return Response.status(403).entity("SESSIONE SCADUTA").build();
            }
        } catch (DataLayerException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
}
