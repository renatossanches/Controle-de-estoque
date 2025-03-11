package com.Estoque.api;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class AlertMsg {

    private static Image alertError = new Image("/images/alertError.png"); 
    private static Image alertSucesso = new Image("/images/alertSucesso.png");
    
    //true = deu certo
    //false = não deu certo
    
    
    public static void mostrarMensagem(String titulo, String mensagem, boolean resp) {
    	Image simbolo;
    	if(resp == true) {
    		simbolo = alertSucesso;
        }
    	else {
    		simbolo = alertError;
    	}
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo); 
        alert.setContentText(mensagem);
        ImageView ajustesDoSimbolo = new ImageView(simbolo);
        ajustesDoSimbolo.setFitHeight(30);
        ajustesDoSimbolo.setFitWidth(30);
        alert.setGraphic(ajustesDoSimbolo); // mudar icone ao lado da mensagem
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(simbolo); //mudar icone da pagina 

        alert.showAndWait();
    }
}
