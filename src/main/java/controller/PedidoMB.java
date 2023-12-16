/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import bean.*;
import bean.enums.SituacaoItemPedido;
import bean.enums.SituacaoPedido;
import dao.AvaliacaoDAO;
import dao.ItemAdicionalDAO;
import dao.PedidoDAO;
import jakarta.annotation.ManagedBean;
import jakarta.annotation.PostConstruct;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Everton
 */
@ManagedBean
@ViewScoped
@Named("pedidoMB")
public class PedidoMB extends BaseMB{
    private Pedido pedido;
    private boolean formItemAtivo, formPedidoAtivo, formAvaliacaoAtivo;
    private ItemPedido itemPedido;
    private PedidoDAO pedidoDAO;    
    private int codItemAdicionalSelecionado;
    private ItemAdicionalDAO itemAdicionalDAO;
    private List<Avaliacao> listaAvaliacao;
    private ItemCardapio itemCardapioAvaliacao;
    private AvaliacaoDAO avaliacaoDAO;

    @Inject
    private LoginMB loginMB;
    private Funcionario funcionario;

    /**
     * Creates a new instance of PedidoMB
     */
    public PedidoMB() {
    }
    
    @PostConstruct
    public void init(){
        this.pedido = new Pedido();
        this.pedido.setItens(new ArrayList<ItemPedido>());
        this.pedido.setDataHora(new Date());
        this.pedido.setFuncionario(loginMB.getFuncionario());
        this.pedido.setCliente(new Cliente(-1));
        this.pedido.setSituacao(SituacaoPedido.PENDENTE);
        this.pedidoDAO = new PedidoDAO();
        this.itemAdicionalDAO = new ItemAdicionalDAO();
        this.avaliacaoDAO = new AvaliacaoDAO();                        
        this.formItemAtivo = false;
        this.formPedidoAtivo = false;
        this.formAvaliacaoAtivo = false;
        this.codItemAdicionalSelecionado = -1;
        this.pageSize = new BigDecimal("5");
    }

    public void salvarPedido(){               
        try {                         
            if (this.pedido.getItens().size() > 0) {                                
                this.pedidoDAO.incluir(this.pedido);                                                
                init();
                enviarMensagemInfo("Pedido incluído com sucesso.");
            } else {
                enviarMensagemErro("O pedido não possui itens incluídos.");
            }            
        } catch (Exception e){
            enviarMensagemErro(e.getMessage());            
        }
    }
    
    public void cancelarPedido(){
        init();
    }        
    
    public void inserirItem(ItemCardapio itemCardapio){
        
        if (formAvaliacaoAtivo) {
            return;
        }
        
        limparFormulario("formItem");
        this.itemPedido = new ItemPedido();        
        this.itemPedido.setItem(itemCardapio);
        this.itemPedido.setValor(itemCardapio.getValor());        
        this.itemPedido.setItensAdicionais(new ArrayList());
        
        if (itemCardapio.isPermitePreparo()) {
            this.itemPedido.setSituacao(SituacaoItemPedido.PENDENTE);   
        } else {
            this.itemPedido.setSituacao(SituacaoItemPedido.PRONTO);   
        }
        
        this.formItemAtivo = true;
    }
    
    public void salvarItem(){
        this.pedido.getItens().add(this.itemPedido);
        calcularValorTotal();
        this.formItemAtivo = false;
        limparFormulario("formItem");
    }
    
    public void cancelarItem(){
        this.formItemAtivo = false;
        limparFormulario("formItem");
    }
    
    public void removerItem(ItemPedido item){
        this.pedido.getItens().remove(item);
        calcularValorTotal();
    }
    
    private void calcularValorTotal(){
        BigDecimal valor = BigDecimal.ZERO;
        
        for (ItemPedido item : this.pedido.getItens()) {
            valor = valor.add(item.getValorTotal());
        }
        
        this.pedido.setValor(valor);
    }
    
    public void aplicarDesconto(){
        calcularValorTotal();
    }
    
