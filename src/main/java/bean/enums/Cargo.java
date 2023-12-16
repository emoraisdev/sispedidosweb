/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.enums;

/**
 *
 * @author Everton
 */
public enum Cargo {
    ATENDENTE(1),COZINHEIRO(2),GERENTE(3);
    
    private int codigo;
    
    private Cargo(int codigo){
        this.codigo = codigo;
    }
    
    public int getCodigo(){
        return this.codigo;
    }
    
    public String getDescricao(){
        switch(this.codigo){
            case 1: return "Atendente";
            case 2: return "Cozinheiro";
            case 3: return "Gerente";
        }
        return null;
    }
    
    public static Cargo converter(int codigo){        
        
        switch(codigo){
            case 1: return Cargo.ATENDENTE;
            case 2: return Cargo.COZINHEIRO;
            case 3: return Cargo.GERENTE;
        }                       
        return null;
    }
}
