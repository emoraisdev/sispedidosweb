/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import bean.Endereco;

import java.io.Serializable;
import java.sql.*;

/**
 *
 * @author Everton
 */
public class EnderecoDAO implements Serializable{    
    private String insert = "insert into endereco (estado, cidade, bairro, numero, rua, complemento, cep)"
            + "values (?,?,?,?,?,?,?)";
    private String update = "update endereco set estado = ?, cidade = ?, bairro = ?, numero = ?, rua = ?, "
            + "complemento = ?, cep = ? where codigo = ?";
    private String selectPorCodigo = "select * from endereco where codigo = ?";
    
    public void incluir(Endereco endereco){
        Connection con = null;
        PreparedStatement pstmtAdiciona = null;
        try {
            con = ConnectionFactory.getConnection();
            pstmtAdiciona = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            
            pstmtAdiciona.setString(1, endereco.getEstado());
            pstmtAdiciona.setString(2, endereco.getCidade());
            pstmtAdiciona.setString(3, endereco.getBairro());           
            pstmtAdiciona.setInt(4, endereco.getNumero());
            pstmtAdiciona.setString(5, endereco.getRua());
            pstmtAdiciona.setString(6, endereco.getComplemento());
            pstmtAdiciona.setString(7, endereco.getCep());
            
            pstmtAdiciona.execute();
            
            ResultSet rs = pstmtAdiciona.getGeneratedKeys();
            rs.next();
            endereco.setCodigo(rs.getInt(1));
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {pstmtAdiciona.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }
    }
    
    public void alterar(Endereco endereco) {
        Connection con = null;
        PreparedStatement pstmtAtualiza = null;
        try {
            con = ConnectionFactory.getConnection();
            pstmtAtualiza = con.prepareStatement(update);  
            
            pstmtAtualiza.setString(1, endereco.getEstado());
            pstmtAtualiza.setString(2, endereco.getCidade());
            pstmtAtualiza.setString(3, endereco.getBairro());           
            pstmtAtualiza.setInt(4, endereco.getNumero());
            pstmtAtualiza.setString(5, endereco.getRua());
            pstmtAtualiza.setString(6, endereco.getComplemento());
            pstmtAtualiza.setString(7, endereco.getCep());
            pstmtAtualiza.setInt(8, endereco.getCodigo());
            
            pstmtAtualiza.executeUpdate();
        } catch (SQLException ex) {
               throw new RuntimeException(ex.getMessage());
        } finally{
            try {pstmtAtualiza.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar pstmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}
 
        }        
    }    
    
    public Endereco obter(int codigo){
        Connection con = null;
        PreparedStatement stmtObter = null;
        ResultSet rs = null;
        Endereco endereco = new Endereco();
        try {
            con = ConnectionFactory.getConnection();
            stmtObter = con.prepareStatement(selectPorCodigo);
            stmtObter.setInt(1, codigo);
            rs = stmtObter.executeQuery();
            if(rs.next()){
                endereco.setCodigo(rs.getInt("codigo"));
                endereco.setEstado(rs.getString("estado"));
                endereco.setCidade(rs.getString("cidade"));
                endereco.setBairro(rs.getString("bairro"));
                endereco.setNumero(rs.getInt("numero"));
                endereco.setRua(rs.getString("rua"));
                endereco.setComplemento(rs.getString("complemento"));
                endereco.setCep(rs.getString("cep"));

            }else{
                throw new RuntimeException("Código inválido= " + codigo);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {rs.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar rs.");}
            try {stmtObter.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }
        return endereco;
    }
}
