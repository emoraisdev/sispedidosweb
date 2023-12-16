/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import bean.CodigoEntrada;
import bean.Funcionario;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Everton
 */
public class CodigoEntradaDAO {
    private String insert = "insert into codigo_entrada (codigo_entrada, data_geracao, validade, funcionario_cod)"
            + "values (?,?,?,?)";
    private String verificaSeExiste = "select * from codigo_entrada where date(data_geracao) = ? and codigo_entrada = ?";
    private String selectCodigoValido = "select * from codigo_entrada where codigo_entrada = ? and validade > ? ";
    private String select = "select * from codigo_entrada where codigo = ?";
    
    public void incluir(CodigoEntrada codigoEntrada){
        Connection con = null;
        PreparedStatement pstmtAdiciona = null;
        try {
            con = ConnectionFactory.getConnection();
            pstmtAdiciona = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            
            int index = 1;
            
            pstmtAdiciona.setString(index++, codigoEntrada.getCodigoEntrada());           
            pstmtAdiciona.setString(index++, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(codigoEntrada.getDataGeracao()));
            pstmtAdiciona.setString(index++, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(codigoEntrada.getValidade()));
            pstmtAdiciona.setInt(index++, codigoEntrada.getFuncionario().getCodigo());
            
            pstmtAdiciona.execute();
            
            ResultSet rs = pstmtAdiciona.getGeneratedKeys();
            rs.next();
            codigoEntrada.setCodigo(rs.getInt(1));
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {pstmtAdiciona.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }
    }
    
    public boolean verificaSeExiste(String codigoEntrada){
        Connection con = null;
        PreparedStatement pstmtVerifica = null;
        try {
            con = ConnectionFactory.getConnection();
            pstmtVerifica = con.prepareStatement(verificaSeExiste);
            
            int index = 1;
                        
            pstmtVerifica.setString(index++, (new SimpleDateFormat("yyyy-MM-dd")).format(new Date()));
            pstmtVerifica.setString(index++, codigoEntrada);           
                        
            ResultSet rs = pstmtVerifica.executeQuery();
            
            return rs.next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {pstmtVerifica.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }
    }
    
    public CodigoEntrada obterValido(String codEntrada){
        Connection con = null;
        PreparedStatement pstmtObter = null;
        CodigoEntrada codigoEntrada = new CodigoEntrada();
        try {
            con = ConnectionFactory.getConnection();
            pstmtObter = con.prepareStatement(selectCodigoValido);
            
            int index = 1;
                        
            pstmtObter.setString(index++, codEntrada);           
            pstmtObter.setString(index++, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));
                        
            ResultSet rs = pstmtObter.executeQuery();
            
            if(rs.next()){
                codigoEntrada.setCodigo(rs.getInt("codigo"));
                codigoEntrada.setCodigoEntrada(rs.getString("codigo_entrada"));
                codigoEntrada.setFuncionario(new Funcionario(rs.getInt("funcionario_cod")));
                codigoEntrada.setDataGeracao(rs.getTimestamp("data_geracao"));
                codigoEntrada.setValidade(rs.getTimestamp("validade"));
            }else{
                return null;
            }

            return codigoEntrada;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {pstmtObter.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }
    }
    
    public CodigoEntrada obter(int codigo){
        Connection con = null;
        PreparedStatement pstmtObter = null;
        CodigoEntrada codigoEntrada = new CodigoEntrada();
        try {
            con = ConnectionFactory.getConnection();
            pstmtObter = con.prepareStatement(select);
            
            int index = 1;
                        
            pstmtObter.setInt(index++, codigo);                                   
            ResultSet rs = pstmtObter.executeQuery();
            
            if(rs.next()){
                codigoEntrada.setCodigo(rs.getInt("codigo"));
                codigoEntrada.setCodigoEntrada(rs.getString("codigo_entrada"));
                codigoEntrada.setFuncionario(new Funcionario(rs.getInt("funcionario_cod")));
                codigoEntrada.setDataGeracao(rs.getTimestamp("data_geracao"));
                codigoEntrada.setValidade(rs.getTimestamp("validade"));
            }else{
                throw new RuntimeException("Código inválido= " + codigo);
            }

            return codigoEntrada;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {pstmtObter.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }
    }
    
}
