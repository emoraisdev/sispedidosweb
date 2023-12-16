/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import bean.Entrada;
import dao.EntradaDAO;
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
@Named("clienteRegistradoMB")
@ViewScoped
public class ClienteRegistradoMB extends BaseMB{

    private String login, nome;
    private List<Entrada> registrosEntrada;
    private EntradaDAO entradaDAO;
    private Entrada entrada;
    private boolean dadosCliente;
    
    /**
     * Creates a new instance of ClienteRegistradoMB
     */
    public ClienteRegistradoMB() {
    }


    @PostConstruct
    public void init(){
        entradaDAO = new EntradaDAO();
        login = null;
        nome = null;
        entrada = null;
        dadosCliente = false;
        startIndex = BigDecimal.ZERO;
        buscarClientesRegistrados(0);
        msgRetornoBusca = "";
    }
    
    public void buscarClientesRegistrados(Integer index){
        startIndex = new BigDecimal(index);
        
        Map<String, Object> filtros = new HashMap();
        
        filtros.put("login", login);
        filtros.put("nome", nome);
        filtros.put("startIndex", this.startIndex
                .multiply(pageSize).intValue());
        filtros.put("pageSize", this.pageSize.intValue());
                
        definirQtdPaginas(entradaDAO.getQtdRegistrosValidos(filtros));         
        registrosEntrada = entradaDAO.obterRegistrosValidos(filtros);
        
        definirMsgRetornoBusca(registrosEntrada.size());
    }
    
    public void visualizarCliente(Entrada entrada){
        this.entrada = entrada;
        dadosCliente = true;        
    }
    
    public void fecharDadosCliente(){
        dadosCliente = false;
    }

    public Entrada getEntrada() {
        return entrada;
    }

    public void setEntrada(Entrada entrada) {
        this.entrada = entrada;
    }

    public boolean isDadosCliente() {
        return dadosCliente;
    }

    public void setDadosCliente(boolean dadosCliente) {
        this.dadosCliente = dadosCliente;
    }

    public List<Entrada> getRegistrosEntrada() {
        return registrosEntrada;
    }

    public void setRegistrosEntrada(List<Entrada> registrosEntrada) {
        this.registrosEntrada = registrosEntrada;
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