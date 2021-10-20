module com.dbms.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires serverModule;
    requires spring.context;


    opens com.dbms.client.model to javafx.fxml;
    opens com.dbms.client to javafx.fxml;
    exports com.dbms.client;
    exports com.dbms.client.model;
}