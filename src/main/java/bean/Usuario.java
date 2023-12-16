/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import bean.enums.SituacaoUsuario;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Everton
 */
public class Usuario implements Serializable{
    
    private int codigo;
    private String nome;
    private String sobrenome;
    private String cpf;
    private Date dataNascimento;
    private String email;
    private String telefone;    
    private String login;
    private String senha;
    private Endereco endereco;
    private SituacaoUsuario situacao;
    
    public Usuario(int codigo){
        this.codigo = codigo;
    }
    
    public Usuario(){
        this.codigo =-1;
    }

    public SituacaoUsuario getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoUsuario situacao) {
        this.situacao = situacao;
    }        

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getCpf() {
        return cpf;
    }
    
    public String getCpfFormatado() {
        return (new StringBuilder(cpf)).insert(3, ".")
                .insert(7, ".")
                .insert(11, "-").toString();
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
    public String getDataNascimentoFormatada() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(this.dataNascimento);
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }
    
    public String getTelefoneFormatado() {
        if (telefone.length() < 11) {
            return (new StringBuilder(telefone)).insert(0, "(")
                .insert(3, ") ")
                .insert(9, "-").toString();
        } else {
            return (new StringBuilder(telefone)).insert(0, "(")
                .insert(3, ") ")
                .insert(10, "-").toString();
        }
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }
    
    
    
}
