package com.Estoque.controllerFXML;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.Estoque.entities.LogsDTO;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("/com/Estoque/gui/RelatoryData.fxml")
public class RelatoryControllerData {

    private List<Map<String, Object>> result;

    private ObservableList<LogsDTO> listLogs = FXCollections.observableArrayList();

    @FXML
    private TableColumn<LogsDTO, String> colEmail;

    @FXML
    private TableColumn<LogsDTO, String> colDate;

    @FXML
    private TableColumn<LogsDTO, String> colMessage;

    @FXML
    private TableView<LogsDTO> tableView = new TableView<LogsDTO>();

    @FXML
    public void initialize() {
        // Configurar as colunas da tabela
        colDate.setCellValueFactory(cellData -> cellData.getValue().date());
    	colEmail.setCellValueFactory(cellData -> cellData.getValue().email());
        colMessage.setCellValueFactory(cellData -> cellData.getValue().message());
        // Atualizar a tabela com os dados
        Platform.runLater(() -> {
            if (result != null && !result.isEmpty()) {
            	loadTable();
            }
        });
    }

    // Método para preencher a tabela com dados de 'resultado'
    private void loadTable() {
    	Platform.runLater(() ->{
        listLogs.clear();

        for (Map<String, Object> log : result) {
            // Extrair as informações de 'resultado' e criar o modelo 'Item'
            String email = (String) log.get("email");
            String date = (String) log.get("dataHora");
            String message = (String) log.get("mensagem");

            // Adicionar o item na lista
            listLogs.add(new LogsDTO(email, date, message));

        }

        // Atribuir a lista à tabela
        tableView.setItems(listLogs);
        tableView.refresh();
    });
    }
    // Método para receber os dados
    public void receiveResult(List<Map<String, Object>> result) {
        this.result = result;
    }
}
