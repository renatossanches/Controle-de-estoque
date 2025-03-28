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
@FxmlView("/com/Estoque/gui/ItemEstoque.fxml")
public class ItemControllerFXML {

	@Autowired
	private EstoqueService service;
	
	@Autowired
	private LogsRepository logs;
	
	@Autowired
	private EstoqueRepository repository;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private TextField txtQuantity;
	
	@FXML
	private TextField txtId;
	
	@FXML
	private Button btnAdd;
	
	@FXML
	private Button btnRemove;
	
	@FXML
	private Button btnClickAdd;
	
	@FXML
	private Button btnAlter;
	
	@FXML
	private Button btnClickRemove;
	
	@FXML
	private Button btnClickAlter;
	
	@FXML
	private Button btnAll;
	
	@FXML
	private Button btnArrowLeft;
	
	@FXML
	private Button btnArrowRight;
	
	@FXML
	private Button screen2;
	
	@FXML
	private Button btnLogOff;
	
	@FXML
	private Label allItems;
	
	@FXML
	private Label labelIdUpdateScreen = new Label("");

	@FXML
	private ScrollPane scrollPaneIdUpdateScreen;

	@FXML
	private Label labelNameUpdateScreen = new Label("");

	@FXML
	private ScrollPane scrollPaneNameUpdateScreen;

	@FXML
	private Label labelQuantityUpdateScreen = new Label("");

	@FXML
	private ScrollPane scrollPaneQuantityUpdateScreen;

	@FXML
	private VBox vboxTxtFields;

	@FXML
	private Label labelTitle = new Label("");

	private List<Item> items;

	private int currentIndex = 0;

	private String state = "";

	private Stage stageOpen;

	@FXML
	public void initialize() {
		if (stageOpen != null && stageOpen.isShowing()) {
			return;
		}
		LoadItems();
		btnLogOff.setVisible(TokenAuthentication.isUserLoggedIn());
		btnAdd.setVisible(false);
		btnRemove.setVisible(false);
		btnAlter.setVisible(false);
		scrollPaneIdUpdateScreen.setVisible(false);
		scrollPaneNameUpdateScreen.setVisible(false);
		scrollPaneQuantityUpdateScreen.setVisible(false);
	}

	@FXML
	private void clickAdd() {
		if (!btnAdd.isVisible()) {
			state = "ADICIONAR";
			updateVisible();
			disableLabels();
			btnAdd.setVisible(true);
			btnRemove.setVisible(false);
			btnAlter.setVisible(false);
			btnArrowRight.setVisible(false);
			btnArrowLeft.setVisible(false);
			txtId.setVisible(false);
			scrollPaneIdUpdateScreen.setVisible(true);
			txtName.setText("");
			txtQuantity.setText("");
		} else {
			btnAdd.setVisible(false);
			updateDisplay();
		}
	}

	@FXML
	private void clickRemove() {
		if (!btnRemove.isVisible()) {
			state = "REMOVER";
			updateVisible();
			LoadItems();
			disableLabels();
			btnAdd.setVisible(false);
			btnRemove.setVisible(true);
			btnAlter.setVisible(false);
			txtName.setVisible(false);
			txtQuantity.setVisible(false);
			scrollPaneNameUpdateScreen.setVisible(true);
			scrollPaneQuantityUpdateScreen.setVisible(true);
		} else {
			btnRemove.setVisible(false);
			updateDisplay();
		}
	}

	@FXML
	private void clickAlter() {
		if (!btnAlter.isVisible()) {
			state = "ALTERAR";
			updateVisible();
			LoadItems();
			disableLabels();
			btnAdd.setVisible(false);
			btnRemove.setVisible(false);
			btnAlter.setVisible(true);
			scrollPaneIdUpdateScreen.setVisible(true);
			txtId.setVisible(false);
		} else {
			btnAlter.setVisible(false);
			updateDisplay();
		}
	}

