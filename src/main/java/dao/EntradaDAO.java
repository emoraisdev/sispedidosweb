/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import bean.Cliente;
import bean.CodigoEntrada;
import bean.Entrada;

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
public class EntradaDAO {

    private String insert = "insert into entrada (codigo_entrada_cod, cliente_cod, data_entrada, data_saida) "           
            + "values (?,?,?,?)";     
    private String selectPorCodigoEntrada = "select * from entrada where codigo_entrada_cod = ?";
    private String selectEntradaCliente = "select * from entrada where cliente_cod = ? and data_saida > ?";
    
    public Entrada incluir(Entrada entrada ){
        Connection con = null;
        PreparedStatement pstmtAdiciona = null;
        try {
            con = ConnectionFactory.getConnection();
            pstmtAdiciona = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
           
            int index = 1;
            
            pstmtAdiciona.setInt(index++, entrada.getCodigoEntrada().getCodigo());
            pstmtAdiciona.setInt(index++, entrada.getCliente().getCodigo());
            pstmtAdiciona.setString(index++, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(entrada.getDataEntrada()));
            pstmtAdiciona.setString(index++, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(entrada.getDataSaida()));
                               
            pstmtAdiciona.execute();
            
            ResultSet rs = pstmtAdiciona.getGeneratedKeys();
            rs.next();
            entrada.setCodigo(rs.getInt(1));
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        } finally{
            try {pstmtAdiciona.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }
        return entrada;
    }
    
    public boolean verificaSeExiste(int codigoEntrada){
        Connection con = null;
        PreparedStatement pstmtVerifica = null;
        try {
            con = ConnectionFactory.getConnection();
            pstmtVerifica = con.prepareStatement(selectPorCodigoEntrada);
                                    
            pstmtVerifica.setInt(1, codigoEntrada);           
                        
            ResultSet rs = pstmtVerifica.executeQuery();
            
            return rs.next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {pstmtVerifica.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }
    }
    
    public Entrada obterEntradaValida(int codCliente){
        Connection con = null;
        PreparedStatement pstmtVerifica = null;
        try {
            con = ConnectionFactory.getConnection();
            pstmtVerifica = con.prepareStatement(selectEntradaCliente);
                                    
            pstmtVerifica.setInt(1, codCliente);           
            pstmtVerifica.setString(2, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));           
                        
            ResultSet rs = pstmtVerifica.executeQuery();                       
            
            Entrada entrada = null;
            
            if(rs.next()){
                entrada = new Entrada();
                entrada.setCodigo(rs.getInt("codigo"));
                entrada.setCliente(new Cliente(rs.getInt("cliente_cod")));
                entrada.setCodigoEntrada(new CodigoEntrada(rs.getInt("codigo_entrada_cod")));
                entrada.setDataEntrada(rs.getTimestamp("data_entrada"));
                entrada.setDataSaida(rs.getTimestamp("data_saida"));
            }

            return entrada;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {pstmtVerifica.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }
    }
    
    public List<Entrada> obterRegistrosValidos(Map<String,Object> filtros){
        Connection con = null;
        PreparedStatement pstmtVerifica = null;
                
        try {
            con = ConnectionFactory.getConnection();
            pstmtVerifica = con.prepareStatement(montarConsultaFiltros(filtros, false));
                                    
            pstmtVerifica.setString(1, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));           
                        
            ResultSet rs = pstmtVerifica.executeQuery();                       
            
            List<Entrada> registrosEntrada = new ArrayList();
            ClienteDAO clienteDAO = new ClienteDAO();
            CodigoEntradaDAO codigoEntradaDAO = new CodigoEntradaDAO();
            
            
            while (rs.next()){
                Entrada entrada = new Entrada();
                
                entrada.setCodigo(rs.getInt("codigo"));
                entrada.setCliente(clienteDAO.obter(rs.getInt("cliente_cod")));
                entrada.setCodigoEntrada(codigoEntradaDAO.obter(rs.getInt("codigo_entrada_cod")));
                entrada.setDataEntrada(rs.getTimestamp("data_entrada"));
                entrada.setDataSaida(rs.getTimestamp("data_saida"));
                
                registrosEntrada.add(entrada);
            }

            return registrosEntrada;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {pstmtVerifica.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }
    }
    
    public BigDecimal getQtdRegistrosValidos(Map<String,Object> filtros) {
        Connection con = null;
        PreparedStatement stmtQtd = null;
        ResultSet rs = null;
        
        try {
            con = ConnectionFactory.getConnection();
            stmtQtd = con.prepareStatement(montarConsultaFiltros(filtros, true));
            stmtQtd.setString(1, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));           
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
                selectFiltros = "select count(*) total from entrada"
                    + " inner join usuario on usuario.codigo = entrada.cliente_cod "
                    + " where data_saida > ? ";
            } else {
                selectFiltros = "select * from entrada"
                    + " inner join usuario on usuario.codigo = entrada.cliente_cod "
                    + " where data_saida > ? ";
            }
            
            if (filtros.get("login") != null) {
                selectFiltros = selectFiltros + " and usuario.login like '%" + (String) filtros.get("login")+"%' ";
            }

            if (filtros.get("nome") != null) {
                selectFiltros = selectFiltros + " and usuario.nome like '%" + (String) filtros.get("nome")+"%' ";
            }  

            if (!qtd) {
                selectFiltros = selectFiltros + " order by entrada.data_entrada desc ";
                
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
