package swp.com.zeitenrechner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class ZeitenRechnerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Font.loadFont(getClass().getResourceAsStream("fonts/Montserrat-Regular.ttf"), 12);
        Font.loadFont(getClass().getResourceAsStream("fonts/Montserrat-Bold.ttf"), 12);
        Font.loadFont(getClass().getResourceAsStream("fonts/Montserrat-Light.ttf"), 12);
        FXMLLoader fxmlLoader = new FXMLLoader(ZeitenRechnerApplication.class.getResource("zeiten-rechner-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 650);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        stage.setTitle("ZeitenRechner");
        stage.setScene(scene);
        stage.setMinWidth(1200);
        stage.setMinHeight(550);

        // Optional: Add application icon
        try {
            stage.getIcons().add(new Image(ZeitenRechnerApplication.class.getResourceAsStream("clock.png")));
        } catch (Exception e) {
            // If icon loading fails, just continue without it
            System.out.println("Icon could not be loaded: " + e.getMessage());
        }

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}