import java.awt.Color;
import java.awt.Dimension;


public class GameConstants {
	
	static int DIAMETER = 20, radius = 10, BALLSPEED = 10; // both radius and diameter are needed to save calculation in paint method
	static final int goalSize = 300;
	static final int gapSliderBoundary = 30;
	static final Dimension boardSize = new Dimension(600, 600);
	static int[][] goalCoordinates;
	static final int MAXGoalCount = 1;
	static final int boundaryWidth = 10;
	static final Color boundaryColor = Color.RED;
	
	// This function should be called before using goalCoordinates. It sets up coordinates of rectangle corresponding to each goal.
	// Ball class uses this to detect ball entering goal
	static public void setupGoalCoordinates(){
		goalCoordinates = new int[4][4]; // for each row, there is, x1, x2. y1, y2
		// condition for collision: (x1<ballx<x2) && y1<bally<y2
		
		//left goal
		goalCoordinates[0][0] = Integer.MIN_VALUE;
		goalCoordinates[0][1] = gapSliderBoundary;
		goalCoordinates[0][2] = boardSize.height/2 - goalSize/2;
		goalCoordinates[0][3] = boardSize.height/2 + goalSize/2;
		
		//top goal
		goalCoordinates[1][0] = (GameConstants.boardSize.width/2) - (GameConstants.goalSize/2);
		goalCoordinates[1][1] = (GameConstants.boardSize.width/2) + (GameConstants.goalSize/2);
		goalCoordinates[1][2] = Integer.MIN_VALUE;
		goalCoordinates[1][3] = gapSliderBoundary;
		
		//right goal
		goalCoordinates[2][0] = boardSize.width - gapSliderBoundary;
		goalCoordinates[2][1] = Integer.MAX_VALUE;
		goalCoordinates[2][2] = boardSize.height/2 - goalSize/2;
		goalCoordinates[2][3] = boardSize.height/2 + goalSize/2;
		
		//bottom goal
		goalCoordinates[3][0] = (GameConstants.boardSize.width/2) - (GameConstants.goalSize/2);
		goalCoordinates[3][1] = (GameConstants.boardSize.width/2) + (GameConstants.goalSize/2);
		goalCoordinates[3][2] = GameConstants.boardSize.height - gapSliderBoundary;
		goalCoordinates[3][3] = Integer.MAX_VALUE;
	}


}
