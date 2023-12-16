/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import bean.Pagamento;
import bean.enums.SituacaoPagamento;

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
public class PagamentoDAO implements Serializable{
    private String insert = "insert into pagamento (valor_pedido, valor_desconto, valor_pago, situacao, "
                + "funcionario_cod) values (?,?,?,?,?)";        
    private String selectPorCodigo = "select * from pagamento where codigo = ?";    
    
    public Pagamento obter(int codigo){
        Connection con = null;
        PreparedStatement stmtObter = null;
        ResultSet rs = null;
        Pagamento pagamento = new Pagamento();
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
        
        try {
            con = ConnectionFactory.getConnection();
            stmtObter = con.prepareStatement(selectPorCodigo);
            stmtObter.setInt(1, codigo);
            rs = stmtObter.executeQuery();
            
            if(rs.next()){
                
                pagamento.setValorPedido(rs.getBigDecimal("valor_pedido"));
                pagamento.setValorDesconto(rs.getBigDecimal("valor_desconto"));
                pagamento.setValorPago(rs.getBigDecimal("valor_pago"));
                pagamento.setSituacao(SituacaoPagamento.converter(rs.getInt("situacao")));
                pagamento.setFuncionario(funcionarioDAO.obter(rs.getInt("funcionario_cod")));
                pagamento.setCodigo(rs.getInt("codigo"));
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
        return pagamento;
    }
    
    public Integer incluir(Pagamento pagamento){
        Connection con = null;
        PreparedStatement pstmtAdiciona = null;
        try {
            con = ConnectionFactory.getConnection();
            pstmtAdiciona = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            
            int index = 1;
            
            pstmtAdiciona.setBigDecimal(index++, pagamento.getValorPedido());
            pstmtAdiciona.setBigDecimal(index++, pagamento.getValorDesconto());
            pstmtAdiciona.setBigDecimal(index++, pagamento.getValorPago());
            pstmtAdiciona.setInt(index++, pagamento.getSituacao().getCodigo());
            pstmtAdiciona.setInt(index++, pagamento.getFuncionario().getCodigo());
            
            pstmtAdiciona.execute();
            
            ResultSet rs = pstmtAdiciona.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {pstmtAdiciona.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }        
    }
    
    public List<Pagamento> getLista(Map<String,Object> filtros) {
        Connection con = null;
        PreparedStatement stmtLista = null;
        ResultSet rs = null;
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();

        try {                        
            con = ConnectionFactory.getConnection();
            stmtLista = con.prepareStatement(montarConsultaFiltros(filtros, false));
            rs = stmtLista.executeQuery();
            List<Pagamento> pagamentos = new ArrayList();
            while (rs.next()) {
                
                Pagamento pagamento = new Pagamento();
                
                pagamento.setValorPedido(rs.getBigDecimal("valor_pedido"));
                pagamento.setValorDesconto(rs.getBigDecimal("valor_desconto"));
                pagamento.setValorPago(rs.getBigDecimal("valor_pago"));
                pagamento.setSituacao(SituacaoPagamento.converter(rs.getInt("situacao")));
                pagamento.setFuncionario(funcionarioDAO.obter(rs.getInt("funcionario_cod")));
                pagamento.setCodigo(rs.getInt("codigo"));

                pagamentos.add(pagamento);
            }
            
            return pagamentos;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
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
                selectFiltros = "select count(*) total from pagamento"
                        + " left join pedido on pagamento.codigo = pedido.pagamento_cod"
                        + " where pagamento.codigo != -1";
            } else {
                selectFiltros = "select pagamento.* from pagamento"
                        + " left join pedido on pagamento.codigo = pedido.pagamento_cod"
                        + " where pagamento.codigo != -1";
            }
            
            if ((filtros.get("periodoDe") != null) && (filtros.get("periodoAte") != null)) {                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                selectFiltros = selectFiltros +  " and DATE(data_hora) between '" + sdf.format((Date) filtros.get("periodoDe")) 
                        +"' and '"+ sdf.format((Date) filtros.get("periodoAte")) + "'";
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
