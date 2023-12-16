/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import bean.GrupoCardapio;
import bean.ItemAdicional;
import bean.ItemCardapio;
import bean.enums.SituacaoItemCardapio;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Everton
 */
public class ItemCardapioDAO implements Serializable{

    private String insert = "insert into item_cardapio (nome, descricao, valor, grupo_cod, situacao, "
            + "permite_avaliacao, permite_preparo) "
            + "values (?,?,?,?,?,?,?)";
    private String update = "update item_cardapio set nome = ?, descricao = ?, valor = ?, grupo_cod = ?, "
            + "situacao = ?, permite_avaliacao = ?, permite_preparo = ? where codigo = ?";
    private String selectPorCodigo = "select * from item_cardapio where codigo = ?";
    private String selectAtivos = "select item_cardapio.*, "
            + "(select avg(nota) from avaliacao where item_cardapio_cod = item_cardapio.codigo) "
            + "media_avaliacao from item_cardapio where situacao = 1";
    private String select = "select item_cardapio.*, "
            + "(select avg(nota) from avaliacao where item_cardapio_cod = item_cardapio.codigo) "
            + "media_avaliacao from item_cardapio";    
    private String insertItemAdicional = "insert into item_cardapio_adicional (item_cardapio_cod, item_adicional_cod) "
            + "values(?,?)";
    private String deleteItemAdicional = "delete from item_cardapio_adicional where "
            + " item_cardapio_cod = ? and item_adicional_cod = ? ";
    
