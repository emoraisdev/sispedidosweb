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
public enum SituacaoPedido {
    CANCELADO(0), PENDENTE(1), EM_PRODUCAO(2), CONCLUIDO(3);
    
    private int codigo;
    
    private SituacaoPedido(int codigo){
        this.codigo = codigo;
    }
    
    public int getCodigo(){
        return this.codigo;
    }
    
    public static SituacaoPedido converter(int codigo){        
        
        switch(codigo){
            case 0: return SituacaoPedido.CANCELADO;
            case 1: return SituacaoPedido.PENDENTE;            
            case 2: return SituacaoPedido.EM_PRODUCAO;
            case 3: return SituacaoPedido.CONCLUIDO;
        }                       
        return null;
    }
    
    public String getDescricao(){        
        
        switch(this.codigo){
            case 0: return "Cancelado";
            case 1: return "Pendente";            
            case 2: return "Em Produção";
            case 3: return "Concluído";
        }                       
        return null;
    }
}
