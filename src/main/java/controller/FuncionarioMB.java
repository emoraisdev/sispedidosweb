/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import bean.Endereco;
import bean.Funcionario;
import bean.enums.Cargo;
import bean.enums.SituacaoUsuario;
import dao.FuncionarioDAO;
import jakarta.annotation.ManagedBean;
import jakarta.annotation.PostConstruct;
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
@Named("funcionarioMB")
public class FuncionarioMB extends BaseMB{
    
    private boolean readOnly, modoAlteracao, formAtivo;
    private Funcionario funcionario;
    private List<Funcionario> listaFuncionario;
    private FuncionarioDAO funcDAO;
    private String confirmarSenha;    
        
    /**
     * Creates a new instance of FuncionarioMB
     */
    public FuncionarioMB() {            
    }

    @PostConstruct
    public void init(){
        this.funcionario = new Funcionario();
        this.funcionario.setEndereco(new Endereco());
        this.funcionario.setSituacao(SituacaoUsuario.ATIVO);
        this.funcionario.setAdministrador(false);
        this.funcionario.setCargo(Cargo.ATENDENTE);
        this.readOnly = false;
        this.funcDAO = new FuncionarioDAO();
        this.startIndex = BigDecimal.ZERO;
        listarFuncionarios(0);
    }        
    
    public void listarFuncionarios(Integer startIndex){
        this.formAtivo = false;        
        
        this.qtdPaginas = Util.criarArrayInteger(funcDAO.getQtdRegistros().divide(pageSize)
                .setScale(0, RoundingMode.CEILING).intValue());
        this.startIndex = new BigDecimal(startIndex);
        listaFuncionario = funcDAO.getLista(this.startIndex
                .multiply(pageSize).intValue(), pageSize.intValue());
    }
    public void salvar(){               
        try {            
            boolean alterarSenha = false;
            if (modoAlteracao) {
                
                if (funcionario.getSenha() != null ){
                    if (this.confirmarSenha != null) {
                        if (!this.funcionario.getSenha().equals(this.confirmarSenha)) {
                            throw new Exception("As senhas informadas não são iguais.");
                        }
                        alterarSenha = true;
                    } else {
                        throw new Exception("O campo Confirmar Senha deve ser preenchido.");
                    }
                            
                } else {
                    funcionario.setSenha(null);
                }
            } else {
                if (!this.funcionario.getSenha().equals(this.confirmarSenha)) {
                    throw new Exception("As senhas informadas não são iguais.");
                }                
            }
            
            if (this.funcionario.getCodigo() == -1) {
                this.funcDAO.incluir(this.funcionario);
            } else {
                this.funcDAO.alterar(this.funcionario, alterarSenha);
            }                                    
            enviarMensagemInfo("Os dados foram salvos com sucesso!");
            formAtivo = false;
            listarFuncionarios(0);
        } catch (Exception e){
            enviarMensagemErro("Ocorreu um erro ao salvar as informações: " + e.getMessage());
        }
    }
        
    public void inserir(){
        this.funcionario = new Funcionario();
        this.funcionario.setEndereco(new Endereco());
        this.funcionario.setSituacao(SituacaoUsuario.ATIVO);
        this.funcionario.setAdministrador(false);
        this.funcionario.setCargo(Cargo.ATENDENTE);
        this.readOnly = false;
        this.modoAlteracao = false;
        this.formAtivo = true;
    }
    
    public void alterar(int codigo){
        this.funcionario = this.funcDAO.obter(codigo);
        this.modoAlteracao = true;
        this.readOnly = false;
        this.formAtivo = true;
    }
    
    public void visualizar(int codigo){
        this.funcionario = this.funcDAO.obter(codigo);
        this.readOnly = true;
        this.formAtivo = true;
    }
    
    public void fecharForm(){
        listarFuncionarios(0);
    }
    
    public SelectItem[] getCargos(){
        SelectItem[] items = new SelectItem[Cargo.values().length];
        int i = 0;
        for(Cargo c: Cargo.values()) {
          items[i++] = new SelectItem(c, c.getDescricao());
        }
        return items;      
    }
    
    public SelectItem[] getSituacaoUsuario(){
        SelectItem[] items = new SelectItem[SituacaoUsuario.values().length];
        int i = 0;
        for(SituacaoUsuario s: SituacaoUsuario.values()) {
          items[i++] = new SelectItem(s, s.getDescricao());
        }
        return items;      
    }

    public boolean isFormAtivo() {
        return formAtivo;
    }

    public void setFormAtivo(boolean formAtivo) {
        this.formAtivo = formAtivo;
    }

    public boolean isModoAlteracao() {
        return modoAlteracao;
    }

    public void setModoAlteracao(boolean modoAlteracao) {
        this.modoAlteracao = modoAlteracao;
    }
        
    public String getConfirmarSenha() {
        return confirmarSenha;
    }

    public void setConfirmarSenha(String confirmarSenha) {
        this.confirmarSenha = confirmarSenha;
    }
        
    public List<Funcionario> getListaFuncionario() {
        return listaFuncionario;
    }

    public void setListaFuncionario(List<Funcionario> listaFuncionario) {
        this.listaFuncionario = listaFuncionario;
    }        
    
    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
    
    
    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }
}
