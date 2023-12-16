/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

import bean.Pedido;
import dao.PedidoDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

/**
 * REST Web Service
 *
 * @author Everton
 */
@Path("pedido")
public class PedidoWS {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of PedidoWS
     */
    public PedidoWS() {
    }

    /**
     * Retrieves representation of an instance of ws.PedidoWS
     * @return an instance of java.lang.String
     */

    @GET
    @Path("/lista/{codcliente}/{ultimosdias}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPedidos(@PathParam("codcliente") Integer codCliente,
                                @PathParam("ultimosdias") String ultimosDias) {
        
        try {
            PedidoDAO pedidoDAO = new PedidoDAO();                        
            
            Map<String, Object> filtros = new HashMap<>();                  
            filtros.put("codCliente", codCliente);
            
            if (!ultimosDias.equals("todo")) {
                
                try {                                        
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(ultimosDias)));                                                                       
                                        
                    filtros.put("periodoDe",calendar.getTime());
                } catch (Exception ex) {
                    throw new RuntimeException("Não foi possível converter a data.");
                }
                
            }
            
            List<Pedido> listaPedido = pedidoDAO.getLista(filtros,false);
            
            GenericEntity<List<Pedido>> 
                    lista = new GenericEntity<List<Pedido>>(listaPedido){};

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
    
    @GET
    @Path("/{codpedido}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPedido(@PathParam("codpedido") Integer codPedido) {
        
        try {
            PedidoDAO pedidoDAO = new PedidoDAO();            
            Pedido pedido = pedidoDAO.obter(codPedido);            

            return Response
                    .status(Response.Status.OK)
                    .entity(pedido)
                    .build();            
        } catch (Exception e) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }
    }
    
    /**
     * PUT method for updating or creating an instance of PedidoWS
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postPedido(Pedido pedido) {
        try {
            pedido.setDataHora(new Date());
            (new PedidoDAO()).incluir(pedido);
           
            return Response
                    .status(Response.Status.OK)
                    .entity("OK")
                    .build();
        } catch (Exception e) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }        
    }
}
