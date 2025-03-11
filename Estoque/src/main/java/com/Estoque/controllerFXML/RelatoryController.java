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
@FxmlView("/com/Estoque/gui/Relatorio.fxml")
public class RelatoryController {
	
	@Autowired
	private LogsRepository logs;
	
	@Autowired
	private ConfigurableApplicationContext context;
	
	@FXML
	private DatePicker datePickerInicial;
	
	@FXML
	private DatePicker datePickerFinal;
	
	@FXML
	private Button btnGerarDados;
	
	@FXML
	private Button btnGerarTabela;
	
    
    @FXML
    private void enterPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			event.consume();// evita que o Enter seja chamado mais de 1x em uma unica requisiçãoS
			
			if(btnGerarDados.isVisible()) {
				gerarDados(); 
			}
		}
}
	
	@FXML
	private void gerarDados() {
	    LocalDate dataInicial = datePickerInicial.getValue();
	    LocalDate dataFinal = datePickerFinal.getValue();
    	if(dataInicial.isBefore(dataFinal) && dataFinal.isAfter(dataInicial)) {
	    List<Map<String, Object>> resultado = logs.getLogsByDateRange(dataInicial, dataFinal);
	
	        String caminhoArquivo = System.getProperty("user.home") + "/Downloads/logs_gerados.pdf";
	        logs.generateLogsPDF(resultado, caminhoArquivo);
	        AlertMsg.mostrarMensagem("Sucesso", "Arquivo gerado para: "+ caminhoArquivo, true);
	        fecharTela();
    	}
    	else {
        	AlertMsg.mostrarMensagem("Erro", "A data final não pode ser antes da inicial", false);
    	}
	}
	
	
	@FXML
	private void gerarDadosEmTabela() {
	    LocalDate dataInicial = datePickerInicial.getValue();
	    LocalDate dataFinal = datePickerFinal.getValue();
    	if(dataInicial.isBefore(dataFinal) && dataFinal.isAfter(dataInicial)) {
	    List<Map<String, Object>> resultado = logs.getLogsByDateRange(dataInicial, dataFinal);
		Platform.runLater(() ->{
			LoadScreen.showScreen("Dados em tabela", RelatoryControllerData.class, "relatorio");
			FxWeaver fx= context.getBean(FxWeaver.class);
			RelatoryControllerData controllerData = fx.getBean(RelatoryControllerData.class);
			controllerData.receberResultado(resultado);
		});
        fecharTela();
    	}
    	else {
        	AlertMsg.mostrarMensagem("Erro", "A data final não pode ser antes da inicial", false);
    	}
	}
	
    private void fecharTela() {
        // Fechar a janela de remoção
        Stage stage = (Stage) btnGerarDados.getScene().getWindow();
        stage.close();
    }
}
