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
public enum SituacaoUsuario {
    ATIVO(1), INATIVO(0);
    
    private int codigo;
    
    private SituacaoUsuario(int codigo){
        this.codigo = codigo;
    }
    
    public int getCodigo(){
        return this.codigo;
    }
    
    public static SituacaoUsuario converter(int codigo){        
        
        switch(codigo){
            case 0: return SituacaoUsuario.INATIVO;
            case 1: return SituacaoUsuario.ATIVO;            
        }                       
        return null;
    }
    
    public String getDescricao(){        
        
        switch(this.codigo){
            case 0: return "Inativo";
            case 1: return "Ativo";            
        }                       
        return null;
    }
}
