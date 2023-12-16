/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import bean.enums.SituacaoItemAdicional;
import controller.Util;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Everton
 */
public class ItemAdicional implements Serializable {
    
    private int codigo;
    private String nome;
    private BigDecimal valor;
    private SituacaoItemAdicional situacao;
    
    public ItemAdicional(){
        this.codigo = -1;
    }
    
    public ItemAdicional(int codigo){
        this.codigo = codigo;
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

    public SituacaoItemAdicional getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoItemAdicional situacao) {
        this.situacao = situacao;
    }
    
    
}
