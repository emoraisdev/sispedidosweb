/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import bean.Funcionario;
import bean.enums.Cargo;
import dao.FuncionarioDAO;
import jakarta.annotation.ManagedBean;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.Serializable;

/**
 *
 * @author Everton
 */
@ManagedBean
@Named("loginMB")
@SessionScoped
public class LoginMB implements Serializable{

    private Funcionario funcionario;
    private FuncionarioDAO funcDAO;
    private boolean autenticado;
    private String login;
    private String senha;
    private boolean admin, principal, cardapio, pedidos,
            preparo, pagamentos, clientes, relatorios, funcionarios;
    
    public LoginMB() {
    }
    
    @PostConstruct
    private void init(){
        funcDAO = new FuncionarioDAO();
        autenticado = false;
        funcionario = null;
        login = "";
        senha = "";
        admin = false;        
        principal = false;
        cardapio = false;
        pedidos = false;
        preparo = false;
        pagamentos = false;
        clientes = false;
        relatorios = false;
        funcionarios = false;
    }
    
    public String logar(){
        FacesContext context = FacesContext.getCurrentInstance();
        
        try {                         
            funcionario = this.funcDAO.autenticarFuncionario(login, 
                    Util.converterParaMD5(senha));
            autenticado = true;
            definirPermissoes();
            if (funcionario.getCargo().equals(Cargo.COZINHEIRO) && !funcionario.isAdministrador()) {
                return "prepararItens";
            } else {                
                return "gerarCodigo";
            }
        } catch (Exception e){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
        return null;
    }
    
    public String verificaLogin(String teste){
        if (!autenticado) {            
            return "index.xhtml";
        } else {
            switch (teste){
                case "principal": if (!principal){return "naoPermitido";}
                    break;
                case "funcionarios": if (!funcionarios){return "naoPermitido";}
                    break;
                case "cardapio": if (!cardapio){return "naoPermitido";}
                    break;
                case "pedidos": if (!pedidos){return "naoPermitido";}
                    break;
                case "preparo": if (!preparo){return "naoPermitido";}
                    break;
                case "pagamentos": if (!pagamentos){return "naoPermitido";}
                    break;
                case "clientes": if (!clientes){return "naoPermitido";}
                    break;
                case "relatorios": if (!relatorios){return "naoPermitido";}
                    break;
            }
            return null;
        }
    }
    
    public String sair(){
        init();
        return "index";
    }

    public void definirPermissoes(){                
        if (funcionario.getCargo().equals(Cargo.GERENTE) || funcionario.isAdministrador()) {
            funcionarios = true;
            principal = true;
            cardapio = true;
            pedidos = true;
            preparo = true;
            pagamentos = true;
            clientes = true;
            relatorios = true; 
        } else if (funcionario.getCargo().equals(Cargo.COZINHEIRO)){
            preparo = true;
        } else if (funcionario.getCargo().equals(Cargo.ATENDENTE)){
            principal = true;
            pedidos = true;
            pagamentos = true;
        }           
        
        admin = funcionario.isAdministrador();
    }

    public boolean isFuncionarios() {
        return funcionarios;
    }

    public void setFuncionarios(boolean funcionarios) {
        this.funcionarios = funcionarios;
    }
    
    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }

    public boolean isCardapio() {
        return cardapio;
    }

    public void setCardapio(boolean cardapio) {
        this.cardapio = cardapio;
    }

    public boolean isPedidos() {
        return pedidos;
    }

    public void setPedidos(boolean pedidos) {
        this.pedidos = pedidos;
    }

    public boolean isPreparo() {
        return preparo;
    }

    public void setPreparo(boolean preparo) {
        this.preparo = preparo;
    }

    public boolean isPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(boolean pagamentos) {
        this.pagamentos = pagamentos;
    }

    public boolean isClientes() {
        return clientes;
    }

    public void setClientes(boolean clientes) {
        this.clientes = clientes;
    }

    public boolean isRelatorios() {
        return relatorios;
    }

    public void setRelatorios(boolean relatorios) {
        this.relatorios = relatorios;
    }


    public boolean isAutenticado() {
        return autenticado;
    }

    public void setAutenticado(boolean autenticado) {
        this.autenticado = autenticado;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }    
}
