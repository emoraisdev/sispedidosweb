/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import bean.ItemAdicional;
import bean.enums.SituacaoItemAdicional;
import dao.ItemAdicionalDAO;
import jakarta.annotation.ManagedBean;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 *
 * @author Everton
 */
@ManagedBean
@ViewScoped
@Named("itemAdicionalCardMB")
public class ItemAdicionalCardMB extends BaseMB{
    
    private ItemAdicional itemAdicional;
    private ItemAdicionalDAO itemAdicionalDAO;
    private List<ItemAdicional> listaItemAdicional;
    private boolean formAtivo;
    private String operacao;

    /**
     * Creates a new instance of ItemAdicionalCardMB
     */
    public ItemAdicionalCardMB() {
    }
    
    @PostConstruct
    public void init(){
        this.itemAdicionalDAO = new ItemAdicionalDAO();
        this.formAtivo = false;        
        this.operacao = "";
        this.itemAdicional = null;
        this.startIndex = BigDecimal.ZERO;
        listarItensAdicionais(0);
    }
    
    public void listarItensAdicionais(Integer startIndex){        
        this.qtdPaginas = Util.criarArrayInteger(itemAdicionalDAO.getQtdRegistros().divide(pageSize)
                .setScale(0, RoundingMode.CEILING).intValue());
        this.startIndex = new BigDecimal(startIndex);
        listaItemAdicional = itemAdicionalDAO.getLista(false, this.startIndex
                .multiply(pageSize).intValue(), pageSize.intValue());
    }
    
    public void inserir(){   
        limparFormulario("formItemAdicional");
        this.itemAdicional = new ItemAdicional();
        this.operacao = "Inserir";
        this.formAtivo = true;
    }
    
    public SelectItem[] getSituacaoItemAdicional(){
        SelectItem[] items = new SelectItem[SituacaoItemAdicional.values().length];
        int i = 0;
        for(SituacaoItemAdicional s: SituacaoItemAdicional.values()) {
          items[i++] = new SelectItem(s, s.getDescricao());
        }
        return items;      
    }
    
    public void salvar(){        
        FacesContext context = FacesContext.getCurrentInstance();
        
        try {            
            if (this.itemAdicional.getCodigo() == -1) {
                this.itemAdicionalDAO.incluir(this.itemAdicional);
            } else {
                this.itemAdicionalDAO.alterar(this.itemAdicional);
            }                                    
            listarItensAdicionais(startIndex.intValue());
            this.formAtivo = false;
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Os dados foram salvos com sucesso!", null));
        } catch (Exception e){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvara informações: "+e.getMessage(), null));
        }
    }    
    
    public void editar(ItemAdicional itemAdicional){
        this.operacao = "Editar";
        this.itemAdicional = itemAdicional;
        this.formAtivo = true;
    }

    public void cancelar(){
        limparFormulario("formItemAdicional");
        init();
    }
    
    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public ItemAdicional getItemAdicional() {
        return itemAdicional;
    }

    public void setItemAdicional(ItemAdicional itemAdicional) {
        this.itemAdicional = itemAdicional;
    }

    public ItemAdicionalDAO getItemAdicionalDAO() {
        return itemAdicionalDAO;
    }

    public void setItemAdicionalDAO(ItemAdicionalDAO itemAdicionalDAO) {
        this.itemAdicionalDAO = itemAdicionalDAO;
    }

    public List<ItemAdicional> getListaItemAdicional() {
        return listaItemAdicional;
    }

    public void setListaItemAdicional(List<ItemAdicional> listaItemAdicional) {
        this.listaItemAdicional = listaItemAdicional;
    }

    public boolean isFormAtivo() {
        return formAtivo;
    }

    public void setFormAtivo(boolean formAtivo) {
        this.formAtivo = formAtivo;
    }
    
    
}
