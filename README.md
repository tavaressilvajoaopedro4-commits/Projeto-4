# Projeto-4

## Tabela pessoa

```sql
CREATE TABLE pessoa (
    id SERIAL PRIMARY KEY, -- No MySQL, use: id INT AUTO_INCREMENT PRIMARY KEY
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    email VARCHAR(100),
    telefone VARCHAR(20)
);
```

## Dependência Maven

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.2</version>
</dependency>
```

## ConnectionFactory

```java
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    // Altere os dados de acordo com o seu banco local
    private static final String URL = "jdbc:postgresql://localhost:5432/seu_banco";
    private static final String USER = "postgres";
    private static final String PASSWORD = "sua_senha";

    public static Connection getConnection() throws SQLException {
        try {
            // Garante o carregamento do driver em projetos legados
            Class.forName("org.postgresql.Driver"); 
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC não encontrado.", e);
        }
    }
}
```

## PessoaDAO

```java
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
                p.setTelefone(rs.getString("telefone")); // ✅ CORRIGIDO: era getTelefone()
                
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
```

## PessoaController

```java
package controller;

import dao.PessoaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Pessoa;
import java.sql.SQLException;

public class PessoaController {

    @FXML private TextField txtNome;
    @FXML private TextField txtCpf;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefone;
    @FXML private TableView<Pessoa> tabelaPessoas;

    private final PessoaDAO pessoaDAO = new PessoaDAO();
    private final ObservableList<Pessoa> obsPessoas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Mapeie suas TableColumns aqui se necessário (ex: colNome.setCellValueFactory(...))
        atualizarTabela();
    }

    @FXML
    private void handleSalvar() {
        try {
            Pessoa p = new Pessoa();
            p.setNome(txtNome.getText());
            p.setCpf(txtCpf.getText());
            p.setEmail(txtEmail.getText());
            p.setTelefone(txtTelefone.getText());

            pessoaDAO.salvar(p);
            exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Pessoa salva com sucesso!");
            limparCampos();
            atualizarTabela();
            
        } catch (SQLException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro no Banco", "Erro ao salvar: " + e.getMessage());
        }
    }

    @FXML
    private void handleListar() {
        atualizarTabela();
    }

    @FXML
    private void handleCancelar() {
        limparCampos();
    }

    private void atualizarTabela() {
        try {
            obsPessoas.setAll(pessoaDAO.listar());
            tabelaPessoas.setItems(obsPessoas);
        } catch (SQLException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Conexão", "Não foi possível listar os dados.");
        }
    }

    private void limparCampos() {
        txtNome.clear();
        txtCpf.clear();
        txtEmail.clear();
        txtTelefone.clear();
    }

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
```
