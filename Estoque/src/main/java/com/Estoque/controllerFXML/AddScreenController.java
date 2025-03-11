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
@FxmlView("/com/Estoque/gui/addScreen.fxml")
public class AddScreenController
{
    @Autowired
    private FirebaseConfig config;
    
    @Autowired
    private LogsRepository logs;
    
    @Autowired
    private EstoqueService service;
    
    @FXML
    private TextField txtAdicionar;
    
    @FXML
    private Button btnAdicionarQuantidade;
    
    @FXML
    private Label labelItem;
    private Item item;
    private Runnable onCloseCallback;
    
    public AddScreenController() {
        this.labelItem = new Label();
    }
    
    @FXML
    public void initialize() {
    }
    
    @FXML
    private void enterPressed(final KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            event.consume();
            if (this.btnAdicionarQuantidade.isVisible()) {
                this.adicionar();
            }
        }
    }
    
    @FXML
    private void adicionar() {
        try {
            if (TokenAuthentication.isUserLoggedIn()) {
                final int quantidadeAdicionar = Integer.parseInt(this.txtAdicionar.getText());
                this.item.setQuantidade(Integer.valueOf(this.item.getQuantidade() + quantidadeAdicionar));
                this.service.alterItem(this.item);
                AlertMsg.mostrarMensagem("Sucesso", "Quantidade adicionada com sucesso", true);
                this.logs.logUserAction("Quantidade adicionada: " + quantidadeAdicionar);
                if (this.onCloseCallback != null) {
                    this.onCloseCallback.run();
                }
                this.fecharTela();
            }
            else {
                AlertMsg.mostrarMensagem("Erro", "Erro ao adcionar a quantidade, Voc\u00ea precisa estar logado para completar essa a\u00e7\u00e3o", false);
                LoadScreen.showScreen("Login", LoginController.class, "login");
                final Stage stage = (Stage)this.btnAdicionarQuantidade.getScene().getWindow();
                stage.close();
            }
        }
        catch (final Exception e) {
            AlertMsg.mostrarMensagem("Erro", "A quantidade deve ser preenchida", false);
        }
    }
    
    private void fecharTela() {
        final Stage stage = (Stage)this.btnAdicionarQuantidade.getScene().getWindow();
        stage.close();
    }
    
    public void receberItem(final Item item, final Runnable onCloseCallback) {
        this.item = item;
        this.onCloseCallback = onCloseCallback;
        this.labelItem.setText(item.toString());
    }
}
