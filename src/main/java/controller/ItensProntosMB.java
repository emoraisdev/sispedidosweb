/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import bean.ItemPedido;
import bean.enums.SituacaoItemPedido;
import dao.ItemPedidoDAO;
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
@Named("itensProntosMB")
public class ItensProntosMB extends BaseMB{
    
    private List<ItemPedido> itensProntos;
    private ItemPedidoDAO itemPedidoDAO;
    private Date periodoDe, periodoAte;
    private Integer codPedido;
    private ItemPedido itemPedido;
    private boolean formItemAtivo;
    private String loginFuncionario;   
    /**
     * Creates a new instance of ItensProntosMB
     */
    public ItensProntosMB() {
    }
    
    @PostConstruct
    public void init(){
        itemPedidoDAO = new ItemPedidoDAO();
        periodoDe = new Date();
        periodoAte = new Date();
        formItemAtivo = false;
        this.startIndex = BigDecimal.ZERO;
        buscarItens(0);
        msgRetornoBusca = "";
    }

    public void buscarItens(Integer index){
        this.startIndex = new BigDecimal(index);
        
        Map<String, Object> filtros = new HashMap<>();
        
        filtros.put("periodoDe", this.periodoDe);
        filtros.put("periodoAte", this.periodoAte);
        filtros.put("codPedido", this.codPedido);
        filtros.put("situacao", SituacaoItemPedido.PRONTO);
        filtros.put("loginFuncionario", this.loginFuncionario);
        filtros.put("startIndex", this.startIndex
                .multiply(pageSize).intValue());
        filtros.put("pageSize", this.pageSize.intValue());
        
        definirQtdPaginas(itemPedidoDAO.getQtdRegistrosLista(filtros)); 
        this.itensProntos = itemPedidoDAO.getLista(filtros);
        
        definirMsgRetornoBusca(this.itensProntos.size());
    }
    
    public void visualizarItem(ItemPedido itemPedido){
        this.itemPedido = itemPedido;
        formItemAtivo = true;
    }
    
    public void fecharForm(){
        formItemAtivo = false;
    }

    public String getLoginFuncionario() {
        return loginFuncionario;
    }

    public void setLoginFuncionario(String loginFuncionario) {
        this.loginFuncionario = loginFuncionario;
    }

    public ItemPedido getItemPedido() {
        return itemPedido;
    }

    public void setItemPedido(ItemPedido itemPedido) {
        this.itemPedido = itemPedido;
    }
    
    public List<ItemPedido> getItensProntos() {
        return itensProntos;
    }

    public void setItensProntos(List<ItemPedido> itensProntos) {
        this.itensProntos = itensProntos;
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

    public boolean isFormItemAtivo() {
        return formItemAtivo;
    }

    public void setFormItemAtivo(boolean formItemAtivo) {
        this.formItemAtivo = formItemAtivo;
    }
    
}