	private void updateVisible() {
		labelIdUpdateScreen.setText("");
		labelNameUpdateScreen.setText("");
		labelQuantityUpdateScreen.setText("");
		scrollPaneIdUpdateScreen.setVisible(false);
		scrollPaneNameUpdateScreen.setVisible(false);
		scrollPaneQuantityUpdateScreen.setVisible(false);
		txtId.setVisible(true);
		txtName.setVisible(true);
		txtQuantity.setVisible(true);
		btnArrowRight.setVisible(true);
		btnArrowLeft.setVisible(true);
	}

	private void updateDisplay() {
		if (!(btnAdd.isVisible() && btnRemove.isVisible() && btnAlter.isVisible())) {
			updateVisible();
			Platform.runLater(() -> LoadItems());

		}
	}

	private void disableLabels() {
		scrollPaneIdUpdateScreen.setVisible(false);
		scrollPaneNameUpdateScreen.setVisible(false);
		scrollPaneQuantityUpdateScreen.setVisible(false);
	}

	@FXML
	private void callSecondScreen() {
		if (TokenAuthentication.isUserLoggedIn()) {
			LoadScreen.showScreen("Tabela de Visualização", ViewTableController.class, "spreadsheet");
		} else {

			try {
				AlertMsg.showMessage("Erro",
						"Erro ao abrir planilha, Você precisa estar logado para completar essa ação", false);
				LoadScreen.showScreen("Login", LoginController.class, "login");
				Stage stage = (Stage)screen2.getScene().getWindow();
				stage.close();
			} catch (Exception e) {
				AlertMsg.showMessage("Erro", "Você precisa estar logado", false);
				e.printStackTrace();
			}
			btnLogOff.setVisible(TokenAuthentication.isUserLoggedIn());
		}
	}

	@FXML
	private void logOff() {
		TokenAuthentication.removeToken();
		Stage stage = (Stage) btnLogOff.getScene().getWindow();
		stage.close();
		btnLogOff.setVisible(false);
	}

	@FXML
	private void addItem() {
		if (btnAdd.isVisible()) {
			clickAdd();
			try {
				if (TokenAuthentication.isUserLoggedIn()) {

					String nome = txtName.getText();
					int quantity = Integer.parseInt(txtQuantity.getText());

					Item item = new Item(nome, Integer.valueOf(quantity));

					service.add(item);
					disableLabels();
					btnArrowRight.setVisible(true);
					btnArrowLeft.setVisible(true);
					AlertMsg.showMessage("Sucesso", "Item adicionado com sucesso!", true);
					txtId.setVisible(true);
					logs.logUserAction("item adicionado");
				} else {

					AlertMsg.showMessage("Erro",
							"Erro ao adicionar item, Você precisa estar logado para completar essa ação", false);
					LoadScreen.showScreen("Login", LoginController.class, "login");
					Stage stage = (Stage) btnAdd.getScene().getWindow();
					stage.close();
				}
				if ("ADICIONAR".equals(state)) {
					clickAdd();
				}
			} catch (Exception e) {
				AlertMsg.showMessage("Erro", " todos campos devem ser preenchidos", false);
				e.printStackTrace();
			}
		} else {

			AlertMsg.showMessage("Erro", "Erro ao adicionar o item", false);
		}
	}

	@FXML
	private void removeItem() {
		if (btnRemove.isVisible()) {
			clickRemove();
			try {
				if (TokenAuthentication.isUserLoggedIn()) {
					Long id = Long.valueOf(Long.parseLong(txtId.getText()));
					service.remove(id);
					txtName.setVisible(true);
					disableLabels();
					AlertMsg.showMessage("Sucesso", "Item removido com sucesso!", true);
					logs.logUserAction("item removido");
					LoadItems();
				} else {

					AlertMsg.showMessage("Erro",
							"Erro ao remover item, Você precisa estar logado para completar essa ação", false);
					LoadScreen.showScreen("Login", LoginController.class, "login");
					Stage stage = (Stage) btnRemove.getScene().getWindow();
					stage.close();
				}
				if ("REMOVER".equals(state)) {
					clickRemove();
				}
			} catch (Exception e) {
				AlertMsg.showMessage("Erro", "Erro ao remover item: " + e.getMessage(), false);
				e.printStackTrace();
			}
		} else {

			AlertMsg.showMessage("Erro", "Erro ao remover o item", false);
		}
	}

