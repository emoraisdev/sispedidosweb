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
public enum NivelGrupo {
    NIVEL1(1), NIVEL2(2), NIVEL3(3);
    
    private int codigo;
    
    private NivelGrupo(int codigo){
        this.codigo = codigo;
    }
    
    public int getCodigo(){
        return this.codigo;
    }
    
    public String getDescricao(){
        switch(this.codigo){
            case 1: return "Nível 1";
            case 2: return "Nível 2";
            case 3: return "Nível 3";
        }
        return null;
    }
    
    public static NivelGrupo converter(int codigo){        
        
        switch(codigo){
            case 1: return NivelGrupo.NIVEL1;
            case 2: return NivelGrupo.NIVEL2;
            case 3: return NivelGrupo.NIVEL3;
        }                       
        return null;
    }
}
