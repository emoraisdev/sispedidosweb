/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

import bean.Sugestao;
import dao.SugestaoDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Date;

/**
 * REST Web Service
 *
 * @author Everton
 */
@Path("sugestao")
public class SugestaoWS {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of SugestaoWS
     */
    public SugestaoWS() {
    }

    /**
     * Retrieves representation of an instance of ws.SugestaoWS
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of SugestaoWS
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postCliente(Sugestao sugestao) {
        try {
            SugestaoDAO sugestaoDAO = new SugestaoDAO();
            sugestao.setDataHora(new Date());
            sugestaoDAO.incluir(sugestao);
           
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
