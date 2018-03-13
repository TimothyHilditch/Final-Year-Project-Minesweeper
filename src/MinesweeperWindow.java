
// This class runs the main window for the game

import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.*;
import javafx.collections.FXCollections;
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
	private Label winLoss, clock, mineCount;	//label to display if you have won or lost
	private boolean firstClick;  //stores whether you have clicked or not
	private Image flag;  		//stores the Image of the flag
	private boolean rigged;		//Whether the player has selected rigged in the selection process
	private int timerCount, flagCount;
	private Timeline timer;
	
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
		Button bHard = new Button("Hard, 22 by 30, 130 mines");
		bHard.setTranslateX(330);
		bHard.setTranslateY(150);
		Label l2 = new Label("Custom Field");
		l2.setTranslateX(370);
		l2.setTranslateY(200);
		TextField tb1 = new TextField("Rows: max 22");
		tb1.setTranslateX(330);
		tb1.setTranslateY(240);
		TextField tb2 = new TextField("Columns: max 30");
		tb2.setTranslateX(330);
		tb2.setTranslateY(270);
		TextField tb3 = new TextField("Mines");
		tb3.setTranslateX(330);
		tb3.setTranslateY(300);
		Button bCustom = new Button("Generate");
		bCustom.setTranslateX(370);
		bCustom.setTranslateY(350);
		
		Button bRigged = new Button("solve");
		bRigged.setTranslateX(400);   //used for testing
		bRigged.setTranslateY(10);

		@SuppressWarnings("unchecked")
		ChoiceBox cbRigged = new ChoiceBox(FXCollections.observableArrayList(
			    "Normal", "Rigged")
			);
		cbRigged.setTranslateX(365);
		cbRigged.setTranslateY(390);
		cbRigged.setValue("Normal");
		
		root.getChildren().addAll(l, bEasy, tb1, bNormal, bHard, l2, tb2, tb3, bCustom, cbRigged);//, bRigged
		
		bEasy.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e){
				rigged = cbRigged.getValue()=="Rigged";
				root.getChildren().removeAll(l, bEasy, tb1, bNormal, bHard, l2, tb2, tb3, bCustom, cbRigged, bRigged);
				play(10, 10, 10);
			}
		});
		bNormal.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e){
				rigged = cbRigged.getValue()=="Rigged";
				root.getChildren().removeAll(l, bEasy, tb1, bNormal, bHard, l2, tb2, tb3, bCustom, cbRigged, bRigged);
				play(20, 20, 40);
			}
		});
		bHard.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e){
				rigged = cbRigged.getValue()=="Rigged";
				root.getChildren().removeAll(l, bEasy, tb1, bNormal, bHard, l2, tb2, tb3, bCustom, cbRigged, bRigged);
				play(22, 30, 130);
			}
		});
		bCustom.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e){
				if(tb1.getText().matches("[0-9]|[0-9][0-9]")&&tb2.getText().matches("[0-9]|[0-9][0-9]")&&tb3.getText().matches("[0-9]|[0-9][0-9]|[0-9][0-9][0-9]")){
					if(Integer.parseInt(tb1.getText())<=22 && Integer.parseInt(tb2.getText())<=30){
						if((Integer.parseInt(tb1.getText())*Integer.parseInt(tb2.getText()))>=(Integer.parseInt(tb3.getText())+9)){
							rigged = cbRigged.getValue()=="Rigged";
							root.getChildren().removeAll(l, bEasy, tb1, bNormal, bHard, l2, tb2, tb3, bCustom, cbRigged, bRigged);
							play(Integer.parseInt(tb1.getText()), Integer.parseInt(tb2.getText()), Integer.parseInt(tb3.getText()));
						}
					}
				}
			}
		});
		bRigged.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e){
				rigged = cbRigged.getValue()=="Rigged";
				root.getChildren().removeAll(l, bEasy, tb1, bNormal, bHard, l2, tb2, tb3, bCustom, cbRigged, bRigged);
				preSetMines();
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
		flag = new Image(getClass().getResourceAsStream("/resources/flag.png"));
		Node flagGraphic = new ImageView(flag);
		//flagGraphic.setScaleX(0.2);
		//flagGraphic.setScaleY(0.2);
		Button menu = new Button("Menu");
		winLoss = new Label("");
		winLoss.setTranslateX(350);
		winLoss.setScaleX(2);
		winLoss.setScaleY(2);
		clock = new Label();
		clock.setTranslateX(650);
		mineCount = new Label("Mines: "+noOfMines);
		flagCount=0;
		mineCount.setTranslateX(570);
		gridPane = new GridPane();  //stores all the mines in here
		gridPane.setTranslateY(25);
		root.getChildren().addAll(menu, gridPane, winLoss, clock, mineCount);
		menu.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e){
					root.getChildren().removeAll(menu, gridPane, winLoss, clock, mineCount); 
					timer.stop();
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
					if(e.getButton()==MouseButton.PRIMARY){
						if(firstClick && b.getText() != "F"){ //checks for if this the first time clicking a mine
							firstClick(mine);
							firstClick = false;
						}
						else if(b.getText() != "F"){  //they can't click on a mine which is flagged
							show(mine);
						}
					}
					else if(e.getButton()==MouseButton.SECONDARY){
						if(b.getText() == "F"){  //flags a mine when right clicked on
							//b.setGraphic(null); //undo a flag
							b.setText("  ");
							flagCount--;
						}
						else{
							//b.setGraphic(flagGraphic);
							b.setText("F");
							//b.setGraphicTextGap(0);
							flagCount++;
						}
						mineCount.setText("Mines: "+(noOfMinesInt-flagCount));
					}
				}
			});
			totalCount++;
		}
		timerCount=0;
		timer = new Timeline(new KeyFrame(Duration.millis(5), new EventHandler<ActionEvent>(){
			@Override
		    public void handle(ActionEvent event) {
		        clock.setText(timerCount/1000+"."+(timerCount-(timerCount/1000)*1000));
		        timerCount=timerCount+5;
		    }
		}));
	}
	
	private void firstClick(Mine mine){  //called when the first mine is clicked on
		addMines(noOfMinesInt, mine);
		addNumbers();
		show(mine);
		timer.setCycleCount(Timeline.INDEFINITE);
		timer.play();
		if(rigged){
			boolean solved = false;
			while(!solved){
				solved = solve();
			}
		}
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
		if(mine.getLocationX()-1>=0){
			firstClick[mine.getLocationX()-1][mine.getLocationY()] = true;
			if(mine.getLocationY()-1>=0){
				firstClick[mine.getLocationX()-1][mine.getLocationY()-1] = true;
			}
			if(mine.getLocationY()+1<columns){
				firstClick[mine.getLocationX()-1][mine.getLocationY()+1] = true;
			}
		}
		if(mine.getLocationY()-1>=0){
			firstClick[mine.getLocationX()][mine.getLocationY()-1] = true;
		}
		if(mine.getLocationY()+1<columns){
			firstClick[mine.getLocationX()][mine.getLocationY()+1] = true;
		}
		if(mine.getLocationX()+1<rows){
			firstClick[mine.getLocationX()+1][mine.getLocationY()] = true;
			if(mine.getLocationY()-1>=0){
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
				mineNumbers[numberRow][numberColumn] = true;  //sorts the mines into their own array
				tCount++;
			}
		}
		Mine m;
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < columns; j++){  //runs through the mine array and puts them into the main array
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
	
	private void addNumbers(){ //counts up the number of mines around the empty squares
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
	
	private void show(Mine mine){//this gets called by the onClickListener when a square is left clicked
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
	
	private void checkForWin(){ //checks if every empty square has been clicked
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
			timer.stop();
		}
	}
	
	private boolean solve(){  //tries to solve the field
		Boolean giveUp = true;//will keep looping until this boolean stays true throughout or it solves the field
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < columns; j++){
				Mine mine = (Mine)mines[i][j];
				//System.out.println(mine.getButton().isDisabled()+"    "+mine.getTotalMinesAround());
				if(mine.getButton().isDisabled()&&mine.getTotalMinesAround()!=0){
					Mine[] availableSpace = new Mine[8];
					int noOfAvailableSpaces = 0, noOfFlags = 0;
					if(i!=rows-1){
						if(!((Mine)mines[i+1][j]).getButton().isDisabled()){
							noOfAvailableSpaces++;
							availableSpace[0] = (Mine)mines[i+1][j];
							if(((Mine)mines[i+1][j]).getButton().getText()=="F"){
								noOfFlags++;
							}
						}
						if(j!=0){
							if(!((Mine)mines[i+1][j-1]).getButton().isDisabled()){
								noOfAvailableSpaces++;
								availableSpace[1] = (Mine)mines[i+1][j-1];
							}
							if(((Mine)mines[i+1][j-1]).getButton().getText()=="F"){
								noOfFlags++;
							}
						}
						if(j!=columns-1){
							if(!((Mine)mines[i+1][j+1]).getButton().isDisabled()){
								noOfAvailableSpaces++;
								availableSpace[2] = (Mine)mines[i+1][j+1];
							}
							if(((Mine)mines[i+1][j+1]).getButton().getText()=="F"){
								noOfFlags++;
							}
						}
					}
					if(j!=0){
						if(!((Mine)mines[i][j-1]).getButton().isDisabled()){
							noOfAvailableSpaces++;
							availableSpace[3] = (Mine)mines[i][j-1];
						}
						if(((Mine)mines[i][j-1]).getButton().getText()=="F"){
							noOfFlags++;
						}
					}
					if(j!=columns-1){
						if(!((Mine)mines[i][j+1]).getButton().isDisabled()){
							noOfAvailableSpaces++;
							availableSpace[4] = (Mine)mines[i][j+1];
						}
						if(((Mine)mines[i][j+1]).getButton().getText()=="F"){
							noOfFlags++;
						}
					}
					if(i!=0){
						if(!((Mine)mines[i-1][j]).getButton().isDisabled()){
							noOfAvailableSpaces++;
							availableSpace[5] = (Mine)mines[i-1][j];
						}
						if(((Mine)mines[i-1][j]).getButton().getText()=="F"){
							noOfFlags++;
						}
						if(j!=0){
							if(!((Mine)mines[i-1][j-1]).getButton().isDisabled()){
								noOfAvailableSpaces++;
								availableSpace[6] = (Mine)mines[i-1][j-1];
							}
							if(((Mine)mines[i-1][j-1]).getButton().getText()=="F"){
								noOfFlags++;
							}
						}
						if(j!=columns-1){
							if(!((Mine)mines[i-1][j+1]).getButton().isDisabled()){
								noOfAvailableSpaces++;
								availableSpace[7] = (Mine)mines[i-1][j+1];
							}
							if(((Mine)mines[i-1][j+1]).getButton().getText()=="F"){
								noOfFlags++;
							}
						}
					}
					//This part flags mines when the available spaces = number on the square
					if(noOfAvailableSpaces == mine.getTotalMinesAround()&&noOfAvailableSpaces != noOfFlags){
						for(Mine m : availableSpace){
							if(m != null){
								m.getButton().setText("F");
								flagCount++;
							}
						}
						giveUp=false;
					}
					//shows squares when mines surrounding it equals number on square
					if(noOfFlags == mine.getTotalMinesAround()&&noOfFlags != noOfAvailableSpaces){
						for(Mine m : availableSpace){
							if(m != null){
								if(m.getButton().getText()!="F"){
									show(m);
								}
							}
						}
						giveUp=false;
					}
				}
			}
		}
		if(giveUp){
			System.out.println("Going into hard");
			giveUp = solveHard();
		}
		if(giveUp){
			System.out.println("Going into hard 2nd Time");
			giveUp = solveHard();
		}
		if(winLoss.getText()=="You have cleared the field :)"|| giveUp){
			return true;
		}
		else{
			mineCount.setText("Mines: "+(noOfMinesInt-flagCount));
			return false;
		}
	}
	
	private boolean solveHard(){
		boolean giveUp = true;
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < columns; j++){
				Mine mine = (Mine)mines[i][j];
				if(mine.getButton().isDisabled()&&mine.getTotalMinesAround()!=0){
					Mine[] availableSpace = new Mine[8];
					int noOfAvailableSpaces = 0, noOfFlags = 0;
					if(i!=rows-1){
						if(!((Mine)mines[i+1][j]).getButton().isDisabled()&&((Mine)mines[i+1][j]).getButton().getText()!="F"){
							noOfAvailableSpaces++;
							availableSpace[0] = (Mine)mines[i+1][j];
							
						}
						if(((Mine)mines[i+1][j]).getButton().getText()=="F"){
							noOfFlags++;
						}
						if(j!=0){
							if(!((Mine)mines[i+1][j-1]).getButton().isDisabled()&&((Mine)mines[i+1][j-1]).getButton().getText()!="F"){
								noOfAvailableSpaces++;
								availableSpace[1] = (Mine)mines[i+1][j-1];
							}
							if(((Mine)mines[i+1][j-1]).getButton().getText()=="F"){
								noOfFlags++;
							}
						}
						if(j!=columns-1){
							if(!((Mine)mines[i+1][j+1]).getButton().isDisabled()&&((Mine)mines[i+1][j+1]).getButton().getText()!="F"){
								noOfAvailableSpaces++;
								availableSpace[2] = (Mine)mines[i+1][j+1];
							}
							if(((Mine)mines[i+1][j+1]).getButton().getText()=="F"){
								noOfFlags++;
							}
						}
					}
					if(j!=0){
						if(!((Mine)mines[i][j-1]).getButton().isDisabled()&&((Mine)mines[i][j-1]).getButton().getText()!="F"){
							noOfAvailableSpaces++;
							availableSpace[3] = (Mine)mines[i][j-1];
						}
						if(((Mine)mines[i][j-1]).getButton().getText()=="F"){
							noOfFlags++;
						}
					}
					if(j!=columns-1){
						if(!((Mine)mines[i][j+1]).getButton().isDisabled()&&((Mine)mines[i][j+1]).getButton().getText()!="F"){
							noOfAvailableSpaces++;
							availableSpace[4] = (Mine)mines[i][j+1];
						}
						if(((Mine)mines[i][j+1]).getButton().getText()=="F"){
							noOfFlags++;
						}
					}
					if(i!=0){
						if(!((Mine)mines[i-1][j]).getButton().isDisabled()&&((Mine)mines[i-1][j]).getButton().getText()!="F"){
							noOfAvailableSpaces++;
							availableSpace[5] = (Mine)mines[i-1][j];
						}
						if(((Mine)mines[i-1][j]).getButton().getText()=="F"){
							noOfFlags++;
						}
						if(j!=0){
							if(!((Mine)mines[i-1][j-1]).getButton().isDisabled()&&((Mine)mines[i-1][j-1]).getButton().getText()!="F"){
								noOfAvailableSpaces++;
								availableSpace[6] = (Mine)mines[i-1][j-1];
							}
							if(((Mine)mines[i-1][j-1]).getButton().getText()=="F"){
								noOfFlags++;
							}
						}
						if(j!=columns-1){
							if(!((Mine)mines[i-1][j+1]).getButton().isDisabled()&&((Mine)mines[i-1][j+1]).getButton().getText()!="F"){
								noOfAvailableSpaces++;
								availableSpace[7] = (Mine)mines[i-1][j+1];
							}
							if(((Mine)mines[i-1][j+1]).getButton().getText()=="F"){
								noOfFlags++;
							}
						}
					}
					for (Mine m: availableSpace){  //for every unknown space around this certain mine
						if(m!=null){
							ArrayList<MineGroup> mineGroups = m.getGroups();//takes all groups related to the space
							System.out.println("XXXXXXXXXXXX Start of this mine. How many groups "+ mineGroups.size());
							for (MineGroup mG: mineGroups){
								if(mG.getHome()!=mine){  //if the home of the mine isn't being accessed
									Boolean fullGroup = true;
									//System.out.println("Home "+ mG.getHome().getLocationX()+", "+mG.getHome().getLocationY());
									for(Mine mineG: mG.getList()){//checks if everything in the list is a available space
										Boolean match = false;    //
										for(Mine as: availableSpace){
											if(as!=null&&mineG!=null){
												if(mineG == as){
													match = true;
													
												}
											}
										}
										if(mineG==null){
											match= true;
										}
										if(match==false){
											fullGroup = false;
										}
										if(mG.getHome().getTotalMinesAround()==3 && mineG!=null){
											System.out.println("Matches: "+mineG.getLocationX()+", "+mineG.getLocationY()+ "   Full Group?"+ fullGroup);
											System.out.println("Mine: "+mine.getLocationX()+", "+mine.getLocationY());
										}
									}
									if(fullGroup){  //if full group matches
										int noOfMines = mG.getMines();
										//if(mG.getGroup()){
											System.out.println(mine.getLocationX()+", "+mine.getLocationY()+" home of the group "+mG.getHome().getLocationX()+", "+mG.getHome().getLocationY());
											System.out.println(noOfMines+"="+mine.getTotalMinesAround()+"-"+noOfFlags);
										//}
										if(noOfMines == mine.getTotalMinesAround()-noOfFlags){ //clears spaces
											for (Mine as: availableSpace){
												if(as!=null){
													Boolean match = false;
													for(Mine mineG: mG.getList()){
														if(mineG == as){
															match = true;
														}
														//if(mineG!=null)
														//System.out.println(mineG.getLocationX()+", "+ mineG.getLocationY());
													}
													if(!match){//in available spaces but not in group
														show(as);
														giveUp=false;
														//as.getButton().setText("?");
														System.out.println("I did it: "+ as.getLocationX()+", "+as.getLocationY()+ " group? "+mG.getGroup());
														mG.delete();
													}
												}
											}
										}
										if(mG.getGroup()){
											System.out.println(noOfAvailableSpaces+"="+mG.getSquares()+"+"+mine.getTotalMinesAround()+"-"+noOfFlags+"-"+noOfMines);
										}
										if(noOfAvailableSpaces==mG.getSquares()+mine.getTotalMinesAround()-noOfFlags-noOfMines&&!mG.getGroup()){
											for (Mine as: availableSpace){//flags mines
												if(as!=null){
													Boolean match = false;
													for(Mine mineG: mG.getList()){
														if(mineG == as){
															match = true;
														}
														if(mineG!=null&&mG.getGroup())
															System.out.println(as.getLocationX()+", "+as.getLocationY()+"Inside the group: "+mineG.getLocationX()+", "+ mineG.getLocationY()+"  match? "+ match);
													}
													if(!match){//in available spaces but not in group
														if(!as.getButton().isDisabled()){
															as.getButton().setText("F");
															giveUp=false;
															flagCount++;
															System.out.println("I flagged it: "+ as.getLocationX()+", "+as.getLocationY()+ " group? "+mG.getGroup());
															mG.delete();
														}
													}
												}
											}
										}
									}
								}
								
							}
							if(giveUp){
								ArrayList<Mine> availableSpaceArray = new ArrayList<Mine>(Arrays.asList(availableSpace));
								MineGroup group = new MineGroup(availableSpaceArray, mine.getTotalMinesAround()-noOfFlags, false, mine);
								m.addToGroup(group);//takes all spaces available and puts them in a group together
								if(mine.getTotalMinesAround()-noOfFlags>1){//splits up the spaces and makes smaller groups
									//Mine[] splitGroup = group.getList();	//for sum 2 spots
									ArrayList<Mine> splitGroup = new ArrayList<Mine>(); 
									int countMin =0;
									for(Mine spiltMine : group.getList()){
										splitGroup.add(countMin, spiltMine);
										countMin++;
									}
									//splitGroup = group.getList();
									ArrayList<Mine> forGroup = group.getList();
									int count =0;
									for(Mine groupMine : forGroup){
										if(groupMine!=null){
											splitGroup.set(count, null);
											MineGroup smallGroup = new MineGroup(splitGroup, mine.getTotalMinesAround()-noOfFlags-1, true, mine);
											//System.out.println("Before:   "+m.getGroups().size());
											m.addToGroup(smallGroup);
											//m.getGroups().get(0).print();
											//System.out.println("after:    "+m.getGroups().size()+ "     count: "+count);
											//int minecount = mine.getTotalMinesAround()-noOfFlags-1;
												//System.out.println("Main mine: "+mine.getLocationX()+", "+mine.getLocationY()+ "   mines in group: "+ minecount);
												//smallGroup.print();
											//splitGroup.set(count, groupMine); //This code changes the group already placed inside the mine
											splitGroup = new ArrayList<Mine>();
											countMin =0;
											for(Mine spiltMine : group.getList()){
												splitGroup.add(countMin, spiltMine);
												countMin++;
											}
											//splitGroup = group.getList();
											
										}
										count++;
									}
								}
							}
						}
					}
				}
			}
		}
		return giveUp;
	}
	
	private void preSetMines(){  //used while testing solve
		int noOfRows = 6;
		int noOfColumns = 6;
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
		noOfMinesInt = 5;
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
					if(e.getButton()==MouseButton.PRIMARY){
						if(firstClick && b.getText() != "F"){ //checks for if this the first time clicking a mine
							Mine m = (Mine)mines[0][0];
							m.setAmIAMine(true);
							Mine m1 = (Mine)mines[3][0];
							m1.setAmIAMine(true);
							Mine m2 = (Mine)mines[5][0];
							m2.setAmIAMine(true);
							Mine m3 = (Mine)mines[0][0];
							m3.setAmIAMine(true);
							Mine m4 = (Mine)mines[0][0];
							m4.setAmIAMine(true);
							addNumbers();
							show(mine);
							if(rigged){
								boolean solved = false;
								while(!solved){
									solved = solve();
								}
							}
							firstClick = false;
						}
						else if(b.getText() != "F"){  //they can't click on a mine which is flagged
							show(mine);
						}
					}
					else if(e.getButton()==MouseButton.SECONDARY){
						if(b.getText() == "F"){  //flags a mine when right clicked on
							//b.setGraphic(null); //undo a flag
							b.setText("  ");
						}
						else{
							//b.setGraphic(flagGraphic);
							b.setText("F");
							//b.setGraphicTextGap(0);
						}
					}
				}
			});
			totalCount++;
		}
	}
	
}
