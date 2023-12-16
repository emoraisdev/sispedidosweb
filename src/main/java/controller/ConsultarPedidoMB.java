/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import bean.ItemPedido;
import bean.Notificacao;
import bean.Pedido;
import bean.enums.SituacaoItemPedido;
import bean.enums.SituacaoPedido;
import dao.ItemPedidoDAO;
import dao.NotificacaoDAO;
import dao.PedidoDAO;
import jakarta.annotation.ManagedBean;
import jakarta.annotation.PostConstruct;
import jakarta.faces.model.SelectItem;
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
@Named("consultarPedidoMB")
public class ConsultarPedidoMB extends BaseMB{

    private List<Pedido> pedidos;
    private PedidoDAO pedidoDAO;
    private Pedido pedido;
    private boolean formPedidoAtivo, conclusaoPedido, notificacaoAtiva, permiteAlteracao;
    private Date periodoDe, periodoAte;
    private Integer codPedido, mesa;
    private SituacaoPedido situacaoPedido;
    private ItemPedidoDAO itemPedidoDAO;
    private NotificacaoDAO notificacaoDAO;
    /**
     * Creates a new instance of ConsultarPedidoMB
     */
    public ConsultarPedidoMB() {
    }

    @PostConstruct
    public void init(){
        this.pedidoDAO = new PedidoDAO();
        this.itemPedidoDAO = new ItemPedidoDAO();
        this.notificacaoDAO = new NotificacaoDAO();
        this.formPedidoAtivo = false;
        this.situacaoPedido = null;
        this.periodoDe = new Date();
        this.periodoAte = new Date();
        this.startIndex = BigDecimal.ZERO;
        buscarPedidos(0);
        msgRetornoBusca = "";
    }
    
    public void buscarPedidos(Integer index){        
        this.startIndex = new BigDecimal(index);
        
        Map<String, Object> filtros = new HashMap<>();
        
        filtros.put("periodoDe", this.periodoDe);
        filtros.put("periodoAte", this.periodoAte);
        filtros.put("codPedido", this.codPedido);
        filtros.put("mesa", this.mesa);
        filtros.put("situacao", this.situacaoPedido);
        filtros.put("startIndex", this.startIndex
                .multiply(pageSize).intValue());
        filtros.put("pageSize", this.pageSize.intValue());
                
        definirQtdPaginas(pedidoDAO.getQtdRegistrosLista(filtros));        
        this.pedidos = pedidoDAO.getLista(filtros);             
        
        definirMsgRetornoBusca(this.pedidos.size());
    }
    
    public void visualizarPedido(Pedido pedido){        
        this.pedido = pedido;
        if (pedido.getSituacao() == SituacaoPedido.CONCLUIDO
                || pedido.getSituacao() == SituacaoPedido.CANCELADO) {
            permiteAlteracao = true;
        } else{
            permiteAlteracao = false;
            verificarConclusaoPedido();
            notificacaoAtiva = (pedido.getCliente().getCodigo() > 0);            
        }
        this.formPedidoAtivo = true;
        
    }
    
    private void verificarConclusaoPedido(){
        this.conclusaoPedido = true;
        for (ItemPedido itemPedido: this.pedido.getItens()) {
            if (itemPedido.getSituacao() != SituacaoItemPedido.PRONTO) {
                this.conclusaoPedido = false;
            }            
        }
    }
    
    public void cancelarPedido(){              
        try {                         
            this.pedido.setSituacao(SituacaoPedido.CANCELADO);
            
            for (ItemPedido itemPedido: pedido.getItens()){
                if (itemPedido.getSituacao() != SituacaoItemPedido.EM_PRODUCAO
                        && itemPedido.getSituacao() != SituacaoItemPedido.PRONTO) {
                    itemPedido.setSituacao(SituacaoItemPedido.CANCELADO);
                    itemPedidoDAO.alterar(itemPedido);
                }
            }
            
            this.pedidoDAO.alterar(this.pedido);
            this.formPedidoAtivo = false;
            buscarPedidos(0);
            enviarMensagemInfo("Pedido cancelado com sucesso.");     
        } catch (Exception e){
            enviarMensagemErro(e.getMessage());
        }
    }      
    
    public void concluirPedido(Integer tipoConclusao) {
        try {                                                         
            this.pedido.setSituacao(SituacaoPedido.CONCLUIDO);
            this.pedidoDAO.alterar(this.pedido);
            
            if (tipoConclusao != null){
                Notificacao notificacao = new Notificacao();
                notificacao.setCliente(pedido.getCliente());
                notificacao.setDataHora(new Date());
                
                String mensagem = (tipoConclusao == 1)? 
                        String.format("O pedido número %1$d está pronto e será entregue em instantes.",pedido.getCodigo()):
                        String.format("O pedido número %1$d está pronto, retire o pedido no balcão.",pedido.getCodigo());
                notificacao.setMensagem(mensagem);
                
                notificacaoDAO.incluir(notificacao);                
            }
            
            this.formPedidoAtivo = false;
            buscarPedidos(0);
            enviarMensagemInfo("Pedido concluído com sucesso.");            
        } catch (Exception e){
            enviarMensagemErro(e.getMessage());
        }
    }
    
    public void solicitarPreparo(ItemPedido itemPedido){
        itemPedido.setSituacao(SituacaoItemPedido.NA_FILA);
        pedido.setSituacao(SituacaoPedido.EM_PRODUCAO);
        pedidoDAO.alterar(this.pedido);
        itemPedidoDAO.alterar(itemPedido);
    }
    
    public void indicarItemComoPronto(ItemPedido itemPedido) {
        itemPedido.setSituacao(SituacaoItemPedido.PRONTO);
        itemPedidoDAO.alterar(itemPedido);
        verificarConclusaoPedido();
    }   
    
    public void fecharPedido(){
        this.pedido = null;
        this.formPedidoAtivo = false;
        buscarPedidos(0);
    }  
    
    public SelectItem[] getSituacoes(){
        SelectItem[] items = new SelectItem[SituacaoPedido.values().length];
        int i = 0;
        for(SituacaoPedido s: SituacaoPedido.values()) {
          items[i++] = new SelectItem(s, s.getDescricao());
        }
        return items;      
    }

    public boolean isPermiteAlteracao() {
        return permiteAlteracao;
    }

    public void setPermiteAlteracao(boolean permiteAlteracao) {
        this.permiteAlteracao = permiteAlteracao;
    }

    public boolean isNotificacaoAtiva() {
        return notificacaoAtiva;
    }

    public void setNotificacaoAtiva(boolean notificacaoAtiva) {
        this.notificacaoAtiva = notificacaoAtiva;
    }

    public boolean isConclusaoPedido() {
        return conclusaoPedido;
    }

    public void setConclusaoPedido(boolean conclusaoPedido) {
        this.conclusaoPedido = conclusaoPedido;
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

    public Integer getCodPedido() {
        return codPedido;
    }

    public void setCodPedido(Integer codPedido) {
        this.codPedido = codPedido;
    }

    public Integer getMesa() {
        return mesa;
    }

    public void setMesa(Integer mesa) {
        this.mesa = mesa;
    }

    public SituacaoPedido getSituacaoPedido() {
        return situacaoPedido;
    }

    public void setSituacaoPedido(SituacaoPedido situacaoPedido) {
        this.situacaoPedido = situacaoPedido;
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

    public boolean isFormPedidoAtivo() {
        return formPedidoAtivo;
    }

    public void setFormPedidoAtivo(boolean formPedidoAtivo) {
        this.formPedidoAtivo = formPedidoAtivo;
    }    
}
