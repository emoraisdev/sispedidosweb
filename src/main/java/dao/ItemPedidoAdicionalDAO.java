/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import bean.ItemPedido;
import bean.ItemPedidoAdicional;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Everton
 */
public class ItemPedidoAdicionalDAO implements Serializable{
    private String insert = "insert into item_pedido_adicional(valor, item_pedido_cod, item_adicional_cod)"
            + " values (?,?,?)";
    private String selectPorItemPedido = "select * from item_pedido_adicional where item_pedido_cod = ?";
    private String select = "select * from item_pedido_adicional";
    
    public void incluir(ItemPedidoAdicional itemPedidoAdicional){
        Connection con = null;
        PreparedStatement pstmtAdiciona = null;
        try {
            con = ConnectionFactory.getConnection();
            pstmtAdiciona = con.prepareStatement(insert);
            
            int index = 1;
            
            pstmtAdiciona.setBigDecimal(index++, itemPedidoAdicional.getValor());
            pstmtAdiciona.setInt(index++, itemPedidoAdicional.getItemPedido().getCodigo());
            pstmtAdiciona.setInt(index++, itemPedidoAdicional.getItemAdicional().getCodigo());
                        
            pstmtAdiciona.execute();                       
                                               
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally{
            try {pstmtAdiciona.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }
    }     
    
  
    
    public List<ItemPedidoAdicional> getListaPorItemPedido(ItemPedido itemPedido) {
        Connection con = null;
        PreparedStatement stmtLista = null;
        ResultSet rs = null;
        try {
            ItemAdicionalDAO itemAdicionalDAO = new ItemAdicionalDAO();
            con = ConnectionFactory.getConnection();
            stmtLista = con.prepareStatement(selectPorItemPedido);
            stmtLista.setInt(1, itemPedido.getCodigo());
            rs = stmtLista.executeQuery();
            List<ItemPedidoAdicional> itens = new ArrayList();
            while (rs.next()) {
                //
                ItemPedidoAdicional itemPedidoAdicional = new ItemPedidoAdicional();
                
                itemPedidoAdicional.setValor(rs.getBigDecimal("valor"));                
                itemPedidoAdicional.setItemPedido(new ItemPedido(itemPedido.getCodigo()));
                itemPedidoAdicional.setItemAdicional(itemAdicionalDAO.obter(rs.getInt("item_adicional_cod")));

                itens.add(itemPedidoAdicional);
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
