/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import bean.Cliente;
import dao.ClienteDAO;
import jakarta.annotation.ManagedBean;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Everton
 */
@ManagedBean
@Named("clienteMB")
@ViewScoped
public class ClienteMB extends BaseMB {
    
    private String login, nome;
    private List<Cliente> clientes;
    private ClienteDAO clienteDAO;
    private Cliente cliente;
    private boolean dadosCliente;

    /**
     * Creates a new instance of ClienteMB
     */
    public ClienteMB() {
    }

    @PostConstruct
    public void init(){
        clienteDAO = new ClienteDAO();
        login = null;
        nome = null;
        cliente = null;
        dadosCliente = false;
        this.startIndex = BigDecimal.ZERO;
        buscarClientes(0);
        msgRetornoBusca = "";
    }
    
    public void buscarClientes(Integer index){    
        this.startIndex = new BigDecimal(index);
        
        Map<String, Object> filtros = new HashMap<>();
        
        filtros.put("login", login);
        filtros.put("nome", nome);
        filtros.put("startIndex", this.startIndex
                .multiply(pageSize).intValue());
        filtros.put("pageSize", this.pageSize.intValue());
                
        definirQtdPaginas(clienteDAO.getQtdRegistrosLista(filtros));         
        clientes = clienteDAO.getLista(filtros);
        
        definirMsgRetornoBusca(clientes.size());
    }
    
    public void visualizarCliente(Cliente cliente){
        this.cliente = cliente;
        dadosCliente = true;        
    }
    
    public void fecharDadosCliente(){
        dadosCliente = false;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public boolean isDadosCliente() {
        return dadosCliente;
    }

    public void setDadosCliente(boolean dadosCliente) {
        this.dadosCliente = dadosCliente;
    }
    
    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    
}
