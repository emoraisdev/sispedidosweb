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
public enum SituacaoPagamento {
    ABERTO(0), PAGO(1);
    
    private int codigo;
    
    private SituacaoPagamento(int codigo){
        this.codigo = codigo;
    }
    
    public int getCodigo(){
        return this.codigo;
    }
    
    public static SituacaoPagamento converter(int codigo){        
        
        switch(codigo){
            case 0: return SituacaoPagamento.ABERTO;
            case 1: return SituacaoPagamento.PAGO;            
        }                       
        return null;
    }
    
    public String getDescricao(){        
        
        switch(this.codigo){
            case 0: return "Aberto";
            case 1: return "Pago";            
        }                       
        return null;
    }    
}
