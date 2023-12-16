/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Everton
 */
public class CodigoEntrada implements Serializable {
    
    private int codigo;
    private String codigoEntrada;
    private Funcionario funcionario;
    private Date validade;
    private Date dataGeracao;

    public CodigoEntrada(){        
    }

    public CodigoEntrada(int codigo) {
        this.codigo = codigo;
    }        
    
    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getCodigoEntrada() {
        return codigoEntrada;
    }

    public void setCodigoEntrada(String codigoEntrada) {
        this.codigoEntrada = codigoEntrada;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public Date getValidade() {
        return validade;
    }

    public void setValidade(Date validade) {
        this.validade = validade;
    }

    public Date getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(Date dataGeracao) {
        this.dataGeracao = dataGeracao;
    }        
}
