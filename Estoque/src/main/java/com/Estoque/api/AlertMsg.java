package com.Estoque.api;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class AlertMsg {

    private static Image alertError = new Image("/images/alertError.png"); 
    private static Image alertSuccess = new Image("/images/alertSuccess.png");
      
    //       true = deu certo              ||||||||     false = não deu certo
    public static void showMessage(String title, String message, boolean isSuccess) {
    	Image symbol;
    	if(isSuccess == true) {
    		symbol = alertSuccess;
        }
    	else {
    		symbol = alertError;
    	}
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); 
        alert.setContentText(message);
        ImageView symbolAdjustments = new ImageView(symbol);
        symbolAdjustments.setFitHeight(30);
        symbolAdjustments.setFitWidth(30);
        alert.setGraphic(symbolAdjustments); // mudar icone ao lado da mensagem
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(symbol); //mudar icone da pagina 

        alert.showAndWait();
    }
}
