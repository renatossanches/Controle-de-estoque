package com.Estoque;

import java.util.Map;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.Estoque.api.LoadFonts;
import com.Estoque.api.LoadScreen;
import com.Estoque.controllerFXML.ItemControllerFXML;
import com.Estoque.controllerFXML.LoginController;
import com.Estoque.repositories.TokenAuthentication;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class EstoqueApp extends Application
{
    private ConfigurableApplicationContext context;
    private Map<String, String> tokens;
    private String refreshToken;
    
    
    public void init() {
        String[] args = this.getParameters().getRaw().toArray(new String[0]);
        LoadScreen.setContext(this.context = new SpringApplicationBuilder(new Class[0]).sources(new Class[] { com.Estoque.Application.class }).run(args));
    }
    
    public void start(Stage stage) throws RuntimeException {
        LoadFonts.allFonts();
        if (TokenAuthentication.isUserLoggedIn()) {
            LoadScreen.showScreen("Controle de Estoque", ItemControllerFXML.class, stage, "ico");
        }
        else {
            LoadScreen.showScreen("Login", LoginController.class, stage, "login");
        }
    }
    
    public void stop() {
        this.context.close();
        Platform.exit();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