    public SelectItem[] getItensAdItemSelecionado(){
        SelectItem[] items = new SelectItem[itemPedido.getItem().getItensAdicionais().size()];
        int i = 0;
        for(ItemAdicional ia: itemPedido.getItem().getItensAdicionais()) {
          items[i++] = new SelectItem(ia.getCodigo(), ia.getNome());
        }
        return items;      
    }
    
    public void incluirItemAdicional(){
        
        if (codItemAdicionalSelecionado > 0) {                
            ItemAdicional itemAdicional = itemAdicionalDAO.obter(codItemAdicionalSelecionado);
        
            ItemPedidoAdicional itemPedidoAdicional = new ItemPedidoAdicional();
            itemPedidoAdicional.setItemAdicional(itemAdicional);
            itemPedidoAdicional.setItemPedido(itemPedido);
            itemPedidoAdicional.setValor(itemAdicional.getValor());
            itemPedido.getItensAdicionais().add(itemPedidoAdicional);
        }
    }
    
    public void visualizarAvaliacao(ItemCardapio itemCardapio){        
        itemCardapioAvaliacao = itemCardapio;
        startIndex = BigDecimal.ZERO;
        listarAvaliacoes(0);
        
        cancelarItem();
        this.formAvaliacaoAtivo = true;
    }
    
    public void listarAvaliacoes(Integer startIndex){
        this.qtdPaginas = Util.criarArrayInteger(avaliacaoDAO.getQtdRegistrosItemCard(itemCardapioAvaliacao)
                .divide(pageSize)
                .setScale(0, RoundingMode.CEILING).intValue());
        this.startIndex = new BigDecimal(startIndex);
       
        listaAvaliacao = avaliacaoDAO.getListaPorItemCardapio(itemCardapioAvaliacao,
                this.startIndex.multiply(pageSize).intValue(), pageSize.intValue());        
    }
    
    public void fecharAvaliacao(){
        this.formAvaliacaoAtivo = false;
    }

    public ItemCardapio getItemCardapioAvaliacao() {
        return itemCardapioAvaliacao;
    }

    public void setItemCardapioAvaliacao(ItemCardapio itemCardapioAvaliacao) {
        this.itemCardapioAvaliacao = itemCardapioAvaliacao;
    }

    public boolean isFormAvaliacaoAtivo() {
        return formAvaliacaoAtivo;
    }

    public void setFormAvaliacaoAtivo(boolean formAvaliacaoAtivo) {
        this.formAvaliacaoAtivo = formAvaliacaoAtivo;
    }

    public List<Avaliacao> getListaAvaliacao() {
        return listaAvaliacao;
    }

    public void setListaAvaliacao(List<Avaliacao> listaAvaliacao) {
        this.listaAvaliacao = listaAvaliacao;
    }
    
    public void removerItemPedidoAdicional(ItemPedidoAdicional itemPedidoAdicional){
        this.itemPedido.getItensAdicionais().remove(itemPedidoAdicional);
    }

    public int getCodItemAdicionalSelecionado() {
        return codItemAdicionalSelecionado;
    }

    public void setCodItemAdicionalSelecionado(int codItemAdicionalSelecionado) {
        this.codItemAdicionalSelecionado = codItemAdicionalSelecionado;
    }

    public boolean isFormItemAtivo() {
        return formItemAtivo;
    }

    public void setFormItemAtivo(boolean formItemAtivo) {
        this.formItemAtivo = formItemAtivo;
    }

    public boolean isFormPedidoAtivo() {
        return formPedidoAtivo;
    }

    public void setFormPedidoAtivo(boolean formPedidoAtivo) {
        this.formPedidoAtivo = formPedidoAtivo;
    }        
        
    public ItemPedido getItemPedido() {
        return itemPedido;
    }

    public void setItemPedido(ItemPedido itemPedido) {
        this.itemPedido = itemPedido;
    }
    
    public boolean isFormAtivo() {
        return formItemAtivo;
    }

    public void setFormAtivo(boolean formAtivo) {
        this.formItemAtivo = formAtivo;
    }   
    
    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }        
    
    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }


}
