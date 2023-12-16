/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import bean.Funcionario;
import bean.Pagamento;
import bean.Pedido;
import bean.enums.SituacaoPagamento;
import bean.enums.SituacaoPedido;
import dao.PedidoDAO;
import jakarta.annotation.ManagedBean;
import jakarta.annotation.PostConstruct;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Everton
 */
@ManagedBean
@ViewScoped
@Named("registrarPagamentoMB")
public class RegistrarPagamentoMB extends BaseMB{
    
    private List<Pedido> pedidos;
    private PedidoDAO pedidoDAO;
    private Pedido pedido;
    private Pagamento pagamento;
    private boolean formAtivo, modoDescontoAtivo;
    private Integer mesa, codPedido;
    private BigDecimal percentual, valorDesconto;

    @Inject
    private LoginMB loginMB;

    private Funcionario funcionario;

    /**
     * Creates a new instance of RegistrarPagamentoMB
     */
    public RegistrarPagamentoMB() {        
    }
    
    @PostConstruct
    public void init(){
        pedidoDAO = new PedidoDAO();        
        formAtivo = false;
        modoDescontoAtivo =false;
        mesa = null;
        codPedido = null;
        this.funcionario = loginMB.getFuncionario();
        this.startIndex = BigDecimal.ZERO;
        buscarPedidos(0);
        msgRetornoBusca = "";
    }
    
    public void buscarPedidos(Integer index){
        this.startIndex = new BigDecimal(index);
                
        Map<String, Object> filtros = new HashMap<>();        
        
        filtros.put("possuiPagamento", false); 
        filtros.put("codPedido", codPedido); 
        filtros.put("mesa", mesa);
        filtros.put("situacao", SituacaoPedido.CONCLUIDO);        
        filtros.put("startIndex", this.startIndex
                .multiply(pageSize).intValue());
        filtros.put("pageSize", this.pageSize.intValue());
                
        definirQtdPaginas(pedidoDAO.getQtdRegistrosLista(filtros));
        pedidos = pedidoDAO.getLista(filtros);
        
        definirMsgRetornoBusca(this.pedidos.size());
    }

    public void visualizarPedido(Pedido pedido){
        
        this.pedido = pedido;        
        this.pagamento  = new Pagamento(-1);        
        pagamento.setSituacao(SituacaoPagamento.ABERTO);
        pagamento.setValorPedido(pedido.getValor());
        pagamento.setValorPago(pedido.getValor());
        pagamento.setValorDesconto(BigDecimal.ZERO);
        pagamento.setSituacao(SituacaoPagamento.PAGO);
        pagamento.setFuncionario(funcionario);

        formAtivo = true;
    }

    public void realizarPagamento(){
        try {                         
            pedido.setPagamento(pagamento);
            pedidoDAO.alterar(pedido);
            enviarMensagemInfo("Pagamento registrado com sucesso.");     
            init();
        } catch (Exception e){
            enviarMensagemErro(e.getMessage());
        }
    }

    public void fecharPagamento(){
        formAtivo = false;
        modoDescontoAtivo =false;
    }
    
    public void aplicarDesconto(){
    
        try {
            if ((percentual == null || percentual.compareTo(BigDecimal.ZERO) <= 0) && 
                    (valorDesconto == null || valorDesconto.compareTo(BigDecimal.ZERO) <= 0) ) {
                throw new Exception("Para aplicação do desconto o campo Percentual ou Valor deve estar preenchido"
                        + " e ser maior que zero.");
            }
            
            if ((percentual != null && percentual.compareTo(BigDecimal.ZERO) > 0) && 
                    (valorDesconto != null && valorDesconto.compareTo(BigDecimal.ZERO) > 0)) {
               throw new Exception("Apenas um dos campos deve estar preenchido para aplicação do desconto.");
            }
            
            if (percentual != null && percentual.compareTo(BigDecimal.ZERO) > 0) {
                if (percentual.compareTo(new BigDecimal("100")) > 0) {
                    throw new Exception("O percentual não pode ser maior que 100.");
                }
                
                pagamento.setValorDesconto(pagamento.getValorPedido().
                        multiply(percentual).divide(new BigDecimal("100")).setScale(2,RoundingMode.FLOOR));                
                pagamento.setValorPago(pagamento.getValorPedido().subtract(pagamento.getValorDesconto()));                            
            }
        
            if (valorDesconto != null && valorDesconto.compareTo(BigDecimal.ZERO) > 0) {
                if (valorDesconto.compareTo(pagamento.getValorPedido()) > 0) {
                    throw new Exception("O valor do desconto não pode ser maior que o valor do pedido.");
                }                
                
                pagamento.setValorDesconto(valorDesconto);
                pagamento.setValorPago(pagamento.getValorPedido().subtract(valorDesconto));                            
            }            
            
            modoDescontoAtivo = false;
            enviarMensagemInfo("Desconto aplicado com sucesso.");
        } catch (Exception e) {
            enviarMensagemErro(e.getMessage());
        }                                       
    }
    
    public void cancelarAplicacaoDesconto(){
        modoDescontoAtivo = false;
    }
    
    public void habilitarDesconto(){
        percentual = null;
        valorDesconto = null;
        modoDescontoAtivo = true;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
        
    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public boolean isModoDescontoAtivo() {
        return modoDescontoAtivo;
    }

    public void setModoDescontoAtivo(boolean modoDescontoAtivo) {
        this.modoDescontoAtivo = modoDescontoAtivo;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }

    public boolean isFormAtivo() {
        return formAtivo;
    }

    public void setFormAtivo(boolean formAtivo) {
        this.formAtivo = formAtivo;
    }
    
    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public Integer getMesa() {
        return mesa;
    }

    public void setMesa(Integer mesa) {
        this.mesa = mesa;
    }

    public Integer getCodPedido() {
        return codPedido;
    }

    public void setCodPedido(Integer codPedido) {
        this.codPedido = codPedido;
    }        
    
    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }
}
