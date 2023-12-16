package controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import bean.Funcionario;
import bean.ItemPedido;
import bean.enums.SituacaoItemPedido;
import bean.enums.SituacaoPedido;
import dao.ItemPedidoDAO;
import dao.PedidoDAO;
import jakarta.annotation.ManagedBean;
import jakarta.annotation.PostConstruct;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

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
@Named("prepararItemMB")
public class PrepararItemMB extends BaseMB{

    private ItemPedidoDAO itemPedidoDAO;
    private List<ItemPedido> itensNaFila;
    private ItemPedido itemSelecionado;
    private boolean formAtivo, modoPreparoAtivo;
    private long inicioPreparo, terminoPreparo;
    @Inject
    private LoginMB loginMB;

    private Funcionario funcionario;
    /**
     * Creates a new instance of PrepararItemMB
     */
    public PrepararItemMB() {
    }
    
    @PostConstruct
    public void init(){
        itemPedidoDAO = new ItemPedidoDAO();                
        formAtivo = false;
        modoPreparoAtivo = false;
        funcionario = loginMB.getFuncionario();
        buscarItensNaFila();
        verificarPreparoEmAndamento();
    }
    
    public void buscarItensNaFila(){
        Map<String, Object> filtros = new HashMap<>();        
        filtros.put("situacao", SituacaoItemPedido.NA_FILA);        
        itensNaFila = itemPedidoDAO.getLista(filtros);         
    }
    
    public void visualizarItem(ItemPedido itemPedido){
        itemSelecionado = itemPedido;
        formAtivo = true;
    }
    
    private void verificarPreparoEmAndamento(){
        ItemPedido itemPedidoEmAndamento = itemPedidoDAO.obterPreparoEmAndamento(funcionario.getCodigo());
        
        if (itemPedidoEmAndamento != null) {
            visualizarItem(itemPedidoEmAndamento);
            inicioPreparo = itemPedidoEmAndamento.getInicioPreparo().getTime();
            modoPreparoAtivo = true;
            
        }
    }

    public void iniciarPreparo(){
        
        ItemPedido itemPedidoSituacao = itemPedidoDAO.obter(itemSelecionado.getCodigo());
        
        if (itemPedidoSituacao.getSituacao() == SituacaoItemPedido.NA_FILA) {
            itemSelecionado.setInicioPreparo(new Date(inicioPreparo));
            modoPreparoAtivo = true;
            itemSelecionado.setSituacao(SituacaoItemPedido.EM_PRODUCAO);
            itemSelecionado.setFuncionario(funcionario);
            itemPedidoDAO.alterar(itemSelecionado);
            buscarItensNaFila();   
        } else {
            enviarMensagemErro("A situação do item não permite o inicio do preparo.");
        }                
        
    }
    
    public void concluirPreparo(){
        modoPreparoAtivo = false;        
        formAtivo = false;
        itemSelecionado.setTerminoPreparo(new Date(terminoPreparo));
        itemSelecionado.setSituacao(SituacaoItemPedido.PRONTO);
        itemPedidoDAO.alterar(itemSelecionado);
        buscarItensNaFila();
        enviarMensagemInfo("Item concluído com sucesso.");
    }
    
    public void cancelarPreparo(){
        modoPreparoAtivo = false;
        itemSelecionado.setInicioPreparo(null);
        
        if ((new PedidoDAO()).obter(itemSelecionado.getPedido().getCodigo()).getSituacao() 
                == SituacaoPedido.CANCELADO) {
            itemSelecionado.setSituacao(SituacaoItemPedido.CANCELADO);
        } else {
            itemSelecionado.setSituacao(SituacaoItemPedido.NA_FILA);            
        }
        
        itemPedidoDAO.alterar(itemSelecionado);
        buscarItensNaFila();
    }
    
    public void fecharForm(){
        formAtivo = false;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public long getInicioPreparo() {
        return inicioPreparo;
    }

    public void setInicioPreparo(long inicioPreparo) {
        this.inicioPreparo = inicioPreparo;
    }

    public long getTerminoPreparo() {
        return terminoPreparo;
    }

    public void setTerminoPreparo(long terminoPreparo) {
        this.terminoPreparo = terminoPreparo;
    }
    
    public boolean isFormAtivo() {
        return formAtivo;
    }

    public boolean isModoPreparoAtivo() {
        return modoPreparoAtivo;
    }

    public void setModoPreparoAtivo(boolean modoPreparoAtivo) {
        this.modoPreparoAtivo = modoPreparoAtivo;
    }

    public void setFormAtivo(boolean formAtivo) {
        this.formAtivo = formAtivo;
    }
    
    public List<ItemPedido> getItensNaFila() {
        return itensNaFila;
    }

    public ItemPedido getItemSelecionado() {
        return itemSelecionado;
    }

    public void setItemSelecionado(ItemPedido itemSelecionado) {
        this.itemSelecionado = itemSelecionado;
    }

    public void setItensNaFila(List<ItemPedido> itensNaFila) {
        this.itensNaFila = itensNaFila;
    }        
}
