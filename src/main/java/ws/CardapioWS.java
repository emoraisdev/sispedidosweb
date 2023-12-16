/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

import bean.GrupoCardapio;
import bean.ItemAdicional;
import bean.ItemCardapio;
import dao.GrupoCardapioDAO;
import dao.ItemAdicionalDAO;
import dao.ItemCardapioDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

/**
 * REST Web Service
 *
 * @author Everton
 */
@Path("cardapio")
public class CardapioWS {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of CardapioWS
     */
    public CardapioWS() {
    }

    /**
     * Retrieves representation of an instance of ws.CardapioWS
     * @return an instance of java.lang.String
     */    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGrupos() {
        try {
            GrupoCardapioDAO grupoCardapioDAO = new GrupoCardapioDAO();
            
            List<GrupoCardapio> listaGrupoCardapio = grupoCardapioDAO
                    .getLista(true, false, false, null, null);            
            
            GenericEntity<List<GrupoCardapio>> 
                    lista = new GenericEntity<List<GrupoCardapio>>(listaGrupoCardapio){};

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
    
    @Path("item")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItens() {
        try {
                        
            List<ItemCardapio> listaItemCardapio = (new ItemCardapioDAO()).getLista(true, null, null);
            
            GenericEntity<List<ItemCardapio>> 
                    lista = new GenericEntity<List<ItemCardapio>>(listaItemCardapio){};

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
    
    @Path("itemadicional")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItensAdicionais() {
        try {            
            List<ItemAdicional> listaItemAdicional = (new ItemAdicionalDAO()).getLista(true, null, null);
            
            GenericEntity<List<ItemAdicional>> 
                    lista = new GenericEntity<List<ItemAdicional>>(listaItemAdicional){};

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

    /**
     * PUT method for updating or creating an instance of CardapioWS
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }
}
