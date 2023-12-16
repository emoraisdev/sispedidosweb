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
public enum SituacaoItemCardapio {
    ATIVO(1), INATIVO(0);
    
    private int codigo;
    
    private SituacaoItemCardapio(int codigo){
        this.codigo = codigo;
    }
    
    public int getCodigo(){
        return this.codigo;
    }
    
    public static SituacaoItemCardapio converter(int codigo){        
        
        switch(codigo){
            case 0: return SituacaoItemCardapio.INATIVO;
            case 1: return SituacaoItemCardapio.ATIVO;            
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