	@FXML
	private void alterItem() {
		if (btnAlter.isVisible()) {
			clickAlter();
			try {
				if (TokenAuthentication.isUserLoggedIn()) {

					Item itemEquals = items.get(currentIndex);
					System.out.println(currentIndex);
					Long id = Long.valueOf(Long.parseLong(txtId.getText()));
					String name = txtName.getText();
					int quantity = Integer.parseInt(txtQuantity.getText());
					Item item = new Item(id, name, Integer.valueOf(quantity));
					service.alterItem(item);
					txtId.setVisible(true);
					disableLabels();
					AlertMsg.showMessage("Sucesso", "Item alterado com sucesso!", true);
					if (!(itemEquals.getQuantity().equals(quantity))
							&& !(itemEquals.getName().equals(name))) {
						logs.logUserAction(
								"Nome alterado de: " + itemEquals.getName() + " para: " + name
										+ "\n\n e Quantidade alterada de: " + String.valueOf(itemEquals.getQuantity())
										+ " para: " + quantity);
					}
					
					else if (!itemEquals.getQuantity().equals(quantity)) {
						logs.logUserAction("Quantidade alterada de: " + itemEquals.getQuantity().toString()
								+ " para: " + quantity);
					}
					else if (!itemEquals.getName().equals(name)) {
						logs.logUserAction(
								"Nome alterado de: " + itemEquals.getName() + " para: " + name);
					}
					LoadItems();
				} else {
					AlertMsg.showMessage("Erro",
							"Erro ao adicionar item, Você precisa estar logado para completar essa ação", false);
					LoadScreen.showScreen("Login", LoginController.class, "login");
					Stage stage = (Stage) btnAdd.getScene().getWindow();
					stage.close();
				}
				if ("ALTERAR".equals(state)) {
					clickAlter();
				}
			} catch (Exception e) {
				AlertMsg.showMessage("Erro", "Erro ao alterar item: " + e.getMessage(), false);
			}
		} else {

			AlertMsg.showMessage("Erro", "Erro ao alterar o item", false);
		}
	}

