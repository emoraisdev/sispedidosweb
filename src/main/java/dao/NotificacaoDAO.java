/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import bean.Cliente;
import bean.Notificacao;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Everton
 */
public class NotificacaoDAO {
    
    private String insert = "insert into notificacao (cliente_cod, mensagem, lida, notificado, data_hora)"
            + "values (?,?,?,?,?)";    
    private String selectPorCliente = "select * from notificacao where cliente_cod = ?"
            + " order by data_hora desc limit 0,10";
    private String update = "update notificacao set lida = ?, notificado = ? where codigo = ?";

    public void incluir(Notificacao notificacao){
        Connection con = null;
        PreparedStatement pstmtAdiciona = null;
        try {
            con = ConnectionFactory.getConnection();
            pstmtAdiciona = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            
            int index = 1;
            
            pstmtAdiciona.setInt(index++, notificacao.getCliente().getCodigo());
            pstmtAdiciona.setString(index++, notificacao.getMensagem());
            pstmtAdiciona.setBoolean(index++, notificacao.isLida());
            pstmtAdiciona.setBoolean(index++, notificacao.isNotificado());
            pstmtAdiciona.setString(index++, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(notificacao.getDataHora()));
            
            pstmtAdiciona.execute();
            
            ResultSet rs = pstmtAdiciona.getGeneratedKeys();
            rs.next();
            notificacao.setCodigo(rs.getInt(1));
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {pstmtAdiciona.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }
    }
    
    public void alterar(Notificacao notificacao){
        Connection con = null;
        PreparedStatement pstmtAtualiza = null;
        PagamentoDAO pagamentoDAO = new PagamentoDAO();
        
        try {
                        
            con = ConnectionFactory.getConnection();
            pstmtAtualiza = con.prepareStatement(update);    
            
            int index = 1;
            
            pstmtAtualiza.setBoolean(index++, notificacao.isLida());
            pstmtAtualiza.setBoolean(index++, notificacao.isNotificado());
            pstmtAtualiza.setInt(index++, notificacao.getCodigo());            
            pstmtAtualiza.execute();                                  
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally{
            try {pstmtAtualiza.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }
    } 
    
    //lista apenas as últimas 10 - regra de negócio
    public List<Notificacao> getLista(Integer codCliente) {
        Connection con = null;
        PreparedStatement stmtLista = null;
        ResultSet rs = null;
        ClienteDAO clienteDAO = new ClienteDAO();
        
        try {
            
            con = ConnectionFactory.getConnection();
            stmtLista = con.prepareStatement(selectPorCliente);            
            stmtLista.setInt(1, codCliente);
            rs = stmtLista.executeQuery();
            
            List<Notificacao> notificacoes = new ArrayList();
            while (rs.next()) {                
                Notificacao notificacao = new Notificacao();
                
                notificacao.setCodigo(rs.getInt("codigo"));
                notificacao.setCliente(new Cliente(rs.getInt("cliente_cod")));
                notificacao.setMensagem(rs.getString("mensagem"));
                notificacao.setLida(rs.getBoolean("lida"));
                notificacao.setNotificado(rs.getBoolean("notificado"));
                notificacao.setDataHora(rs.getTimestamp("data_hora")); 

                notificacoes.add(notificacao);
            }
            
            return notificacoes;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {rs.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar rs.");}
            try {stmtLista.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}
            
        }       
    }
    
}
