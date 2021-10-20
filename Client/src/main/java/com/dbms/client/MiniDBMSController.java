package com.dbms.client;

import com.dbms.client.model.NewColumn;
import com.dbms.server.model.NewColumnDTO;
import com.dbms.server.server.IService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


public class MiniDBMSController {
    @FXML TreeView<String> treeviewComponent;
    ContextMenu contextMenu;
    @FXML VBox dinamicBox;
    MenuItem createDbMenuItem;
    MenuItem createTableMenuItem;
    MenuItem createIndexMenuItem;
    MenuItem dropTableMenuItem;
    MenuItem dropDatabaseMenuItem;
    @FXML TableView<NewColumn> table;
    @FXML TableColumn<NewColumn, TextField> columnName;
    @FXML TableColumn<NewColumn, ComboBox<String>> dataType;
    @FXML TableColumn<NewColumn, CheckBox> isPrimaryKey;
    @FXML TableColumn<NewColumn, CheckBox> isNull;
    @FXML TableColumn<NewColumn, CheckBox> isUniqueKey;
    @FXML Button addColumn;
    @FXML Label label;
    @FXML TextField databaseName;
    TextField tableName;
    Button createDbBtn;
    @FXML Button createTableBtn;
    @FXML HBox dinamicHBox;
    Alert alert;

    Button createIndexBtn;

    IService server;

    ComboBox<String> chooseDatabase;
    ComboBox<String> chooseTable;
    Button dropDatabaseBtn;
    Button dropTableBtn;
    Label dropDbLabel;
    Label dropTableLabel;
    ListView<String> indexColumnsList;

    private ObservableList<NewColumn> newTableColumnsList= FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        // creating components that are rendered on the right side Vertical box
        createRightSideVerticalBox();

        // context menu components
        contextMenu = new ContextMenu();
        createDbMenuItem=new MenuItem("Create database");
        createTableMenuItem=new MenuItem("Create table");
        createIndexMenuItem=new MenuItem("Create index");
        dropDatabaseMenuItem=new MenuItem("Drop database");
        dropTableMenuItem=new MenuItem("Drop table");
        contextMenu.getItems().add(createDbMenuItem);
        contextMenu.getItems().add(createTableMenuItem);
        contextMenu.getItems().add(createIndexMenuItem);
        contextMenu.getItems().add(dropDatabaseMenuItem);
        contextMenu.getItems().add(dropTableMenuItem);


        //setting the database side tree view
        TreeItem<String> rootItem = new TreeItem<>("Databases");
        treeviewComponent.setContextMenu(contextMenu);
        treeviewComponent.setRoot(rootItem);

