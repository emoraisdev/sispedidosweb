/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import bean.Cliente;
import bean.enums.SituacaoUsuario;
import controller.VerificaDadosCliente;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Everton
 */
public class ClienteDAO implements Serializable{
    private String insert = "insert into usuario (nome, sobrenome, cpf, data_nascimento, email, telefone,"
            + "tipo, situacao, login, senha, endereco_cod, administrador, data_cadastro) "
            + "values (?,?,?,?,?,?,?,?,?,?,?,false,?)";
    private String update = "update usuario set nome = ?, sobrenome = ?, cpf = ?, data_nascimento = ?,"
            + " email = ?, telefone = ?, tipo = ?, situacao = ?, login = ?, senha = ?, endereco_cod = ? "
            + "where codigo = ?";
    private String select = "select * from usuario where tipo = 0";
    private String selectLogin = "select * from usuario where tipo = 0 and login = ? and senha = ?"
            + " and situacao = 1";
    private String selectPorCodigo = "select * from usuario where tipo = 0 and codigo = ?";
    private EnderecoDAO enderecoDAO = new EnderecoDAO();
    
    public Cliente incluir(Cliente cliente){
        Connection con = null;
        PreparedStatement pstmtAdiciona = null;
        try {
            con = ConnectionFactory.getConnection();
            pstmtAdiciona = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
           
            enderecoDAO.incluir(cliente.getEndereco());
            
            pstmtAdiciona.setString(1, cliente.getNome());
            pstmtAdiciona.setString(2, cliente.getSobrenome());
            pstmtAdiciona.setString(3, cliente.getCpf());
            pstmtAdiciona.setDate(4, new java.sql.Date(cliente.getDataNascimento().getTime()));
            pstmtAdiciona.setString(5, cliente.getEmail());
            pstmtAdiciona.setString(6, cliente.getTelefone());
            pstmtAdiciona.setInt(7, 0);//Cliente
            pstmtAdiciona.setInt(8, SituacaoUsuario.ATIVO.getCodigo());
            pstmtAdiciona.setString(9, cliente.getLogin());
            pstmtAdiciona.setString(10, cliente.getSenha());
            pstmtAdiciona.setInt(11, cliente.getEndereco().getCodigo());
            pstmtAdiciona.setDate(12, new java.sql.Date(cliente.getDataCadastro().getTime()));
                               
            pstmtAdiciona.execute();
            
            ResultSet rs = pstmtAdiciona.getGeneratedKeys();
            rs.next();
            cliente.setCodigo(rs.getInt(1));
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        } finally{
            try {pstmtAdiciona.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }
        return cliente;
    }
    
    public void alterar(Cliente cliente) {
        Connection con = null;
        PreparedStatement pstmtAtualiza = null;
        try {
            con = ConnectionFactory.getConnection();
            pstmtAtualiza = con.prepareStatement(update);  
            
            enderecoDAO.alterar(cliente.getEndereco());
            
            pstmtAtualiza.setString(1, cliente.getNome());
            pstmtAtualiza.setString(2, cliente.getSobrenome());
            pstmtAtualiza.setString(3, cliente.getCpf());
            pstmtAtualiza.setDate(4, new java.sql.Date(cliente.getDataNascimento().getTime()));
            pstmtAtualiza.setString(5, cliente.getEmail());
            pstmtAtualiza.setString(6, cliente.getTelefone());
            pstmtAtualiza.setInt(7, 0);//Cliente
            pstmtAtualiza.setInt(8, cliente.getSituacao().getCodigo());
            pstmtAtualiza.setString(9, cliente.getLogin());
            pstmtAtualiza.setString(10, cliente.getSenha());           
            pstmtAtualiza.setInt(11, cliente.getEndereco().getCodigo());
            pstmtAtualiza.setInt(12, cliente.getCodigo());
            
            pstmtAtualiza.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        } finally{
            try {pstmtAtualiza.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar pstmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}
 
        }        
    }
    
    public List<Cliente> getLista(Map<String,Object> filtros) {
        Connection con = null;
        PreparedStatement stmtLista = null;
        ResultSet rs = null;
        
        try {
            con = ConnectionFactory.getConnection();
            stmtLista = con.prepareStatement(montarConsultaFiltros(filtros, false));
            rs = stmtLista.executeQuery();
            List<Cliente> clientes = new ArrayList();
            while (rs.next()) {
                //
                Cliente cliente = new Cliente();
                
                cliente.setCodigo(rs.getInt("codigo"));
                cliente.setNome(rs.getString("nome"));
                cliente.setSobrenome(rs.getString("sobrenome"));
                cliente.setCpf(rs.getString("cpf"));
                cliente.setDataNascimento(rs.getTimestamp("data_nascimento"));
                cliente.setEmail(rs.getString("email"));
                cliente.setTelefone(rs.getString("telefone"));
                cliente.setSituacao(SituacaoUsuario.converter(rs.getInt("situacao")));
                cliente.setLogin(rs.getString("login"));
                cliente.setSenha(rs.getString("senha"));
                cliente.setCodigo(rs.getInt("codigo"));
                cliente.setEndereco(enderecoDAO.obter(rs.getInt("endereco_cod")));                
                cliente.setDataCadastro(rs.getTimestamp("data_cadastro"));

                clientes.add(cliente);
            }
            
            return clientes;
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
                selectFiltros = "select count(*) total from usuario where tipo = 0";        
            } else {
                selectFiltros = "select * from usuario where tipo = 0";
            }

            if (filtros.get("login") != null) {
                selectFiltros = selectFiltros + " and login like '%" + (String) filtros.get("login")+"%' ";
            }

            if (filtros.get("nome") != null) {
                selectFiltros = selectFiltros + " and nome like '%" + (String) filtros.get("nome")+"%' ";
            }  

            if (!qtd) {
                selectFiltros = selectFiltros + " order by usuario.codigo desc ";
                
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
    
    public Cliente obter(int codigo){
        Connection con = null;
        PreparedStatement stmtObter = null;
        ResultSet rs = null;
        Cliente cliente = new Cliente();
        try {
            con = ConnectionFactory.getConnection();
            stmtObter = con.prepareStatement(selectPorCodigo);
            stmtObter.setInt(1, codigo);
            rs = stmtObter.executeQuery();
            if(rs.next()){
                cliente.setCodigo(rs.getInt("codigo"));
                cliente.setNome(rs.getString("nome"));
                cliente.setSobrenome(rs.getString("sobrenome"));
                cliente.setCpf(rs.getString("cpf"));
                cliente.setDataNascimento(rs.getTimestamp("data_nascimento"));
                cliente.setEmail(rs.getString("email"));
                cliente.setTelefone(rs.getString("telefone"));
                cliente.setSituacao(SituacaoUsuario.converter(rs.getInt("situacao")));
                cliente.setLogin(rs.getString("login"));
                cliente.setSenha(rs.getString("senha"));                
                cliente.setCodigo(rs.getInt("codigo"));
                cliente.setEndereco(enderecoDAO.obter(rs.getInt("endereco_cod")));
                cliente.setDataCadastro(rs.getTimestamp("data_cadastro"));

            }else{
                throw new RuntimeException("Código inválido= " + codigo);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {rs.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar rs.");}
            try {stmtObter.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}
            
        }
        return cliente;
    }
    
    public Cliente autenticarCliente(String login, String senha){        
        Connection con = null;
        PreparedStatement stmtObter = null;
        ResultSet rs = null;
        Cliente cliente = new Cliente();
        try {
            con = ConnectionFactory.getConnection();
            stmtObter = con.prepareStatement(selectLogin);
            stmtObter.setString(1, login);
            stmtObter.setString(2, senha);
            rs = stmtObter.executeQuery();
            if(rs.next()){
                cliente.setCodigo(rs.getInt("codigo"));
                cliente.setNome(rs.getString("nome"));
                cliente.setSobrenome(rs.getString("sobrenome"));
                cliente.setCpf(rs.getString("cpf"));
                cliente.setDataNascimento(rs.getTimestamp("data_nascimento"));
                cliente.setEmail(rs.getString("email"));
                cliente.setTelefone(rs.getString("telefone"));
                cliente.setSituacao(SituacaoUsuario.converter(rs.getInt("situacao")));
                cliente.setLogin(rs.getString("login"));
                cliente.setSenha(rs.getString("senha"));
                cliente.setCodigo(rs.getInt("codigo"));
                cliente.setEndereco(enderecoDAO.obter(rs.getInt("endereco_cod")));
                cliente.setDataCadastro(rs.getTimestamp("data_cadastro"));

            }else{
                throw new RuntimeException("Usuário ou senha inválidos.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {rs.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar rs.");}
            try {stmtObter.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}
            
        }
        return cliente;
    }  
    
    public VerificaDadosCliente ValidaDados(String cpf, String login){
        Connection con = null;
        PreparedStatement stmtVerifica = null;
        ResultSet rs = null;
        VerificaDadosCliente verificaDadosCliente  = new VerificaDadosCliente();
        
        String verificaLogin = "select * from usuario where tipo = 0 and login = ?";
        String verificaCPF = "select * from usuario where tipo = 0 and cpf = ?";
        
        try {
            con = ConnectionFactory.getConnection();
            stmtVerifica = con.prepareStatement(verificaLogin);
            stmtVerifica.setString(1, login);
            rs = stmtVerifica.executeQuery();
            
            if(rs.next()){
               verificaDadosCliente.setLoginExistente(true);               
            }else{
               verificaDadosCliente.setLoginExistente(false);
            }
            
            stmtVerifica.close();            
            stmtVerifica = con.prepareStatement(verificaCPF);
            stmtVerifica.setString(1, cpf);
            rs = stmtVerifica.executeQuery();
            
            if(rs.next()){
               verificaDadosCliente.setCPFExistente(true);               
            }else{
               verificaDadosCliente.setCPFExistente(false);
            }
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            try {rs.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar rs.");}
            try {stmtVerifica.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}
            
        }
        return verificaDadosCliente;
    }
    
}
