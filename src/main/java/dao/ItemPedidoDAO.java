/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import bean.Funcionario;
import bean.ItemPedido;
import bean.ItemPedidoAdicional;
import bean.Pedido;
import bean.enums.SituacaoItemPedido;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Everton
 */
public class ItemPedidoDAO implements Serializable{

    private String insert = "insert into item_pedido (quantidade, valor, observacao, situacao, pedido_cod, "
            + "item_cardapio_cod) values (?,?,?,?,?,?)";
    private String update = "update item_pedido set quantidade = ?, valor = ?, observacao = ?, situacao = ?, "
            + "inicio_preparo = ?, termino_preparo = ?, funcionario_cod = ?, pedido_cod = ?, "
            + "item_cardapio_cod =? where codigo = ?";
    private String selectPorCodigo = "select * from item_pedido where codigo = ?";
    private String selectPorPedido = "select * from item_pedido where pedido_cod = ?";

    public void incluir(ItemPedido item) {
        Connection con = null;
        PreparedStatement pstmtAdiciona = null;
        ItemPedidoAdicionalDAO itemPedidoAdicionalDAO = new ItemPedidoAdicionalDAO();
        try {
            con = ConnectionFactory.getConnection();
            pstmtAdiciona = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);

            int index = 1;

            pstmtAdiciona.setInt(index++, item.getQuantidade());
            pstmtAdiciona.setBigDecimal(index++, item.getValor());
            pstmtAdiciona.setString(index++, item.getObservacao());
            pstmtAdiciona.setInt(index++, item.getSituacao().getCodigo());
            pstmtAdiciona.setInt(index++, item.getPedido().getCodigo());
            pstmtAdiciona.setInt(index++, item.getItem().getCodigo());

            pstmtAdiciona.execute();

            ResultSet rs = pstmtAdiciona.getGeneratedKeys();
            rs.next();
            item.setCodigo(rs.getInt(1));

            for (ItemPedidoAdicional itemPedidoAdicional : item.getItensAdicionais()) {
                itemPedidoAdicional.setItemPedido(item);
                itemPedidoAdicionalDAO.incluir(itemPedidoAdicional);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                pstmtAdiciona.close();
            } catch (SQLException ex) {
                throw new RuntimeException("Falha ao fechar stmt.");
            }
            try {
                con.close();
            } catch (SQLException ex) {
                throw new RuntimeException("Falha ao fechar conexão.");
            }
        }
    }

    public void alterar(ItemPedido item) {
        Connection con = null;
        PreparedStatement pstmtAtualiza = null;
        try {
            con = ConnectionFactory.getConnection();
            pstmtAtualiza = con.prepareStatement(update);

            int index = 1;
            pstmtAtualiza.setInt(index++, item.getQuantidade());
            pstmtAtualiza.setBigDecimal(index++, item.getValor());
            pstmtAtualiza.setString(index++, item.getObservacao());
            pstmtAtualiza.setInt(index++, item.getSituacao().getCodigo());
            
            if (item.getInicioPreparo() != null) {
                pstmtAtualiza.setString(index++, 
                        (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(item.getInicioPreparo()));
            } else {
                pstmtAtualiza.setNull(index++, java.sql.Types.DATE);
            }
            
            if (item.getTerminoPreparo() != null) {
                pstmtAtualiza.setString(index++, 
                        (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(item.getTerminoPreparo()));
            } else {
                pstmtAtualiza.setNull(index++, java.sql.Types.DATE);
            }

            if (item.getFuncionario().getCodigo() > 0) {
                pstmtAtualiza.setInt(index++, item.getFuncionario().getCodigo());
            } else {
                pstmtAtualiza.setNull(index++, java.sql.Types.INTEGER);
            }

            pstmtAtualiza.setInt(index++, item.getPedido().getCodigo());
            pstmtAtualiza.setInt(index++, item.getItem().getCodigo());
            pstmtAtualiza.setInt(index++, item.getCodigo());

            pstmtAtualiza.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
            try {
                pstmtAtualiza.close();
            } catch (SQLException ex) {
                throw new RuntimeException("Falha ao fechar pstmt.");
            }
            try {
                con.close();
            } catch (SQLException ex) {
                throw new RuntimeException("Falha ao fechar conexão.");
            }

        }
    }

    public ItemPedido obter(Integer codigo) {
        Connection con = null;
        PreparedStatement stmtObter = null;
        ResultSet rs = null;
        ItemPedido itemPedido = null;
        try {
            ItemCardapioDAO itemCardapioDAO = new ItemCardapioDAO();
            ItemPedidoAdicionalDAO itemPedidoAdicionalDAO = new ItemPedidoAdicionalDAO();
            con = ConnectionFactory.getConnection();
            stmtObter = con.prepareStatement(selectPorCodigo);
            stmtObter.setInt(1, codigo);
            rs = stmtObter.executeQuery();                       
            while (rs.next()) {
                itemPedido = new ItemPedido();

                itemPedido.setCodigo(rs.getInt("codigo"));
                itemPedido.setQuantidade(rs.getInt("quantidade"));
                itemPedido.setValor(rs.getBigDecimal("valor"));
                itemPedido.setObservacao(rs.getString("observacao"));
                itemPedido.setSituacao(SituacaoItemPedido.converter(rs.getInt("situacao")));
                
                if (rs.getString("inicio_preparo") != null) {                    
                    itemPedido.setInicioPreparo(rs.getTimestamp("inicio_preparo")); 
                }
                
                if (rs.getString("termino_preparo") != null) {                    
                    itemPedido.setTerminoPreparo(rs.getTimestamp("termino_preparo")); 
                }

                if (rs.getInt("funcionario_cod") > 0) {
                    itemPedido.setFuncionario(new Funcionario(rs.getInt("funcionario_cod")));
                } else {
                    itemPedido.setFuncionario(new Funcionario(-1));
                }

                itemPedido.setPedido(new Pedido(rs.getInt("pedido_cod")));
                itemPedido.setItem(itemCardapioDAO.obter(rs.getInt("item_cardapio_cod")));
                itemPedido.setItensAdicionais(itemPedidoAdicionalDAO.getListaPorItemPedido(itemPedido));               
            }

            return itemPedido;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                rs.close();
            } catch (SQLException ex) {
                throw new RuntimeException("Falha ao fechar rs.");
            }
            try {
                stmtObter.close();
            } catch (SQLException ex) {
                throw new RuntimeException("Falha ao fechar stmt.");
            }
            try {
                con.close();
            } catch (SQLException ex) {
                throw new RuntimeException("Falha ao fechar conexão.");
            }

        }
    }
    
        public ItemPedido obterPreparoEmAndamento(Integer codFuncionario) {
        Connection con = null;
        PreparedStatement stmtObter = null;
        ResultSet rs = null;
        ItemPedido itemPedido = null;
        String consulta = "select * from item_pedido"
                + " where funcionario_cod = ? and inicio_preparo is not null"
                + " and termino_preparo is null and situacao = 2";
        try {
            ItemCardapioDAO itemCardapioDAO = new ItemCardapioDAO();            
            ItemPedidoAdicionalDAO itemPedidoAdicionalDAO = new ItemPedidoAdicionalDAO();
            PedidoDAO pedidoDAO = new PedidoDAO();
            con = ConnectionFactory.getConnection();
            stmtObter = con.prepareStatement(consulta);
            stmtObter.setInt(1, codFuncionario);
            rs = stmtObter.executeQuery();                       
            if (rs.next()) {
                itemPedido = new ItemPedido();

                itemPedido.setCodigo(rs.getInt("codigo"));
                itemPedido.setQuantidade(rs.getInt("quantidade"));
                itemPedido.setValor(rs.getBigDecimal("valor"));
                itemPedido.setObservacao(rs.getString("observacao"));
                itemPedido.setSituacao(SituacaoItemPedido.converter(rs.getInt("situacao")));
                
                if (rs.getString("inicio_preparo") != null) {                    
                    itemPedido.setInicioPreparo(rs.getTimestamp("inicio_preparo")); 
                }
                
                if (rs.getString("termino_preparo") != null) {                    
                    itemPedido.setTerminoPreparo(rs.getTimestamp("termino_preparo")); 
                }


                itemPedido.setFuncionario(new Funcionario(codFuncionario));                
                itemPedido.setPedido(pedidoDAO.obter(rs.getInt("pedido_cod")));
                itemPedido.setItem(itemCardapioDAO.obter(rs.getInt("item_cardapio_cod")));
                itemPedido.setItensAdicionais(itemPedidoAdicionalDAO.getListaPorItemPedido(itemPedido));               
            }

            return itemPedido;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                rs.close();
            } catch (SQLException ex) {
                throw new RuntimeException("Falha ao fechar rs.");
            }
            try {
                stmtObter.close();
            } catch (SQLException ex) {
                throw new RuntimeException("Falha ao fechar stmt.");
            }
            try {
                con.close();
            } catch (SQLException ex) {
                throw new RuntimeException("Falha ao fechar conexão.");
            }

        }
    }
    
    public List<ItemPedido> getListaPorPedido(Pedido pedido) {
        Connection con = null;
        PreparedStatement stmtLista = null;
        ResultSet rs = null;
        try {
            con = ConnectionFactory.getConnection();
            stmtLista = con.prepareStatement(selectPorPedido);
            stmtLista.setInt(1, pedido.getCodigo());
            rs = stmtLista.executeQuery();
            List<ItemPedido> itens = new ArrayList();
            ItemCardapioDAO itemCardapioDAO = new ItemCardapioDAO();
            ItemPedidoAdicionalDAO itemPedidoAdicionalDAO = new ItemPedidoAdicionalDAO();
            while (rs.next()) {
                //
                ItemPedido item = new ItemPedido();

                item.setCodigo(rs.getInt("codigo"));
                item.setQuantidade(rs.getInt("quantidade"));
                item.setValor(rs.getBigDecimal("valor"));
                item.setObservacao(rs.getString("observacao"));
                item.setSituacao(SituacaoItemPedido.converter(rs.getInt("situacao")));

                if (rs.getString("inicio_preparo") != null) {                    
                    item.setInicioPreparo(rs.getTimestamp("inicio_preparo")); 
                }
                
                if (rs.getString("termino_preparo") != null) {                    
                    item.setTerminoPreparo(rs.getTimestamp("termino_preparo")); 
                }


                if (rs.getInt("funcionario_cod") > 0) {
                    item.setFuncionario(new Funcionario(rs.getInt("funcionario_cod")));
                } else {
                    item.setFuncionario(new Funcionario(-1));
                }

                item.setPedido(new Pedido(pedido.getCodigo()));
                item.setItem(itemCardapioDAO.obter(rs.getInt("item_cardapio_cod")));
                item.setItensAdicionais(itemPedidoAdicionalDAO.getListaPorItemPedido(item));

                itens.add(item);
            }

            return itens;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                rs.close();
            } catch (SQLException ex) {
                throw new RuntimeException("Falha ao fechar rs.");
            }
            try {
                stmtLista.close();
            } catch (SQLException ex) {
                throw new RuntimeException("Falha ao fechar stmt.");
            }
            try {
                con.close();
            } catch (SQLException ex) {
                throw new RuntimeException("Falha ao fechar conexão.");
            }

        }
    }

    public List<ItemPedido> getLista(Map<String, Object> filtros) {
        Connection con = null;
        PreparedStatement stmtLista = null;
        ResultSet rs = null;    
        
        try {
            ItemCardapioDAO itemCardapioDAO = new ItemCardapioDAO();
            ItemPedidoAdicionalDAO itemPedidoAdicionalDAO = new ItemPedidoAdicionalDAO();
            PedidoDAO pedidoDAO = new PedidoDAO();

            FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
            
            con = ConnectionFactory.getConnection();
            stmtLista = con.prepareStatement(montarConsultaFiltros(filtros, false));
            rs = stmtLista.executeQuery();
            List<ItemPedido> itens = new ArrayList();                        
            while (rs.next()) {
                //
                ItemPedido item = new ItemPedido();

                item.setCodigo(rs.getInt("codigo"));
                item.setQuantidade(rs.getInt("quantidade"));
                item.setValor(rs.getBigDecimal("valor"));
                item.setObservacao(rs.getString("observacao"));
                item.setSituacao(SituacaoItemPedido.converter(rs.getInt("situacao")));

                if (rs.getString("inicio_preparo") != null) {                    
                    item.setInicioPreparo(rs.getTimestamp("inicio_preparo")); 
                }
                
                if (rs.getString("termino_preparo") != null) {                    
                    item.setTerminoPreparo(rs.getTimestamp("termino_preparo")); 
                }


                if (rs.getInt("funcionario_cod") > 0) {
                    item.setFuncionario(funcionarioDAO.obter(rs.getInt("funcionario_cod")));
                } else {
                    item.setFuncionario(new Funcionario(-1));
                }

                item.setPedido(pedidoDAO.obter(rs.getInt("pedido_cod")));
                item.setItem(itemCardapioDAO.obter(rs.getInt("item_cardapio_cod")));
                item.setItensAdicionais(itemPedidoAdicionalDAO.getListaPorItemPedido(item));

                itens.add(item);
            }

            return itens;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                rs.close();
            } catch (SQLException ex) {
                throw new RuntimeException("Falha ao fechar rs.");
            }
            try {
                stmtLista.close();
            } catch (SQLException ex) {
                throw new RuntimeException("Falha ao fechar stmt.");
            }
            try {
                con.close();
            } catch (SQLException ex) {
                throw new RuntimeException("Falha ao fechar conexão.");
            }

        }
    }
    
    public BigDecimal getQtdRegistrosLista(Map<String,Object> filtros) {
        Connection con = null;
        PreparedStatement stmtQtd = null;
        ResultSet rs = null;
        
        try {
            con = ConnectionFactory.getConnection();
            stmtQtd = con.prepareStatement(montarConsultaFiltros(filtros, true));
            rs = stmtQtd.executeQuery();
            if (rs.next()) {                
                return rs.getBigDecimal("total");                
            } else{
                return BigDecimal.ZERO;
            }
            
        } catch (SQLException e) {
            throw new RuntimeException(e); 
        } catch (RuntimeException e) {
            throw e;
        } finally{
            try {rs.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar rs.");}
            try {stmtQtd.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }       
    }
    
    private String montarConsultaFiltros(Map<String,Object> filtros, boolean qtd){
        try {
            String selectFiltros;
            if (qtd) {
                selectFiltros = "select count(*) total from item_pedido "
                    + "inner join pedido on pedido.codigo = item_pedido.pedido_cod "
                    + "left join usuario on usuario.codigo = item_pedido.funcionario_cod "
                    + "where item_pedido.codigo != -1 "; 
            } else {
                selectFiltros = "select item_pedido.* from item_pedido "
                    + "inner join pedido on pedido.codigo = item_pedido.pedido_cod "
                    + "left join usuario on usuario.codigo = item_pedido.funcionario_cod "
                    + "where item_pedido.codigo != -1 "; 
            }
            
            if ((filtros.get("periodoDe") != null) && (filtros.get("periodoAte") != null)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                selectFiltros = selectFiltros + " and DATE(pedido.data_hora) between '" + sdf.format((Date) filtros.get("periodoDe"))
                        + "' and '" + sdf.format((Date) filtros.get("periodoAte")) + "'";
            }

            if (filtros.get("codPedido") != null && (Integer) filtros.get("codPedido") > 0) {
                selectFiltros = selectFiltros + " and pedido.codigo = " + Integer.toString((Integer) filtros.get("codPedido"));
            }

            if (filtros.get("situacao") != null) {
                selectFiltros = selectFiltros + " and item_pedido.situacao = " + ((SituacaoItemPedido) filtros.get("situacao")).getCodigo();

                if (((SituacaoItemPedido) filtros.get("situacao")) == SituacaoItemPedido.PRONTO) {
                    selectFiltros = selectFiltros + " and item_pedido.funcionario_cod is not null";
                }
            }

            if (filtros.get("loginFuncionario") != null && !filtros.get("loginFuncionario").equals("")) {
                selectFiltros = selectFiltros + " and usuario.login like '%" + (filtros.get("loginFuncionario"))+"%'";
            }


            if (!qtd) {
                selectFiltros = selectFiltros + " order by pedido.codigo desc ";
                
                if (filtros.get("startIndex") != null && filtros.get("pageSize") != null) {
                    selectFiltros = selectFiltros+ " limit " 
                            +Integer.toString((Integer) filtros.get("startIndex"))+","
                            +Integer.toString((Integer) filtros.get("pageSize"));
                }
            }

            return selectFiltros;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
