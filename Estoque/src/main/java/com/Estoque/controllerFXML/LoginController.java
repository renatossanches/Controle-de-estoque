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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("/com/Estoque/gui/Login.fxml")
public class LoginController {

    @Autowired
	private ConfigurableApplicationContext context;
	
	@Autowired
	private TokenAuthentication logs;
	
	@FXML
	private Button btnEnviar;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private TextField txtSenha;

    @FXML
    private void enterPressed(KeyEvent event) {
    		if (event.getCode() == KeyCode.ENTER) {
    			event.consume();// evita que o Enter seja chamado mais de 1x em uma unica requisiçãoS
    			if(btnEnviar.isVisible()) {
    				enviarLogin();
    			}
    		}
    }
	
    @FXML
    private void enviarLogin() {
        String email = txtEmail.getText();
        String senha = txtSenha.getText();
        
        if (email.isEmpty() || senha.isEmpty()) {
        	AlertMsg.mostrarMensagem("Campos vazios", "Por favor, preencha todos os campos.", false);
            return;
        }
        try {
            // Envia a requisição HTTP
            Map<String, String> response = logs.authenticateWithEmailAndPassword(email, senha);
            if (response != null) {
            	if(!TokenAuthentication.isUserLoggedIn()) {
            	AlertMsg.mostrarMensagem("Sucesso", "Usuário autenticado com sucesso!", true);
            	TokenAuthentication.salvarTokenNoArquivo(response);
            	showMainScreen();
            	fecharTela();
                System.out.println("Token: " + response);
            	}
            	else {
                	fecharTela();
                	AlertMsg.mostrarMensagem("Erro", "Usuário ja está autenticado", false);
            	}
            } else {
            	AlertMsg.mostrarMensagem("Erro", "Falha na autenticação.", false);
            }
        } catch (IOException e) {
        	AlertMsg.mostrarMensagem("Erro", "Erro ao conectar com o servidor. Tente novamente.", false);
            e.printStackTrace();
        }
    }
    
    private void showMainScreen() {
       LoadScreen.setContext(context);
       LoadScreen.showScreen("Controle de Estoque", ItemControllerFXML.class, "ico");
    }
    private void fecharTela() {
        // Fechar a janela de remoção
        Stage stage = (Stage) btnEnviar.getScene().getWindow();
        stage.close();
    }

	public Button getBtnEnviar() {
		return btnEnviar;
	}
	
    
}
