package uz.khurozov.route;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/app.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Route");
        stage.setScene(scene);
        stage.getIcons().add(new Image(Objects.requireNonNull(App.class.getResourceAsStream("/images/gps.png"))));
        stage.setMaximized(true);
        Controller controller = fxmlLoader.getController();
        controller.bindKeys();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}