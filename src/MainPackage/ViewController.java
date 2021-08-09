package MainPackage;

import java.net.URL;
import java.util.ArrayList;

import java.util.List;
import java.util.ResourceBundle;

import Algorithms.AlgorithmsType;
import Algorithms.SearchAlgorithms;
import Models.Unit;
import Models.UnitType;
import SpecificImplementation.DefinedVariables;
import SpecificImplementation.Util;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ViewController implements Initializable {

	@FXML
	private BorderPane bpMain;

	@FXML
	private Button btnStart;

	@FXML
	private Button btnRestart;

	@FXML
	private Label lblMessage;

	@FXML
	private Button btnKeepMazeRestart;

	@FXML
	private Label lblMode;

	@FXML
	private Label lblStart;

	@FXML
	private Label lblEnd;

	@FXML
	private Label lblUnitModeTxt;

	@FXML
	private Label lblAlgoExplanation;

	@FXML
	private ComboBox<AlgorithmsType> comboBoxAlgorithms;

	@FXML
	private ComboBox<Integer> comboBoxWeightValue;

	@FXML
	private TextArea txtAreaMarkModes;

	private GridPane grid;

	private final int startRowValue = 12;
	private final int startColValue = 11;
	private final int endRowValue = 12;
	private final int endColValue = 34;

	private UnitType markMode;

	private Thread startAnimation;

	private Unit[][] maze;
	private Unit startUnit, goalUnit;
	private List<Button> gridButtons;
	Stage myStage;

	/*
	 * grid - responsible for UI
	 * maze - hold each unit - matching the grid
	 * startUnit - the start point for the maze
	 * goalUnit - the goal point for the maze
	 */

	private void initMaze() {
		int index;
		Unit unit;
		maze = new Unit[DefinedVariables.MAZE_HEIGHT][DefinedVariables.MAZE_WIDTH];
		for (int row = 0; row < DefinedVariables.MAZE_HEIGHT; row++)
			for (int col = 0; col < DefinedVariables.MAZE_WIDTH; col++) {
				index = Util.calculateIndexForMaze(row, col);
				maze[row][col] = new Unit(row, col, index, DefinedVariables.FREE_UNIT, UnitType.Free);
			}

		// Start unit
		unit = maze[startRowValue - 1][startColValue - 1];
		unit.setUt(UnitType.Start);
		unit.setStyle(DefinedVariables.START_UNIT);
		startUnit = unit;

		// Goal Unit
		unit = maze[endRowValue - 1][endColValue - 1];
		unit.setUt(UnitType.Goal);
		unit.setStyle(DefinedVariables.GOAL_UNIT);
		goalUnit = unit;

	}

	private void initSavedMaze() {
		Unit oldUnit;
		for (int row = 0; row < DefinedVariables.MAZE_HEIGHT; row++)
			for (int col = 0; col < DefinedVariables.MAZE_WIDTH; col++) {
				oldUnit = maze[row][col];
				switch (oldUnit.getUt()) {
				case Free:
					oldUnit.setStyle(DefinedVariables.FREE_UNIT);
					break;
				case Wall:
					oldUnit.setStyle(DefinedVariables.WALL_UNIT);
					break;
				case Weight:
					oldUnit.setStyle(DefinedVariables.WEIGHT_UNIT);
					break;
				case Start:
					oldUnit.setStyle(DefinedVariables.START_UNIT);
					break;
				case Goal:
					oldUnit.setStyle(DefinedVariables.GOAL_UNIT);
					break;
				}
			}
	}

	/*
	 * each button matches to a unit in the maze
	 * 
	 */
	private void initGridPane() {
		// init new grid
		grid = new GridPane();
		Unit unit;
		// init new button list for gridpane
		gridButtons = new ArrayList<>();
		for (int row = 0; row < DefinedVariables.MAZE_HEIGHT; row++) {
			for (int col = 0; col < DefinedVariables.MAZE_WIDTH; col++) {

				final Button btn = new Button();
				unit = maze[row][col];
				btn.setStyle(unit.getStyle());
				btn.setPrefSize(25, 25);

				grid.add(btn, col, row);
				btn.setUserData(unit);

				gridButtons.add(btn);

				btn.setOnDragDetected(e -> bpMain.startFullDrag());
				btn.setOnMouseDragEntered(e -> {
					if (e.getButton() == MouseButton.PRIMARY)
						changeUnit(btn);
				});
				btn.setOnMouseClicked(e -> {
					if (e.getButton() == MouseButton.PRIMARY)
						changeUnit(btn);
				});
			}
		}
		grid.setAlignment(Pos.TOP_CENTER);
		bpMain.setCenter(grid);
		bpMain.setPadding(new Insets(10, 10, 10, 10));
		bpMain.setStyle(DefinedVariables.MAIN);

		bpMain.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
			switch (key.getText()) {
			case "1":
				setMode(UnitType.Start);
				break;
			case "2":
				setMode(UnitType.Goal);
				break;
			case "3":
				setMode(UnitType.Wall);
				break;
			case "4":
				setMode(UnitType.Free);
				break;
			case "5":
				setMode(UnitType.Weight);
				break;
			default:
				break;
			}
		});

	}

	private void initSavedGridPane() {
		gridButtons.clear();
		Unit unit;
		Button btn;
		for (int row = 0; row < DefinedVariables.MAZE_HEIGHT; row++) {
			for (int col = 0; col < DefinedVariables.MAZE_WIDTH; col++) {
				unit = maze[row][col];
				String style = unit.getStyle();
				btn = (Button) grid.getChildren().get(unit.getIndex());
				btn.setStyle(style);
				gridButtons.add(btn);

			}
		}

		// Reset grid in main
		bpMain.setCenter(null);
		grid.setAlignment(Pos.TOP_CENTER);
		bpMain.setCenter(grid);
	}

	// Need to stop thread when leaving program
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initMaze();
		initGridPane();
		initSceneBuilderButtons();
		initSceneBuilderLabelsAndText();
		initAlgorithmSelectionComboBox();
		initWeightSelectionComboBox();

		markMode = UnitType.Free;

	}

	private void changeUnit(Button btn) {
		switch (markMode) {
		case Start:
			putStart(btn);
			break;
		case Goal:
			putGoal(btn);
			break;
		case Wall:
			putWall(btn);
			break;
		case Weight:
			putWeight(btn);
			break;
		case Free:
			putFree(btn);
		default:
			break;
		}
	}

	// Replace the btn with a start button
	private void putStart(Button btn) {

		// startUnit - hold the start unit,
		// goalUnit - hold the goal unit

		// start
		Unit newStartUnit = (Unit) btn.getUserData(); // new unit info
		Button oldBtn = (Button) grid.getChildren().get(startUnit.getIndex()); // old button

		if (newStartUnit.getUt() == UnitType.Free || newStartUnit.getUt() == UnitType.Weight
				|| newStartUnit.getUt() == UnitType.Wall) {
			// Modify old unit
			oldBtn.setText(null);
			setDetailsPressedButton(startUnit, oldBtn, 1, DefinedVariables.FREE_UNIT, UnitType.Free);

			// Modify new unit
			btn.setText(null);
			setDetailsPressedButton(newStartUnit, btn, 1, DefinedVariables.START_UNIT, UnitType.Start);

			startUnit = newStartUnit;
		}
	}

	// Replace the btn with a goal button
	private void putGoal(Button btn) {

		// startUnit - hold the start unit,
		// goalUnit - hold the goal unit

		// start
		Unit newGoalUnit = (Unit) btn.getUserData(); // new unit info
		Button oldBtn = (Button) grid.getChildren().get(goalUnit.getIndex()); // old button

		if (newGoalUnit.getUt() == UnitType.Free || newGoalUnit.getUt() == UnitType.Weight
				|| newGoalUnit.getUt() == UnitType.Wall) {

			// Modify old unit
			oldBtn.setText(null);
			setDetailsPressedButton(goalUnit, oldBtn, 1, DefinedVariables.FREE_UNIT, UnitType.Free);

			// Modify new unit
			btn.setText(null);
			setDetailsPressedButton(newGoalUnit, btn, 1, DefinedVariables.GOAL_UNIT, UnitType.Goal);

			goalUnit = newGoalUnit;
		}
	}

	// Replace the btn with a free button
	private void putFree(Button btn) {
		Unit unitInfo = (Unit) btn.getUserData();
		if (unitInfo.getUt() == UnitType.Weight || unitInfo.getUt() == UnitType.Wall) {
			// set unit in the maze array
			setDetailsPressedButton(unitInfo, btn, 1, DefinedVariables.FREE_UNIT, UnitType.Free);
			btn.setText(null);
		}
	}

	// Replace the btn with a Wall button
	private void putWall(Button btn) {
		Unit unitInfo = (Unit) btn.getUserData();
		if (unitInfo.getUt() == UnitType.Free || unitInfo.getUt() == UnitType.Weight) {
			// set unit in the maze array
			setDetailsPressedButton(unitInfo, btn, 1, DefinedVariables.WALL_UNIT, UnitType.Wall);
			btn.setText(null);
		}
	}

	private void putWeight(Button btn) {
		Unit unitInfo = (Unit) btn.getUserData();
		int weight;
		if (!isWeightedAlgorithm()) {
			lblMessage.setText("Please change mode or choose weighted algorithm!");
			return;
		}

		if (!weightValueSelected()) {
			lblMessage.setText("Please choose weight");
			return;
		}

		if (unitInfo.getUt() != UnitType.Goal && unitInfo.getUt() != UnitType.Start) {
			// set button information in the grid
			weight = comboBoxWeightValue.getValue();
			setDetailsPressedButton(unitInfo, btn, weight, DefinedVariables.WEIGHT_UNIT, UnitType.Weight);
			if (weight == 1)
				btn.setText(null);
			else
				btn.setText(Integer.toString(weight));
		}
	}

	private void setDetailsPressedButton(Unit unitInfo, Button btn, int weight, String style, UnitType type) {
		unitInfo.setUt(type);
		unitInfo.setStyle(style);
		unitInfo.setWeight(weight);

		btn.setStyle(style);
	}

	// init buttons
	private void initSceneBuilderButtons() {
		setBtnStart();
		setBtnRestart();
		setBtnRestartKeepMaze();
	}

	private void initSceneBuilderLabelsAndText() {
		lblMessage.setText("Press start to find path");
		lblMode.setText("");

		lblUnitModeTxt.setStyle("-fx-font-size: 14px");
		lblMessage.setStyle("-fx-font-size: 14px");
		lblMode.setStyle("-fx-font-size: 14px");

		lblAlgoExplanation.setStyle("-fx-font-size: 14px");

		setMarkModeExplanation();
	}

	@FXML
	private void setBtnStart() {

		btnStart.setOnAction(e -> {
			// did not choose algorithm
			if (!algorithmSelected()) {
				lblMessage.setText("Please select algorithm");
				return;
			}

			btnStart.setDisable(true);
			grid.setMouseTransparent(true); // disable grid touching
			comboBoxAlgorithms.setMouseTransparent(true); // disable combobox selecting
			comboBoxWeightValue.setMouseTransparent(true);// disable combobox selecting

			startAnimation = new Thread(new Runnable() {
				public void run() {
					boolean backTrailRes;

					switch (comboBoxAlgorithms.getValue()) {
					case BFS:
						SearchAlgorithms.BFS(maze, grid, startUnit, goalUnit);
						break;
					case DFS:
						SearchAlgorithms.DFS(maze, grid, startUnit, goalUnit);
						break;
					case Dijkstra:
						SearchAlgorithms.Dijkstra(maze, grid, startUnit, goalUnit);
						break;
					case AStar:
						SearchAlgorithms.AStar(maze, grid, startUnit, goalUnit);
						break;
					}

					backTrailRes = SearchAlgorithms.backTrail(maze, grid, startUnit, goalUnit);
					Platform.runLater(() -> {
						if (!backTrailRes)
							lblMessage.setText("There is no trail between start and goal");
					});

				}
			});
			startAnimation.start();
		});

	}

	private void setBtnRestart() {

		btnRestart.setOnAction(e -> {
			if (startAnimation != null && startAnimation.isAlive())
				lblMessage.setText("Please wait untill path finder finish");
			else {
				btnStart.setDisable(false);
				// no need to set true on grid.setMouseTransparent since I am creating new grid
				comboBoxAlgorithms.setMouseTransparent(false); // disable combobox selecting
				comboBoxWeightValue.setMouseTransparent(false);// disable combobox selecting
				initMaze();
				initGridPane();
			}
		});
	}

	private void setBtnRestartKeepMaze() {

		btnKeepMazeRestart.setText("Restart current maze");

		btnKeepMazeRestart.setOnAction(e -> {
			if (startAnimation != null && startAnimation.isAlive())
				lblMessage.setText("Please wait untill path finder finish");
			else {
				btnStart.setDisable(false);
				grid.setMouseTransparent(false);
				comboBoxAlgorithms.setMouseTransparent(false); // disable combobox selecting
				comboBoxWeightValue.setMouseTransparent(false);// disable combobox selecting
				initSavedMaze();
				initSavedGridPane();
			}
		});
	}

	private void setMarkModeExplanation() {
		txtAreaMarkModes.setEditable(false);
		String text = "Tooltip for maze marks, press the following numbers in order to mark: \n"
				+ "1 - Start Unit (Green unit).\n" + "2 - Goal Unit (Red unit).\n" + "3 - Wall Unit (Blue unit).\n"
				+ "4 - Free Unit (White unit).\n"
				+ "5 - Weight Unit (Numbered unit), Weight choosing is only available at weighted algorithms.\n"
				+ "By default, all units have weight of 1";

		txtAreaMarkModes.setText(text);
	}

	// Return true if operation can continue otherwise return false
	// set lblMessage accordinly
	private void setMode(UnitType mode) {
		lblMode.setText(mode.toString());
		markMode = mode;
	}

	private void initAlgorithmSelectionComboBox() {
		comboBoxAlgorithms.setStyle("-fx-font-size: 14px");
		comboBoxWeightValue.setStyle("-fx-font-size: 14px");

		comboBoxAlgorithms.getItems().clear();
		for (AlgorithmsType type : AlgorithmsType.values())
			comboBoxAlgorithms.getItems().add(type);
		comboBoxAlgorithms.valueProperty().addListener((observable, oldvalue, newvalue) -> {

			boolean weightedAlgo = isWeightedAlgorithm();
			comboBoxWeightValue.setVisible(weightedAlgo);

			// update any free units if changed from weighted algo
			if (!weightedAlgo)
				for (int row = 0; row < maze.length; row++) {
					for (int col = 0; col < maze[0].length; col++) {
						Unit unit = maze[row][col];
						if (unit.getUt() == UnitType.Weight) {
							Button btn = (Button) grid.getChildren().get(unit.getIndex());
							putFree(btn);
						}
					}
				}

		});
	}

	private void initWeightSelectionComboBox() {
		comboBoxWeightValue.setVisible(false);
		comboBoxAlgorithms.setStyle("-fx-font-size: 14px");
		comboBoxWeightValue.getItems().clear();
		for (int i = 1; i <= 5; i++)
			comboBoxWeightValue.getItems().add(i);

	}

	private boolean algorithmSelected() {
		return comboBoxAlgorithms.getValue() != null;
	}

	private boolean weightValueSelected() {
		return comboBoxWeightValue.getValue() != null;
	}

	private boolean isWeightedAlgorithm() {
		AlgorithmsType type = comboBoxAlgorithms.getValue();
		if (type == null) {
			lblAlgoExplanation.setText(null);
			return false;
		}

		switch (type) {
		case Dijkstra:
			lblAlgoExplanation.setText(DefinedVariables.DijkstraExplanation);
			return true;
		case AStar:
			lblAlgoExplanation.setText(DefinedVariables.AStarExplanation);
			return true;
		case BFS:
			lblAlgoExplanation.setText(DefinedVariables.BFSExplanation);
			return false;
		case DFS:
			lblAlgoExplanation.setText(DefinedVariables.DFSExplanation);
			return false;
		default:
			return false;
		}
	}

}
