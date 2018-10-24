package presentation;

import business.Annuncio;
import business.dao.data.DataLayerException;
import controller.ControllerOfferta;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("offerte")
public class RESTofferte {

    @Inject
    ControllerOfferta controllerOfferta;

    /**
     * Restituisce un annuncio dal ID
     *
     * GET - URI: /rest/offerte/{id: [0-9]+}
     *
     * @param idAnnuncio
     * @return annuncio
     */
    @GET
    @Path("{id: [0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOfferta(@PathParam("id") long idAnnuncio){

        try{
            Annuncio annuncio = controllerOfferta.getAnnuncioById(idAnnuncio);
            return  Response.ok(annuncio).build();
        }catch (DataLayerException ex){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    /**
     * Restituisce una lista di tutte le offerte di tirocinio presenti nel sistema. Ciascun elemento della lista avrà la struttura
     * vista per la GET sulla URL offerte/{ID}. La lista potrà essere ulteriormente filtrata tramite la query string {FILTER} che
     * può contenere tutti i parametri previsti per la ricerca sulla bacheca offerte specificati dal progetto Internship tutor
     * Poiché gli elementi della lista potrebbero essere molti, adotteremo una strategia di paginazione. Includendo i parametri
     * first ed eventualmente last, la lista conterrà solo gli annunci dal numero m al numero n inclusi (supponendo qualche
     * tipo di ordinamento tra gli annunci, magari per data). Omettendo last, verranno restituiti tutti gli annunci dal numero m
     * all’ultimo. Solo omettendo entrambi i parametri verrà restituita la lista completa.
     *
     * GET - URI: /rest/offerte?{FILTER}[first={m}[&last={n}]]/
     *
     * @param cityFilter
     * @param n
     * @param m
     * @return  List<Annunci>
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOfferWithQuery(@QueryParam("cityFilter") String cityFilter, @QueryParam("first") int n, @QueryParam("last") int m) {
        try {

            List<Annuncio> listAnnuncio = controllerOfferta.getAllAnnunciFilter(cityFilter,n,m);
            return Response.ok(listAnnuncio).build();
        }catch (DataLayerException ex){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

}


