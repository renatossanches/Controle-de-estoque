package com.Estoque.controllerFXML;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.Estoque.entities.LogsDTO;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("/com/Estoque/gui/RelatorioData.fxml")
public class RelatoryControllerData {

    private List<Map<String, Object>> resultado;

    private ObservableList<LogsDTO> listaLogs = FXCollections.observableArrayList();

    @FXML
    private TableColumn<LogsDTO, String> colEmail;

    @FXML
    private TableColumn<LogsDTO, String> colData;

    @FXML
    private TableColumn<LogsDTO, String> colMensagem;

    @FXML
    private TableView<LogsDTO> tableView = new TableView<LogsDTO>();

    @FXML
    public void initialize() {
        // Configurar as colunas da tabela
        colData.setCellValueFactory(cellData -> cellData.getValue().data());
    	colEmail.setCellValueFactory(cellData -> cellData.getValue().email());
        colMensagem.setCellValueFactory(cellData -> cellData.getValue().mensagem());
        // Atualizar a tabela com os dados
        Platform.runLater(() -> {
            if (resultado != null && !resultado.isEmpty()) {
                carregarTabela();
            }
        });
    }

    // Método para preencher a tabela com dados de 'resultado'
    private void carregarTabela() {
    	Platform.runLater(() ->{
        listaLogs.clear();

        for (Map<String, Object> log : resultado) {
            // Extrair as informações de 'resultado' e criar o modelo 'Item'
            String email = (String) log.get("email");
            String data = (String) log.get("dataHora");
            String mensagem = (String) log.get("mensagem");

            // Adicionar o item na lista
            listaLogs.add(new LogsDTO(email, data, mensagem));

        }

        // Atribuir a lista à tabela
        tableView.setItems(listaLogs);
        tableView.refresh();
    });
    }
    // Método para receber os dados
    public void receberResultado(List<Map<String, Object>> resultado) {
        this.resultado = resultado;
    }
}
