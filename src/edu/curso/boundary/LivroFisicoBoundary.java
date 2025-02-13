package edu.curso.boundary;

import java.time.LocalDate;

import edu.curso.controller.LivroFisicoController;
import edu.curso.exceptions.LivroException;
import edu.curso.model.LivroFisico;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

public class LivroFisicoBoundary implements ITela {

    private Label lbCodigo = new Label("0");
    private TextField txtNome = new TextField();
    private TextField txtAutor = new TextField();
    private DatePicker txtDataLancamento = new DatePicker();
    private TextField txtEditora = new TextField();

    private LivroFisicoController control = null;

    TableView<LivroFisico> tableView = new TableView<>();

    @Override
    public Pane render() {
        try {
            control = new LivroFisicoController();
            
        } catch (LivroException e) {
            new Alert(AlertType.ERROR, "Algo deu errado ao iniciar o sistema", ButtonType.OK).showAndWait();
        }

        BorderPane panePrincipal = new BorderPane();
        GridPane paneForm = new GridPane();

        Button btnGravar = new Button("Gravar");
        Button btnBuscar = new Button("Buscar");
        Button btnLimpar = new Button("Limpar");

        btnGravar.setOnAction(e -> {
            try {
                control.gravar();
            } catch (LivroException err) {
                new Alert(AlertType.ERROR, "Nao foi possivel registrar o Livro no sistema", ButtonType.OK)
                        .showAndWait();
            }
        });
        btnBuscar.setOnAction(e -> {
            try {
                control.buscar();
            } catch (LivroException err) {
                new Alert(AlertType.ERROR, "Nao foi possível encontrar o Livro no sistema", ButtonType.OK)
                        .showAndWait();
            }
        });
        btnLimpar.setOnAction(e -> control.limpar());

        paneForm.add(new Label("Codigo: "), 0, 0);
        paneForm.add(lbCodigo, 1, 0);
        paneForm.add(new Label("Nome: "), 0, 1);
        paneForm.add(txtNome, 1, 1);
        paneForm.add(new Label("Autor: "), 0, 2);
        paneForm.add(txtAutor, 1, 2);
        paneForm.add(new Label("Data de Lancamento: "), 0, 3);
        paneForm.add(txtDataLancamento, 1, 3);
        paneForm.add(new Label("Editora: "), 0, 4);
        paneForm.add(txtEditora, 1, 4);
        
        paneForm.add(btnGravar, 0, 5);
        paneForm.add(btnBuscar, 1, 5);
        paneForm.add(btnLimpar, 3, 5);

        
        gerarLigacoes();
        gerarColunas();

        panePrincipal.setTop(paneForm);
        panePrincipal.setCenter(tableView);

        return panePrincipal;
    }

    
    @SuppressWarnings("unchecked")
    private void gerarColunas() {
        TableColumn<LivroFisico, Long> col1 = new TableColumn<>("Codigo");
        col1.setCellValueFactory( new PropertyValueFactory<LivroFisico, Long>("codigo"));

        TableColumn<LivroFisico, String> col2 = new TableColumn<>("Nome");
        col2.setCellValueFactory( new PropertyValueFactory<LivroFisico, String>("nome"));

        TableColumn<LivroFisico, String> col3 = new TableColumn<>("Autor");
        col3.setCellValueFactory( new PropertyValueFactory<LivroFisico, String>("autor"));

        TableColumn<LivroFisico, LocalDate> col4 = new TableColumn<>("Data de Lancamento");
        col4.setCellValueFactory( new PropertyValueFactory<LivroFisico, LocalDate>("dataLancamento"));

        TableColumn<LivroFisico, String> col5 = new TableColumn<>("Editora");
        col5.setCellValueFactory( new PropertyValueFactory<LivroFisico, String>("editora"));

        tableView.getSelectionModel().selectedItemProperty().addListener( (obs, antigo, novo) -> {
            if (novo != null) { 
                control.paraTela(novo);
            }
        });

        Callback<TableColumn<LivroFisico, Void>, TableCell<LivroFisico, Void>> cb = new Callback<>() {
            @Override
            public TableCell<LivroFisico, Void> call(TableColumn<LivroFisico, Void> param){
                TableCell<LivroFisico, Void> celula = new TableCell<>() {
                    final Button btnExcluir = new Button("Excluir");
                    {
                        btnExcluir.setOnAction(e -> {
                            LivroFisico l = tableView.getItems().get(getIndex());
                            try{
                                control.deletar(l);
                            } catch(LivroException err){
                                new Alert(AlertType.ERROR, "Erro ao excluir do sistema!", ButtonType.OK).showAndWait();
                            }
                        });   
                    }

                    @Override
                    public void updateItem( Void item, boolean empty){
                        if (!empty) {
                            setGraphic(btnExcluir);
                        } else {
                            setGraphic(null);
                        }
                    }
                };

                return celula;
            }
        };

        TableColumn<LivroFisico, Void> col6 = new TableColumn<>("Ações");
        col6.setCellFactory(cb);
        
        tableView.getColumns().clear();     //limpa as colunas para evitar que se multipliquem a cada interacao com o menu
        tableView.getColumns().addAll(col1, col2, col3, col4, col5, col6);
        tableView.setItems(control.getLivrosFisicos());
    }



    private void gerarLigacoes() {

        control.getId().addListener((obs, antigo, novo) ->
            lbCodigo.setText(String.valueOf(novo) )
        );

        Bindings.bindBidirectional(control.getNome(), txtNome.textProperty());
        Bindings.bindBidirectional(control.getAutor(), txtAutor.textProperty());
        Bindings.bindBidirectional(control.getDataLancamento(), txtDataLancamento.valueProperty());
        Bindings.bindBidirectional( txtEditora.textProperty(), control.getEditora());
        
    }

}
