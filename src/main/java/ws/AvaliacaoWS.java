/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

import bean.Avaliacao;
import bean.ItemCardapio;
import dao.AvaliacaoDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Date;
import java.util.List;

/**
 * REST Web Service
 *
 * @author Everton
 */
@Path("avaliacao")
public class AvaliacaoWS {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of AvaliacaoWS
     */
    public AvaliacaoWS() {
    }

    /**
     * Retrieves representation of an instance of ws.AvaliacaoWS
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of AvaliacaoWS
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postCliente(Avaliacao avaliacao) {
        try {
            AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
            avaliacao.setDataHora(new Date());
            avaliacaoDAO.incluir(avaliacao);
           
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
    
    @GET
    @Path("/{coditemcardapio}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPedidos(@PathParam("coditemcardapio") Integer codItemCardapio) {
        
        try {
            AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
            
            List<Avaliacao> listaAvaliacao = avaliacaoDAO
                    .getListaPorItemCardapio(new ItemCardapio(codItemCardapio), null, null);                                    
            
            GenericEntity<List<Avaliacao>> 
                    lista = new GenericEntity<List<Avaliacao>>(listaAvaliacao){};

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
}
