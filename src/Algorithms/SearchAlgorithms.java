package Algorithms;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import Models.Unit;
import Models.UnitComparator;
import Models.UnitType;
import SpecificImplementation.DefinedVariables;
import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class SearchAlgorithms {

	// Singleton interface
	private static UnitComparator myComparator = UnitComparator.getInstance(); // compare units based on their distance
	private static int time = 0; // used for DFS Algorithm
	private static boolean reachedGoal = false;

	// we wont update any any after that it has been removed from queue, so as soon we remove goal unit
	// we can finish the run
	public static void Dijkstra(Unit[][] maze, GridPane grid, Unit startUnit, Unit goalUnit) {

		/*
		 * Dijkstra Setup:
		 * Color = Does not affect Dijkstra
		 * Distance from start unit is Infinity
		 * Prev unit is null
		 * prio queue including all units
		 */

		initUnitsForAlgorithm(maze);
		startUnit.setDistance(0);

		// This array will be our dijkstra queue and contain all nodes.
		ArrayList<Unit> unitsDistanceQueue = getAllUnitsExceptWall(maze);
		unitsDistanceQueue.sort(myComparator);

		ArrayList<Unit> neighbors;
		reachedGoal = false;
		Unit origin;
		while (!unitsDistanceQueue.isEmpty()) {
			origin = unitsDistanceQueue.remove(0); // get min unit

			// If origin distance == Infinity, there are no more reachable nodes (min queue)
			if (origin.getDistance() == Double.POSITIVE_INFINITY)
				return;

			// If reached goal unit no need to go thourgh all of the neighbors again
			// just need to mark all units with valid distance as finished and finish the algorithm
			if (!reachedGoal) {
				neighbors = getNeighbors(maze, origin);
				for (Unit unit : neighbors) {
					if (unitsDistanceQueue.contains(unit)) {
						UnitType type = unit.getUt();
						if (type == UnitType.Free || type == UnitType.Goal || type == UnitType.Weight) {
							decresateDistanceForUnit(origin, unit);
							if (type != UnitType.Goal) {
								unit.setStyle(DefinedVariables.CUR_SCANED_UNIT);
								updateOnPlatform(unit, grid);
							}
						}
					}
					threadSleep(3);
				}
				unitsDistanceQueue.sort(myComparator);
			}
			if (origin.getDistance() != Double.POSITIVE_INFINITY && unitFreeOrWeight(origin.getUt())) {
				origin.setStyle(DefinedVariables.FINISHED_SCANED_UNIT);
				updateOnPlatform(origin, grid);
				threadSleep(3);
			}
			if (origin == goalUnit)
				reachedGoal = true;
		}

	}

	// algorithm source - https://github.com/OneLoneCoder/videos/blob/master/OneLoneCoder_PathFinding_AStar.cpp
	public static void AStar(Unit[][] maze, GridPane grid, Unit startUnit, Unit goalUnit) {

		ArrayList<Unit> closedSet = new ArrayList<>();
		ArrayList<Unit> openSet = new ArrayList<>();
		ArrayList<Unit> neighbors;

		initUnitsForAlgorithm(maze);
		startUnit.setgScore(0); // local distance
		startUnit.setDistance(getManhattanDistance(startUnit, goalUnit)); // global distance

		openSet.add(startUnit);

		Unit current = startUnit;
		reachedGoal = false;
		double gNew;

		while (!openSet.isEmpty()) {
			openSet.sort(myComparator);
			current = openSet.remove(0);
			if (current.equals(goalUnit))
				reachedGoal = true;
			if (!current.equals(startUnit) && !current.equals(goalUnit)) {
				current.setStyle(DefinedVariables.FINISHED_SCANED_UNIT);
				updateOnPlatform(current, grid);
				threadSleep(5);
			}

			if (closedSet.contains(current))
				continue;

			closedSet.add(current);
			if (!reachedGoal) {
				neighbors = getNeighbors(maze, current);
				for (Unit neighbor : neighbors) {
					if (closedSet.contains(neighbor))
						continue;
					openSet.add(neighbor);
					if (!neighbor.equals(goalUnit)) {
						neighbor.setStyle(DefinedVariables.CUR_SCANED_UNIT);
						updateOnPlatform(neighbor, grid);
						threadSleep(5);
					}
					gNew = current.getgScore() + neighbor.getWeight(); // weight is equivalent to distance
					if (gNew < neighbor.getgScore()) {
						neighbor.setPreviousUnit(current);
						neighbor.setgScore(gNew);
						neighbor.setDistance(gNew + getManhattanDistance(neighbor, goalUnit));
					}
				}
			}
		}
	}

	// Used for AStar algorithm as heuristic function
	private static double getManhattanDistance(Unit neighbor, Unit goalUnit) {
		return Math.abs(neighbor.getRow() - goalUnit.getRow()) + Math.abs(neighbor.getCol() - goalUnit.getCol());
	}

	public static void BFS(Unit[][] maze, GridPane grid, Unit startUnit, Unit goalUnit) {
		/*
		 * BFS Setup:
		 * Color = white
		 * Distance from start unit is Infinity
		 * Prev unit is null
		 *
		 */

		initUnitsForAlgorithm(maze);

		startUnit.setAlgorithmColor(Color.RED);
		startUnit.setDistance(0);
		// grid.getChildren().get(startUnit.getIndex()).setUserData(startUnit);

		Queue<Unit> queue = new LinkedList<>();
		ArrayList<Unit> neighbors;
		reachedGoal = false;

		queue.add(startUnit);

		// while queue is not empty
		while (!queue.isEmpty()) {
			Unit origin = queue.poll();
			if (!reachedGoal) // placed here in order to make sure the queue is empty, so all units will be painted
			{
				neighbors = getNeighbors(maze, origin);
				// we should run on neighbors that have not been visited yet and their type is either free or goal.
				for (Unit unit : neighbors) {
					if (validBFSUnit(unit)) {
						// if (unit.getAlgorithmColor() == Color.WHITE && (type == UnitType.Free || type == UnitType.Goal)) {

						// Update unit
						unit.setPreviousUnit(origin);
						unit.setDistance(origin.getDistance() + 1);
						unit.setAlgorithmColor(Color.RED);

						if (unit.getUt() != UnitType.Goal)
							unit.setStyle(DefinedVariables.CUR_SCANED_UNIT);
						else
							reachedGoal = true;

						queue.add(unit);
						updateOnPlatform(unit, grid);
						threadSleep(10);
					}
				}
			}
			// Update unit
			origin.setAlgorithmColor(Color.BLACK);

			// Update grid
			if (origin.getUt() == UnitType.Free) {
				origin.setStyle(DefinedVariables.FINISHED_SCANED_UNIT);
				updateOnPlatform(origin, grid);
				threadSleep(10);
			}
		}

	}

	public static void DFS(Unit[][] maze, GridPane grid, Unit startUnit, Unit goalUnit) {

		/*
		 * DFS Setup:
		 * Color = white
		 * Prev unit is null
		 * reveal time and finish time are zero
		 */
		initUnitsForAlgorithm(maze);

		// get array with all units that are not wall
		ArrayList<Unit> units = getAllUnitsExceptWall(maze);
		reachedGoal = false; // init global reachedgoal boolean

		// set start unit to be first unit
		units.remove(startUnit);
		units.add(0, startUnit);

		time = 0; // init global timer
		Unit unit;

		while (!units.isEmpty()) {
			unit = units.remove(0); // remove first element
			// Run through units that have not been visited
			if (unit.getAlgorithmColor() == Color.WHITE)
				DFSVisit(maze, grid, unit, startUnit, goalUnit);
			if (reachedGoal)
				break;
		}

	}

	private static void DFSVisit(Unit[][] maze, GridPane grid, Unit unit, Unit startUnit, Unit goalUnit) {

		boolean shouldPaint = unitFreeOrWeight(unit.getUt());
		unit.setAlgorithmColor(Color.RED);
		// There are no wall units in queue of DFS function

		if (shouldPaint) {
			unit.setStyle(DefinedVariables.CUR_SCANED_UNIT);
			updateOnPlatform(unit, grid);
			threadSleep(10);
		}
		time++;
		unit.setRevealTime(time);

		if (unit.equals(goalUnit))
			reachedGoal = true;

		// Wall unit cannot be in neighbors, getNeighbors filtered it
		if (!reachedGoal) {
			ArrayList<Unit> neighbors = getNeighbors(maze, unit);
			for (Unit neighbor : neighbors) {
				if (neighbor.getAlgorithmColor() == Color.WHITE) {
					neighbor.setPreviousUnit(unit);
					DFSVisit(maze, grid, neighbor, startUnit, goalUnit);
					if (reachedGoal)
						break;
					threadSleep(10);

				}
			}
		}
		unit.setAlgorithmColor(Color.BLACK);
		time++;
		unit.setFinishTime(time);
		if (shouldPaint) {
			unit.setStyle(DefinedVariables.FINISHED_SCANED_UNIT);
			updateOnPlatform(unit, grid);
			threadSleep(10);
		}

	}

	// This function will mark the trail from goal unit to start if found.
	public static boolean backTrail(Unit[][] maze, GridPane grid, Unit startUnit, Unit goalUnit) {
		Unit runner = goalUnit.getPreviousUnit();
		if (runner == null) {
			return false; // there is no trail to follow
		}
		while (runner != null && runner != startUnit) {
			runner.setStyle(DefinedVariables.DIR_PATH_UNIT);
			updateOnPlatform(runner, grid);
			threadSleep(50);
			runner = runner.getPreviousUnit();
		}
		return true;
	}

	private static void initUnitsForAlgorithm(Unit[][] maze) {
		Unit unit;
		for (int row = 0; row < maze.length; row++)
			for (int col = 0; col < maze[0].length; col++) {
				unit = maze[row][col];
				unit.setDistance(Double.POSITIVE_INFINITY); // BFS Dijstra AStar
				unit.setgScore(Double.POSITIVE_INFINITY); // AStar
				unit.setAlgorithmColor(Color.WHITE); // BFS DFS
				unit.setPreviousUnit(null); // All
				unit.setRevealTime(0); // DFS
				unit.setFinishTime(0); // DFS
			}
	}

	// will return the 4 neighbors of the unit "origin", in addition it will filter out the wall units
	private static ArrayList<Unit> getNeighbors(Unit[][] maze, Unit origin) {
		int row = origin.getRow();
		int col = origin.getCol();

		ArrayList<Unit> neighbors = new ArrayList<Unit>();

		if (row + 1 < maze.length)
			neighbors.add(maze[row + 1][col]);
		if (col + 1 < maze[0].length)
			neighbors.add(maze[row][col + 1]);
		if (row - 1 >= 0)
			neighbors.add(maze[row - 1][col]);
		if (col - 1 >= 0)
			neighbors.add(maze[row][col - 1]);

		neighbors.removeIf(neighbor -> (neighbor.getUt() == UnitType.Wall)); // remove all units that are walls

		return neighbors;

	}

	// Return an array containing all units in the maze that are not wall units
	private static ArrayList<Unit> getAllUnitsExceptWall(Unit[][] maze) {
		ArrayList<Unit> arr = new ArrayList<Unit>();
		Unit unit;
		for (int row = 0; row < maze.length; row++)
			for (int col = 0; col < maze[0].length; col++) {
				unit = maze[row][col];
				if (!unit.isWall())
					arr.add(maze[row][col]);
			}
		return arr;
	}

	// if the path to v until now is "heavier" than path of u + step to v,
	// unit is being changed
	private static void decresateDistanceForUnit(Unit origin, Unit unit) {
		// weight will be adjusted when my maze will support weights
		double weight = unit.getWeight();
		double originDistance = origin.getDistance();
		double unitDistance = unit.getDistance();

		// Distance is double in order to use Double.POSITIVE_INFINITY
		if (originDistance + weight < unitDistance) {
			unit.setDistance(originDistance + weight);
			unit.setPreviousUnit(origin);
		}

	}

	// wrap function in order to have code clearnce
	private static void updateOnPlatform(Unit unit, GridPane grid) {
		Platform.runLater(() -> {
			grid.getChildren().get(unit.getIndex()).setStyle(unit.getStyle());
		});
	}

	// wrap function in order to have code clearnce
	private static void threadSleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * return true if the unit is either free or goal unit and has not been visited yet
	 * Color.White - check only for units that have not been visited yet
	 * Type - we will look only for goal unit and free units, goal unit is included because we want to update goalUnit.prev
	 */
	private static boolean validBFSUnit(Unit unit) {
		return (unit.getAlgorithmColor() == Color.WHITE)
				&& (unit.getUt() == UnitType.Free || unit.getUt() == UnitType.Goal);
	}

	// only free or weighted units can be painted
	private static boolean unitFreeOrWeight(UnitType type) {
		return type == UnitType.Free || type == UnitType.Weight;
	}
}
