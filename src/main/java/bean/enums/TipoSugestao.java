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
public enum TipoSugestao {
    ELOGIO(0),SUGESTAO(1),CRITICA(2);
    
    private int codigo;
    
    private TipoSugestao(int codigo){
        this.codigo = codigo;
    }
    
    public int getCodigo(){
        return this.codigo;
    }
    
    public String getDescricao(){
        switch(this.codigo){
            case 0: return "Elogio";
            case 1: return "Sugestão";
            case 2: return "Crítica";
        }
        return null;
    }
    
    public static TipoSugestao converter(int codigo){        
        
        switch(codigo){
            case 0: return TipoSugestao.ELOGIO;
            case 1: return TipoSugestao.SUGESTAO;
            case 2: return TipoSugestao.CRITICA;
        }                       
        return null;
    }
}
