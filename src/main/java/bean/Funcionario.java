package bean;

import bean.enums.Cargo;

import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Everton
 */
public class Funcionario extends Usuario implements Serializable {
    private Cargo cargo;
    private boolean administrador;
    
    public Funcionario (){
        
    }
    
    public Funcionario(int codigo){
        super(codigo);
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public boolean isAdministrador() {
        return administrador;
    }

    public void setAdministrador(boolean administrador) {
        this.administrador = administrador;
    }        
}
