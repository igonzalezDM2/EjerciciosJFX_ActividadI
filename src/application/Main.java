package application;
	
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			
			ResourceBundle bundle = ResourceBundle.getBundle("properties.messages", new Locale("eu", "ES"));
			
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("/fxml/ActividadB.fxml"), bundle);
			Scene scene = new Scene(root);
			primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/hito.jpg")));
			primaryStage.setTitle("PERSONAS");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
