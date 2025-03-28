package com.Estoque.controllerFXML;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.Estoque.api.AlertMsg;
import com.Estoque.api.LoadScreen;
import com.Estoque.repositories.LogsRepository;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("/com/Estoque/gui/Relatory.fxml")
public class RelatoryController {
	
	@Autowired
	private LogsRepository logs;
	
	@Autowired
	private ConfigurableApplicationContext context;
	
	@FXML
	private DatePicker datePickerInitial;
	
	@FXML
	private DatePicker datePickerFinal;
	
	@FXML
	private Button btnGenerateData;
	
	@FXML
	private Button btnGenerateTable;
	
    
    @FXML
    private void enterPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			event.consume();// evita que o Enter seja chamado mais de 1x em uma unica requisições
			
			if(btnGenerateData.isVisible()) {
				generateData(); 
			}
		}
}
	
	@FXML
	private void generateData() {
	    LocalDate dateInitial = datePickerInitial.getValue();
	    LocalDate dateFinal = datePickerFinal.getValue();
    	if(dateInitial.isBefore(dateFinal) && dateFinal.isAfter(dateInitial)) {
	    List<Map<String, Object>> result = logs.getLogsByDateRange(dateInitial, dateFinal);
	
	        String filePath = System.getProperty("user.home") + "/Downloads/logs_gerados.pdf";
	        logs.generateLogsPDF(result, filePath);
	        AlertMsg.showMessage("Sucesso", "Arquivo gerado para: "+ filePath, true);
	        closeScreen();
    	}
    	else {
        	AlertMsg.showMessage("Erro", "A data final não pode ser antes da inicial", false);
    	}
	}
	
	
	@FXML
	private void generateTableData() {
	    LocalDate dateInitial = datePickerInitial.getValue();
	    LocalDate dateFinal = datePickerFinal.getValue();
    	if(dateInitial.isBefore(dateFinal) && dateFinal.isAfter(dateInitial)) {
	    List<Map<String, Object>> result = logs.getLogsByDateRange(dateInitial, dateFinal);
		Platform.runLater(() ->{
			LoadScreen.showScreen("Dados em tabela", RelatoryControllerData.class, "relatorio");
			FxWeaver fx= context.getBean(FxWeaver.class);
			RelatoryControllerData controllerData = fx.getBean(RelatoryControllerData.class);
			controllerData.receiveResult(result);
		});
		closeScreen();
    	}
    	else {
        	AlertMsg.showMessage("Erro", "A data final não pode ser antes da inicial", false);
    	}
	}
	
    private void closeScreen() {
        // Fechar a janela de remoção
        Stage stage = (Stage) btnGenerateData.getScene().getWindow();
        stage.close();
    }
}
