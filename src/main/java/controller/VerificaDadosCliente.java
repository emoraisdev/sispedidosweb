/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

/**
 *
 * @author Everton
 */
public class VerificaDadosCliente {
    private boolean CPFExistente, loginExistente;

    public boolean isCPFExistente() {
        return CPFExistente;
    }

    public void setCPFExistente(boolean CPFExistente) {
        this.CPFExistente = CPFExistente;
    }

    public boolean isLoginExistente() {
        return loginExistente;
    }

    public void setLoginExistente(boolean loginExistente) {
        this.loginExistente = loginExistente;
    }
        
}
