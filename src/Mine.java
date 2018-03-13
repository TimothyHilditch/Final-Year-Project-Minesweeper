import java.util.ArrayList;
import javafx.scene.control.Button;

//This class models one of the squares on the field.
public class Mine {
	private Boolean amIAMine;        //Whether it is a mine or not
	private int totalMinesAround;    //How many mines in the surrounding 8 spaces
	private Button button;			 //The button this mine is.
	private int locationX;
	private int locationY;
	private ArrayList<MineGroup> groups;  //the mine groups this mine is in. Only used in solving the field.
	
	public Mine(Button button, int locationX, int locationY){
		this.button = button;
		this.locationX = locationX;
		this.locationY = locationY;
		groups = new ArrayList<MineGroup>();
		amIAMine = false;
	}
	
	public ArrayList<MineGroup> getGroups(){
		return groups;
	}
	
	public void addToGroup(MineGroup group){
		groups.add(group);
		//System.out.println("I added this group to "+locationX+", "+locationY+ "   Mine number: "+group.getMines());
		//group.print();
	}
	
	public void removeGroups(){
		groups = new ArrayList<MineGroup>();
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
