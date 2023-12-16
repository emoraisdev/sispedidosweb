/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;


import bean.ItemAdicional;
import bean.ItemCardapio;
import bean.enums.SituacaoItemAdicional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Everton
 */
public class ItemAdicionalDAO implements Serializable{
    private String insert = "insert into item_adicional(nome, valor, situacao) values (?,?,?)";
    private String update = "update item_adicional set nome = ?, valor = ?, situacao = ? where codigo = ?";
    private String selectPorCodigo = "select * from item_adicional where codigo = ?";
    private String select = "select * from item_adicional";
    private String selectAtivos = "select * from item_adicional where situacao = 1";
    
    public void incluir(ItemAdicional itemAdicional){
        Connection con = null;
        PreparedStatement pstmtAdiciona = null;
        try {
            con = ConnectionFactory.getConnection();
            pstmtAdiciona = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            
            int index = 1;
            
            pstmtAdiciona.setString(index++, itemAdicional.getNome());
            pstmtAdiciona.setBigDecimal(index++, itemAdicional.getValor());
            pstmtAdiciona.setInt(index++, itemAdicional.getSituacao().getCodigo());
                        
            pstmtAdiciona.execute();                       
                        
            ResultSet rs = pstmtAdiciona.getGeneratedKeys();
            rs.next();
            itemAdicional.setCodigo(rs.getInt(1));            
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally{
            try {pstmtAdiciona.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }
    }   
    
    public void alterar(ItemAdicional itemAdicional) {
        Connection con = null;
        PreparedStatement pstmtAtualiza = null;
        try {
            con = ConnectionFactory.getConnection();
            pstmtAtualiza = con.prepareStatement(update);  
            
            int index = 1;
            
            pstmtAtualiza.setString(index++, itemAdicional.getNome());
            pstmtAtualiza.setBigDecimal(index++, itemAdicional.getValor());
            pstmtAtualiza.setInt(index++, itemAdicional.getSituacao().getCodigo());
            pstmtAtualiza.setInt(index++, itemAdicional.getCodigo());
            
            pstmtAtualiza.executeUpdate();
        } catch (SQLException ex) {
               throw new RuntimeException(ex.getMessage());
        } finally{
            try {pstmtAtualiza.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar pstmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}
 
        }        
    } 
    
    public ItemAdicional obter(int codigo){
        Connection con = null;
        PreparedStatement stmtObter = null;
        ResultSet rs = null;
        ItemAdicional itemAdicional = new ItemAdicional();
        try {
            con = ConnectionFactory.getConnection();
            stmtObter = con.prepareStatement(selectPorCodigo);
            stmtObter.setInt(1, codigo);
            rs = stmtObter.executeQuery();
            if(rs.next()){
                itemAdicional.setCodigo(rs.getInt("codigo"));
                itemAdicional.setNome(rs.getString("nome"));
                itemAdicional.setValor(rs.getBigDecimal("valor"));                
                itemAdicional.setSituacao(SituacaoItemAdicional.converter(rs.getInt("situacao")));                
            } else {
                throw new RuntimeException("Código inválido= " + codigo);
            }
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {rs.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar rs.");}
            try {stmtObter.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }
        return itemAdicional;
    }
    
    public List<ItemAdicional> getLista(){
        return getLista(false, null, null);
    }
    
    public List<ItemAdicional> getLista(boolean somenteAtivos, Integer startIndex, Integer pageSize) {
        Connection con = null;
        PreparedStatement stmtLista = null;
        ResultSet rs = null;
        
        String consulta = select;
        
        if (somenteAtivos) {
            consulta = selectAtivos;
        }
        
        if (startIndex != null && pageSize != null) {
            consulta = consulta+ " limit "+String.valueOf(startIndex)+","+String.valueOf(pageSize);
        }
        
        try {
            con = ConnectionFactory.getConnection();
            stmtLista = con.prepareStatement(consulta);
            rs = stmtLista.executeQuery();
            List<ItemAdicional> itens = new ArrayList();
            while (rs.next()) {
                //
                ItemAdicional itemAdicional = new ItemAdicional();
                
                itemAdicional.setCodigo(rs.getInt("codigo"));
                itemAdicional.setNome(rs.getString("nome"));
                itemAdicional.setValor(rs.getBigDecimal("valor"));                
                itemAdicional.setSituacao(SituacaoItemAdicional.converter(rs.getInt("situacao")));            

                itens.add(itemAdicional);
            }
            
            return itens;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {rs.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar rs.");}
            try {stmtLista.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}
            
        }       
    }    
    
    public BigDecimal getQtdRegistros() {
        Connection con = null;
        PreparedStatement stmtQtd = null;
        ResultSet rs = null;
        
        try {
            con = ConnectionFactory.getConnection();
            stmtQtd = con.prepareStatement("select count(*) total from item_adicional");
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
    
    public List<ItemAdicional> getListaPorItemCardapio(ItemCardapio item) {
        return this.getListaPorItemCardapio(item, false);
    }
    
    public List<ItemAdicional> getListaPorItemCardapio(ItemCardapio item, boolean somenteAtivos) {
        Connection con = null;
        PreparedStatement stmtLista = null;
        ResultSet rs = null;
        try {
            String selectPorItemCardapio = "select * from item_adicional"
                + " inner join item_cardapio_adicional on item_adicional_cod = codigo "
                + "where item_cardapio_cod = ?";
            
            if (somenteAtivos) {
                selectPorItemCardapio = selectPorItemCardapio + " and item_adicional.situacao = 1";
            }
            
            con = ConnectionFactory.getConnection();
            stmtLista = con.prepareStatement(selectPorItemCardapio);
            stmtLista.setInt(1, item.getCodigo());
            rs = stmtLista.executeQuery();
            List<ItemAdicional> itens = new ArrayList();
            while (rs.next()) {
                //
                ItemAdicional itemAdicional = new ItemAdicional();
                
                itemAdicional.setCodigo(rs.getInt("codigo"));
                itemAdicional.setNome(rs.getString("nome"));
                itemAdicional.setValor(rs.getBigDecimal("valor"));                
                itemAdicional.setSituacao(SituacaoItemAdicional.converter(rs.getInt("situacao")));            

                itens.add(itemAdicional);
            }
            
            return itens;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {rs.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar rs.");}
            try {stmtLista.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}
            
        }       
    } 
}
