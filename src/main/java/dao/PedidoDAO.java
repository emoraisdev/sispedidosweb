/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import bean.Cliente;
import bean.Funcionario;
import bean.ItemPedido;
import bean.Pedido;
import bean.enums.SituacaoPedido;

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
public class PedidoDAO implements Serializable{
    private String insert = "insert into pedido(data_hora, valor, observacao, situacao, funcionario_cod, mesa, "
               + "cliente_cod) values (?,?,?,?,?,?,?)";    
    private String selectPorCodigo = "select * from pedido where codigo = ?";    
    private String update = "update pedido set data_hora=?, valor=?, observacao=?, situacao=?, funcionario_cod=?, mesa=?, "
               + "cliente_cod=?, pagamento_cod = ? where codigo = ?";
    
    public void incluir(Pedido pedido){
        Connection con = null;
        PreparedStatement pstmtAdiciona = null;
        ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAO();
        try {
            con = ConnectionFactory.getConnection();
            pstmtAdiciona = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            
            int index = 1;
            
            pstmtAdiciona.setString(index++, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                    .format(pedido.getDataHora()));
            pstmtAdiciona.setBigDecimal(index++, pedido.getValor());
            pstmtAdiciona.setString(index++, pedido.getObservacao());
            pstmtAdiciona.setInt(index++, pedido.getSituacao().getCodigo());
            
            if (pedido.getFuncionario() != null){
                pstmtAdiciona.setInt(index++, pedido.getFuncionario().getCodigo()); 
            } else {
                pstmtAdiciona.setNull(index++, java.sql.Types.INTEGER);
            }
            
            if (pedido.getMesa() != null) {
                pstmtAdiciona.setInt(index++, pedido.getMesa());
            } else {
                pstmtAdiciona.setNull(index++, java.sql.Types.INTEGER);
            }
            
            if (pedido.getCliente().getCodigo() != -1){
                pstmtAdiciona.setInt(index++, pedido.getCliente().getCodigo()); 
            } else {
                pstmtAdiciona.setNull(index++, java.sql.Types.INTEGER);
            }            
                        
            pstmtAdiciona.execute();                       
                        
            ResultSet rs = pstmtAdiciona.getGeneratedKeys();
            rs.next();
            pedido.setCodigo(rs.getInt(1));
            
            for (ItemPedido item : pedido.getItens()) {
                item.setPedido(pedido);
                itemPedidoDAO.incluir(item);
            }
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally{
            try {pstmtAdiciona.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }
    }     
    
    public void alterar(Pedido pedido){
        Connection con = null;
        PreparedStatement pstmtAtualiza = null;
        PagamentoDAO pagamentoDAO = new PagamentoDAO();
        
        try {
            
            if (pedido.getPagamento() != null && pedido.getPagamento().getCodigo() == -1){
                pedido.getPagamento().setCodigo(pagamentoDAO.incluir(pedido.getPagamento()));
            }
            
            con = ConnectionFactory.getConnection();
            pstmtAtualiza = con.prepareStatement(update);
            
            int index = 1;                        
            
            pstmtAtualiza.setString(index++, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                    .format(pedido.getDataHora()));
            pstmtAtualiza.setBigDecimal(index++, pedido.getValor());
            pstmtAtualiza.setString(index++, pedido.getObservacao());           
            pstmtAtualiza.setInt(index++, pedido.getSituacao().getCodigo());

            if (pedido.getFuncionario().getCodigo() != -1){
                pstmtAtualiza.setInt(index++, pedido.getFuncionario().getCodigo()); 
            } else {
                pstmtAtualiza.setNull(index++, java.sql.Types.INTEGER);
            }
            
            if (pedido.getMesa() != null) {
                pstmtAtualiza.setInt(index++, pedido.getMesa());
            } else {
                pstmtAtualiza.setNull(index++, java.sql.Types.INTEGER);
            }
            
            if (pedido.getCliente().getCodigo() != -1){
                pstmtAtualiza.setInt(index++, pedido.getCliente().getCodigo()); 
            } else {
                pstmtAtualiza.setNull(index++, java.sql.Types.INTEGER);
            }            
            
            if (pedido.getPagamento() != null && pedido.getPagamento().getCodigo() != -1){
                pstmtAtualiza.setInt(index++, pedido.getPagamento().getCodigo()); 
            } else {
                pstmtAtualiza.setNull(index++, java.sql.Types.INTEGER);
            }
                        
            pstmtAtualiza.setInt(index++, pedido.getCodigo());
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
    
    public Pedido obter(int codigo){
        Connection con = null;
        PreparedStatement stmtObter = null;
        ResultSet rs = null;
        Pedido pedido = new Pedido();
        ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAO();
        PagamentoDAO pagamentoDAO = new PagamentoDAO();
        try {
            con = ConnectionFactory.getConnection();
            stmtObter = con.prepareStatement(selectPorCodigo);
            stmtObter.setInt(1, codigo);
            rs = stmtObter.executeQuery();
            if(rs.next()){
                pedido.setCodigo(rs.getInt("codigo"));
                pedido.setDataHora(rs.getTimestamp("data_hora"));                
                pedido.setValor(rs.getBigDecimal("valor"));
                pedido.setObservacao(rs.getString("observacao"));
                
                if (rs.getInt("mesa") > 0 ){
                    pedido.setMesa(rs.getInt("mesa"));
                }
                
                pedido.setSituacao(SituacaoPedido.converter(rs.getInt("situacao")));
                
                if (rs.getInt("funcionario_cod") > 0) {
                    pedido.setFuncionario(new Funcionario(rs.getInt("funcionario_cod")));
                } else {
                    pedido.setFuncionario(new Funcionario(-1));
                }
                
                if (rs.getInt("cliente_cod") > 0) {
                    pedido.setCliente(new Cliente(rs.getInt("cliente_cod")));
                } else {
                    pedido.setCliente(new Cliente(-1));
                }
                
                if (rs.getInt("pagamento_cod") > 0) {
                    pedido.setPagamento(pagamentoDAO.obter(rs.getInt("pagamento_cod")));
                } else {
                    pedido.setPagamento(null);
                }
                
                pedido.setItens(itemPedidoDAO.getListaPorPedido(pedido));
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
        return pedido;
    }
    
    public List<Pedido> getLista(Map<String,Object> filtros) {
        return getLista(filtros, true);
    }         
    
    public List<Pedido> getLista(Map<String,Object> filtros, boolean carregarItens) {
        Connection con = null;
        PreparedStatement stmtLista = null;
        ResultSet rs = null;
        ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAO();
        ClienteDAO clienteDAO = new ClienteDAO();
        PagamentoDAO pagamentoDAO = new PagamentoDAO();
        try {                        
            
            con = ConnectionFactory.getConnection();
            stmtLista = con.prepareStatement(montarConsultaFiltros(filtros, false));            
            rs = stmtLista.executeQuery();
            List<Pedido> pedidos = new ArrayList();
            while (rs.next()) {
                //
                Pedido pedido = new Pedido();
                
                pedido.setCodigo(rs.getInt("codigo"));
                pedido.setDataHora(rs.getTimestamp("data_hora"));                
                pedido.setValor(rs.getBigDecimal("valor"));
                pedido.setObservacao(rs.getString("observacao"));
                
                if (rs.getInt("mesa") > 0 ){
                    pedido.setMesa(rs.getInt("mesa"));
                }
                
                pedido.setSituacao(SituacaoPedido.converter(rs.getInt("situacao")));
                
                if (rs.getInt("funcionario_cod") > 0) {
                    pedido.setFuncionario(new Funcionario(rs.getInt("funcionario_cod")));
                } else {
                    pedido.setFuncionario(new Funcionario(-1));
                }
                
                if (rs.getInt("cliente_cod") > 0) {
                    pedido.setCliente(clienteDAO.obter(rs.getInt("cliente_cod")));
                } else {
                    pedido.setCliente(new Cliente(-1));
                }
                
                if (rs.getInt("pagamento_cod") > 0) {
                    pedido.setPagamento(pagamentoDAO.obter(rs.getInt("pagamento_cod")));
                } else {
                    pedido.setPagamento(null);
                }
                
                if (carregarItens) {
                    pedido.setItens(itemPedidoDAO.getListaPorPedido(pedido));            
                }

                pedidos.add(pedido);
            }
            
            return pedidos;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally{
            try {rs.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar rs.");}
            try {stmtLista.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}
            
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
                selectFiltros = "select count(*) total from pedido"
                        + " left join pagamento on pedido.pagamento_cod = pagamento.codigo"
                        + " where pedido.codigo != -1";        
            } else {
                selectFiltros = "select pedido.* from pedido"
                        + " left join pagamento on pedido.pagamento_cod = pagamento.codigo"
                        + " where pedido.codigo != -1";
            }


            if ((filtros.get("periodoDe") != null) && (filtros.get("periodoAte") != null)) {                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                selectFiltros = selectFiltros +  " and DATE(data_hora) between '" 
                        + sdf.format((Date) filtros.get("periodoDe")) 
                        +"' and '"+ sdf.format((Date) filtros.get("periodoAte")) + "'";
            }

            if (filtros.get("periodoDe") != null) {                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                selectFiltros = selectFiltros +  " and DATE(data_hora) >= '" 
                        + sdf.format((Date) filtros.get("periodoDe"))+ "'";
            }

            if (filtros.get("codPedido") != null && (Integer) filtros.get("codPedido") > 0) {
                selectFiltros = selectFiltros + " and pedido.codigo = " + Integer.toString((Integer) filtros.get("codPedido"));
            }

            if (filtros.get("mesa") != null && (Integer) filtros.get("mesa") > 0) {
                selectFiltros = selectFiltros + " and mesa = " + Integer.toString((Integer) filtros.get("mesa"));
            }

            if (filtros.get("situacao") != null) {
                selectFiltros = selectFiltros + " and pedido.situacao = " + ((SituacaoPedido) filtros.get("situacao")).getCodigo();
            }            

            if (filtros.get("possuiPagamento") != null) {
                if ((boolean) filtros.get("possuiPagamento")) {
                    selectFiltros = selectFiltros + " and pagamento.codigo is not null";
                } else {
                    selectFiltros = selectFiltros + " and pagamento.codigo is null";
                }
            }

            if (filtros.get("codCliente") != null) {
                selectFiltros = selectFiltros + " and pedido.cliente_cod = " + Integer.toString((Integer) filtros.get("codCliente"));
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
