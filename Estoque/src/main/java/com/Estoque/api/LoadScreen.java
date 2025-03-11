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
    private static Map<String, Stage> stagesAbertos = new HashMap<>();

    // Método para exibir a tela
    public static void showScreen(String titulo, Class<?> classe, String imagem) {
        String janelaNome = classe.getSimpleName(); // Nome da classe FXML como identificador (você pode mudar isso conforme necessário)

        // Verifica se já existe um Stage aberto para esta janela
        Stage stageExistente = stagesAbertos.get(janelaNome);
        if (stageExistente != null && stageExistente.isShowing()) {
        	stageExistente.toFront();
            return; // Não cria uma nova janela
        }

        // Caso contrário, cria um novo Stage
        FxWeaver fxWeaver = context.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(classe);
        
        // Verifica se o root foi carregado corretamente
        if (root == null) {
            throw new RuntimeException("Root is null. Verifique se o arquivo FXML e o controller estão corretamente configurados.");
        }

        // Cria um novo Stage, se não houver um aberto
        Stage novoStage = new Stage(); 
        novoStage.setTitle(titulo);
        novoStage.getIcons().add(new Image(LoadScreen.class.getResourceAsStream("/images/" + imagem + ".png")));
        Scene scene = new Scene(root);
        novoStage.setScene(scene);
        novoStage.show();
        stagesAbertos.put(janelaNome, novoStage);

        // Adiciona um listener para quando o Stage for fechado, removendo do mapa
        novoStage.setOnCloseRequest(event -> stagesAbertos.remove(janelaNome));
    }

    // Método para exibir a tela com um Stage já fornecido
    public static void showScreen(String titulo, Class<?> classe, Stage stage, String imagem) {
        // Se o stage passado for nulo ou não estiver visível, cria um novo
        if (stage == null || !stage.isShowing()) {
            // Usa o mesmo fluxo do primeiro método
            FxWeaver fxWeaver = context.getBean(FxWeaver.class);
            Parent root = fxWeaver.loadView(classe);
            
            // Verifica se o root foi carregado corretamente
            if (root == null) {
                throw new RuntimeException("Root is null. Verifique se o arquivo FXML e o controller estão corretamente configurados.");
            }

            // Cria um novo Stage
            stage = new Stage();
            stage.setTitle(titulo);
            stage.getIcons().add(new Image(LoadScreen.class.getResourceAsStream("/images/" + imagem + ".png")));
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
