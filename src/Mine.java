import javafx.scene.control.Button;

public class Mine {
	private Boolean amIAMine;        //Whether it is a mine or not
	private int totalMinesAround;    //How many mines in the surrounding 8 spaces
	private Button button;			 //The button this mine is.
	private int locationX;
	private int locationY;
	
	public Mine(Button button, int locationX, int locationY){
		this.button = button;
		this.locationX = locationX;
		this.locationY = locationY;
	}
	
	
	public Boolean getAmIAMine(){
		return amIAMine;
	}
	public int getTotalMinesAround(){
		return totalMinesAround;
	}
	public Button getButton(){
		return button;
	}
	public void setAmIAMine(Boolean m){
		amIAMine = m;
	}
	public void setTotalMinesAround(int total){
		totalMinesAround = total;
	}
	public int getLocationX() {
		return locationX;
	}
	public int getLocationY() {
		return locationY;
	}
}
