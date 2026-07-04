package dao;

import database.ConnectionFactory;
import model.Pessoa;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PessoaDAO {

    public void salvar(Pessoa p) throws SQLException {
        String sql = "INSERT INTO pessoa (nome, cpf, email, telefone) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getCpf());
            stmt.setString(3, p.getEmail());
            stmt.setString(4, p.getTelefone());
            stmt.executeUpdate();

            // Recupera o ID auto-incrementado gerado pelo banco
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Pessoa> listar() throws SQLException {
        String sql = "SELECT * FROM pessoa";
        List<Pessoa> pessoas = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Pessoa p = new Pessoa();
                p.setId(rs.getInt("id"));
                p.setNome(rs.getString("nome"));
                p.setCpf(rs.getString("cpf"));
                p.setEmail(rs.getString("email"));
                p.setTelefone(rs.getString("telefone"));
                
                pessoas.add(p);
            }
        }
        return pessoas;
    }

    public void editar(Pessoa p) throws SQLException {
        String sql = "UPDATE pessoa SET nome = ?, cpf = ?, email = ?, telefone = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getCpf());
            stmt.setString(3, p.getEmail());
            stmt.setString(4, p.getTelefone());
            stmt.setInt(5, p.getId());

            stmt.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM pessoa WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
