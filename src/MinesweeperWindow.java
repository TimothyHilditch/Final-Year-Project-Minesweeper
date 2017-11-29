/*
 * This class runs the main window for the game
 */
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.stage.*;

import java.util.ArrayList;
import java.util.Random;

import javafx.application.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;


public class MinesweeperWindow extends Application{
	public static void main(String[] args) {
		//System.out.println("start");
		launch(args);
	}
	
	private FlowPane root;
	private Scene scene;
	private Stage stage;
	private GridPane gridPane;
	private Mine[][] mines;
	private Random random;
	private int rows;
	private int columns;
	
	public void start(Stage stage){
		//System.out.println("inside");
		root = new FlowPane();
		scene = new Scene(root, 800, 600);
		//System.out.println("escape");
		stage = new Stage();
		stage.setTitle("Minesweeper Optimised");
		stage.setScene(scene);
		gridPane = new GridPane();  //stores all the mines in here
		root.getChildren().add(gridPane);
		
		play(10, 10, 10);
		
		stage.show();
	}
	/**
	 * Starts the game by generating the field using rows and columns
	 * @param noOfRows
	 * @param noOfColumns
	 * @param noOfMines
	 */
	public void play(int noOfRows, int noOfColumns, int noOfMines){
		mines = new Mine[noOfRows][noOfColumns];
		rows = noOfRows;
		columns = noOfColumns;
		int rowCount =0, columnCount = 0, totalCount=0;
		while(rowCount<noOfRows && columnCount<noOfColumns){
			Button b = new Button("  ");
			Mine mine = new Mine(b);
			//System.out.println(rowCount + "==Row    column=="+ columnCount);
			mines[rowCount][columnCount] = mine;
			gridPane.setConstraints(b, columnCount, rowCount);
			gridPane.getChildren().add(b);
			rowCount++;
			if(rowCount==noOfRows && columnCount!=noOfColumns){
				rowCount=0;
				columnCount++;
			}
			
			
			b.setOnAction(new EventHandler<ActionEvent>(){
				@Override public void handle(ActionEvent e){
					show(mine);
				}
			});
			totalCount++;
		}
		addMines(noOfMines);
		addNumbers();
	}
	
	/**
	 * Gets called when generating the field. This method will assign mines to the field.
	 * @param totalOfSquares
	 * @param totalMines
	 */
	private void addMines(int totalMines){
		random = new Random();
		boolean mineNumbers[][] = new boolean [rows][columns];
		int tCount = 0;
		while(tCount<totalMines){
			int numberRow = random.nextInt(rows); //takes the total number squares and takes one.
			int numberColumn = random.nextInt(columns);
			if(mineNumbers[numberRow][numberColumn]){
			}
			else{
				mineNumbers[numberRow][numberColumn] = true;
				tCount++;
			}
		}
		Mine m;
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < columns; j++){
				m = (Mine)mines[i][j];
				if(mineNumbers[i][j]){
					m.setAmIAMine(true);
					//System.out.println(i+"===rows      columns==="+j);
				}
				else{
					m.setAmIAMine(false);
				}
			}
		}
	}
	
	private void addNumbers(){
		Mine m;
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < columns; j++){
				//System.out.println(i+"==rows     columns=="+j+((Mine)mines[i][j]).getAmIAMine());
				m = (Mine)mines[i][j];
				if(!m.getAmIAMine()){
					int total = 0;
					if(i!=rows-1){
						if(((Mine)mines[i+1][j]).getAmIAMine()){
							total++;
						}
						if(j!=0){
							if(((Mine)mines[i+1][j-1]).getAmIAMine()){
								total++;
							}
						}
						if(j!=columns-1){
							if(((Mine)mines[i+1][j+1]).getAmIAMine()){
								total++;
							}
						}
					}
					if(j!=0){
						if(((Mine)mines[i][j-1]).getAmIAMine()){
							total++;
						}
					}
					if(j!=columns-1){
						if(((Mine)mines[i][j+1]).getAmIAMine()){
							total++;
						}
					}
					if(i!=0){
						if(((Mine)mines[i-1][j]).getAmIAMine()){
							total++;
						}
						if(j!=0){
							if(((Mine)mines[i-1][j-1]).getAmIAMine()){
								total++;
							}
						}
						if(j!=columns-1){
							if(((Mine)mines[i-1][j+1]).getAmIAMine()){
								total++;
							}
						}
					}
					//System.out.println(""+total);
					m.setTotalMinesAround(total);
				}
			}
		}
	}
	private void show(Mine mine){
		Button b= mine.getButton();
		String message;
		if(mine.getAmIAMine()){
			message = "X";
		}
		else{
			message = ""+mine.getTotalMinesAround();
		}
		b.setText(message);
	}
}
