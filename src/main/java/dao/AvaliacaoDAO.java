/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import bean.Avaliacao;
import bean.ItemCardapio;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Everton
 */
public class AvaliacaoDAO {
     private String insert = "insert into avaliacao (item_cardapio_cod, cliente_cod, nota, comentario, data_hora)"
            + "values (?,?,?,?,?)";     
    
    public void incluir(Avaliacao avaliacao){
        Connection con = null;
        PreparedStatement pstmtAdiciona = null;
        try {
            con = ConnectionFactory.getConnection();
            pstmtAdiciona = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            
            int index = 1;
            
            pstmtAdiciona.setInt(index++, avaliacao.getItem().getCodigo());
            pstmtAdiciona.setInt(index++, avaliacao.getCliente().getCodigo());
            pstmtAdiciona.setInt(index++, avaliacao.getNota());
            pstmtAdiciona.setString(index++, avaliacao.getComentario());
            pstmtAdiciona.setString(index++, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                    .format(avaliacao.getDataHora()));

            
            pstmtAdiciona.execute();
            
            ResultSet rs = pstmtAdiciona.getGeneratedKeys();
            rs.next();
            avaliacao.setCodigo(rs.getInt(1));
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {pstmtAdiciona.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }
    }
    
       public List<Avaliacao> getListaPorItemCardapio(ItemCardapio item, Integer startIndex, Integer pageSize) {
        Connection con = null;
        PreparedStatement stmtLista = null;
        ResultSet rs = null;
        ClienteDAO clienteDAO = new ClienteDAO();        
        String consulta = "select * from avaliacao where item_cardapio_cod = ? order by data_hora desc";
        
        if (startIndex != null && pageSize != null) {
            consulta = consulta+ " limit "+String.valueOf(startIndex)+","+String.valueOf(pageSize);
        }
        
        try {
            con = ConnectionFactory.getConnection();
            stmtLista = con.prepareStatement(consulta);
            stmtLista.setInt(1, item.getCodigo());
            rs = stmtLista.executeQuery();
            List<Avaliacao> avaliacoes = new ArrayList();
            while (rs.next()) {
                //
                Avaliacao avaliacao = new Avaliacao();
                
                avaliacao.setCodigo(rs.getInt("codigo"));
                avaliacao.setItem(new ItemCardapio(rs.getInt("item_cardapio_cod")));
                avaliacao.setCliente(clienteDAO.obter(rs.getInt("cliente_cod")));
                avaliacao.setNota(rs.getInt("nota"));
                avaliacao.setComentario(rs.getString("comentario"));
                avaliacao.setDataHora(rs.getTimestamp("data_hora")); 

                avaliacoes.add(avaliacao);
            }
            
            return avaliacoes;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {rs.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar rs.");}
            try {stmtLista.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}
            
        }       
    } 
       
    public BigDecimal getQtdRegistrosItemCard(ItemCardapio item) {
        Connection con = null;
        PreparedStatement stmtQtd = null;
        ResultSet rs = null;
        
        try {
            con = ConnectionFactory.getConnection();
            stmtQtd = con.prepareStatement("select count(*) total from avaliacao where item_cardapio_cod = ?");
            stmtQtd.setInt(1, item.getCodigo());
            rs = stmtQtd.executeQuery();
            if (rs.next()) {                
                return rs.getBigDecimal("total");                
            } else{
                return BigDecimal.ZERO;
            }
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {rs.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar rs.");}
            try {stmtQtd.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }       
    }
}
