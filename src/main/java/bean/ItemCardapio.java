/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import bean.enums.SituacaoItemCardapio;
import controller.Util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Everton
 */

public class ItemCardapio implements Serializable {

    private int codigo;
    private String nome;    
    private String descricao;    
    private BigDecimal valor;
    private GrupoCardapio grupo;
    private SituacaoItemCardapio situacao;
    private boolean permiteAvaliacao;
    private boolean permitePreparo;
    private List<ItemAdicional> itensAdicionais;
    private String nota;

    public ItemCardapio(int codigo){        
        this.codigo = codigo;
    }    
    
    public ItemCardapio(){
        this.codigo = -1;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }
        
    public List<ItemAdicional> getItensAdicionais() {
        return itensAdicionais;
    }

    public void setItensAdicionais(List<ItemAdicional> itensAdicionais) {
        this.itensAdicionais = itensAdicionais;
    }
    
    public boolean isPermiteAvaliacao() {
        return permiteAvaliacao;
    }

    public void setPermiteAvaliacao(boolean permiteAvaliacao) {
        this.permiteAvaliacao = permiteAvaliacao;
    }

    public boolean isPermitePreparo() {
        return permitePreparo;
    }

    public void setPermitePreparo(boolean permitePreparo) {
        this.permitePreparo = permitePreparo;
    }
    
    public SituacaoItemCardapio getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoItemCardapio situacao) {
        this.situacao = situacao;
    }        
    


    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String Descricao) {
        this.descricao = Descricao;
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

    public BigDecimal getValor() {
        return valor;
    }
    
    public String getValorEmReais() {
        return Util.BigDecimalToReal(valor);
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public GrupoCardapio getGrupo() {
        return grupo;
    }

    public void setGrupo(GrupoCardapio grupo) {
        this.grupo = grupo;
    }
}
