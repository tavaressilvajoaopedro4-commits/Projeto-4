# Projeto-4

Sistema de Gerenciamento de Pessoas com JavaFX e PostgreSQL

## 🚀 Setup/Instalação

### Pré-requisitos
- **Java 11+** (recomendado Java 17+)
- **PostgreSQL 12+**
- **Maven 3.6+**

### Passos para executar

1. **Criar o banco de dados:**
```sql
CREATE DATABASE seu_banco;
```

2. **Executar o SQL da tabela:**
```sql
CREATE TABLE pessoa (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    email VARCHAR(100),
    telefone VARCHAR(20)
);
```

3. **Configurar credenciais:**
   - Abra `src/main/java/database/ConnectionFactory.java`
   - Altere `URL`, `USER` e `PASSWORD` com suas credenciais do PostgreSQL

4. **Compilar e executar:**
```bash
mvn clean install
mvn javafx:run
```

---

## 📊 Tabela pessoa

```sql
CREATE TABLE pessoa (
    id SERIAL PRIMARY KEY, -- No MySQL, use: id INT AUTO_INCREMENT PRIMARY KEY
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    email VARCHAR(100),
    telefone VARCHAR(20)
);
```

---

## 📦 Dependências Maven

```xml
<!-- PostgreSQL Driver -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.2</version>
</dependency>

<!-- JavaFX -->
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>21.0.2</version>
</dependency>
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-fxml</artifactId>
    <version>21.0.2</version>
</dependency>

<!-- Maven JavaFX Plugin -->
<plugin>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-maven-plugin</artifactId>
    <version>0.0.8</version>
</plugin>
```

---

## 👤 Classe Pessoa (Model)

```java
package model;

public class Pessoa {
    private int id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;

    // Construtores
    public Pessoa() {}

    public Pessoa(String nome, String cpf, String email, String telefone) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public String toString() {
        return "Pessoa{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }
}
```

---

## 🔌 ConnectionFactory

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

---

## 💾 PessoaDAO

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
```

---

## 🎮 PessoaController

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
    @FXML private TableColumn<Pessoa, Integer> colId;
    @FXML private TableColumn<Pessoa, String> colNome;
    @FXML private TableColumn<Pessoa, String> colCpf;
    @FXML private TableColumn<Pessoa, String> colEmail;
    @FXML private TableColumn<Pessoa, String> colTelefone;

    private final PessoaDAO pessoaDAO = new PessoaDAO();
    private final ObservableList<Pessoa> obsPessoas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Mapeamento das colunas da tabela
        colId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colNome.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNome()));
        colCpf.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCpf()));
        colEmail.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        colTelefone.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTelefone()));
        
        atualizarTabela();
    }

    @FXML
    private void handleSalvar() {
        try {
            if (validarCampos()) {
                Pessoa p = new Pessoa();
                p.setNome(txtNome.getText());
                p.setCpf(txtCpf.getText());
                p.setEmail(txtEmail.getText());
                p.setTelefone(txtTelefone.getText());

                pessoaDAO.salvar(p);
                exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Pessoa salva com sucesso!");
                limparCampos();
                atualizarTabela();
            }
            
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

    private boolean validarCampos() {
        if (txtNome.getText().isEmpty() || txtCpf.getText().isEmpty()) {
            exibirAlerta(Alert.AlertType.WARNING, "Validação", "Nome e CPF são obrigatórios!");
            return false;
        }
        if (txtCpf.getText().length() != 14) {
            exibirAlerta(Alert.AlertType.WARNING, "Validação", "CPF deve ter 14 caracteres (XXX.XXX.XXX-XX)");
            return false;
        }
        return true;
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

---

## ✅ Melhorias Implementadas

- ✅ Adicionada seção de Setup/Instalação
- ✅ Classe Pessoa completa com getters e setters
- ✅ Dependências JavaFX adicionadas
- ✅ Plugin Maven para JavaFX
- ✅ TableColumn mappings no controller
- ✅ Validações básicas de campos
- ✅ Método `toString()` na classe Pessoa
