/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import bean.GrupoCardapio;
import bean.enums.NivelGrupo;
import dao.GrupoCardapioDAO;
import jakarta.annotation.ManagedBean;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import java.util.List;

/**
 *
 * @author Everton
 */
@ManagedBean
@RequestScoped
@Named("visualizarCardMB")
public class VisualizarCardMB {
    private List<GrupoCardapio> grupos;
    private GrupoCardapioDAO grupoDAO;
    /**
     * Creates a new instance of VisualizarCardMB
     */
    public VisualizarCardMB() {
    }
    
    @PostConstruct
    public void init(){
        this.grupoDAO = new GrupoCardapioDAO();
        this.grupos = grupoDAO.getGruposNivel(NivelGrupo.NIVEL1,true, true);
    }

    public List<GrupoCardapio> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<GrupoCardapio> grupos) {
        this.grupos = grupos;
    }        
}
