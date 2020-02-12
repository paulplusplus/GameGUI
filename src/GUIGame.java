import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class GUIGame extends Application{
	
	ArrayList<Point> pointList = new ArrayList<Point>();
	ArrayList<Player> playerList = new ArrayList<Player>();
	int numOfPlayers = 0;
	int currentPlayer = 0;
	Point current;
	Label playerName;

	public static void main(String[] args){
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Stage window = primaryStage;
		window.setOnCloseRequest(e -> {
			Platform.exit();
			System.exit(0);
		});
		int[] info = questionBox.runAndDisplay();
		playerName = new Label("Player Here");
		VBox box = new VBox(20);
		numOfPlayers = info[0];
		for(int i = 1; i <= numOfPlayers; i++) {
			playerList.add(new Player(i));
		}
		setUpBoard(info[1], box);
		Scene scene = new Scene(box);
		window.setTitle("GUIGame");
		window.setScene(scene);
		window.show();
	}
	
	public void setUpBoard(int size, VBox box) {
		GridPane gp = new GridPane();
		gp.setPadding(new Insets(10, 10, 10, 10));
		gp.setAlignment(Pos.CENTER);
		for(int i = 0; i < size; i++) {
			for(int j =0; j < size; j++) {
				EButton b = new EButton("   ", i, j);
				String color;
				if ((i + j) % 2 == 0) color = "white";
				else color = "gainsboro";
				b.b.setStyle("-fx-background-color: " + color + ";");
				b.b.setMinSize(45, 45);
				b.b.setOnAction(e -> {
					handleGameEvent(b.b, b.getXCoord(), b.getYCoord());
				});
				gp.add(b.b,  i,  j);
			}
		}
		box.getChildren().add(gp);
	}//setUpBoard
	
	void handleGameEvent(Button b, int x, int y) {
		Point newPoint = new Point(x, y);
		if(pointList.isEmpty()) { //If list is empty, just add anyway
			pointList.add(newPoint);
			current = newPoint;
			b.setStyle("-fx-background-color: " + playerList.get(currentPlayer).color + ";");
			incrementPlayer();
			return;
		}

		//If illegal move
		if(Math.abs(current.x - x) + Math.abs(current.y - y) > 1) { //Illegal move
			String message = "Player " + (currentPlayer + 1) + " made an illegal move. Try again!";
			ErrorBox.runWindow(message, "Game Error");
			return;
		}
		
		if(pointList.get(0).comparePoint(newPoint)) {
			if(pointList.size() < 3) {
				String message = "Intersection detected. Player " + (currentPlayer + 1) + " loses, everyone else wins!";
				ErrorBox.runWindow(message, "End of game");
				Platform.exit();
				System.exit(0);
			}
			String message = "Self-avoiding path! Player " + (currentPlayer + 1) + " wins! Everyone else loses!";
			ErrorBox.runWindow(message, "End of game");
			Platform.exit();
			System.exit(0);
		}
		
		for(Point points : pointList){ //If there is an intersection
			if(points.comparePoint(newPoint)) {
				String message = "Intersection detected. Player " + (currentPlayer + 1) + " loses, everyone else wins!";
				ErrorBox.runWindow(message, "End of game");
				Platform.exit();
				System.exit(0);
			}
		}
		
		
		pointList.add(newPoint);
		current = newPoint;
		b.setStyle("-fx-background-color: "+ playerList.get(currentPlayer).color +";");
		incrementPlayer();
		
	}//handleGameEvent
	
	void incrementPlayer() {
		if(currentPlayer < (numOfPlayers - 1)) currentPlayer++;
		else currentPlayer = 0;
	}
	
	class Point{
		int x;
		int y;
		Point(int px, int py){
			x = px;
			y = py;
		}
		Boolean comparePoint(Point cd) {
			if(cd.x == x && cd.y == y) return true;
			else return false;
		}
	}//Point class
	class Player{
		String color;
		int num;
		Player(int pnum){
			color = generateColor();
			num = pnum;
		}
		String generateColor() {
			int x, y, z;
			String ret;
			x = (int)(Math.random() * 256);
			y = (int)(Math.random() * 256);
			z = (int)(Math.random() * 256);
			ret = "rgb(" + x + "," + y + "," + z + ")";
			return ret;
		}
	}
	class EButton{
		Button b = new Button();
		int x = 0, y = 0;
		EButton(String title, int px, int py){
			b.setText(title);
			x = px;
			y = py;
		}
		public int getXCoord() {
			return x;
		}
		public int getYCoord() {
			return y;
		}
		
	}
} //GameGui

class questionBox{
	public static int[] runAndDisplay() {
		int[] ret = new int[2];
		Stage window = new Stage();
		window.setTitle("Game setup");
		window.initModality(Modality.APPLICATION_MODAL);
		window.initStyle(StageStyle.UNDECORATED);;
		VBox vb = new VBox(20);
		HBox hb = new HBox(20);
		vb.setPadding(new Insets(50, 50, 50, 50));
		Label players = new Label("How many players for this game?");
		TextField playerNum = new TextField();
		playerNum.setPromptText("Number of players");
		TextField boardSizeNum = new TextField();
		boardSizeNum.setPromptText("Size of board");
		Label boardSize = new Label("How large should the board size be?");
		Button confirm = new Button("Accept");
		Button exit = new Button("Close");
		confirm.setDefaultButton(true);
		exit.setCancelButton(true);
		hb.getChildren().addAll(confirm, exit);
		confirm.setOnAction(e -> {
			String hey = playerNum.getText();
			String dey = boardSizeNum.getText();
			try {
				ret[0] = Integer.parseInt(hey);
				ret[1] = Integer.parseInt(dey);
			} catch( Exception exp) {
				ErrorBox.runWindow("You did not provide a proper number. Numerical only!", "Number error");
				Platform.exit();
				System.exit(0);
			}
			window.close();
			
		});
		exit.setOnAction(e -> {
			Platform.exit();
			System.exit(0);
		});
		vb.getChildren().addAll(players, playerNum, boardSize, boardSizeNum, hb);
		Scene scene = new Scene(vb, 500, 500);
		window.setScene(scene);
		window.showAndWait();
		return ret;
		
	}
} //QuestionBox

class ErrorBox{
	public static void runWindow(String message, String title) {
		Stage stage = new Stage();
		Label label = new Label(message);
		stage.initModality(Modality.APPLICATION_MODAL);
		Button ok = new Button("OK");
		ok.setOnAction(e -> {
			stage.close();
		});
		VBox vbox = new VBox(15);
		vbox.setPadding(new Insets(20, 20, 20, 20));
		vbox.getChildren().addAll(label, ok);
		Scene scene = new Scene(vbox, 400, 200);
		stage.setScene(scene);
		stage.setTitle(title);
		stage.showAndWait();
	}
}// ErrorBox
