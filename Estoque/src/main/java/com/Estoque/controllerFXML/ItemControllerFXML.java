package com.Estoque.controllerFXML;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.Estoque.api.AlertMsg;
import com.Estoque.api.LoadScreen;
import com.Estoque.entities.Item;
import com.Estoque.repositories.EstoqueRepository;
import com.Estoque.repositories.LogsRepository;
import com.Estoque.repositories.TokenAuthentication;
import com.Estoque.service.EstoqueService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("/com/Estoque/gui/estoque.fxml")
public class ItemControllerFXML {

	@Autowired
	private EstoqueService service;
	
	@Autowired
	private LogsRepository logs;
	
	@Autowired
	private EstoqueRepository repository;
	
	@FXML
	private TextField txtNome;
	
	@FXML
	private TextField txtQuantidade;
	
	@FXML
	private TextField txtId;
	
	@FXML
	private Button btnAdicionar;
	
	@FXML
	private Button btnRemover;
	
	@FXML
	private Button btnClickAdicionar;
	
	@FXML
	private Button btnAlterar;
	
	@FXML
	private Button btnClickRemover;
	
	@FXML
	private Button btnClickAlterar;
	
	@FXML
	private Button btnAll;
	
	@FXML
	private Button btnSetaEsquerda;
	
	@FXML
	private Button btnSetaDireita;
	
	@FXML
	private Button screen2;
	
	@FXML
	private Button btnLogOff;
	
	@FXML
	private Label allItens;
	
	@FXML
	private Label labelIdAttTela = new Label("");

	@FXML
	private ScrollPane SPIdAttTela;

	@FXML
	private Label labelNomeAttTela = new Label("");

	@FXML
	private ScrollPane SPNomeAttTela;

	@FXML
	private Label labelQuantidadeAttTela = new Label("");

	@FXML
	private ScrollPane SPQuantidadeAttTela;

	@FXML
	private VBox vboxTxtFields;

	@FXML
	private Label labelTitulo = new Label("");

	private List<Item> itens;

	private int currentIndex = 0;

	private String estado = "";

	private Stage stageAberta;

	@FXML
	public void initialize() {
		if (this.stageAberta != null && this.stageAberta.isShowing()) {
			return;
		}
		carregarItens();
		btnLogOff.setVisible(TokenAuthentication.isUserLoggedIn());
		btnAdicionar.setVisible(false);
		btnRemover.setVisible(false);
		btnAlterar.setVisible(false);
		SPIdAttTela.setVisible(false);
		SPNomeAttTela.setVisible(false);
		SPQuantidadeAttTela.setVisible(false);
	}

	@FXML
	private void clickAdicionar() {
		if (!btnAdicionar.isVisible()) {
			estado = "ADICIONAR";
			attVisible();
			desativarLabels();
			btnAdicionar.setVisible(true);
			btnRemover.setVisible(false);
			btnAlterar.setVisible(false);
			btnSetaDireita.setVisible(false);
			btnSetaEsquerda.setVisible(false);
			txtId.setVisible(false);
			SPIdAttTela.setVisible(true);
			txtNome.setText("");
			txtQuantidade.setText("");
		} else {
			btnAdicionar.setVisible(false);
			attDisplay();
		}
	}

	@FXML
	private void clickRemover() {
		if (!btnRemover.isVisible()) {
			estado = "REMOVER";
			attVisible();
			carregarItens();
			desativarLabels();
			btnAdicionar.setVisible(false);
			btnRemover.setVisible(true);
			btnAlterar.setVisible(false);
			txtNome.setVisible(false);
			txtQuantidade.setVisible(false);
			SPNomeAttTela.setVisible(true);
			SPQuantidadeAttTela.setVisible(true);
		} else {
			btnRemover.setVisible(false);
			attDisplay();
		}
	}

	@FXML
	private void clickAlterar() {
		if (!btnAlterar.isVisible()) {
			estado = "ALTERAR";
			attVisible();
			carregarItens();
			desativarLabels();
			btnAdicionar.setVisible(false);
			btnRemover.setVisible(false);
			btnAlterar.setVisible(true);
			SPIdAttTela.setVisible(true);
			txtId.setVisible(false);
		} else {
			btnAlterar.setVisible(false);
			attDisplay();
		}
	}

	private void attVisible() {
		labelIdAttTela.setText("");
		labelNomeAttTela.setText("");
		labelQuantidadeAttTela.setText("");
		SPIdAttTela.setVisible(false);
		SPNomeAttTela.setVisible(false);
		SPQuantidadeAttTela.setVisible(false);
		txtId.setVisible(true);
		txtNome.setVisible(true);
		txtQuantidade.setVisible(true);
		btnSetaDireita.setVisible(true);
		btnSetaEsquerda.setVisible(true);
	}

	private void attDisplay() {
		if (!(btnAdicionar.isVisible() && btnRemover.isVisible() && btnAlterar.isVisible())) {
			attVisible();
			Platform.runLater(() -> carregarItens());

		}
	}

	private void desativarLabels() {
		SPIdAttTela.setVisible(false);
		SPNomeAttTela.setVisible(false);
		SPQuantidadeAttTela.setVisible(false);
	}


	private void preencherCampos(Item item) {
		txtId.setText(String.valueOf(item.getId()));
		txtNome.setText(item.getNome());
		txtQuantidade.setText(String.valueOf(item.getQuantidade()));
		if (btnRemover.isVisible()) {
			labelNomeAttTela.setText(item.getNome());
			labelQuantidadeAttTela.setText(String.valueOf(item.getQuantidade()));
		}
		if (btnAdicionar.isVisible()) {
			labelIdAttTela.setText(String.valueOf(item.getId()));
		}
		if (btnAlterar.isVisible()) {
			labelIdAttTela.setText(String.valueOf(item.getId()));
		}
	}


	public void carregarItens() {
		try {
			repository.addRealTimeListener(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					List<Item> novaLista = new ArrayList<>();
					for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
						Item item = snapshot.getValue(Item.class);
						if (item != null) {
							item.setFirebaseId(snapshot.getKey()); // Salva o ID único do Firebase
							novaLista.add(item);
						}
					}

					Platform.runLater(() -> {
						if (!novaLista.isEmpty()) {
							itens = novaLista; // Atualiza a lista de itens
							currentIndex = 0; // Reseta para o primeiro item
							preencherCampos(itens.get(currentIndex)); // Atualiza a UI
						} else {
							AlertMsg.mostrarMensagem("Nenhum item", "Nenhum item foi encontrado.", false);
						}
					});
				}

				@Override
				public void onCancelled(DatabaseError error) {
					Platform.runLater(() -> AlertMsg.mostrarMensagem("Erro",
							"Erro ao carregar itens: " + error.getMessage(), false));
				}
			});
		} catch (Exception e) {
			AlertMsg.mostrarMensagem("Erro", "Erro ao carregar os itens: " + e.getMessage(), false);
		}
	}

}
