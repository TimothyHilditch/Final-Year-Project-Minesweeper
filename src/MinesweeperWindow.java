/*
 * This class runs the main window for the game
 */
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.application.*;


public class MinesweeperWindow extends Application{
	public static void main(String[] args) {
		//System.out.println("start");
		launch(args);
	}
	
	FlowPane root;
	Scene scene;
	Stage stage;
	
	
	public void start(Stage stage){
		System.out.println("inside");
		root = new FlowPane();
		scene = new Scene(root, 800, 600);
		System.out.println("escape");
		stage = new Stage();
		stage.setTitle("Minesweeper Optimised");
		stage.setScene(scene);
		stage.show();
	}
}
