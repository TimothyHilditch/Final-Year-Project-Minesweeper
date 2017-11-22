import javafx.scene.control.Button;

public class Mine {
	Boolean amIAMine;        //Whether it is a mine or not
	int totalMinesAround;    //How many mines in the surrounding 8 spaces
	Button button;			 //The button this mine is.
	
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
