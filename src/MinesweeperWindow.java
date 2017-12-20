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
import java.util.Stack;

import javafx.application.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;


public class MinesweeperWindow extends Application{
	public static void main(String[] args) {
		launch(args);
	}
	
	private Pane root;
	private Scene scene;
	private Stage stage;
	private GridPane gridPane;
	private Mine[][] mines;
	private Random random;
	private int rows;	//stores number of rows
	private int columns;  //stores number of columns
	private int noOfMinesInt;  //stores total number of mines on the field
	private Stack<Mine> clearSpaces;//Stores stack of mines that should be cleared. Used when there is gaps.
	private Label winLoss;			//label to display if you have won or lost
	private boolean firstClick;  //stores whether you have clicked or not
	
	public void start(Stage stage){
		root = new Pane();
		scene = new Scene(root, 800, 600);
		stage = new Stage();
		stage.setTitle("Minesweeper Optimised");
		stage.setScene(scene);
		
		menu();
		
		clearSpaces = new Stack<Mine>();
		
		stage.show();
	}
	
	public void menu(){
		Label l= new Label("Please pick difficulty");
		l.setTranslateX(350);
		l.setTranslateY(20);
		Button bEasy = new Button("Easy, 10 by 10, 10 mines");
		bEasy.setTranslateX(330);
		bEasy.setTranslateY(50);
		Button bNormal = new Button("Normal, 20 by 20, 40 mines");
		bNormal.setTranslateX(320);
		bNormal.setTranslateY(100);
		Button bHard = new Button("Hard, 22 by 30, 90 mines");
		bHard.setTranslateX(330);
		bHard.setTranslateY(150);
		Label l2 = new Label("Custom Field");
		l2.setTranslateX(370);
		l2.setTranslateY(200);
		TextField tb1 = new TextField("Rows");
		tb1.setTranslateX(330);
		tb1.setTranslateY(240);
		TextField tb2 = new TextField("Columns");
		tb2.setTranslateX(330);
		tb2.setTranslateY(270);
		TextField tb3 = new TextField("Mines");
		tb3.setTranslateX(330);
		tb3.setTranslateY(300);
		Button bCustom = new Button("Generate");
		bCustom.setTranslateX(370);
		bCustom.setTranslateY(350);
		
		root.getChildren().addAll(l, bEasy, tb1, bNormal, bHard, l2, tb2, tb3, bCustom);
		
		bEasy.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e){
				root.getChildren().removeAll(l, bEasy, tb1, bNormal, bHard, l2, tb2, tb3, bCustom);
				play(10, 10, 10);
			}
		});
		bNormal.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e){
				root.getChildren().removeAll(l, bEasy, tb1, bNormal, bHard, l2, tb2, tb3, bCustom);
				play(20, 20, 40);
			}
		});
		bHard.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e){
				root.getChildren().removeAll(l, bEasy, tb1, bNormal, bHard, l2, tb2, tb3, bCustom);
				play(22, 30, 90);
			}
		});
		bCustom.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e){
				root.getChildren().removeAll(l, bEasy, tb1, bNormal, bHard, l2, tb2, tb3, bCustom);
				play(Integer.parseInt(tb1.getText()), Integer.parseInt(tb2.getText()), Integer.parseInt(tb3.getText()));
			}
		});
	}
	
	/**
	 * Starts the game by generating the field using rows and columns
	 * @param noOfRows
	 * @param noOfColumns
	 * @param noOfMines
	 */
	public void play(int noOfRows, int noOfColumns, int noOfMines){
		Button menu = new Button("Menu");
		winLoss = new Label("");
		winLoss.setTranslateX(350);
		winLoss.setScaleX(2);
		winLoss.setScaleY(2);
		gridPane = new GridPane();  //stores all the mines in here
		gridPane.setTranslateY(25);
		root.getChildren().addAll(menu, gridPane, winLoss);
		menu.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e){
					root.getChildren().removeAll(menu, gridPane, winLoss); 
					menu();  //sends you back to the menu
				}
		});
		mines = new Mine[noOfRows][noOfColumns];
		rows = noOfRows;
		columns = noOfColumns;
		noOfMinesInt = noOfMines;
		int rowCount =0, columnCount = 0, totalCount=0;
		firstClick = true;
		while(rowCount<noOfRows && columnCount<noOfColumns){
			Button b = new Button("  ");
			Mine mine = new Mine(b, rowCount, columnCount);
			//System.out.println(rowCount + "==Row    column=="+ columnCount);
			mines[rowCount][columnCount] = mine;
			gridPane.setConstraints(b, columnCount, rowCount);
			gridPane.getChildren().add(b);
			rowCount++;
			if(rowCount==noOfRows && columnCount!=noOfColumns){
				rowCount=0;
				columnCount++;
			}
			b.setOnMouseClicked(new EventHandler<MouseEvent>(){
				@Override public void handle(MouseEvent e){
					if(firstClick){ //checks for if this the first time clicking a mine
						firstClick(mine);
						firstClick = false;
					}
					if(e.getButton()==MouseButton.PRIMARY){
						if(b.getText()!="F"){  //they can't click on a mine which is flagged
							show(mine);
						}
					}
					else if(e.getButton()==MouseButton.SECONDARY){
						if(b.getText()=="F"){  //flags a mine when right clicked on
							b.setText("  "); //undo a flag 
						}
						else{
							b.setText("F");
						}
					}
				}
			});
			totalCount++;
		}
	}
	
	private void firstClick(Mine mine){  //called when the first mine is clicked on
		addMines(noOfMinesInt, mine);
		addNumbers();
	}
	
	/**
	 * Gets called when generating the field. This method will assign mines to the field.
	 * rigs the first click using the mine passed in
	 * @param totalOfSquares
	 * @param totalMines
	 */
	private void addMines(int totalMines, Mine mine){  
		random = new Random();
		boolean firstClick[][] = new boolean [rows][columns];  //array of 8 mines around and the one you clicked
		firstClick[mine.getLocationX()][mine.getLocationY()] = true;
		if(mine.getLocationX()-1>0){
			firstClick[mine.getLocationX()-1][mine.getLocationY()] = true;
			if(mine.getLocationY()-1>0){
				firstClick[mine.getLocationX()-1][mine.getLocationY()-1] = true;
			}
			if(mine.getLocationY()+1<columns){
				firstClick[mine.getLocationX()-1][mine.getLocationY()+1] = true;
			}
		}
		if(mine.getLocationY()-1>0){
			firstClick[mine.getLocationX()][mine.getLocationY()-1] = true;
		}
		if(mine.getLocationY()+1<columns){
			firstClick[mine.getLocationX()][mine.getLocationY()+1] = true;
		}
		if(mine.getLocationX()+1<rows){
			firstClick[mine.getLocationX()+1][mine.getLocationY()] = true;
			if(mine.getLocationY()-1>0){
				firstClick[mine.getLocationX()+1][mine.getLocationY()-1] = true;
			}
			if(mine.getLocationY()+1<columns){
				firstClick[mine.getLocationX()+1][mine.getLocationY()+1] = true;
			}
		}
		boolean mineNumbers[][] = new boolean [rows][columns];
		int tCount = 0;
		while(tCount<totalMines){
			int numberRow = random.nextInt(rows); //takes the total number squares and takes one.
			int numberColumn = random.nextInt(columns);
			if(mineNumbers[numberRow][numberColumn] || firstClick[numberRow][numberColumn]){
			}  //checks if the mine has already assigned or one of ones you clicked on
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
					m.setAmIAMine(true);//sets the value in the mine class
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
	
	private void show(Mine mine){//this gets called by the onClickListener when a mine is clicked
		Button b= mine.getButton();
		b.setDisable(true);
		String message;
		if(mine.getAmIAMine()){
			message = "X";
			lose();
		}
		else{
			if(mine.getTotalMinesAround()==0){
				emptySquaresAround(mine);
				message = "  ";
			}
			else{
				message = ""+mine.getTotalMinesAround();
			}
			checkForWin();
		}
		b.setText(message);
		emptyStackOfMines();
	}
	
	private void lose(){
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < columns; j++){
				Mine mine = (Mine)mines[i][j];
				Button b= mine.getButton();
				b.setDisable(true);
				String message;
				if(mine.getAmIAMine()){
					message = "X";
				}
				else{
					if(mine.getTotalMinesAround()==0){
						message = "  ";
					}
					else{
						message = ""+mine.getTotalMinesAround();
					}
				}
				b.setText(message);
			}
		}
		winLoss.setText("You clicked on a mine :(");
	}
	
	private void emptySquaresAround(Mine mine){
		int i = mine.getLocationX();
		int j = mine.getLocationY();
		if(i!=rows-1){
			if(!((Mine)mines[i+1][j]).getButton().isDisabled())
				clearSpaces.push((Mine)mines[i+1][j]);
			if(j!=0){
				if(!((Mine)mines[i+1][j-1]).getButton().isDisabled())
					clearSpaces.push((Mine)mines[i+1][j-1]);
			}
			if(j!=columns-1){
				if(!((Mine)mines[i+1][j+1]).getButton().isDisabled())
					clearSpaces.push((Mine)mines[i+1][j+1]);
			}
		}
		if(j!=0){
			if(!((Mine)mines[i][j-1]).getButton().isDisabled())
				clearSpaces.push((Mine)mines[i][j-1]);
		}
		if(j!=columns-1){
			if(!((Mine)mines[i][j+1]).getButton().isDisabled())
				clearSpaces.push((Mine)mines[i][j+1]);
		}
		if(i!=0){
			if(!((Mine)mines[i-1][j]).getButton().isDisabled())
				clearSpaces.push((Mine)mines[i-1][j]);
			if(j!=0){
				if(!((Mine)mines[i-1][j-1]).getButton().isDisabled())
					clearSpaces.push((Mine)mines[i-1][j-1]);
			}
			if(j!=columns-1){
				if(!((Mine)mines[i-1][j+1]).getButton().isDisabled())
					clearSpaces.push((Mine)mines[i-1][j+1]);
			}
		}
	}
	
	private void emptyStackOfMines(){
		while(!clearSpaces.isEmpty()){
			Mine m = clearSpaces.pop();
			show(m);
		}
	}
	
	private void checkForWin(){
		Boolean b = true;
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < columns; j++){
				Mine mine = (Mine)mines[i][j];
				if(!mine.getAmIAMine()){
					if(!mine.getButton().isDisabled()){
						b = false;
					}
				}
			}
		}
		if(b){
			winLoss.setText("You have cleared the field :)");
		}
	}
}
