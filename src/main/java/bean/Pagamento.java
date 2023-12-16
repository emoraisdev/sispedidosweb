/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import bean.enums.SituacaoPagamento;
import controller.Util;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Everton
 */
public class Pagamento implements Serializable {
 
    private int codigo;
    private BigDecimal valorPedido;
    private BigDecimal valorPago;
    private BigDecimal valorDesconto;
    private SituacaoPagamento situacao;
    private Funcionario funcionario;

    public Pagamento(){        
    }
    
    public Pagamento(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public BigDecimal getValorPedido() {
        return valorPedido;
    }

    public String getValorPedidoEmReais(){
        return Util.BigDecimalToReal(valorPedido);
    }
    
    public void setValorPedido(BigDecimal valorPedido) {
        this.valorPedido = valorPedido;
    }        

    public BigDecimal getValorPago() {
        return valorPago;
    }
    
    public String getValorPagoEmReais(){
        return Util.BigDecimalToReal(valorPago);
    }    
    
    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }
    
    public String getValorDescontoEmReais(){
        return Util.BigDecimalToReal(valorDesconto);
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public SituacaoPagamento getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoPagamento situacao) {
        this.situacao = situacao;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }
        
}