    public void incluir(ItemCardapio item){
        Connection con = null;
        PreparedStatement pstmtAdiciona = null;
        try {
            con = ConnectionFactory.getConnection();
            pstmtAdiciona = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            
            int index = 1;
            
            pstmtAdiciona.setString(index++, item.getNome());
            if ("".equals(item.getDescricao())) {
                pstmtAdiciona.setNull(index++, java.sql.Types.VARCHAR);
            } else {
                pstmtAdiciona.setString(index++, item.getDescricao());
            }
            pstmtAdiciona.setBigDecimal(index++, item.getValor());
            pstmtAdiciona.setInt(index++, item.getGrupo().getCodigo());
            pstmtAdiciona.setInt(index++, item.getSituacao().getCodigo());
            pstmtAdiciona.setBoolean(index++, item.isPermiteAvaliacao());
            pstmtAdiciona.setBoolean(index++, item.isPermitePreparo());
            
            pstmtAdiciona.execute();
            
            ResultSet rs = pstmtAdiciona.getGeneratedKeys();
            rs.next();
            item.setCodigo(rs.getInt(1));
            
            for (ItemAdicional itemAdicional: item.getItensAdicionais()) {                
                incluirItemAdicional(item.getCodigo(), itemAdicional.getCodigo());
            }
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {pstmtAdiciona.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }
    }
    
    private void incluirItemAdicional(Integer codItemCardapio, Integer codItemAdicional){
        Connection con = null;
        PreparedStatement pstmtAdiciona = null;
        try {
            con = ConnectionFactory.getConnection();
            pstmtAdiciona = con.prepareStatement(insertItemAdicional);
                           
            pstmtAdiciona.setInt(1, codItemCardapio);
            pstmtAdiciona.setInt(2, codItemAdicional);                

            pstmtAdiciona.execute();
                        
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {pstmtAdiciona.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }
    }
    
    private void excluirItemAdicional(Integer codItemCardapio, Integer codItemAdicional){
        Connection con = null;
        PreparedStatement pstmtAdiciona = null;
        try {
            con = ConnectionFactory.getConnection();
            pstmtAdiciona = con.prepareStatement(deleteItemAdicional);
                           
            pstmtAdiciona.setInt(1, codItemCardapio);
            pstmtAdiciona.setInt(2, codItemAdicional);                

            pstmtAdiciona.execute();
                        
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {pstmtAdiciona.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }
    }
    
    public void alterar(ItemCardapio item) {
        Connection con = null;
        PreparedStatement pstmtAtualiza = null;
        ItemAdicionalDAO itemAdicionalDAO  = new ItemAdicionalDAO();
        List<ItemAdicional> listaItensAdBanco = null;
        try {
            con = ConnectionFactory.getConnection();
            pstmtAtualiza = con.prepareStatement(update);  
            
            int index = 1;
            
            pstmtAtualiza.setString(index++, item.getNome());
            if ("".equals(item.getDescricao())) {
                pstmtAtualiza.setNull(index++, java.sql.Types.VARCHAR);
            } else {
                pstmtAtualiza.setString(index++, item.getDescricao());
            }
            pstmtAtualiza.setBigDecimal(index++, item.getValor());
            pstmtAtualiza.setInt(index++, item.getGrupo().getCodigo());
            pstmtAtualiza.setInt(index++, item.getSituacao().getCodigo());
            pstmtAtualiza.setBoolean(index++, item.isPermiteAvaliacao());
            pstmtAtualiza.setBoolean(index++, item.isPermitePreparo());
            pstmtAtualiza.setInt(index++, item.getCodigo());
            
            listaItensAdBanco = itemAdicionalDAO.getListaPorItemCardapio(item);
            boolean bEncontrou;
            for (ItemAdicional itemAdBanco: listaItensAdBanco) {
                bEncontrou = false;
                
                for (ItemAdicional itemAdNovo: item.getItensAdicionais()){                   
                    if (itemAdBanco.getCodigo() == itemAdNovo.getCodigo()) {
                        bEncontrou = true;
                    }
                }   
                
                if (!bEncontrou){
                    excluirItemAdicional(item.getCodigo(), itemAdBanco.getCodigo());
                }
            }
            
            for (ItemAdicional itemAdNovo: item.getItensAdicionais()){
                bEncontrou = false;
                for (ItemAdicional itemAdBanco: listaItensAdBanco) {
                    if (itemAdBanco.getCodigo() == itemAdNovo.getCodigo()) {
                        bEncontrou = true;
                    }
                }
                
                if (!bEncontrou) {
                    incluirItemAdicional(item.getCodigo(), itemAdNovo.getCodigo());
                }
            }  
            
            pstmtAtualiza.executeUpdate();
        } catch (SQLException ex) {
               throw new RuntimeException(ex.getMessage());
        } finally{
            try {pstmtAtualiza.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar pstmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}
 
        }        
    }    
    
    private void alterarItensAdicionais(ItemCardapio item){
        Connection con = null;
        PreparedStatement pstmtAtualiza = null;
        
        try {
            con = ConnectionFactory.getConnection();                                                                          
            pstmtAtualiza = con.prepareStatement(update);  
            
            int index = 1;
            
            pstmtAtualiza.setString(index++, item.getNome());
            if ("".equals(item.getDescricao())) {
                pstmtAtualiza.setNull(index++, java.sql.Types.VARCHAR);
            } else {
                pstmtAtualiza.setString(index++, item.getDescricao());
            }
            pstmtAtualiza.setBigDecimal(index++, item.getValor());
            pstmtAtualiza.setInt(index++, item.getGrupo().getCodigo());
            pstmtAtualiza.setInt(index++, item.getSituacao().getCodigo());
            pstmtAtualiza.setBoolean(index++, item.isPermiteAvaliacao());
            pstmtAtualiza.setBoolean(index++, item.isPermitePreparo());
            pstmtAtualiza.setInt(index++, item.getCodigo());
            
            pstmtAtualiza.executeUpdate();
        } catch (SQLException ex) {
               throw new RuntimeException(ex.getMessage());
        } finally{
            try {pstmtAtualiza.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar pstmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}
 
        }  
    }   
    
    public ItemCardapio obter(int codigo){
        Connection con = null;
        PreparedStatement stmtObter = null;
        ResultSet rs = null;
        ItemCardapio item = new ItemCardapio();
        ItemAdicionalDAO itemAdicionalDAO = new ItemAdicionalDAO();
        try {
            con = ConnectionFactory.getConnection();
            stmtObter = con.prepareStatement(selectPorCodigo);
            stmtObter.setInt(1, codigo);
            rs = stmtObter.executeQuery();
            if(rs.next()){
                item.setCodigo(rs.getInt("codigo"));
                item.setNome(rs.getString("nome"));
                item.setDescricao(rs.getString("descricao"));
                item.setValor(rs.getBigDecimal("valor"));
                item.setGrupo(new GrupoCardapio(rs.getInt("grupo_cod")));
                item.setSituacao(SituacaoItemCardapio.converter(rs.getInt("situacao")));
                item.setPermiteAvaliacao(rs.getBoolean("permite_avaliacao"));
                item.setPermitePreparo(rs.getBoolean("permite_preparo"));
                item.setItensAdicionais(itemAdicionalDAO.getListaPorItemCardapio(item));
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
        return item;
    }    
    
    public List<ItemCardapio> getLista(){
        return getLista(false, null, null);
    }
    
    public List<ItemCardapio> getLista(boolean somenteAtivos, Integer startIndex, Integer pageSize) {
        Connection con = null;
        PreparedStatement stmtLista = null;
        ResultSet rs = null;
        GrupoCardapioDAO grupoDAO = new GrupoCardapioDAO();
        ItemAdicionalDAO itemAdicionalDAO = new ItemAdicionalDAO();
        
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
            List<ItemCardapio> itens = new ArrayList();            
            while (rs.next()) {
                //
                ItemCardapio item = new ItemCardapio();
                
                item.setCodigo(rs.getInt("codigo"));
                item.setNome(rs.getString("nome"));
                item.setValor(rs.getBigDecimal("valor"));
                item.setDescricao(rs.getString("descricao"));
                item.setGrupo(grupoDAO.obter(rs.getInt("grupo_cod")));
                item.setSituacao(SituacaoItemCardapio.converter(rs.getInt("situacao")));
                item.setPermiteAvaliacao(rs.getBoolean("permite_avaliacao"));
                item.setPermitePreparo(rs.getBoolean("permite_preparo"));
                item.setItensAdicionais(itemAdicionalDAO.getListaPorItemCardapio(item, somenteAtivos));
                if (rs.getBigDecimal("media_avaliacao") != null) {
                    item.setNota(rs.getBigDecimal("media_avaliacao").setScale(1, RoundingMode.CEILING)
                            .toString().replace(".", ","));
                }

                itens.add(item);
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
            stmtQtd = con.prepareStatement("select count(*) total from item_cardapio");
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
    
    public List<ItemCardapio> getListaPorGrupo(GrupoCardapio grupo) {
        return this.getListaPorGrupo(grupo, false);
    }
    
    public List<ItemCardapio> getListaPorGrupo(GrupoCardapio grupo, boolean somenteAtivos) {
        Connection con = null;
        PreparedStatement stmtLista = null;
        ResultSet rs = null;
        ItemAdicionalDAO itemAdicionalDAO = new ItemAdicionalDAO();
        try {
            String selectPorGrupo = "select item_cardapio.*, "
                + "(select avg(nota) from avaliacao where item_cardapio_cod = item_cardapio.codigo) "
                + "media_avaliacao from item_cardapio"
                + " where grupo_cod = ?";
            
            if (somenteAtivos){
                selectPorGrupo = selectPorGrupo + " and situacao = 1";
            }
            
            con = ConnectionFactory.getConnection();
            stmtLista = con.prepareStatement(selectPorGrupo);
            stmtLista.setInt(1, grupo.getCodigo());
            rs = stmtLista.executeQuery();
            List<ItemCardapio> itens = new ArrayList();
            while (rs.next()) {
                //
                ItemCardapio item = new ItemCardapio();
                
                item.setCodigo(rs.getInt("codigo"));
                item.setNome(rs.getString("nome"));
                item.setDescricao(rs.getString("descricao"));
                item.setValor(rs.getBigDecimal("valor"));
                item.setSituacao(SituacaoItemCardapio.converter(rs.getInt("situacao")));
                item.setPermiteAvaliacao(rs.getBoolean("permite_avaliacao"));
                item.setPermitePreparo(rs.getBoolean("permite_preparo"));
                item.setItensAdicionais(itemAdicionalDAO.getListaPorItemCardapio(item, somenteAtivos));
                if (rs.getBigDecimal("media_avaliacao") != null) {
                    item.setNota(rs.getBigDecimal("media_avaliacao").setScale(1, RoundingMode.CEILING)
                            .toString().replace(".", ","));
                }
                item.setGrupo(grupo);                

                itens.add(item);
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
