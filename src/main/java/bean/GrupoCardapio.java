/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import bean.enums.NivelGrupo;
import bean.enums.SituacaoGrupo;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Everton
 */
public class GrupoCardapio implements Serializable {
    private int codigo;
    private String nome;
    private GrupoCardapio grupoPai;
    private NivelGrupo nivel;
    private List<GrupoCardapio> gruposFilhos;
    private List<ItemCardapio> itens;
    private SituacaoGrupo situacao;

   
    public SituacaoGrupo getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoGrupo situacao) {
        this.situacao = situacao;
    }
    
    public List<GrupoCardapio> getGruposFilhos() {
        return gruposFilhos;
    }

    public void setGruposFilhos(List<GrupoCardapio> gruposFilhos) {
        this.gruposFilhos = gruposFilhos;
    }

    public List<ItemCardapio> getItens() {
        return itens;
    }

    public void setItens(List<ItemCardapio> itens) {
        this.itens = itens;
    }   
    
    public NivelGrupo getNivel() {
        return nivel;
    }

    public void setNivel(NivelGrupo nivel) {
        this.nivel = nivel;
    }        
    
    public GrupoCardapio(int codigo){
        this.codigo = codigo;
    }
    
    public GrupoCardapio(){ 
        this.codigo = -1;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public GrupoCardapio getGrupoPai() {
        return grupoPai;
    }

    public void setGrupoPai(GrupoCardapio grupoPai) {
        this.grupoPai = grupoPai;
    }        
}
