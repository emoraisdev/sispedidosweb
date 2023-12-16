/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import jakarta.annotation.ManagedBean;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.inject.Named;

/**
 *
 * @author Everton
 */
@ManagedBean
@RequestScoped
@Named("principalMB")
public class PrincipalMB extends BaseMB{

    private String pagina;
    
    @ManagedProperty(value="#{loginMB.autenticado}")
    private boolean autenticado;
    
    /**
     * Creates a new instance of PrincipalMB
     */
    public PrincipalMB() {
    }
    
    public String redirecionar(){
        if (autenticado) {
            return pagina;
        } else {
            return "index.xhtml";
        }
    }
    
    public String verificaLogin(String teste){
        if (!autenticado) {            
            return "index.xhtml";
        } else {
            return null;
        }
    }

    public String getPagina() {
        return pagina;
    }

    public void setPagina(String pagina) {
        this.pagina = pagina;
    }    

    public boolean isAutenticado() {
        return autenticado;
    }

    public void setAutenticado(boolean autenticado) {
        this.autenticado = autenticado;
    }
    
}
