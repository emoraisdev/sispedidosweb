/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import bean.Sugestao;
import bean.enums.TipoSugestao;

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
public class SugestaoDAO {
    private String insert = "insert into sugestao (tipo, mensagem, cliente_cod, data_hora)"
            + "values (?,?,?,?)";
    
    public void incluir(Sugestao sugestao){
        Connection con = null;
        PreparedStatement pstmtAdiciona = null;
        try {
            con = ConnectionFactory.getConnection();
            pstmtAdiciona = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            
            int index = 1;
            
            pstmtAdiciona.setInt(index++, sugestao.getTipo().getCodigo());
            pstmtAdiciona.setString(index++, sugestao.getMensagem());
            pstmtAdiciona.setInt(index++, sugestao.getCliente().getCodigo());
            pstmtAdiciona.setString(index++, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                    .format(sugestao.getDataHora()));
            
            pstmtAdiciona.execute();
            
            ResultSet rs = pstmtAdiciona.getGeneratedKeys();
            rs.next();
            sugestao.setCodigo(rs.getInt(1));
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {pstmtAdiciona.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }
    }
    
       public List<Sugestao> getLista(Map<String,Object> filtros) {
        Connection con = null;
        PreparedStatement stmtLista = null;
        ResultSet rs = null;
        ClienteDAO clienteDAO = new ClienteDAO();
        
        try {
            
            con = ConnectionFactory.getConnection();
            stmtLista = con.prepareStatement(montarConsultaFiltros(filtros, false));
            
            rs = stmtLista.executeQuery();
            
            List<Sugestao> sugestoes = new ArrayList();
            while (rs.next()) {
                //
                Sugestao sugestao = new Sugestao();
                
                sugestao.setCodigo(rs.getInt("codigo"));
                sugestao.setTipo(TipoSugestao.converter(rs.getInt("tipo")));
                sugestao.setCliente(clienteDAO.obter(rs.getInt("cliente_cod")));
                sugestao.setMensagem(rs.getString("mensagem"));
                sugestao.setDataHora(rs.getTimestamp("data_hora")); 

                sugestoes.add(sugestao);
            }
            
            return sugestoes;
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
                selectFiltros = "select count(*) total from sugestao where codigo != -1 ";
            } else {
                selectFiltros = "select * from sugestao where codigo != -1 ";
            }
            
            if ((filtros.get("periodoDe") != null) && (filtros.get("periodoAte") != null)) {                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                selectFiltros = selectFiltros +  " and DATE(data_hora) between '" + sdf.format((Date) filtros.get("periodoDe")) 
                        +"' and '"+ sdf.format((Date) filtros.get("periodoAte")) + "'";
            }
            
            if (filtros.get("tipoSugestao") != null) {
                selectFiltros = selectFiltros + " and tipo = " + ((TipoSugestao) filtros.get("tipoSugestao")).getCodigo();
            } 
             
            if (!qtd) {
                selectFiltros = selectFiltros + " order by data_hora desc";                
                
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
