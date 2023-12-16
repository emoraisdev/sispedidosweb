/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import bean.GrupoCardapio;
import bean.enums.NivelGrupo;
import bean.enums.SituacaoGrupo;
import dao.GrupoCardapioDAO;
import jakarta.annotation.ManagedBean;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.IOException;
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
@Named("grupoCardMB")
public class GrupoCardMB extends BaseMB{
    private GrupoCardapio grupo;
    private List<GrupoCardapio> listaGrupo;
    private List<GrupoCardapio> listaGrupoPai;
    private GrupoCardapioDAO grupoDAO;
    private boolean formAtivo;
    private String operacao;
    
    /**
     * Creates a new instance of GrupoCardMB
     */
    public GrupoCardMB() {
    }
    
    @PostConstruct
    public void init(){
        this.grupoDAO = new GrupoCardapioDAO();
        this.listaGrupo = grupoDAO.getLista(false);
        this.listaGrupoPai = new ArrayList<>();
        this.grupo = null;
        this.formAtivo = false;
        this.operacao = "";
        this.startIndex = BigDecimal.ZERO;
        listarGrupos(0);
    }
    
    public void listarGrupos(Integer startIndex){
        this.qtdPaginas = Util.criarArrayInteger(grupoDAO.getQtdRegistros().divide(pageSize)
                .setScale(0, RoundingMode.CEILING).intValue());
        this.startIndex = new BigDecimal(startIndex);
        listaGrupo = grupoDAO.getLista(false, false, true, this.startIndex
                .multiply(pageSize).intValue(), pageSize.intValue());
    }
    
    public SelectItem[] getGruposPai(){
        SelectItem[] items = new SelectItem[listaGrupoPai.size()];
        int i = 0;
        for(GrupoCardapio g: listaGrupoPai) {
          items[i++] = new SelectItem(g.getCodigo(), g.getNome());
        }
        return items;      
    }          
        
    public void carregarGruposPai(NivelGrupo nivel) {
        NivelGrupo nivelPermitido = null;
        
        switch (nivel) {
            case NIVEL2:
                nivelPermitido = NivelGrupo.NIVEL1;
                break;
            case NIVEL3:
                nivelPermitido = NivelGrupo.NIVEL2;
                break;
        }
        
        if (nivel == NivelGrupo.NIVEL1){
            this.listaGrupoPai.clear();
        } else {
            this.listaGrupoPai = grupoDAO.getGruposNivel(nivelPermitido);
            GrupoCardapio grupoRemover = null;
            //Remove da lista grupo pai caso seja o mesmo grupo em alteração
            if (operacao.equals("Editar")) {
                for (GrupoCardapio gp: this.listaGrupoPai) {
                    if (gp.getCodigo() == this.grupo.getCodigo()) {
                        grupoRemover = gp;
                    }
                }
            }
            
            if (grupoRemover != null) {
                this.listaGrupoPai.remove(grupoRemover);
            }

        }
    }
    
    public SelectItem[] getNiveis(){
        SelectItem[] items = new SelectItem[NivelGrupo.values().length];
        int i = 0;
        for(NivelGrupo n: NivelGrupo.values()) {
          items[i++] = new SelectItem(n, n.getDescricao());
        }
        return items;      
    }
    
    public SelectItem[] getSituacao(){
        SelectItem[] items = new SelectItem[SituacaoGrupo.values().length];
        int i = 0;
        for(SituacaoGrupo s: SituacaoGrupo.values()) {
          items[i++] = new SelectItem(s, s.getDescricao());
        }
        return items;      
    }
    
    public void salvar(){        
        FacesContext context = FacesContext.getCurrentInstance();
        
        try { 
            
            if ((this.grupo.getNivel() != NivelGrupo.NIVEL1) && 
                    (this.grupo.getGrupoPai().getCodigo() == -1)) {
                throw new Exception("Para este nível é obrigatório definir o Grupo Pai");                
            }
            
            if (this.grupo.getCodigo() == -1) {
                this.grupoDAO.incluir(this.grupo);
            } else {
                this.grupoDAO.alterar(this.grupo);
            }                                                
            this.grupo = new GrupoCardapio();
            listarGrupos(startIndex.intValue());
            this.formAtivo = false;
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Os dados foram salvos com sucesso!", null));
        } catch (Exception e){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }
    
    public void inserir(){
        limparFormulario("formGrupoCardapio");        
        this.grupo = new GrupoCardapio();
        this.grupo.setGrupoPai(new GrupoCardapio());
        this.listaGrupoPai.clear();
        this.operacao = "Inserir";
        this.formAtivo = true;
    }
    
    public void editar(GrupoCardapio grupo){
        this.operacao = "Editar";       
        this.grupo = grupo;
        carregarGruposPai(grupo.getNivel());
        this.formAtivo = true;
    }
    
    public void cancelar(){
        limparFormulario("formGrupoCardapio");
        init();
    }    
    
    public void redirectGrupoCardapio() throws IOException{
         FacesContext.getCurrentInstance().getExternalContext().redirect("grupoCardapio.xhtml");
    }


    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }       

    public GrupoCardapio getGrupo() {
        return grupo;
    }

    public void setGrupo(GrupoCardapio grupo) {
        this.grupo = grupo;
    }        

    public List<GrupoCardapio> getListaGrupo() {
        return listaGrupo;
    }

    public void setListaGrupo(List<GrupoCardapio> listaGrupo) {
        this.listaGrupo = listaGrupo;
    }  

    public boolean isFormAtivo() {
        return formAtivo;
    }

    public void setFormAtivo(boolean formAtivo) {
        this.formAtivo = formAtivo;
    }           
}
