package com.Estoque.controllerFXML;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.Estoque.api.LoadScreen;
import com.Estoque.entities.Item;
import com.Estoque.repositories.EstoqueRepository;
import com.Estoque.service.EstoqueService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("/com/Estoque/gui/ViewTable.fxml")
public class ViewTableController {
		
    @Autowired
    private EstoqueService service;
    
    @Autowired
    private EstoqueRepository repository;
    
    @Autowired
    private ConfigurableApplicationContext context;

    @FXML
    private TableView<Item> tableView = new TableView<Item>();

    @FXML
    private TableColumn<Item, Long> colId;

    @FXML
    private TableColumn<Item, String> colName;

    @FXML
    private TableColumn<Item, Integer> colQuantity;

    @FXML
    private TableColumn<Item, Void> colAlter;

    @FXML
    private TableColumn<Item, Void> colDelete;

    @FXML
    private TableColumn<Item, Void> colAdd;
    
    private ObservableList<Item> listItems = FXCollections.observableArrayList();

    public void initialize() {
        // Definir as células das colunas
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // Configurar as colunas de botões
        configurateColumnAlter();
        configureteColumnDelete();
        configurateColumnAdd();

        // Carregar os itens
        loadItems();

        // Definir os itens na TableView
        tableView.setItems(listItems);
        styleButtons();// Estilizar tamanho dos botoes e evitar codigo verboso
    }

