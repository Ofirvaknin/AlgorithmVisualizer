package SpecificImplementation;

public class Util {

	// This function recevice row index and col index
	// Calculate the index of the unit in the grid
	public static int calculateIndexForMaze(int row, int col) {
		return row * DefinedVariables.MAZE_WIDTH + col;
	}

}
