/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import bean.GrupoCardapio;
import bean.enums.NivelGrupo;
import bean.enums.SituacaoGrupo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Everton
 */
public class GrupoCardapioDAO implements Serializable{
    private String insert = "insert into grupo_cardapio (nome, grupo_pai_cod, nivel, situacao) values (?,?,?,?)";
    private String update = "update grupo_cardapio set nome = ?, grupo_pai_cod = ?, nivel = ?, situacao = ? "
            + "where codigo = ?";
    private String selectPorCodigo = "select * from grupo_cardapio where codigo = ?";
    private String select = "select * from grupo_cardapio";
    private String selectAtivos = "select * from grupo_cardapio where situacao = 1";
    
    public void incluir(GrupoCardapio grupo){
        Connection con = null;
        PreparedStatement pstmtAdiciona = null;
        try {
            con = ConnectionFactory.getConnection();
            pstmtAdiciona = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            
            pstmtAdiciona.setString(1, grupo.getNome());
            if (grupo.getGrupoPai().getCodigo() != -1){
                pstmtAdiciona.setInt(2, grupo.getGrupoPai().getCodigo());
            } else {
                pstmtAdiciona.setNull(2, java.sql.Types.INTEGER);
            }
            pstmtAdiciona.setInt(3, grupo.getNivel().getCodigo());
            pstmtAdiciona.setInt(4, grupo.getSituacao().getCodigo());
            
            pstmtAdiciona.execute();
            
            ResultSet rs = pstmtAdiciona.getGeneratedKeys();
            rs.next();
            grupo.setCodigo(rs.getInt(1));
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {pstmtAdiciona.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }
    }
    
    public void alterar(GrupoCardapio grupo) {
        Connection con = null;
        PreparedStatement pstmtAtualiza = null;
        try {
            con = ConnectionFactory.getConnection();
            pstmtAtualiza = con.prepareStatement(update);  
            
            pstmtAtualiza.setString(1, grupo.getNome());
            if (grupo.getGrupoPai().getCodigo() != -1){
                pstmtAtualiza.setInt(2, grupo.getGrupoPai().getCodigo());
            } else {
                pstmtAtualiza.setNull(2, java.sql.Types.INTEGER);
            }
            pstmtAtualiza.setInt(3, grupo.getNivel().getCodigo());
            pstmtAtualiza.setInt(4, grupo.getSituacao().getCodigo());
            pstmtAtualiza.setInt(5, grupo.getCodigo());           
            
            pstmtAtualiza.executeUpdate();
        } catch (SQLException ex) {
               throw new RuntimeException(ex.getMessage());
        } finally{
            try {pstmtAtualiza.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar pstmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}
 
        }        
    }    
    
    
    public GrupoCardapio obter(int codigo){
        return obter(codigo, false);
    }
    
    public GrupoCardapio obter(int codigo, boolean carregarItens){
        Connection con = null;
        PreparedStatement stmtObter = null;
        ResultSet rs = null;
        GrupoCardapio grupo = new GrupoCardapio();
        ItemCardapioDAO itemDAO = new ItemCardapioDAO();
        try {
            con = ConnectionFactory.getConnection();
            stmtObter = con.prepareStatement(selectPorCodigo);
            stmtObter.setInt(1, codigo);
            rs = stmtObter.executeQuery();
            if(rs.next()){
                grupo.setCodigo(rs.getInt("codigo"));
                grupo.setNome(rs.getString("nome"));
                grupo.setNivel(NivelGrupo.converter(rs.getInt("nivel")));
                grupo.setSituacao(SituacaoGrupo.converter(rs.getInt("situacao")));
                grupo.setGrupoPai(new GrupoCardapio(rs.getInt("grupo_pai_cod")));
                
                if (carregarItens) {
                    grupo.setItens(itemDAO.getListaPorGrupo(grupo));
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {rs.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar rs.");}
            try {stmtObter.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }
        return grupo;        
    }
    public List<GrupoCardapio> getLista(boolean somenteAtivos) {
        return getLista(somenteAtivos, false, true, null, null);
    }
    
    public List<GrupoCardapio> getLista(boolean somenteAtivos, boolean carregarItens, boolean carregarFilhos,
             Integer startIndex, Integer pageSize) {
        Connection con = null;
        PreparedStatement stmtLista = null;
        ResultSet rs = null;
        ItemCardapioDAO itemDAO = new ItemCardapioDAO();
        String consulta = select;
        
        if (somenteAtivos){
            consulta = selectAtivos;
        }
        
        if (startIndex != null && pageSize != null) {
            consulta = consulta+ " limit "+String.valueOf(startIndex)+","+String.valueOf(pageSize);
        }
        
        try {
            con = ConnectionFactory.getConnection();
            stmtLista = con.prepareStatement(consulta);
            rs = stmtLista.executeQuery();
            List<GrupoCardapio> grupos = new ArrayList();
            while (rs.next()) {
                //
                GrupoCardapio grupo = new GrupoCardapio();
                
                grupo.setCodigo(rs.getInt("codigo"));
                grupo.setNome(rs.getString("nome"));
                grupo.setNivel(NivelGrupo.converter(rs.getInt("nivel")));
                grupo.setSituacao(SituacaoGrupo.converter(rs.getInt("situacao")));
                grupo.setGrupoPai(obter(rs.getInt("grupo_pai_cod")));
                
                if (carregarFilhos) {
                    grupo.setGruposFilhos(getGruposFilhos(grupo));
                }
                
                if (carregarItens){
                    grupo.setItens(itemDAO.getListaPorGrupo(grupo));
                }
                
                grupos.add(grupo);
            }                                                
            
            return grupos;
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
            stmtQtd = con.prepareStatement("select count(*) total from grupo_cardapio");
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
    
    public List<GrupoCardapio> getGruposNivel(NivelGrupo nivel) {
        return getGruposNivel(nivel, false, false);
    }    
    
    public List<GrupoCardapio> getGruposNivel(NivelGrupo nivel, boolean carregarItens, boolean somenteAtivos) {
        Connection con = null;
        PreparedStatement stmtLista = null;
        ResultSet rs = null;
        ItemCardapioDAO itemDAO = new ItemCardapioDAO();
        try {
            con = ConnectionFactory.getConnection();            
            
            String selectNivel = "select * from grupo_cardapio where nivel = ?";
            
            if (somenteAtivos) {
                selectNivel = selectNivel + " and situacao = 1";
            }
            
            stmtLista = con.prepareStatement(selectNivel);
            stmtLista.setInt(1, nivel.getCodigo());
            rs = stmtLista.executeQuery();
            List<GrupoCardapio> grupos = new ArrayList();
            while (rs.next()) {
                //
                GrupoCardapio grupo = new GrupoCardapio();
                
                grupo.setCodigo(rs.getInt("codigo"));
                grupo.setNome(rs.getString("nome"));
                grupo.setNivel(NivelGrupo.converter(rs.getInt("nivel")));
                grupo.setSituacao(SituacaoGrupo.converter(rs.getInt("situacao")));
                grupo.setGrupoPai(obter(rs.getInt("grupo_pai_cod")));
                grupo.setGruposFilhos(getGruposFilhos(grupo, carregarItens, somenteAtivos));               

                if (carregarItens){
                    grupo.setItens(itemDAO.getListaPorGrupo(grupo, somenteAtivos));
                }
                
                grupos.add(grupo);
            }                                                
            
            return grupos;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {rs.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar rs.");}
            try {stmtLista.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}
            
        }       
    }
    
    public List<GrupoCardapio> getGruposFilhos(GrupoCardapio grupo) {
        return getGruposFilhos(grupo, false, false);
    }
    
    public List<GrupoCardapio> getGruposFilhos(GrupoCardapio grupo, boolean carregarItens, boolean somenteAtivos) {
        Connection con = null;
        PreparedStatement stmtLista = null;
        ResultSet rs = null;
        ItemCardapioDAO itemDAO = new ItemCardapioDAO();
        try {
            con = ConnectionFactory.getConnection();            
            
            String selectFilhos = "select * from grupo_cardapio where grupo_pai_cod = ?";
            
            if (somenteAtivos) {
                selectFilhos = selectFilhos + " and situacao = 1";
            }
                        
            stmtLista = con.prepareStatement(selectFilhos);
            stmtLista.setInt(1, grupo.getCodigo());
            rs = stmtLista.executeQuery();
            List<GrupoCardapio> grupos = new ArrayList();
            while (rs.next()) {
                //
                GrupoCardapio grupoFilho = new GrupoCardapio();
                
                grupoFilho.setCodigo(rs.getInt("codigo"));
                grupoFilho.setNome(rs.getString("nome"));
                grupoFilho.setNivel(NivelGrupo.converter(rs.getInt("nivel")));
                grupoFilho.setSituacao(SituacaoGrupo.converter(rs.getInt("situacao")));
                grupoFilho.setGrupoPai(grupo);
                grupoFilho.setGruposFilhos(getGruposFilhos(grupoFilho, carregarItens, somenteAtivos));
                
                if (carregarItens){
                    grupoFilho.setItens(itemDAO.getListaPorGrupo(grupoFilho, somenteAtivos));
                }

                grupos.add(grupoFilho);
            }                                                
            
            return grupos;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {rs.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar rs.");}
            try {stmtLista.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}
            
        }       
    }
}
