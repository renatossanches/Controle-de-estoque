package com.Estoque.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ConfigurableApplicationContext;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;

public class LoadScreen {

    private static ConfigurableApplicationContext context;

    // Variável global para controlar o Stage aberto
    private static Map<String, Stage> openStages = new HashMap<>();

    // Método para exibir a tela
    public static void showScreen(String title, Class<?> classParameter, String nameImage) {
        String nameWindow = classParameter.getSimpleName(); // Nome da classe FXML como identificador (você pode mudar isso conforme necessário)

        // Verifica se já existe um Stage aberto para esta janela
        Stage existingStage = openStages.get(nameWindow);
        if (existingStage != null && existingStage.isShowing()) {
        	existingStage.toFront();
            return; // Não cria uma nova janela
        }

        // Caso contrário, cria um novo Stage
        FxWeaver fxWeaver = context.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(classParameter);
        
        // Verifica se o root foi carregado corretamente
        if (root == null) {
            throw new RuntimeException("Root é nulo. Verifique se o arquivo FXML e o controller estão corretamente configurados.");
        }

        // Cria um novo Stage, se não houver um aberto
        Stage newStage = new Stage(); 
        newStage.setTitle(title);
        newStage.getIcons().add(new Image(LoadScreen.class.getResourceAsStream("/images/" + nameImage + ".png")));
        Scene scene = new Scene(root);
        newStage.setScene(scene);
        newStage.show();
        openStages.put(nameWindow, newStage);

        // Adiciona um listener para quando o Stage for fechado, removendo do mapa
        newStage.setOnCloseRequest(event -> openStages.remove(nameWindow));
    }

    // Método para exibir a tela com um Stage já fornecido
    public static void showScreen(String title, Class<?> classParameter, Stage stage, String nameImage) {
        // Se o stage passado for nulo ou não estiver visível, cria um novo
        if (stage == null || !stage.isShowing()) {
            // Usa o mesmo fluxo do primeiro método
            FxWeaver fxWeaver = context.getBean(FxWeaver.class);
            Parent root = fxWeaver.loadView(classParameter);
            
            // Verifica se o root foi carregado corretamente
            if (root == null) {
                throw new RuntimeException("Root é nulo. Verifique se o arquivo FXML e o controller estão corretamente configurados.");
            }

            // Cria um novo Stage
            stage = new Stage();
            stage.setTitle(title);
            stage.getIcons().add(new Image(LoadScreen.class.getResourceAsStream("/images/" + nameImage + ".png")));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            // Se o stage já estiver visível, traz para frente
            stage.toFront();
        }
    }

    // Método para configurar o contexto do Spring
    public static void setContext(ConfigurableApplicationContext context) {
        LoadScreen.context = context;
    }
}
