/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

import bean.Cliente;
import controller.VerificaDadosCliente;
import dao.ClienteDAO;

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
@Path("cliente")
public class ClienteWS {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ClienteWS
     */
    public ClienteWS() {
    }

    /**
     * Retrieves representation of an instance of ws.ClienteWS
     * @return an instance of java.lang.String
     */
    @GET
    @Path("/valida/{cpf}/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getCliente(@PathParam("cpf") String cpf,
                                @PathParam("login") String login) {
        
        try {
            ClienteDAO clienteDAO = new ClienteDAO();                    
            VerificaDadosCliente verificaDadosCliente = clienteDAO.ValidaDados(cpf, login);

            return Response
                    .status(Response.Status.OK)
                    .entity(verificaDadosCliente)
                    .build();
        } catch (Exception e) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }
    }
    
    @GET
    @Path("/{login}/{senha}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAutenticarCliente(@PathParam("login") String login,
                            @PathParam("senha") String senha) {
        
        try {
            ClienteDAO clienteDAO = new ClienteDAO();                    
            Cliente cliente = clienteDAO.autenticarCliente(login, senha);

            return Response
                    .status(Response.Status.OK)
                    .entity(cliente)
                    .build();
        } catch (Exception e) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * PUT method for updating or creating an instance of ClienteWS
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postCliente(Cliente cliente) {
        try {
            ClienteDAO clienteDAO = new ClienteDAO();                    
            Integer codigo = -1;
            if (cliente.getCodigo() == -1){
                cliente.setDataCadastro(new Date());
                cliente = clienteDAO.incluir(cliente);
            } else {
                clienteDAO.alterar(cliente);
            }
                        
            return Response
                    .status(Response.Status.OK)
                    .entity(cliente)
                    .build();
        } catch (Exception e) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }
        
    }    
}
