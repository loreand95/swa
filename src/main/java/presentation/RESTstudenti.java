package presentation;

import business.Studente;
import business.Utente;
import business.dao.data.DataLayerException;
import controller.ControllerStudente;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;

@Path("studenti")
public class RESTstudenti {

    @Inject
    ControllerStudente controllerStudente;

    /**
     * Registra un nuovo studente. Il payload dovrà essere una struttura contente tutti i dati richiesti in fase di
     * registrazione, come risulta dalla specifica del progetto Internship tutor, inclusi username e password dell’
     * utenza corrispondente. In output, come da standard, ci sarà la URL necessaria a manipolare i dati dell’utente
     * appena inserito (credenziali escluse!).
     *
     * POST - URI: /rest/studenti/
     * @param c
     * @param studente
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertStudente(@Context UriInfo c, Studente studente) {
        System.out.println(studente.toString());

        try {

            Utente nuovoStudente = controllerStudente.insertStudente(studente);
            if ( nuovoStudente != null) {
                URI u = c.getBaseUriBuilder()
                        .path(RESTauth.class)
                        .path(RESTauth.class, "getStudente")
                        .path(RESTsub_studenti.class, "getStudentInfo")
                        .build(nuovoStudente.getId());

                return Response.created(u).build();

            } else {
                return Response.serverError().build();
            }

        } catch (DataLayerException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }

    }
}
