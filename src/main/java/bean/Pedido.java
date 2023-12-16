/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import bean.enums.SituacaoItemPedido;
import bean.enums.SituacaoPedido;
import controller.Util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Everton
 */
public class Pedido implements Serializable {
    private int codigo;
    private Date dataHora;
    private BigDecimal valor;  
    private String observacao;
    private List<ItemPedido> itens;
    private SituacaoPedido situacao;
    private Integer mesa;
    private Funcionario funcionario;
    private Cliente cliente;    
    private Entrada entrada;
    private Pagamento pagamento;

    public String getSituacaoItens(){
        String situacaoItens = "Pronto";
        for (ItemPedido item: itens){
            if (item.getSituacao() != SituacaoItemPedido.PRONTO) {
                situacaoItens = "Pendente";
                if (item.getSituacao() == SituacaoItemPedido.CANCELADO) {
                    situacaoItens = "Cancelado";
                    break;
                }
            }
        }
        return situacaoItens;
    } 

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }        
    
    public Entrada getEntrada() {
        return entrada;
    }

    public void setEntrada(Entrada entrada) {
        this.entrada = entrada;
    }

    public Pedido(Integer codigo) {
        this.codigo = codigo;
    }  
    
    public Pedido() {
        this.valor = BigDecimal.ZERO;
    }       

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }        

    public Integer getMesa() {
        return mesa;
    }

    public void setMesa(Integer mesa) {
        this.mesa = mesa;
    }
        
    public SituacaoPedido getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoPedido situacao) {
        this.situacao = situacao;
    }
        
    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public Date getDataHora() {
        return dataHora;
    }
    
    public String getDataHoraFormatada() {
        return Util.FormatarData(dataHora);
    }

    public void setDataHora(Date dataHora) {
        this.dataHora = dataHora;
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

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }            
}
