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
import com.Estoque.service.EstoqueService;
import com.Estoque.repositories.LogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.Estoque.config.FirebaseConfig;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/com/Estoque/gui/AddScreen.fxml")
public class AddScreenController
{
    @Autowired
    private FirebaseConfig config;
    
    @Autowired
    private LogsRepository logs;
    
    @Autowired
    private EstoqueService service;
    
    @FXML
    private TextField txtAdd;
    
    @FXML
    private Button btnAddQuantity;
    
    @FXML
    private Label labelItem;
    private Item item;
    private Runnable onCloseCallback;
    
    public AddScreenController() {
        labelItem = new Label();
    }
    
    @FXML
    public void initialize() {
    }
    
    @FXML
    private void enterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            event.consume();
            if (btnAddQuantity.isVisible()) {
                add();
            }
        }
    }
    
    @FXML
    private void add() {
        try {
            if (TokenAuthentication.isUserLoggedIn()) {
                int addQuantity = Integer.parseInt(txtAdd.getText());
                item.setQuantity(Integer.valueOf(item.getQuantity() + addQuantity));
                service.alterItem(item);
                AlertMsg.showMessage("Sucesso", "Quantidade adicionada com sucesso", true);
                logs.logUserAction("Quantidade adicionada: " + addQuantity);
                if (onCloseCallback != null) {
                    onCloseCallback.run();
                }
                closeScreen();
            }
            else {
                AlertMsg.showMessage("Erro", "Erro ao adcionar a quantidade, Você precisa estar logado para completar essa ação", false);
                LoadScreen.showScreen("Login", LoginController.class, "login");
                Stage stage = (Stage)btnAddQuantity.getScene().getWindow();
                stage.close();
            }
        }
        catch (Exception e) {
            AlertMsg.showMessage("Erro", "A quantidade deve ser preenchida", false);
        }
    }
    
    private void closeScreen() {
        Stage stage = (Stage)btnAddQuantity.getScene().getWindow();
        stage.close();
    }
    
    public void receiveItem(Item item, Runnable onCloseCallback) {
        this.item = item;
        this.onCloseCallback = onCloseCallback;
        labelItem.setText(item.toString());
    }
}
