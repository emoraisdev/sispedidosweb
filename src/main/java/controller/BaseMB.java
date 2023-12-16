/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.FacesContext;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 *
 * @author Everton
 */
public class BaseMB implements Serializable{
    
    protected BigDecimal pageSize = new BigDecimal("10"), startIndex;
    protected List<Integer> qtdPaginas;
    protected String msgRetornoBusca;
    
    public void limparFormulario(String idForm) {
        UIViewRoot view = FacesContext.getCurrentInstance().getViewRoot();
        UIComponent fc = view.findComponent(idForm);
        if (null != fc) {
            List<UIComponent> components = fc.getChildren();
            for (UIComponent component : components) {
                if (component instanceof UIInput) {
                    UIInput input = (UIInput) component;
                    input.resetValue();
                }
            }
        }
    }
    
    public void enviarMensagemErro(String msg){
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));        
    }
    
    public void enviarMensagemInfo(String msg){
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }
    
    public String retornarStatusPagina(Integer index){
        
        if (startIndex.intValue() == index) {
            return "active";
        } else {
            return "";
        }
    }
    
    protected void definirQtdPaginas(BigDecimal qtdRegistros){
        this.qtdPaginas = Util.criarArrayInteger(qtdRegistros.divide(pageSize,0, RoundingMode.CEILING)
                .setScale(0, RoundingMode.CEILING).intValue());
    }
    
    protected void definirMsgRetornoBusca(Integer qtdReg){
        msgRetornoBusca = (qtdReg > 0)? ""
                : "Náo foram encontrados registros para os filtros informados.";
    }

    public BigDecimal getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(BigDecimal startIndex) {
        this.startIndex = startIndex;
    }

    public List<Integer> getQtdPaginas() {
        return qtdPaginas;
    }

    public void setQtdPaginas(List<Integer> qtdPaginas) {
        this.qtdPaginas = qtdPaginas;
    }        

    public String getMsgRetornoBusca() {
        return msgRetornoBusca;
    }

    public void setMsgRetornoBusca(String msgRetornoBusca) {
        this.msgRetornoBusca = msgRetornoBusca;
    }
    
}
