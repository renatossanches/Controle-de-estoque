// 
// Decompiled by Procyon v0.6.0
// 

package com.Estoque.controllerFXML;

import javafx.stage.Stage;
import com.Estoque.api.LoadScreen;
import com.Estoque.api.AlertMsg;
import com.Estoque.repositories.TokenAuthentication;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import com.Estoque.entities.Item;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import com.Estoque.repositories.LogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.Estoque.service.EstoqueService;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/com/Estoque/gui/AlterScreen.fxml")
public class AlterScreenController
{
    @Autowired
    private EstoqueService service;
    
    @Autowired
    private LogsRepository logs;
    
    @FXML
    private TextField txtQuantity;
    
    @FXML
    private TextField txtName;
    
    @FXML
    private Button btnAlter;
    
    @FXML
    private Label labelItem;
    private Item item;
    private Runnable onCloseCallback;
    
    public AlterScreenController() {
    	txtQuantity = new TextField();
        txtName = new TextField();
        labelItem = new Label();
    }
    
    @FXML
    public void initialize() {
    }
    
    @FXML
    private void enterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            event.consume();
            if (btnAlter.isVisible()) {
                alter();
            }
        }
    }
    
    @FXML
    private void alter() {
        try {
            if (TokenAuthentication.isUserLoggedIn()) {
                item.setQuantity(Integer.valueOf(Integer.parseInt(txtQuantity.getText())));
                item.setName(txtName.getText());
                service.alterItem(item);
                AlertMsg.showMessage("Sucesso", "Item alterado com sucesso", true);
                if (!item.getQuantity().equals(Integer.parseInt(txtQuantity.getText())) && !item.getName().equals(txtName.getText())) {
                    logs.logUserAction("Nome alterado de: " + item.getName() + " para: " + txtName.getText() + "\n\n e Quantidade alterada de: " + String.valueOf(item.getQuantity()) + " para: " + txtQuantity.getText());
                }
                else if (!item.getQuantity().equals(Integer.parseInt(txtQuantity.getText()))) {
                    logs.logUserAction("Quantidade alterada de: " + item.getQuantity().toString() + " para: " + txtQuantity.getText());
                }
                else if (!item.getName().equals(txtName.getText())) {
                    logs.logUserAction("Nome alterado de: " + item.getName() + " para: " + txtName.getText());
                }
                if (onCloseCallback != null) {
                    onCloseCallback.run();
                }
                closeScreen();
            }
            else {
                AlertMsg.showMessage("Erro", "Erro ao adicionar a quantidade, Você precisa estar logado para completar essa ação", false);
                LoadScreen.showScreen("Login", LoginController.class, "login");
                Stage stage = (Stage)btnAlter.getScene().getWindow();
                stage.close();
            }
    	}
        catch (Exception e) {
            AlertMsg.showMessage("Erro", "Todos os campos devem ser preenchidos", false);
            e.printStackTrace();
        }
    }


    private void closeScreen() {
        Stage stage = (Stage)btnAlter.getScene().getWindow();
        stage.close();
    }
    
    public void receiveItem(Item item, Runnable onCloseCallback) {
        this.item = item;
        this.onCloseCallback = onCloseCallback;
        this.labelItem.setText(item.toString());
        this.txtName.setText(item.getName());
        this.txtQuantity.setText(String.valueOf(item.getQuantity()));
    }
}
