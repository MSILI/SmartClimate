package app.com.main;

import java.time.LocalDate;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Classe Main : point de lancement de l'application.
 *
 */
public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {

		Parent root = FXMLLoader.load(getClass().getResource("/app/com/views/Main.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		primaryStage.setTitle("Smart Climate © " + LocalDate.now().getYear());
		primaryStage.setX(primaryScreenBounds.getMinX());
		primaryStage.setY(primaryScreenBounds.getMinY());
		primaryStage.setWidth(primaryScreenBounds.getWidth());
		primaryStage.setHeight(primaryScreenBounds.getHeight());
		primaryStage.getIcons().add(new Image("/images/icone.png"));
		primaryStage.show();
	}

	/**
	 * La méthode main de la classe Main.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
