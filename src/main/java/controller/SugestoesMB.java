/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import bean.Sugestao;
import bean.enums.TipoSugestao;
import dao.SugestaoDAO;
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
@Named("sugestoesMB")
public class SugestoesMB extends BaseMB{
    
    private Date periodoDe, periodoAte;
    private TipoSugestao tipoSugestao;
    private List<Sugestao> sugestoes;
    private SugestaoDAO sugestaoDAO;

    /**
     * Creates a new instance of SugestoesMB
     */
    public SugestoesMB() {
    }
    
    @PostConstruct
    public void init(){
        sugestaoDAO = new SugestaoDAO();
        periodoDe = new Date();
        periodoAte = new Date();
        this.startIndex = BigDecimal.ZERO;
        buscarSugestoes(0);
        msgRetornoBusca = "";
    }

    public void buscarSugestoes(Integer index){
        this.startIndex = new BigDecimal(index);
        Map<String, Object> filtros = new HashMap<>();
        
        filtros.put("periodoDe", periodoDe);
        filtros.put("periodoAte", periodoAte);
        filtros.put("tipoSugestao", tipoSugestao);        
        filtros.put("startIndex", this.startIndex
                .multiply(pageSize).intValue());
        filtros.put("pageSize", this.pageSize.intValue());
                
        definirQtdPaginas(sugestaoDAO.getQtdRegistrosLista(filtros));         
        sugestoes = sugestaoDAO.getLista(filtros);
        
        definirMsgRetornoBusca(sugestoes.size());
    }
    
    public SelectItem[] getTipos(){
        SelectItem[] items = new SelectItem[TipoSugestao.values().length];
        int i = 0;
        for(TipoSugestao s: TipoSugestao.values()) {
          items[i++] = new SelectItem(s, s.getDescricao());
        }
        return items;      
    }

    public List<Sugestao> getSugestoes() {
        return sugestoes;
    }

    public void setSugestoes(List<Sugestao> sugestoes) {
        this.sugestoes = sugestoes;
    }

    public TipoSugestao getTipoSugestao() {
        return tipoSugestao;
    }

    public void setTipoSugestao(TipoSugestao tipoSugestao) {
        this.tipoSugestao = tipoSugestao;
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
}
