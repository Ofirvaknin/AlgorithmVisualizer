package SpecificImplementation;

public class DefinedVariables {

	// All units include border
	public static String BORDER = ";-fx-border-color: #00FFFF;-fx-border-width: 1px 1px 0px 1px;-fx-font-size:10"; // light blue (cyan)
	public static String FREE_UNIT = "-fx-background-color: #F8F8FF" + BORDER; // White
	public static String WALL_UNIT = "-fx-background-color: #191970" + BORDER; // midnight blue
	public static String START_UNIT = "-fx-background-color: #3CB371" + BORDER; // Light green
	public static String GOAL_UNIT = "-fx-background-color: #CD5C5C" + BORDER; // light red
	public static String WEIGHT_UNIT = "-fx-background-color: #FFFFFF" + BORDER; // Color
	public static String CUR_SCANED_UNIT = "-fx-background-color: #6A5ACD" + BORDER; // SLATEBLUE
	public static String FINISHED_SCANED_UNIT = "-fx-background-color: #87CEEB" + BORDER; // green tourquise
	public static String DIR_PATH_UNIT = "-fx-background-color: #FAFAD2" + BORDER; // light gold yellow

	public static String DijkstraExplanation = "Dijkstra's Algorithm is weighted and guarantees the shortest path!";
	public static String BFSExplanation = "BFS is unweighted and guarantees the shortest path!";
	public static String DFSExplanation = "DFS is unweighted and does not guarantee the shortest path!";
	public static String AStarExplanation = "A* Search is weighted and guarantees the shortest path!";


	// Pane colors
	public static String MAIN = "-fx-background-color: #00FFFF"; // light blue (cyan)

	public static int MAZE_WIDTH = 45;
	public static int MAZE_HEIGHT = 25;

}
