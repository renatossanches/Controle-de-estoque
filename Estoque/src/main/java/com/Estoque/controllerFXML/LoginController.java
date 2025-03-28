package com.Estoque.controllerFXML;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.Estoque.api.AlertMsg;
import com.Estoque.api.LoadScreen;
import com.Estoque.repositories.TokenAuthentication;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("/com/Estoque/gui/Login.fxml")
public class LoginController {

    @Autowired
	private ConfigurableApplicationContext context;
	
	@Autowired
	private TokenAuthentication logs;
	
	@FXML
	private Button btnSubmit;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private TextField txtPassword;

    @FXML
    private void enterPressed(KeyEvent event) {
    		if (event.getCode() == KeyCode.ENTER) {
    			event.consume();// evita que o Enter seja chamado mais de 1x em uma unica requisiçãoS
    			if(btnSubmit.isVisible()) {
    				submitLogin();
    			}
    		}
    }
	
    @FXML
    private void submitLogin() {
        String email = txtEmail.getText();
        String password = txtPassword.getText();
        
        if (email.isEmpty() || password.isEmpty()) {
        	AlertMsg.showMessage("Campos vazios", "Por favor, preencha todos os campos.", false);
            return;
        }
        try {
            // Envia a requisição HTTP
            Map<String, String> response = logs.authenticateWithEmailAndPassword(email, password);
            if (response != null) {
            	if(!TokenAuthentication.isUserLoggedIn()) {
            	AlertMsg.showMessage("Sucesso", "Usuário autenticado com sucesso!", true);
            	TokenAuthentication.salveTokenInFile(response);
            	showMainScreen();
            	closeScreen();
                System.out.println("Token: " + response);
            	}
            	else {
            		closeScreen();
                	AlertMsg.showMessage("Erro", "Usuário ja está autenticado", false);
            	}
            } else {
            	AlertMsg.showMessage("Erro", "Falha na autenticação.", false);
            }
        } catch (IOException e) {
        	AlertMsg.showMessage("Erro", "Erro ao conectar com o servidor. Tente novamente.", false);
            e.printStackTrace();
        }
    }
    
    private void showMainScreen() {
       LoadScreen.setContext(context);
       LoadScreen.showScreen("Controle de Estoque", ItemControllerFXML.class, "ico");
    }
    private void closeScreen() {
        // Fechar a janela de remoção
        Stage stage = (Stage) btnSubmit.getScene().getWindow();
        stage.close();
    }

	public Button getBtnSubmit() {
		return btnSubmit;
	}
	
    
}
