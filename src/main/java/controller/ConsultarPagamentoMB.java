/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import bean.Pedido;
import dao.PedidoDAO;
import jakarta.annotation.ManagedBean;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Everton
 */
@ManagedBean
@ViewScoped
@Named("consultarPagamentoMB")
public class ConsultarPagamentoMB extends BaseMB {

    private List<Pedido> pedidos;
    private PedidoDAO pedidoDAO;
    private Pedido pedido;
    private boolean formAtivo;
    private Date periodoDe, periodoAte;
    /**
     * Creates a new instance of consultarPagamentoMB
     */
    public ConsultarPagamentoMB() {
    }
    
    @PostConstruct
    public void init(){
        pedidoDAO = new PedidoDAO();
        formAtivo = false;
        periodoDe = new Date();
        periodoAte = new Date();
        this.startIndex = BigDecimal.ZERO;
        buscarPagamentos(0);
        msgRetornoBusca = "";
    }

    public void buscarPagamentos(Integer index){
        this.startIndex = new BigDecimal(index);
        Map<String, Object> filtros = new HashMap<>();
        
        filtros.put("periodoDe", this.periodoDe);
        filtros.put("periodoAte", this.periodoAte);
        filtros.put("possuiPagamento", true);
        filtros.put("startIndex", this.startIndex
                .multiply(pageSize).intValue());
        filtros.put("pageSize", this.pageSize.intValue());
                
        definirQtdPaginas(pedidoDAO.getQtdRegistrosLista(filtros));       
        this.pedidos = pedidoDAO.getLista(filtros);                
        
        definirMsgRetornoBusca(pedidos.size());
   }
    
    public void fecharPagamento(){
        formAtivo = false;
    }
    
    public void visualizarPagamento(Pedido pedido){
        this.pedido = pedido;
        formAtivo = true;
    }

    public Date getPeriodoDe() {
        return periodoDe;
    }

    public void setPeriodoDe(Date periodoDe) {
        this.periodoDe = periodoDe;
    }

    public Date getPeriodoAte() {
        return periodoAte;
    }

    public void setPeriodoAte(Date periodoAte) {
        this.periodoAte = periodoAte;
    }    

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public boolean isFormAtivo() {
        return formAtivo;
    }

    public void setFormAtivo(boolean formAtivo) {
        this.formAtivo = formAtivo;
    }    
}
