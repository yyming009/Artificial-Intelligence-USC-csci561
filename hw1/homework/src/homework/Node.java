package homework;

public class Node {
	private int[][] inputArray;
	private InputStream input;
	private boolean state;
	
	public Node() {
		input = new InputStream();
		this.inputArray = input.getNursery();
		state = false;
	}
	
	public int[][] getNode() {
		return inputArray;
	}
	
	public boolean getState() {
		return state;
	}
	
	public void putLizard(int i, int j) {
		inputArray[i][j] = 1;
	}
}
