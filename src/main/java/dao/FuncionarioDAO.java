/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import bean.Funcionario;
import bean.enums.Cargo;
import bean.enums.SituacaoUsuario;
import controller.Util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Everton
 */
public class FuncionarioDAO implements Serializable{
    private String insert = "insert into usuario (nome, sobrenome, cpf, data_nascimento, email, telefone,"
            + "tipo, situacao, login, senha, cargo, administrador, endereco_cod) "
            + "values (?,?,?,?,?,?,?,?,?,?,?,?,?)";    
    private String selectLogin = "select * from usuario where tipo = 1 and login = ? and senha = ?";
    private String selectPorCodigo = "select * from usuario where tipo = 1 and codigo = ?";
    private EnderecoDAO enderecoDAO = new EnderecoDAO();
    
    public void incluir(Funcionario funcionario){
        Connection con = null;
        PreparedStatement pstmtAdiciona = null;
        try {
            con = ConnectionFactory.getConnection();
            pstmtAdiciona = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
           
            enderecoDAO.incluir(funcionario.getEndereco());
            
            pstmtAdiciona.setString(1, funcionario.getNome());
            pstmtAdiciona.setString(2, funcionario.getSobrenome());
            pstmtAdiciona.setString(3, funcionario.getCpf());
            pstmtAdiciona.setDate(4, new java.sql.Date(funcionario.getDataNascimento().getTime()));
            pstmtAdiciona.setString(5, funcionario.getEmail());
            pstmtAdiciona.setString(6, funcionario.getTelefone());
            pstmtAdiciona.setInt(7, 1);//Funcionário
            pstmtAdiciona.setInt(8, funcionario.getSituacao().getCodigo());
            pstmtAdiciona.setString(9, funcionario.getLogin());
            pstmtAdiciona.setString(10, Util.converterParaMD5(funcionario.getSenha()));
            pstmtAdiciona.setInt(11, funcionario.getCargo().getCodigo());
            pstmtAdiciona.setInt(12, (funcionario.isAdministrador()?1:0));
            pstmtAdiciona.setInt(13, funcionario.getEndereco().getCodigo());
                               
            pstmtAdiciona.execute();
            
            ResultSet rs = pstmtAdiciona.getGeneratedKeys();
            rs.next();
            funcionario.setCodigo(rs.getInt(1));
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } finally{
            try {pstmtAdiciona.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}            
        }
    }
    
