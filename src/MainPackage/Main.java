package MainPackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		Parent root = FXMLLoader.load(getClass().getResource("ViewSB.fxml"));

		primaryStage.initStyle(StageStyle.DECORATED);
		primaryStage.setMaximized(false);

		primaryStage.setTitle("Algorithm Visualizer");

		Scene scene = new Scene(root);

		primaryStage.setResizable(false);

		primaryStage.setScene(scene);
		primaryStage.show();

	}

}
