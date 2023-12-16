/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import bean.GrupoCardapio;
import bean.ItemAdicional;
import bean.ItemCardapio;
import bean.enums.SituacaoItemCardapio;
import dao.GrupoCardapioDAO;
import dao.ItemAdicionalDAO;
import dao.ItemCardapioDAO;
import jakarta.annotation.ManagedBean;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Everton
 */
@ManagedBean
@ViewScoped
@Named("itemCardMB")
public class ItemCardMB extends BaseMB{
    private ItemCardapio item;
    private ItemCardapioDAO itemDAO;
    private GrupoCardapioDAO grupoDAO;
    private ItemAdicionalDAO itemAdicionalDAO;
    private boolean formAtivo;
    private List<GrupoCardapio> listaGrupo;
    private List<ItemCardapio> listaItem;
    private List<ItemAdicional> listaItemAdicional;
    private List<Integer> codItensAdSelecionados;
    private List<Integer> codItensAdExcluir;
    private String operacao;
    private boolean modoVisualizar;
    
    /**
     * Creates a new instance of itemCardMB
     */
    public ItemCardMB() {
    }
    
    @PostConstruct
    public void init(){
        this.itemDAO = new ItemCardapioDAO();
        this.grupoDAO = new GrupoCardapioDAO();        
        this.itemAdicionalDAO = new ItemAdicionalDAO();
        this.listaGrupo = grupoDAO.getLista(false);
        this.listaItemAdicional = itemAdicionalDAO.getLista();
        this.formAtivo = false;
        this.modoVisualizar = false;
        this.operacao = "";
        this.item = null;        
        this.startIndex = BigDecimal.ZERO;
        listarItens(0);
    }        
    
    public void listarItens(Integer startIndex){        
        this.qtdPaginas = Util.criarArrayInteger(itemDAO.getQtdRegistros().divide(pageSize)
                .setScale(0, RoundingMode.CEILING).intValue());
        this.startIndex = new BigDecimal(startIndex);
        listaItem = itemDAO.getLista(false, this.startIndex
                .multiply(pageSize).intValue(), pageSize.intValue());
    }
    
    public SelectItem[] getGrupos(){
        SelectItem[] items = new SelectItem[listaGrupo.size()];
        int i = 0;
        for(GrupoCardapio g: listaGrupo) {
          items[i++] = new SelectItem(g.getCodigo(), g.getNome());
        }
        return items;      
    }
    
    public SelectItem[] getItensAdicionais(){
        SelectItem[] items = new SelectItem[listaItemAdicional.size()];
        int i = 0;
        for(ItemAdicional ia: listaItemAdicional) {
          items[i++] = new SelectItem(ia.getCodigo(), ia.getNome());
        }
        return items;      
    }
    
    public SelectItem[] getItensAdicionaisCadastrados(){
        SelectItem[] items = new SelectItem[item.getItensAdicionais().size()];
        int i = 0;
        for(ItemAdicional ia: item.getItensAdicionais()) {
          items[i++] = new SelectItem(ia.getCodigo(), ia.getNome());
        }
        return items;      
    }
    
    
    public SelectItem[] getSituacaoItem(){
        SelectItem[] items = new SelectItem[SituacaoItemCardapio.values().length];
        int i = 0;
        for(SituacaoItemCardapio s: SituacaoItemCardapio.values()) {
          items[i++] = new SelectItem(s, s.getDescricao());
        }
        return items;      
    }
    
    public void salvar(){        
        FacesContext context = FacesContext.getCurrentInstance();
        
        try {            
            if (this.item.getCodigo() == -1) {
                this.itemDAO.incluir(this.item);
            } else {
                this.itemDAO.alterar(this.item);
            }                                    
            listarItens(startIndex.intValue());
            this.formAtivo = false;
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Os dados foram salvos com sucesso!", null));
        } catch (Exception e){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvar as informações: "+e.getMessage(), null));
        }
    }
    
    public void inserir(){        
        limparFormulario("formItem");
        this.modoVisualizar = false;
        this.item = new ItemCardapio();
        this.item.setGrupo(new GrupoCardapio());
        this.item.setItensAdicionais(new ArrayList());
        this.operacao = "Inserir";
        this.formAtivo = true;
    }
    
    public void editar(ItemCardapio item){
        this.modoVisualizar = false;
        this.operacao = "Editar";
        this.item = item;
        this.formAtivo = true;
    }
    
    public void visualizar(ItemCardapio item){
        this.modoVisualizar = true;
        this.operacao = "Detalhes";
        this.item = item;
        this.formAtivo = true;
    }
    
    public void cancelar(){
        limparFormulario("formItem");
        init();
    }
    
    public void incluirItensAdicionais(){        
        for (Integer codItem: codItensAdSelecionados) {

            boolean incluir = true;
                    
            for (ItemAdicional ia: item.getItensAdicionais()) {
                //Verifica se o item já existe
                if (ia.getCodigo() == codItem){
                    incluir = false;
                }            
            }
            
            if (incluir){
                item.getItensAdicionais().add(itemAdicionalDAO.obter(codItem));
            }
        }
    }
    
    public void excluirItensAdicionais(){
        for (Integer codItem: codItensAdExcluir) {

            ItemAdicional itemAdicionalExcluir = null;
                    
            for (ItemAdicional ia: item.getItensAdicionais()) {
                //Verifica se o item já existe
                if (ia.getCodigo() == codItem){
                    itemAdicionalExcluir = ia;
                }            
            }
            
            if (itemAdicionalExcluir != null){
                item.getItensAdicionais().remove(itemAdicionalExcluir);
            }
        }
    }

    public List<Integer> getQtdPaginas() {
        return qtdPaginas;
    }

    public void setQtdPaginas(List<Integer> qtdPaginas) {
        this.qtdPaginas = qtdPaginas;
    }

    public BigDecimal getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(BigDecimal startIndex) {
        this.startIndex = startIndex;
    }

    public boolean isModoVisualizar() {
        return modoVisualizar;
    }

    public void setModoVisualizar(boolean modoVisualizar) {
        this.modoVisualizar = modoVisualizar;
    }

    public List<Integer> getCodItensAdExcluir() {
        return codItensAdExcluir;
    }

    public void setCodItensAdExcluir(List<Integer> codItensAdExcluir) {
        this.codItensAdExcluir = codItensAdExcluir;
    }
       
    public ItemCardapio getItem() {
        return item;
    }

    public List<Integer> getCodItensAdSelecionados() {
        return codItensAdSelecionados;
    }

    public void setCodItensAdSelecionados(List<Integer> codItensAdSelecionados) {
        this.codItensAdSelecionados = codItensAdSelecionados;
    }

    public void setItem(ItemCardapio item) {
        this.item = item;
    }

    public boolean isFormAtivo() {
        return formAtivo;
    }

    public void setFormAtivo(boolean formAtivo) {
        this.formAtivo = formAtivo;
    }

    public List<GrupoCardapio> getListaGrupo() {
        return listaGrupo;
    }

    public void setListaGrupo(List<GrupoCardapio> listaGrupo) {
        this.listaGrupo = listaGrupo;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public List<ItemCardapio> getListaItem() {
        return listaItem;
    }

    public void setListaItem(List<ItemCardapio> listaItem) {
        this.listaItem = listaItem;
    }
        
}