    private void configurateColumnAlter() {
        colAlter.setCellFactory(new Callback<TableColumn<Item, Void>, TableCell<Item, Void>>() {
            @Override
            public TableCell<Item, Void> call(TableColumn<Item, Void> param) {
                return new TableCell<Item, Void>() {
                    private Button btnAlter = new Button();

                    {              
                    	btnAlter.setGraphic(styleButtons().get(2));
                    	btnAlter.getStyleClass().add("btn");
                    	btnAlter.getStyleClass().add("btn-alter");
                        btnAlter.setOnAction(event -> {
                            Item item = getTableView().getItems().get(getIndex());
                            callAlterScreen(item);
                        });
                        setAlignment(Pos.CENTER);
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnAlter);
                        }
                    }
                };
            }
        });
    }

    private void configureteColumnDelete() {
        colDelete.setCellFactory(new Callback<TableColumn<Item, Void>, TableCell<Item, Void>>() {
            @Override
            public TableCell<Item, Void> call(TableColumn<Item, Void> param) {
                return new TableCell<Item, Void>() {
                    private final Button btnDelete = new Button("");

                    {
                    	btnDelete.setGraphic(styleButtons().get(1));
                    	btnDelete.getStyleClass().add("btn");
                    	btnDelete.getStyleClass().add("btn-delete");
                    	btnDelete.setOnAction(event -> {
                            Item item = getTableView().getItems().get(getIndex());
                            callRemoveScreen(item);
                    	});	
                    	setAlignment(Pos.CENTER);
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnDelete);
                        }
                    }
                };
            }
        });
    }

    private void configurateColumnAdd() {
        colAdd.setCellFactory(new Callback<TableColumn<Item, Void>, TableCell<Item, Void>>() {
            @Override
            public TableCell<Item, Void> call(TableColumn<Item, Void> param) {
                return new TableCell<Item, Void>() {
                    private final Button btnAdd = new Button("");

                    {
                    	btnAdd.setGraphic(styleButtons().get(0));
                    	btnAdd.getStyleClass().add("btn");
                    	btnAdd.getStyleClass().add("btn-add");
                    	btnAdd.setOnAction(event -> {
                            Item item = getTableView().getItems().get(getIndex());
                            callAddScreen(item);
                        });
                        setAlignment(Pos.CENTER);
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnAdd);
                        }
                    }
                };
            }
        });
    }

    public void loadItems() {
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
                        	listItems.setAll(newList);  // Atualiza a lista de itens na TableView
                            tableView.setItems(FXCollections.observableArrayList(newList)); // Agora atualiza a TableView
                            tableView.refresh();
                        } else {
                            System.out.println("Nenhum item encontrado");
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Platform.runLater(() -> System.out.printf("Erro", "Erro ao carregar itens: " ));
                }
            });
        } catch (Exception e) {
        	System.out.printf("Erro", "Erro ao carregar os itens: " + e.getMessage());
        }
    }
    
    @FXML
    private void callAlterScreen(Item item) {
        // Obter o item selecionado na TableView

        if (item != null) {  

        	Platform.runLater(() -> {
                FxWeaver fxWeaver = context.getBean(FxWeaver.class);
                Parent root = fxWeaver.loadView(AlterScreenController.class);

                AlterScreenController alterScreenController = fxWeaver.getBean(AlterScreenController.class);
                alterScreenController.receiveItem(item, this::loadItems);

                Scene cenaDeAlter = new Scene(root);
                Stage telaDeAlter = new Stage();
                telaDeAlter.getIcons().add(new Image("images/btnAlter.png"));
                telaDeAlter.setTitle("Alterar quantidade");
                telaDeAlter.setScene(cenaDeAlter);
                telaDeAlter.show();
            });
        }
	
}
    
    @FXML
    private void callRemoveScreen(Item item) {
    	  // Código para abrir a tela de remoção
        Platform.runLater(() -> {
            FxWeaver fxWeaver = context.getBean(FxWeaver.class);
            Parent root = fxWeaver.loadView(RemoveScreenController.class); // Ou a tela de remoção

            RemoveScreenController removeScreenController = fxWeaver.getBean(RemoveScreenController.class);
            removeScreenController.receiveItem(item, this::loadItems); // Passa o item para o controller de remoção

            Scene removeScene = new Scene(root);
            Stage removeScreen = new Stage();
            removeScreen.getIcons().add(new Image("images/btnRemove.png"));
            removeScreen.setTitle("Remover quantidade");
            removeScreen.setScene(removeScene);
            removeScreen.show();
        });
    }
        @FXML
        private void callAddScreen(Item item) {
            // Obter o item selecionado na TableView

            if (item != null) {  

            	Platform.runLater(() -> {
                    FxWeaver fxWeaver = context.getBean(FxWeaver.class);
                    Parent root = fxWeaver.loadView(AddScreenController.class);

                    AddScreenController addScreenController = fxWeaver.getBean(AddScreenController.class);
                    addScreenController.receiveItem(item, this::loadItems);

                    Scene addScene = new Scene(root);
                    Stage addScreen = new Stage();
                    addScreen.getIcons().add(new Image("images/btnAdd.png"));
                    addScreen.setTitle("Adicionar quantidade");
                    addScreen.setScene(addScene);
                    addScreen.show();
                });
            }
    	
    }
        @FXML
        private void callAccessScreen() {
            try {
                LoadScreen.showScreen("Relatório", RelatoryController.class, "relatory");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    private List<ImageView> styleButtons(){
    	Image imageAlter = new Image("/images/btnAlter.png");
    	ImageView viewAlter = new ImageView(imageAlter);
    	Image imageRemove = new Image("/images/btnRemove.png");
    	ImageView viewRemove = new ImageView(imageRemove);
    	Image imageAdd = new Image("/images/btnAdd.png");
    	ImageView viewAdd = new ImageView(imageAdd);
        viewAlter.setFitWidth(30);
        viewAlter.setFitHeight(30);
        viewRemove.setFitWidth(30);
        viewRemove.setFitHeight(30);
        viewAdd.setFitWidth(30);
        viewAdd.setFitHeight(30);
        List<ImageView> viewList = new ArrayList<ImageView>();
        viewList.add(viewAdd);
        viewList.add(viewRemove);
        viewList.add(viewAlter);
        return viewList;
    	
    }
}
