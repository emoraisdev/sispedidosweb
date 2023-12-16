/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

import bean.Notificacao;
import dao.NotificacaoDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

/**
 * REST Web Service
 *
 * @author Everton
 */
@Path("notificacao")
public class NotificacaoWS {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of NotificacaoWS
     */
    public NotificacaoWS() {
    }

    /**
     * Retrieves representation of an instance of ws.NotificacaoWS
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of NotificacaoWS
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }
    
    @GET
    @Path("/lista/{codcliente}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getNotificacoes(@PathParam("codcliente") Integer codCliente) {
        
        try {
            
            List<Notificacao> listaNotificacao = (new NotificacaoDAO()).getLista(codCliente);
            
            GenericEntity<List<Notificacao>> 
                    lista = new GenericEntity<List<Notificacao>>(listaNotificacao){};

            return Response
                    .status(Response.Status.OK)
                    .entity(lista)
                    .build();            
        } catch (Exception e) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postNotificacao(Notificacao notificacao) {
        try {
              
            (new NotificacaoDAO()).alterar(notificacao);
           
            return Response
                    .ok()
                    .build();
        } catch (Exception e) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }        
    }
}
