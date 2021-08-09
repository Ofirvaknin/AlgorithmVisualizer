package Models;

import javafx.scene.paint.Color;

public class Unit {

	private int row, col, index;
	private UnitType ut;
	private String style;

	// Used in order to implement algorithms
	private Color algorithmColor; // used for BFS Algorithm
	private double distance; // used for bfs + dijkstra Algorithms
	private double weight; // Used for dijkstra Algorithm
	private int revealTime = 0; // used for DFS Algorithm
	private int finishTime = 0; // used for DFS Algorithm
	// private double fScore, gScore, hScore; // used for AStar Algorithm
	private double gScore; // used for AStar Algorithm

	private Unit previousUnit; // Helps backtrace the trail of shortest path

	public Unit(int row, int col, int index, String style, UnitType ut) {
		this.row = row;
		this.col = col;
		this.index = index;
		this.ut = ut;
		this.style = style;

		// Attributes for algorithms
		
		this.distance = Double.POSITIVE_INFINITY;
		this.previousUnit = null;

		this.algorithmColor = Color.WHITE;
		this.weight = 1;

		this.revealTime = 0;
		this.finishTime = 0;

		this.gScore = Double.POSITIVE_INFINITY; // Local score for AStar algorithm

	}

	public int getRevealTime() {
		return revealTime;
	}

	public double getgScore() {
		return gScore;
	}

	public void setgScore(double gScore) {
		this.gScore = gScore;
	}

	public void setRevealTime(int revealTime) {
		this.revealTime = revealTime;
	}

	public int getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(int finishTime) {
		this.finishTime = finishTime;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public Color getAlgorithmColor() {
		return algorithmColor;
	}

	public void setAlgorithmColor(Color algorithmColor) {
		this.algorithmColor = algorithmColor;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public UnitType getUt() {
		return ut;
	}

	public void setUt(UnitType ut) {
		this.ut = ut;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public Unit getPreviousUnit() {
		return previousUnit;
	}

	public void setPreviousUnit(Unit previousUnit) {
		this.previousUnit = previousUnit;
	}

	public boolean validPath() {
		return this.ut == UnitType.Free || this.ut == UnitType.Weight;
	}

	public boolean isWall() {
		return this.ut == UnitType.Wall;
	}

	@Override
	public boolean equals(Object other) {
		Unit unit = (Unit) other;
		return unit.row == this.row && unit.col == this.col;

	}

	@Override
	public String toString() {
		return Integer.toString(index);

	}
}
