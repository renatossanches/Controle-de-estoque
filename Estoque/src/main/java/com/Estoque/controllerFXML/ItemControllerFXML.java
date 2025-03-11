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

	@FXML
	private void chamarSegundaTela() {
		if (TokenAuthentication.isUserLoggedIn()) {
			LoadScreen.showScreen("Tabela de Visualização", ViewTableController.class, "planilha");
		} else {

			try {
				AlertMsg.mostrarMensagem("Erro",
						"Erro ao abrir planilha, Você precisa estar logado para completar essa ação", false);
				LoadScreen.showScreen("Login", LoginController.class, "login");
				Stage stage = (Stage) this.screen2.getScene().getWindow();
				stage.close();
			} catch (Exception e) {
				AlertMsg.mostrarMensagem("Erro", "Você precisa estar logado", false);
				e.printStackTrace();
			}
			this.btnLogOff.setVisible(TokenAuthentication.isUserLoggedIn());
		}
	}

	@FXML
	private void logOff() {
		TokenAuthentication.removerToken();
		Stage stage = (Stage) this.btnLogOff.getScene().getWindow();
		stage.close();
		this.btnLogOff.setVisible(false);
	}

	@FXML
	private void adicionarItem() {
		if (this.btnAdicionar.isVisible()) {
			clickAdicionar();
			try {
				if (TokenAuthentication.isUserLoggedIn()) {

					String nome = this.txtNome.getText();
					int quantidade = Integer.parseInt(this.txtQuantidade.getText());

					Item item = new Item(nome, Integer.valueOf(quantidade));

					this.service.add(item);
					desativarLabels();
					this.btnSetaDireita.setVisible(true);
					this.btnSetaEsquerda.setVisible(true);
					AlertMsg.mostrarMensagem("Sucesso", "Item adicionado com sucesso!", true);
					this.txtId.setVisible(true);
					this.logs.logUserAction("item adicionado");
				} else {

					AlertMsg.mostrarMensagem("Erro",
							"Erro ao adicionar item, Você precisa estar logado para completar essa ação", false);
					LoadScreen.showScreen("Login", LoginController.class, "login");
					Stage stage = (Stage) this.btnAdicionar.getScene().getWindow();
					stage.close();
				}
				if ("ADICIONAR".equals(this.estado)) {
					clickAdicionar();
				}
			} catch (Exception e) {
				AlertMsg.mostrarMensagem("Erro", " todos campos devem ser preenchidos", false);
				e.printStackTrace();
			}
		} else {

			AlertMsg.mostrarMensagem("Erro", "Erro ao adicionar o item", false);
		}
	}

	@FXML
	private void removerItem() {
		if (this.btnRemover.isVisible()) {
			clickRemover();
			try {
				if (TokenAuthentication.isUserLoggedIn()) {
					Long id = Long.valueOf(Long.parseLong(this.txtId.getText()));
					this.service.remove(id);
					this.txtNome.setVisible(true);
					desativarLabels();
					AlertMsg.mostrarMensagem("Sucesso", "Item removido com sucesso!", false);
					this.logs.logUserAction("item removido");
					carregarItens();
				} else {

					AlertMsg.mostrarMensagem("Erro",
							"Erro ao remover item, Você precisa estar logado para completar essa ação", false);
					LoadScreen.showScreen("Login", LoginController.class, "login");
					Stage stage = (Stage) this.btnRemover.getScene().getWindow();
					stage.close();
				}
				if ("REMOVER".equals(this.estado)) {
					clickRemover();
				}
			} catch (Exception e) {
				AlertMsg.mostrarMensagem("Erro", "Erro ao remover item: " + e.getMessage(), false);
				e.printStackTrace();
			}
		} else {

			AlertMsg.mostrarMensagem("Erro", "Erro ao remover o item", false);
		}
	}

	@FXML
	private void alterarItem() {
		if (this.btnAlterar.isVisible()) {
			clickAlterar();
			try {
				if (TokenAuthentication.isUserLoggedIn()) {

					Item itemEquals = this.itens.get(this.currentIndex);

					Long id = Long.valueOf(Long.parseLong(this.txtId.getText()));
					String nome = this.txtNome.getText();
					int quantidade = Integer.parseInt(this.txtQuantidade.getText());
					Item item = new Item(id, nome, Integer.valueOf(quantidade));
					this.service.alterItem(item);
					this.txtId.setVisible(true);
					desativarLabels();
					AlertMsg.mostrarMensagem("Sucesso", "Item alterado com sucesso!", true);
					if (!itemEquals.getQuantidade()
							.equals(Integer.valueOf(Integer.parseInt(this.txtQuantidade.getText())))
							&& !itemEquals.getNome().equals(this.txtNome.getText())) {
						this.logs.logUserAction(
								"Nome alterado de: " + itemEquals.getNome() + " para: " + this.txtNome.getText()
										+ "\n\n e Quantidade alterada de: " + String.valueOf(itemEquals.getQuantidade())
										+ " para: " + this.txtQuantidade.getText());
					} else if (!itemEquals.getQuantidade()
							.equals(Integer.valueOf(Integer.parseInt(this.txtQuantidade.getText())))) {
						this.logs.logUserAction("Quantidade alterada de: " + itemEquals.getQuantidade().toString()
								+ " para: " + this.txtQuantidade.getText());
					} else if (!itemEquals.getNome().endsWith(this.txtNome.getText())) {
						this.logs.logUserAction(
								"Nome alterado de: " + itemEquals.getNome() + " para: " + this.txtNome.getText());
					}
					carregarItens();
				} else {

					AlertMsg.mostrarMensagem("Erro",
							"Erro ao adicionar item, Você precisa estar logado para completar essa ação", false);
					LoadScreen.showScreen("Login", LoginController.class, "login");
					Stage stage = (Stage) this.btnAdicionar.getScene().getWindow();
					stage.close();
				}
				if ("ALTERAR".equals(this.estado)) {
					clickAlterar();
				}
			} catch (Exception e) {
				AlertMsg.mostrarMensagem("Erro", "Erro ao alterar item: " + e.getMessage(), false);
			}
		} else {

			AlertMsg.mostrarMensagem("Erro", "Erro ao alterar o item", false);
		}
	}

	@FXML
	private void ListarTodosOsItens() {
		try {
			// Inicia a escuta em tempo real para sempre atualizar os itens
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
						// Atualiza a lista de itens e a interface com os novos dados
						if (!novaLista.isEmpty()) {
							itens = novaLista; // Atualiza a lista de itens
							currentIndex = 0; // Reseta para o primeiro item
							preencherCampos(itens.get(currentIndex)); // Atualiza a UI
							allItens.setText(itens.toString()); // Exibe todos os itens
						} else {
							allItens.setText("Nenhum item encontrado.");
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
			AlertMsg.mostrarMensagem("Erro", "Erro ao listar: " + e.getMessage(), false);
		}
	}

	@FXML
	private void moverEsquerda() {
		try {
			tirarFocus();
			if (itens != null && !itens.isEmpty()) {
				currentIndex = (currentIndex > 0) ? currentIndex - 1 : itens.size() - 1; // Move para o índice anterior
																							// ou volta para o último
																							// item
				preencherCampos(itens.get(currentIndex));
			}
			tirarFocus();
		} catch (Exception e) {
			AlertMsg.mostrarMensagem("Erro", "Erro ao mover para a esquerda: " + e.getMessage(), false);
		}
	}

	@FXML
	private void moverEsquerdaPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.LEFT) {
			moverEsquerda();
		}
	}

	@FXML
	private void moverDireita() {
		tirarFocus();
		try {
			if (itens != null && !itens.isEmpty()) {
				currentIndex = (currentIndex < itens.size() - 1) ? currentIndex + 1 : 0; // Move para o próximo índice
				preencherCampos(itens.get(currentIndex));
			}
		} catch (Exception e) {
			AlertMsg.mostrarMensagem("Erro", "Erro ao mover para a direita: " + e.getMessage(), false);
		}
	}

	@FXML
	private void moverDireitaPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.RIGHT) {
			moverDireita();
		}
	}

	@FXML
	private void tirarFocus() {
		// Desfoca todos os campos de texto ao navegar
		Platform.runLater(() -> {
			txtNome.getParent().requestFocus();
			txtQuantidade.getParent().requestFocus();
			txtId.getParent().requestFocus();
			txtId.setFocusTraversable(false);
			txtNome.setFocusTraversable(false);
			txtQuantidade.setFocusTraversable(false);
		});
	}

	@FXML
	private void voltarFocus() {
		Platform.runLater(() -> {
			txtId.requestFocus();
			txtId.setFocusTraversable(true);
			txtNome.setFocusTraversable(true);
			txtQuantidade.setFocusTraversable(true);
		});
	}

	@FXML
	private void enterPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			event.consume();// evita que o Enter seja chamado mais de 1x em uma unica requisiçãoS
			if (btnAdicionar.isVisible()) {
				adicionarItem();
			} else if (btnRemover.isVisible()) {
				removerItem();
			} else if (btnAlterar.isVisible()) {
				alterarItem();
			}
		}
	} 
	
	// Carrega todos os itens para navegar
	@FXML
	private void buscarPorId() {
		try {
			Item item = new Item();
			item = service.findById(Long.parseLong(txtId.getText())).join();
			currentIndex = itens.indexOf(item);
			preencherCampos(item);
		} catch (Exception e) {
			AlertMsg.mostrarMensagem("Erro", "Erro ao carregar o item: " + "id não existe", false);
		}
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