    public void alterar(Funcionario funcionario, boolean alterarSenha) {
        Connection con = null;
        PreparedStatement pstmtAtualiza = null;
        try {
            String update = "update usuario set nome = ?, sobrenome = ?, cpf = ?, data_nascimento = ?,"
                + " email = ?, telefone = ?, tipo = ?, situacao = ?, login = ?, cargo = ?, "
                + ((alterarSenha) ? "senha = ?, ":"")
                + "administrador = ?, endereco_cod = ? where codigo = ?";
            
            con = ConnectionFactory.getConnection();
            pstmtAtualiza = con.prepareStatement(update);  
            
            enderecoDAO.alterar(funcionario.getEndereco());
            
            int index = 1;
            
            pstmtAtualiza.setString(index++, funcionario.getNome());
            pstmtAtualiza.setString(index++, funcionario.getSobrenome());
            pstmtAtualiza.setString(index++, funcionario.getCpf());
            pstmtAtualiza.setDate(index++, new java.sql.Date(funcionario.getDataNascimento().getTime()));
            pstmtAtualiza.setString(index++, funcionario.getEmail());
            pstmtAtualiza.setString(index++, funcionario.getTelefone());
            pstmtAtualiza.setInt(index++, 1);//Funcionário
            pstmtAtualiza.setInt(index++, funcionario.getSituacao().getCodigo());
            pstmtAtualiza.setString(index++, funcionario.getLogin());
            pstmtAtualiza.setInt(index++, funcionario.getCargo().getCodigo());
            
            if (alterarSenha) {
                pstmtAtualiza.setString(index++, Util.converterParaMD5(funcionario.getSenha()));                           
            }
            pstmtAtualiza.setInt(index++, (funcionario.isAdministrador()?1:0));
            pstmtAtualiza.setInt(index++, funcionario.getEndereco().getCodigo());
            pstmtAtualiza.setInt(index++, funcionario.getCodigo());
            
            pstmtAtualiza.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);        
        } finally{
            try {pstmtAtualiza.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar pstmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}
 
        }        
    }
    
    public List<Funcionario> getLista() {
        return getLista(null, null);
    }
    
    public List<Funcionario> getLista(Integer startIndex, Integer pageSize) {
        Connection con = null;
        PreparedStatement stmtLista = null;
        ResultSet rs = null;
        String consulta = "select * from usuario where tipo = 1";
        
        if (startIndex != null && pageSize != null) {
            consulta = consulta+ " limit "+String.valueOf(startIndex)+","+String.valueOf(pageSize);
        }
        
        try {
            con = ConnectionFactory.getConnection();
            stmtLista = con.prepareStatement(consulta);
            rs = stmtLista.executeQuery();
            List<Funcionario> funcionarios = new ArrayList();
            while (rs.next()) {
                //
                Funcionario funcionario = new Funcionario();
                
                funcionario.setCodigo(rs.getInt("codigo"));
                funcionario.setNome(rs.getString("nome"));
                funcionario.setSobrenome(rs.getString("sobrenome"));
                funcionario.setCpf(rs.getString("cpf"));
                funcionario.setDataNascimento(rs.getTimestamp("data_nascimento"));
                funcionario.setEmail(rs.getString("email"));
                funcionario.setTelefone(rs.getString("telefone"));
                funcionario.setSituacao(SituacaoUsuario.converter(rs.getInt("situacao")));
                funcionario.setLogin(rs.getString("login"));
                funcionario.setSenha(rs.getString("senha"));
                funcionario.setCargo(Cargo.converter(rs.getInt("cargo")));
                funcionario.setAdministrador((rs.getInt("administrador") == 1));
                funcionario.setCodigo(rs.getInt("codigo"));
                funcionario.setEndereco(enderecoDAO.obter(rs.getInt("endereco_cod")));                

                funcionarios.add(funcionario);
            }
            
            return funcionarios;
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
            stmtQtd = con.prepareStatement("select count(*) total from usuario where tipo = 1");
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
    
    public Funcionario obter(int codigo){
        Connection con = null;
        PreparedStatement stmtObter = null;
        ResultSet rs = null;
        Funcionario funcionario = new Funcionario();
        try {
            con = ConnectionFactory.getConnection();
            stmtObter = con.prepareStatement(selectPorCodigo);
            stmtObter.setInt(1, codigo);
            rs = stmtObter.executeQuery();
            if(rs.next()){
                funcionario.setCodigo(rs.getInt("codigo"));
                funcionario.setNome(rs.getString("nome"));
                funcionario.setSobrenome(rs.getString("sobrenome"));
                funcionario.setCpf(rs.getString("cpf"));
                funcionario.setDataNascimento(rs.getTimestamp("data_nascimento"));
                funcionario.setEmail(rs.getString("email"));
                funcionario.setTelefone(rs.getString("telefone"));
                funcionario.setSituacao(SituacaoUsuario.converter(rs.getInt("situacao")));
                funcionario.setLogin(rs.getString("login"));
                funcionario.setSenha(rs.getString("senha"));
                funcionario.setCargo(Cargo.converter(rs.getInt("cargo")));
                funcionario.setAdministrador((rs.getInt("administrador") == 1));
                funcionario.setCodigo(rs.getInt("codigo"));
                funcionario.setEndereco(enderecoDAO.obter(rs.getInt("endereco_cod")));

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
        return funcionario;
    }
    
    public Funcionario autenticarFuncionario(String login, String senha){        
        Connection con = null;
        PreparedStatement stmtObter = null;
        ResultSet rs = null;
        Funcionario funcionario = new Funcionario();
        try {
            con = ConnectionFactory.getConnection();
            stmtObter = con.prepareStatement(selectLogin);
            stmtObter.setString(1, login);
            stmtObter.setString(2, senha);
            rs = stmtObter.executeQuery();
            if(rs.next()){
                funcionario.setCodigo(rs.getInt("codigo"));
                funcionario.setNome(rs.getString("nome"));
                funcionario.setSobrenome(rs.getString("sobrenome"));
                funcionario.setCpf(rs.getString("cpf"));
                funcionario.setDataNascimento(rs.getTimestamp("data_nascimento"));
                funcionario.setEmail(rs.getString("email"));
                funcionario.setTelefone(rs.getString("telefone"));
                funcionario.setSituacao(SituacaoUsuario.converter(rs.getInt("situacao")));
                funcionario.setLogin(rs.getString("login"));
                funcionario.setSenha(rs.getString("senha"));
                funcionario.setCargo(Cargo.converter(rs.getInt("cargo")));
                funcionario.setAdministrador((rs.getInt("administrador") == 1));
                funcionario.setCodigo(rs.getInt("codigo"));
                funcionario.setEndereco(enderecoDAO.obter(rs.getInt("endereco_cod")));
                
                if (funcionario.getSituacao() == SituacaoUsuario.INATIVO) {
                    throw new RuntimeException("Usuário ou senha inválidos.");
                }
            }else{
                throw new RuntimeException("Usuário ou senha inválidos.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally{
            try {rs.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar rs.");}
            try {stmtObter.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar stmt.");}
            try {con.close();} catch (SQLException ex) {throw new RuntimeException("Falha ao fechar conexão.");}
            
        }
        return funcionario;
    } 
    
}