	@FXML
	private void ListAllItems() {
		try {
			// Inicia a escuta em tempo real para sempre atualizar os itens
			repository.addRealTimeListener(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					List<Item> newList = new ArrayList<>();
					for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
						Item item = snapshot.getValue(Item.class);
						if (item != null) {
							item.setFirebaseId(snapshot.getKey()); // Salva o ID único do Firebase
							newList.add(item);
						}
					}

					Platform.runLater(() -> {
						// Atualiza a lista de items e a interface com os novos dados
						if (!newList.isEmpty()) {
							items = newList; // Atualiza a lista de itens
							currentIndex = 0; // Reseta para o primeiro item
							fillInFields(items.get(currentIndex)); // Atualiza a UI
							allItems.setText(items.toString()); // Exibe todos os itens
						} else {
							allItems.setText("Nenhum item encontrado.");
						}
					});
				}

				@Override
				public void onCancelled(DatabaseError error) {
					Platform.runLater(() -> AlertMsg.showMessage("Erro",
							"Erro ao carregar items: " + error.getMessage(), false));
				}
			});
		} catch (Exception e) {
			AlertMsg.showMessage("Erro", "Erro ao listar: " + e.getMessage(), false);
		}
	}

	@FXML
	private void moveLeft() {
		try {
			takeFocus();
			if (items != null && !items.isEmpty()) {
				currentIndex = (currentIndex > 0) ? currentIndex - 1 : items.size() - 1; // Move para o índice anterior
																							// ou volta para o último
																							// item
				fillInFields(items.get(currentIndex));
			}
			takeFocus();
		} catch (Exception e) {
			AlertMsg.showMessage("Erro", "Erro ao mover para a esquerda: " + e.getMessage(), false);
		}
	}

	@FXML
	private void moveLeftPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.LEFT) {
			moveLeft();
		}
	}

	@FXML
	private void moveRight() {
		takeFocus();
		try {
			if (items != null && !items.isEmpty()) {
				currentIndex = (currentIndex < items.size() - 1) ? currentIndex + 1 : 0; // Move para o próximo índice
				fillInFields(items.get(currentIndex));
			}
		} catch (Exception e) {
			AlertMsg.showMessage("Erro", "Erro ao mover para a direita: " + e.getMessage(), false);
		}
	}

	@FXML
	private void moveRightPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.RIGHT) {
			moveRight();
		}
	}

	@FXML
	private void takeFocus() {
		// Desfoca todos os campos de texto ao navegar
		Platform.runLater(() -> {
			txtName.getParent().requestFocus();
			txtQuantity.getParent().requestFocus();
			txtId.getParent().requestFocus();
			txtId.setFocusTraversable(false);
			txtName.setFocusTraversable(false);
			txtQuantity.setFocusTraversable(false);
		});
	}

	@FXML
	private void backFocus() {
		Platform.runLater(() -> {
			txtId.requestFocus();
			txtId.setFocusTraversable(true);
			txtName.setFocusTraversable(true);
			txtQuantity.setFocusTraversable(true);
		});
	}

	@FXML
	private void enterPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			event.consume();// evita que o Enter seja chamado mais de 1x em uma unica requisição
			if (btnAdd.isVisible()) {
				addItem();
			} else if (btnRemove.isVisible()) {
				removeItem();
			} else if (btnAlter.isVisible()) {
				alterItem();
			}
		}
	} 
	
	// Carrega todos os itens para navegar
	@FXML
	private void findById() {
		try {
			Item item = new Item();
			item = service.findById(Long.parseLong(txtId.getText())).join();
			currentIndex = items.indexOf(item);
			fillInFields(item);
		} catch (Exception e) {
			AlertMsg.showMessage("Erro", "Erro ao carregar o item: " + "id não existe", false);
		}
	}
	
	private void fillInFields(Item item) {
		txtId.setText(String.valueOf(item.getId()));
		txtName.setText(item.getName());
		txtQuantity.setText(String.valueOf(item.getQuantity()));
		if (btnRemove.isVisible()) {
			labelNameUpdateScreen.setText(item.getName());
			labelQuantityUpdateScreen.setText(String.valueOf(item.getQuantity()));
		}
		if (btnAdd.isVisible()) {
			labelIdUpdateScreen.setText(String.valueOf(item.getId()));
		}
		if (btnAlter.isVisible()) {
			labelIdUpdateScreen.setText(String.valueOf(item.getId()));
		}
	}


	public void LoadItems() {
		try {
			repository.addRealTimeListener(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					List<Item> newList = new ArrayList<>();
					for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
						Item item = snapshot.getValue(Item.class);
						if (item != null) {
							item.setFirebaseId(snapshot.getKey()); // Salva o ID único do Firebase
							newList.add(item);
						}
					}

					Platform.runLater(() -> {
						if (!newList.isEmpty()) {
							items = newList; // Atualiza a lista de itens
							currentIndex = 0; // Reseta para o primeiro item
							fillInFields(items.get(currentIndex)); // Atualiza a UI
						} else {
							AlertMsg.showMessage("Nenhum item", "Nenhum item foi encontrado.", false);
						}
					});
				}

				@Override
				public void onCancelled(DatabaseError error) {
					Platform.runLater(() -> AlertMsg.showMessage("Erro",
							"Erro ao carregar itens: " + error.getMessage(), false));
				}
			});
		} catch (Exception e) {
			AlertMsg.showMessage("Erro", "Erro ao carregar os items: " + e.getMessage(), false);
		}
	}

}
