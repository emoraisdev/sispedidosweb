/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

import bean.Cliente;
import bean.CodigoEntrada;
import bean.Entrada;
import dao.CodigoEntradaDAO;
import dao.EntradaDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Calendar;
import java.util.Date;

/**
 * REST Web Service
 *
 * @author Everton
 */
@Path("entrada")
public class EntradaWS {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of EntradaWS
     */
    public EntradaWS() {
    }

    /**
     * Retrieves representation of an instance of ws.EntradaWS
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of EntradaWS
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }
    
    @GET    
    @Path("/{codcliente}/{codentrada}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getEntrada(@PathParam("codcliente") Integer codCliente,
                                @PathParam("codentrada") String codEntrada) {
        try {
            boolean codigoRegistrado = false;
            EntradaDAO entradaDAO = new EntradaDAO();
            CodigoEntrada codigoEntrada = (new CodigoEntradaDAO()).obterValido(codEntrada);
            
            if (codigoEntrada != null) {                
                codigoRegistrado = entradaDAO.verificaSeExiste(codigoEntrada.getCodigo());
            }
            
            if ((codigoEntrada == null) ||codigoRegistrado){
                throw new Exception("Código Inválido.");
            }
            
            Entrada entrada = new Entrada();
            entrada.setCodigoEntrada(codigoEntrada);
            entrada.setCliente(new Cliente(codCliente));
            entrada.setDataEntrada(new Date());
            //Adiciona 3 horas
            entrada.setDataSaida(new Date(Calendar
                    .getInstance()
                    .getTimeInMillis() + (3 * 60 * 60000))); //60000 = 1 minuto in milissegundos


            entrada = entradaDAO.incluir(entrada);            
           
            return Response
                    .ok(entrada)                    
                    .build();
        } catch (Exception e) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }        
    }
    
    @GET    
    @Path("/{codcliente}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response verificarRegistroEntrada(@PathParam("codcliente") Integer codCliente) {
        try {
            
            Entrada entrada = (new EntradaDAO()).obterEntradaValida(codCliente);

            if (entrada == null) {
                throw new Exception("");
            }
            
            return Response
                    .ok(entrada)                    
                    .build();
        } catch (Exception e) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }        
    }
}
