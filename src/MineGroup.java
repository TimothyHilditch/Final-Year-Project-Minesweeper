import java.util.ArrayList;

//A group of mines used when solving the field 
public class MineGroup {
	private int squares; //number of squares in the group
	private int mines; //number of mines in the group
	private ArrayList<Mine> mineList; //collection of all the mines in the group
	private boolean group;
	private Mine parent;
	
	public MineGroup(ArrayList listOfMines, int howManyMines, boolean small, Mine homeMine){
		mineList = listOfMines;
		mines = howManyMines;
		int count=0;
		for(Mine m: mineList){
			if(m!=null){
				count++;
			}
		}
		squares=count;
		group = small;
		parent = homeMine;
	}
	
	public Mine getHome(){
		return parent;
	}
	
	public boolean getGroup(){
		return group;
	}
	
	public void print(){
		System.out.println("Group: ");
		for(Mine m: mineList){
			if(m!=null){
				System.out.println(m.getLocationX()+", "+m.getLocationY());; 
			}
		}
	}
	public void delete(){
		for(Mine m: mineList){
			if(m!=null){
				m.removeGroups(); 
			}
		}
	}
	public ArrayList<Mine> getList(){
		return mineList;
	}
	
	public int getSquares() {
		return squares;
	}

	public int getMines() {
		return mines;
	}
}
