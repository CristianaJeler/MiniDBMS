package com.dbms.client;

import com.dbms.server.server.IService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.Objects;

public class MiniDBMSApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MiniDBMSApp.class.getResource("dbmsView.fxml"));
        Parent parent= fxmlLoader.load();
        MiniDBMSController mainController=fxmlLoader.getController();
        ApplicationContext factory=new ClassPathXmlApplicationContext("classpath:client.xml");
        IService server= (IService) factory.getBean("server");
        mainController.setServer(server);
        mainController.afterInitialize();
        Scene scene = new Scene(parent, 800, 600);
        stage.setTitle("MiniDBMS");
        stage.setScene(scene);
        stage.getIcons().add(new Image(Objects.requireNonNull(MiniDBMSApp.class.getResourceAsStream("icon.png"))));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}