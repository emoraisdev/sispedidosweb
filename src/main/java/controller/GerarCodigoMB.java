/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import bean.CodigoEntrada;
import bean.Funcionario;
import dao.CodigoEntradaDAO;
import jakarta.annotation.ManagedBean;
import jakarta.annotation.PostConstruct;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author Everton
 */
@ManagedBean
@ViewScoped
@Named("gerarCodigoMB")
public class GerarCodigoMB extends BaseMB{

    private String codigo;
    private CodigoEntradaDAO codigoEntradaDAO;

    @Inject
    private LoginMB loginMB;

    private Funcionario funcionario;
    
    /**
     * Creates a new instance of GerarCodigoMB
     */
    public GerarCodigoMB() {
    }
    
    @PostConstruct
    public void init(){
        codigo = "";
        codigoEntradaDAO = new CodigoEntradaDAO();
        funcionario = loginMB.getFuncionario();
    }
    
    public void gerarCodigo(){
        Random random = new Random();
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String codigo="";    
                
        for (int i = 0; i < 6; i++) {
            codigo = codigo + chars.charAt(random.nextInt(chars.length()));
        }
        
        if (codigoEntradaDAO.verificaSeExiste(codigo)){
            this.codigo = "";
            enviarMensagemErro("Ocorreu um erro ao gerar o código, tente novamente!");
        } else {        
            CodigoEntrada codigoEntrada  = new CodigoEntrada();
            codigoEntrada.setCodigoEntrada(codigo);
            codigoEntrada.setDataGeracao(new Date());
            //adicionado a validade de 20 minutos
            codigoEntrada.setValidade(new Date(Calendar.getInstance().getTimeInMillis() 
                                               + (20 * 60000))); //60000 = 1 minuto in milissegundos
            codigoEntrada.setFuncionario(funcionario);
            
            codigoEntradaDAO.incluir(codigoEntrada);
            this.codigo = codigo;
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }        
}
