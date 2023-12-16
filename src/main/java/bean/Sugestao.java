/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import bean.enums.TipoSugestao;
import controller.Util;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Everton
 */
public class Sugestao implements Serializable {
    
    private int codigo;
    private String mensagem;
    private TipoSugestao tipo;
    private Cliente cliente;
    private Date dataHora;

    public Date getDataHora() {
        return dataHora;
    }

    public String getDataHoraFormatada() {
        return Util.FormatarData(dataHora);
    }
    
    public void setDataHora(Date dataHora) {
        this.dataHora = dataHora;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public TipoSugestao getTipo() {
        return tipo;
    }

    public void setTipo(TipoSugestao tipo) {
        this.tipo = tipo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
}
