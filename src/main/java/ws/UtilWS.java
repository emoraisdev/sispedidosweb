/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

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
@Path("util")
public class UtilWS {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of UtilWS
     */
    public UtilWS() {
    }

    /**
     * Retrieves representation of an instance of ws.UtilWS
     * @return an instance of java.lang.String
     */
    @GET
    @Path("/datetime")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getDateTime() {
        try {           
            
            ServerDate sd = new ServerDate();
            sd.setData(new Date());
            
            return Response
                    .ok(sd)
                    .build();            
        } catch (Exception e) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * PUT method for updating or creating an instance of UtilWS
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }
}
