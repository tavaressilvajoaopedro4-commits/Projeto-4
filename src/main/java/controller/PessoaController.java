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