        // button to add a new column when creating table
        addColumn.setOnAction(event -> newTableColumnsList.add(new NewColumn()));
        addColumn.setVisible(false);
        addColumn.setManaged(false);
    }

    public void createIndex(){
        List<String> indexColumns= new ArrayList<>(indexColumnsList.getSelectionModel().getSelectedItems());

        if(!indexColumns.isEmpty()) {
            boolean success=server.createIndex(chooseDatabase.getSelectionModel().getSelectedItem(), chooseTable.getSelectionModel().getSelectedItem(), indexColumns);
            if(success){
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Create index");
                alert.setHeaderText("Success!");
                alert.setContentText("Index created successfully!");
                alert.showAndWait();
                setTreeView();
            }
        } else{
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Create index");
            alert.setHeaderText("Attention!");
            alert.setContentText("Choose columns for index!");
            alert.showAndWait();
        }
    }

    public void createTable(){
        AtomicBoolean validColumns= new AtomicBoolean(true);
        List<NewColumnDTO> newColumns = newTableColumnsList
                                        .stream()
                                        .map(column->
                                        new NewColumnDTO(column.getColumnName().getText(),
                                                column.getDataTypes().getSelectionModel().getSelectedItem(),
                                                column.getIsPrimaryKey().isSelected(),
                                                column.getIsUniqueKey().isSelected(),
                                                column.getIsNull().isSelected()))
                                        .collect(Collectors.toList());

        newColumns.forEach(column->{
            if(column.getColumnName()==null || column.getDataType()==null
                    || chooseDatabase.getSelectionModel().getSelectedItem()==null
                    || chooseDatabase.getSelectionModel().getSelectedItem().equals("Select database")
                    || tableName.getText() == null){
                validColumns.set(false);
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Create table");
                alert.setHeaderText("Attention!");
                alert.setContentText("Insert valid names and choose datatypes!");
                alert.showAndWait();
            }
        });
        if(validColumns.get()) {
            boolean success=server.createTable(newColumns, chooseDatabase.getSelectionModel().getSelectedItem(), tableName.getText());
            if(success){
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Create table");
                alert.setHeaderText("Success!");
                alert.setContentText("Table created successfully!");
                alert.showAndWait();
                setTreeView();
            }else{
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Create table");
                alert.setHeaderText("Attention!");
                alert.setContentText("Table already exists!");
                alert.showAndWait();
            }
        }
    }

    public void dropTable(){
            boolean success=server.dropTable(chooseDatabase.getSelectionModel().getSelectedItem(), chooseTable.getSelectionModel().getSelectedItem());
            if(success){
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Drop table");
                alert.setHeaderText("Success!");
                alert.setContentText("Table dropped successfully!");
                alert.showAndWait();

                setChooseTable();
                chooseTable.getSelectionModel().selectFirst();
                setTreeView();
            }
    }

    private void cleanup(){
        newTableColumnsList= FXCollections.observableArrayList();

        if (table != null) {
            table.getItems().clear();
            table.setVisible(false);
            table.setManaged(false);
        }

        if(addColumn!=null){
            addColumn.setVisible(false);
            addColumn.setManaged(false);
        }

        if(createDbBtn != null){
            createDbBtn.setVisible(false);
            createDbBtn.setManaged(false);
        }
        if(label!=null){
            label.setVisible(false);
            label.setManaged(false);
        }

        if(databaseName!=null){
            databaseName.setVisible(false);
            databaseName.setManaged(false);
        }

        if(createTableBtn!=null){
            createTableBtn.setVisible(false);
            createTableBtn.setManaged(false);
        }

        if(dinamicHBox!=null){
            dinamicHBox.setVisible(false);
            dinamicHBox.setManaged(false);
        }

        if(chooseDatabase!=null) {
            chooseDatabase.setVisible(false);
            chooseDatabase.setManaged(false);
        }

        if(dropDatabaseBtn!=null) {
            dropDatabaseBtn.setVisible(false);
            dropDatabaseBtn.setManaged(false);
        }

        if(dropDbLabel!=null){
            dropDbLabel.setVisible(false);
            dropDbLabel.setManaged(false);
        }

        if(chooseTable!=null) {
            chooseTable.setVisible(false);
            chooseTable.setManaged(false);
        }

        if(dropTableBtn!=null) {
            dropTableBtn.setVisible(false);
            dropTableBtn.setManaged(false);
        }

        if(dropTableLabel!=null){
            dropTableLabel.setVisible(false);
            dropTableLabel.setManaged(false);
        }

        if(tableName!=null){
            tableName.setVisible(false);
            tableName.setManaged(false);
        }

        if(createIndexBtn!=null){
            createIndexBtn.setVisible(false);
            createIndexBtn.setManaged(false);
        }

        if(indexColumnsList!=null){
            indexColumnsList.setVisible(false);
            indexColumnsList.setManaged(false);
        }
    }

    public void setServer(IService server) {
        this.server = server;
    }

    public void afterInitialize(){
        //setting the side tree view
        setTreeView();

        // set actions for context menu items
        setActionToOpenDropTable();
        setActionToOpenDropDatabase();
        setActionToOpenCreateTable();
        setActionToOpenCreateDatabase();
        setActionToOpenCreateIndex();
    }

    public void setTreeView(){
        List<String> databases= server.getDatabasesFromCatalog();
        treeviewComponent.getRoot().getChildren().clear();
        databases.forEach(db-> {
            List<String> tables = server.getAllTablesFromDb(db.toString());
            var dbItem=new TreeItem<>(db);
            var tablesItem=new TreeItem<>("Tables");
            dbItem.getChildren().add(tablesItem);
            for(var table:tables){
                tablesItem.getChildren().add(new TreeItem<>(table));
            }
            treeviewComponent.getRoot().getChildren().add(dbItem);
        });
        treeviewComponent.getRoot().setExpanded(true);
    }

    public void setActionToOpenDropTable(){
        dropTableMenuItem.setOnAction(event->{
            cleanup();

            dropDbLabel.setVisible(true);
            dropDbLabel.setManaged(true);
            dropDbLabel.setStyle("-fx-padding: 10px;-fx-border-insets: 10px;-fx-background-insets: 10px;-fx-font-size: 14");

            chooseDatabase.setVisible(true);
            chooseDatabase.setManaged(true);
            chooseDatabase.setStyle("-fx-padding: 10px;-fx-border-insets: 10px;-fx-background-insets: 10px;-fx-font-size: 14");
            setChooseDatabase();
            chooseDatabase.getSelectionModel().selectFirst();
            chooseDatabase.valueProperty().addListener((ov, t, t1) -> {
                setChooseTable();
            });

            dropTableLabel.setVisible(true);
            dropTableLabel.setManaged(true);
            dropTableLabel.setStyle("-fx-padding: 10px;-fx-border-insets: 10px;-fx-background-insets: 10px;-fx-font-size: 14");

            chooseTable.setVisible(true);
            chooseTable.setManaged(true);
            chooseTable.setStyle("-fx-padding: 10px;-fx-border-insets: 10px;-fx-background-insets: 10px;-fx-font-size: 14");
            setChooseTable();

            dropTableBtn.setVisible(true);
            dropTableBtn.setManaged(true);
            dropTableBtn.setStyle("-fx-padding: 10px;-fx-border-insets: 10px;-fx-background-insets: 10px;-fx-font-size: 14");
            dropTableBtn.setText("Drop table");
            dropTableBtn.setOnMouseClicked(e->dropTable());
        });
    }

    public void setActionToOpenDropDatabase(){
        dropDatabaseMenuItem.setOnAction(event->{
            cleanup();

            dropDbLabel.setVisible(true);
            dropDbLabel.setManaged(true);
            dropDbLabel.setStyle("-fx-padding: 10px;-fx-border-insets: 10px;-fx-background-insets: 10px;-fx-font-size: 14");

            chooseDatabase.setVisible(true);
            chooseDatabase.setManaged(true);
            chooseDatabase.setStyle("-fx-padding: 10px;-fx-border-insets: 10px;-fx-background-insets: 10px;-fx-font-size: 14");

            dropDatabaseBtn.setVisible(true);
            dropDatabaseBtn.setManaged(true);
            dropDatabaseBtn.setStyle("-fx-padding: 10px;-fx-border-insets: 10px;-fx-background-insets: 10px;-fx-font-size: 14");
            dropDatabaseBtn.setText("Drop database");
            // TO DO - database list from xml
            setChooseDatabase();

            dropDatabaseBtn.setOnMouseClicked(e->{
                var dbName=chooseDatabase.getSelectionModel().getSelectedItem();
                if(Objects.equals(dbName, "")) {
                    alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Drop database");
                    alert.setHeaderText("Attention!");
                    alert.setContentText("Choose a database to drop!");
                    alert.showAndWait();
                } else if(server.dropDatabase(dbName)){
                    alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Drop database");
                    alert.setHeaderText("Success");
                    alert.setContentText("Database dropped successfully!");

                    alert.showAndWait();
                }else{
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Drop database");
                    alert.setHeaderText("Fail");
                    alert.setContentText("Database does not exist!");

                    alert.showAndWait();
                }
                setTreeView();
                setChooseDatabase();
            });
        });
    }

    public void setActionToOpenCreateTable(){
        createTableMenuItem.setOnAction(event->{
            cleanup();

            columnName.setCellValueFactory(new PropertyValueFactory<>("columnName"));
            dataType.setCellValueFactory(new PropertyValueFactory<>("dataTypes"));
            isPrimaryKey.setCellValueFactory(new PropertyValueFactory<>("isPrimaryKey"));
            isUniqueKey.setCellValueFactory(new PropertyValueFactory<>("isUniqueKey"));
            isNull.setCellValueFactory(new PropertyValueFactory<>("isNull"));

            newTableColumnsList.add(new NewColumn());
            table.setItems(newTableColumnsList);

            chooseDatabase.setVisible(true);
            chooseDatabase.setManaged(true);
            chooseDatabase.setStyle("-fx-padding: 10px;-fx-border-insets: 10px;-fx-background-insets: 10px;-fx-font-size: 14");
            chooseDatabase.setValue("Select database");
            setChooseDatabase();

            createTableBtn.setStyle("-fx-padding: 10px;-fx-border-insets: 10px;-fx-background-insets: 10px;-fx-font-size: 14");
            createTableBtn.setVisible(true);
            createTableBtn.setManaged(true);
            createTableBtn.setText("Create table");

            dinamicHBox.setVisible(true);
            dinamicHBox.setManaged(true);
            addColumn.setVisible(true);
            addColumn.setManaged(true);

            String style = "-fx-padding: 10px;-fx-border-insets: 10px;-fx-background-insets: 10px;-fx-font-size: 12";
            table.setVisible(true);
            table.setManaged(true);
            table.setStyle(style);
            addColumn.setVisible(true);

            label.setText("Table name");
            label.setStyle(style);
            label.setVisible(true);
            label.setManaged(true);

            tableName.setVisible(true);
            tableName.setManaged(true);
            tableName.setStyle(style);
        });

        createTableBtn.setOnMouseClicked(e->createTable());
    }

    public void setActionToOpenCreateDatabase(){
        createDbMenuItem.setOnAction(event->{
            cleanup();

            String style = "-fx-padding: 10px;-fx-border-insets: 10px;-fx-background-insets: 10px;-fx-font-size: 16";

            label=new Label("Database name");
            label.setStyle(style);

            databaseName = new TextField();
            databaseName.setMaxWidth(200);
            databaseName.setStyle(style);

            createDbBtn = new Button("Create database");
            createDbBtn.setStyle(style);
            createDbBtn.setVisible(true);
            createDbBtn.setManaged(true);
            createDbBtn.setOnMouseClicked(e->{
                var dbName=databaseName.getText();
                if(Objects.equals(dbName, "")) {
                    alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Create database");
                    alert.setHeaderText("Attention!");
                    alert.setContentText("Introduce a name for the new database!");
                    alert.showAndWait();
                } else if(server.createDatabase(dbName)){
                    alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Create database");
                    alert.setHeaderText("Success");
                    alert.setContentText("Database created successfully!");

                    alert.showAndWait();
                }else{
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Create database");
                    alert.setHeaderText("Fail");
                    alert.setContentText("Database already exists!");

                    alert.showAndWait();
                }
                setTreeView();
            });


            dinamicBox.getChildren().addAll(label, databaseName, createDbBtn);
        });
    }

    public void setActionToOpenCreateIndex(){
        createIndexMenuItem.setOnAction(e->{
            cleanup();

            dropDbLabel.setVisible(true);
            dropDbLabel.setManaged(true);
            dropDbLabel.setStyle("-fx-padding: 10px;-fx-border-insets: 10px;-fx-background-insets: 10px;-fx-font-size: 14");
            dropDbLabel.setText("Choose a database");

            chooseDatabase.setVisible(true);
            chooseDatabase.setManaged(true);
            chooseDatabase.setStyle("-fx-padding: 10px;-fx-border-insets: 10px;-fx-background-insets: 10px;-fx-font-size: 14");
            setChooseDatabase();
            chooseDatabase.getSelectionModel().selectFirst();
            chooseDatabase.valueProperty().addListener((ov, t, t1) -> {
                setChooseTable();
            });

            dropTableLabel.setVisible(true);
            dropTableLabel.setManaged(true);
            dropTableLabel.setStyle("-fx-padding: 10px;-fx-border-insets: 10px;-fx-background-insets: 10px;-fx-font-size: 14");
            dropTableLabel.setText("Choose table");

            chooseTable.setVisible(true);
            chooseTable.setManaged(true);
            chooseTable.setStyle("-fx-padding: 10px;-fx-border-insets: 10px;-fx-background-insets: 10px;-fx-font-size: 14");
            setChooseTable();
            chooseTable.getSelectionModel().selectFirst();
            chooseTable.valueProperty().addListener((ov, t, t1) -> {
                setIndexColumnsList();
            });

            createIndexBtn.setVisible(true);
            createIndexBtn.setManaged(true);
            createIndexBtn.setStyle("-fx-padding: 10px;-fx-border-insets: 10px;-fx-background-insets: 10px;-fx-font-size: 14");
            createIndexBtn.setOnMouseClicked(event->createIndex());

            indexColumnsList.setVisible(true);
            indexColumnsList.setManaged(true);
            setIndexColumnsList();
        });

        createIndexBtn.setOnAction(event->createIndex());
    }

    public void createRightSideVerticalBox(){
        chooseDatabase=new ComboBox<>();
        chooseDatabase.setVisible(false);
        chooseDatabase.setManaged(false);
        chooseTable=new ComboBox<>();
        chooseTable.setVisible(false);
        chooseTable.setManaged(false);
        dropDatabaseBtn=new Button();
        dropDatabaseBtn.setVisible(false);
        dropDatabaseBtn.setManaged(false);
        dropTableBtn=new Button();
        dropTableBtn.setVisible(false);
        dropTableBtn.setManaged(false);
        dropDbLabel=new Label("Choose a database to drop:");
        dropDbLabel.setVisible(false);
        dropDbLabel.setManaged(false);
        dropTableLabel=new Label("Choose a table to drop:");
        dropTableLabel.setVisible(false);
        dropTableLabel.setManaged(false);
        table.setVisible(false);
        table.setManaged(false);
        createTableBtn.setVisible(false);
        createTableBtn.setManaged(false);
        tableName=new TextField();
        tableName.setVisible(false);
        tableName.setManaged(false);
        label=new Label();
        label.setVisible(false);
        label.setManaged(false);
        createIndexBtn=new Button("Create index");
        createIndexBtn.setVisible(false);
        createIndexBtn.setManaged(false);

        indexColumnsList=new ListView<>();
        indexColumnsList.setVisible(false);
        indexColumnsList.setManaged(false);
        indexColumnsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        dinamicBox.getChildren().addAll(label, tableName, dropDbLabel, chooseDatabase, dropTableLabel, chooseTable, dropDatabaseBtn, dropTableBtn, indexColumnsList, createIndexBtn);

    }

    public void setChooseDatabase(){
        chooseDatabase.setItems(FXCollections.observableArrayList(server.getDatabasesFromCatalog()));
    }

    public void setChooseTable(){
        chooseTable.setItems(FXCollections.observableArrayList(server.getAllTablesFromDb(chooseDatabase.getSelectionModel().getSelectedItem())));
        chooseTable.getSelectionModel().selectFirst();
    }

    public void setIndexColumnsList(){
        indexColumnsList.setItems(FXCollections.observableArrayList(server.getAllColumnsFromTable(chooseDatabase.getSelectionModel().getSelectedItem(), chooseTable.getSelectionModel().getSelectedItem())));
    }
}