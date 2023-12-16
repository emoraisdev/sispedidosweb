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
public enum SituacaoItemPedido {
    PENDENTE(0), NA_FILA(1), EM_PRODUCAO(2), PRONTO(3), CANCELADO(4);
    
    private int codigo;
    
    private SituacaoItemPedido(int codigo){
        this.codigo = codigo;
    }
    
    public int getCodigo(){
        return this.codigo;
    }
    
    public static SituacaoItemPedido converter(int codigo){        
        
        switch(codigo){
            case 0: return SituacaoItemPedido.PENDENTE;
            case 1: return SituacaoItemPedido.NA_FILA;            
            case 2: return SituacaoItemPedido.EM_PRODUCAO;
            case 3: return SituacaoItemPedido.PRONTO;
            case 4: return SituacaoItemPedido.CANCELADO;
        }                       
        return null;
    }
    
    public String getDescricao(){        
        
        switch(this.codigo){
            case 0: return "Pendente";
            case 1: return "Na Fila";            
            case 2: return "Em Produção";
            case 3: return "Pronto";
            case 4: return "Cancelado";
        }                       
        return null;
    }
}
