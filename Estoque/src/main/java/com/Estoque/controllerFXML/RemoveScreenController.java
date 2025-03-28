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
@FxmlView("/com/Estoque/gui/RemoveScreen.fxml")
public class RemoveScreenController
{
    @Autowired
    private EstoqueService service;
    
    @Autowired
    private LogsRepository logs;
    
    @FXML
    private TextField txtRemove;
    
    @FXML
    private Button btnRemoveQuantity;
    
    @FXML
    private Label labelItem;
    private Item item;
    private Runnable onCloseCallback;
    
    public RemoveScreenController() {
        labelItem = new Label();
    }
    
    @FXML
    public void initialize() {
    }
    
    @FXML
    private void enterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            event.consume();
            if (btnRemoveQuantity.isVisible()) {
                remove();
            }
        }
    }
    
    @FXML
    private void remove() {
        try {
            if (TokenAuthentication.isUserLoggedIn()) {
                int QuantityRemove = Integer.parseInt(txtRemove.getText());
                item.setQuantity(Integer.valueOf(item.getQuantity() - QuantityRemove));
                service.alterItem(item);
                AlertMsg.showMessage("Sucesso", "Quantidade removida com sucesso", true);
                logs.logUserAction("Quantidade removida: " + QuantityRemove);
                if (onCloseCallback != null) {
                    onCloseCallback.run();
                }
                closeScreen();
            }
            else {
                AlertMsg.showMessage("Erro", "Erro ao adcionar a Quantidade, Você precisa estar logado para completar essa ação", false);
                LoadScreen.showScreen("Login", LoginController.class, "login");
                final Stage stage = (Stage)btnRemoveQuantity.getScene().getWindow();
                stage.close();
            }
        }
        catch (final Exception e) {
            AlertMsg.showMessage("Erro", "A Quantidade deve ser preenchida", false);
        }
    }
    
    private void closeScreen() {
        final Stage stage = (Stage)btnRemoveQuantity.getScene().getWindow();
        stage.close();
    }
    
    public void receiveItem(Item item, Runnable onCloseCallback) {
        this.item = item;
        this.onCloseCallback = onCloseCallback;
        this.labelItem.setText(item.toString());
    }
}
