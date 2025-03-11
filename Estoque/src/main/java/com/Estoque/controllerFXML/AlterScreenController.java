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
@FxmlView("/com/Estoque/gui/alterScreen.fxml")
public class AlterScreenController
{
    @Autowired
    private EstoqueService service;
    
    @Autowired
    private LogsRepository logs;
    
    @FXML
    private TextField txtQuantidade;
    
    @FXML
    private TextField txtNome;
    
    @FXML
    private Button btnAlterar;
    
    @FXML
    private Label labelItem;
    private Item item;
    private Runnable onCloseCallback;
    
    public AlterScreenController() {
        this.txtQuantidade = new TextField();
        this.txtNome = new TextField();
        this.labelItem = new Label();
    }
    
    @FXML
    public void initialize() {
    }
    
    @FXML
    private void enterPressed(final KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            event.consume();
            if (this.btnAlterar.isVisible()) {
                this.alterar();
            }
        }
    }
    
    @FXML
    private void alterar() {
        try {
            if (TokenAuthentication.isUserLoggedIn()) {
                this.item.setQuantidade(Integer.valueOf(Integer.parseInt(this.txtQuantidade.getText())));
                this.item.setNome(this.txtNome.getText());
                this.service.alterItem(this.item);
                AlertMsg.mostrarMensagem("Sucesso", "Item alterado com sucesso", true);
                if (!this.item.getQuantidade().equals(Integer.parseInt(this.txtQuantidade.getText())) && !this.item.getNome().equals(this.txtNome.getText())) {
                    this.logs.logUserAction("Nome alterado de: " + this.item.getNome() + " para: " + this.txtNome.getText() + "\n\n e Quantidade alterada de: " + String.valueOf(this.item.getQuantidade()) + " para: " + this.txtQuantidade.getText());
                }
                else if (!this.item.getQuantidade().equals(Integer.parseInt(this.txtQuantidade.getText()))) {
                    this.logs.logUserAction("Quantidade alterada de: " + this.item.getQuantidade().toString() + " para: " + this.txtQuantidade.getText());
                }
                else if (!this.item.getNome().equals(this.txtNome.getText())) {
                    this.logs.logUserAction("Nome alterado de: " + this.item.getNome() + " para: " + this.txtNome.getText());
                }
                if (this.onCloseCallback != null) {
                    this.onCloseCallback.run();
                }
                this.fecharTela();
            }
            else {
                AlertMsg.mostrarMensagem("Erro", "Erro ao adcionar a quantidade, Voc\u00ea precisa estar logado para completar essa a\u00e7\u00e3o", false);
                LoadScreen.showScreen("Login", LoginController.class, "login");
                final Stage stage = (Stage)this.btnAlterar.getScene().getWindow();
                stage.close();
            }
        }
        catch (final Exception e) {
            AlertMsg.mostrarMensagem("Erro", "Todos os campos devem ser preenchidos", false);
            e.printStackTrace();
        }
    }
    
    private void fecharTela() {
        final Stage stage = (Stage)this.btnAlterar.getScene().getWindow();
        stage.close();
    }
    
    public void receberItem(final Item item, final Runnable onCloseCallback) {
        this.item = item;
        this.onCloseCallback = onCloseCallback;
        this.labelItem.setText(item.toString());
        this.txtNome.setText(item.getNome());
        this.txtQuantidade.setText(String.valueOf(item.getQuantidade()));
    }
}
