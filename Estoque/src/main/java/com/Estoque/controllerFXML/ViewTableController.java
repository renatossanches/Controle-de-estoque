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
@FxmlView("/com/Estoque/gui/viewTable.fxml")
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
    private TableColumn<Item, String> colNome;

    @FXML
    private TableColumn<Item, Integer> colQuantidade;

    @FXML
    private TableColumn<Item, Void> colAlterar;

    @FXML
    private TableColumn<Item, Void> colDeletar;

    @FXML
    private TableColumn<Item, Void> colalterar;

    private ObservableList<Item> listaItens = FXCollections.observableArrayList();

    public void initialize() {
        // Definir as células das colunas
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));

        // Configurar as colunas de botões
        configurarColunaAlterar();
        configurarColunaDeletar();
        configurarColunaAdicionar();

        // Carregar os itens
        carregarItens();

        // Definir os itens na TableView
        tableView.setItems(listaItens);
        estilizandoBotoes();// Estilizar tamanho dos botoes e evitar codigo verboso
    }

    private void configurarColunaAlterar() {
        colAlterar.setCellFactory(new Callback<TableColumn<Item, Void>, TableCell<Item, Void>>() {
            @Override
            public TableCell<Item, Void> call(TableColumn<Item, Void> param) {
                return new TableCell<Item, Void>() {
                    private Button btnAlterar = new Button();

                    {              
                    	btnAlterar.setGraphic(estilizandoBotoes().get(2));
                    	btnAlterar.getStyleClass().add("btn");
                    	btnAlterar.getStyleClass().add("btn-alterar");
                        btnAlterar.setOnAction(event -> {
                            Item item = getTableView().getItems().get(getIndex());
                            chamarTelaDeAlterar(item);
                        });
                        setAlignment(Pos.CENTER);
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnAlterar);
                        }
                    }
                };
            }
        });
    }

    private void configurarColunaDeletar() {
        colDeletar.setCellFactory(new Callback<TableColumn<Item, Void>, TableCell<Item, Void>>() {
            @Override
            public TableCell<Item, Void> call(TableColumn<Item, Void> param) {
                return new TableCell<Item, Void>() {
                    private final Button btnDeletar = new Button("");

                    {
                    	btnDeletar.setGraphic(estilizandoBotoes().get(1));
                    	btnDeletar.getStyleClass().add("btn");
                    	btnDeletar.getStyleClass().add("btn-deletar");
                    	btnDeletar.setOnAction(event -> {
                            Item item = getTableView().getItems().get(getIndex());
                    		chamarTelaDeRemover(item);
                    	});	
                    	setAlignment(Pos.CENTER);
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnDeletar);
                        }
                    }
                };
            }
        });
    }

    private void configurarColunaAdicionar() {
        colalterar.setCellFactory(new Callback<TableColumn<Item, Void>, TableCell<Item, Void>>() {
            @Override
            public TableCell<Item, Void> call(TableColumn<Item, Void> param) {
                return new TableCell<Item, Void>() {
                    private final Button btnAdicionar = new Button("");

                    {
                    	btnAdicionar.setGraphic(estilizandoBotoes().get(0));
                    	btnAdicionar.getStyleClass().add("btn");
                    	btnAdicionar.getStyleClass().add("btn-adicionar");
                        btnAdicionar.setOnAction(event -> {
                            Item item = getTableView().getItems().get(getIndex());
                        	chamarTelaDeAdicionar(item);
                        });
                        setAlignment(Pos.CENTER);
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnAdicionar);
                        }
                    }
                };
            }
        });
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
                        	listaItens.setAll(novaLista);  // Atualiza a lista de itens na TableView
                            tableView.setItems(FXCollections.observableArrayList(novaLista)); // Agora atualiza a TableView
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
    private void chamarTelaDeAlterar(Item item) {
        // Obter o item selecionado na TableView

        if (item != null) {  

        	Platform.runLater(() -> {
                FxWeaver fxWeaver = context.getBean(FxWeaver.class);
                Parent root = fxWeaver.loadView(AlterScreenController.class);

                AlterScreenController alterScreenController = fxWeaver.getBean(AlterScreenController.class);
                alterScreenController.receberItem(item, this::carregarItens);

                Scene cenaDeAlterar = new Scene(root);
                Stage telaDeAlterar = new Stage();
                telaDeAlterar.getIcons().add(new Image("images/btnAlterar.png"));
                telaDeAlterar.setTitle("Alterar Quantidade");
                telaDeAlterar.setScene(cenaDeAlterar);
                telaDeAlterar.show();
            });
        }
	
}
    
    @FXML
    private void chamarTelaDeRemover(Item item) {
    	  // Código para abrir a tela de remoção
        Platform.runLater(() -> {
            FxWeaver fxWeaver = context.getBean(FxWeaver.class);
            Parent root = fxWeaver.loadView(RemoveScreenController.class); // Ou a tela de remoção

            RemoveScreenController removeScreenController = fxWeaver.getBean(RemoveScreenController.class);
            removeScreenController.receberItem(item, this::carregarItens); // Passa o item para o controller de remoção

            Scene cenaDeRemover = new Scene(root);
            Stage telaDeRemover = new Stage();
            telaDeRemover.getIcons().add(new Image("images/btnRemover.png"));
            telaDeRemover.setTitle("Remover Quantidade");
            telaDeRemover.setScene(cenaDeRemover);
            telaDeRemover.show();
        });
    }
        @FXML
        private void chamarTelaDeAdicionar(Item item) {
            // Obter o item selecionado na TableView

            if (item != null) {  

            	Platform.runLater(() -> {
                    FxWeaver fxWeaver = context.getBean(FxWeaver.class);
                    Parent root = fxWeaver.loadView(AddScreenController.class);

                    AddScreenController addScreenController = fxWeaver.getBean(AddScreenController.class);
                    addScreenController.receberItem(item, this::carregarItens);

                    Scene cenaDeAdicionar = new Scene(root);
                    Stage telaDeAdicionar = new Stage();
                    telaDeAdicionar.getIcons().add(new Image("images/btnAdicionar.png"));
                    telaDeAdicionar.setTitle("Adicionar Quantidade");
                    telaDeAdicionar.setScene(cenaDeAdicionar);
                    telaDeAdicionar.show();
                });
            }
    	
    }
        @FXML
        private void chamarTelaDeAcessos() {
            try {
                LoadScreen.showScreen("Relat\u00f3rio", RelatoryController.class, "relatorio");
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    private List<ImageView> estilizandoBotoes(){
    	Image imagemAlterar = new Image("/images/btnAlterar.png");
    	ImageView viewAlterar = new ImageView(imagemAlterar);
    	Image imagemRemover = new Image("/images/btnRemover.png");
    	ImageView viewRemover = new ImageView(imagemRemover);
    	Image imagemAdicionar = new Image("/images/btnAdicionar.png");
    	ImageView viewAdicionar = new ImageView(imagemAdicionar);
        viewAlterar.setFitWidth(30);
        viewAlterar.setFitHeight(30);
        viewRemover.setFitWidth(30);
        viewRemover.setFitHeight(30);
        viewAdicionar.setFitWidth(30);
        viewAdicionar.setFitHeight(30);
        List<ImageView> listaDeViews = new ArrayList<ImageView>();
        listaDeViews.add(viewAdicionar);
        listaDeViews.add(viewRemover);
        listaDeViews.add(viewAlterar);
        return listaDeViews;
    	
    }
}
