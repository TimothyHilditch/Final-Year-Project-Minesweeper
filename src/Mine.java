import javafx.scene.control.Button;

public class Mine {
	private Boolean amIAMine;        //Whether it is a mine or not
	private int totalMinesAround;    //How many mines in the surrounding 8 spaces
	private Button button;			 //The button this mine is.
	
	public Mine(Button button){
		this.button = button;
	}
	
	
	Boolean getAmIAMine(){
		return amIAMine;
	}
	int getTotalMinesAround(){
		return totalMinesAround;
	}
	Button getButton(){
		return button;
	}
	void setAmIAMine(Boolean m){
		amIAMine = m;
	}
	void setTotalMinesAround(int total){
		totalMinesAround = total;
	}
}
