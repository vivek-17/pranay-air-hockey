import java.awt.Dimension;


public class Board {
	final int hole_size = 200;
	final static Dimension boardSize = new Dimension(600, 600);
    int slider_positions[][];
	
	public Board(){
		slider_positions = new int[4][2];
		initializeSliders();
	}
	
	private void initializeSliders(){
		slider_positions[0][0] = boardSize.height/2;
		//slider_positions[0][1] = Slider.BoundaryGap;
		
	}
	
	// Check for collision and set balls slope, direction and yIntercept accordingly
	public static void handleCollisions(){
		int nextX, nextY; // next coordinates of ball 
		// TODO: Check collision with sliders
		
		// TODO: Check if ball enters goal
		
		// Checking collisions with board boundary
		if(Ball.forward){
			nextX = Ball.currentX + 1;
		} else {
			nextX = Ball.currentX - 1;
		}
		
		nextY = (int) (Ball.slope * nextX) + Ball.yIntercept;

		System.out.println(Ball.forward+" "+ Ball.currentX+" "+Ball.currentY+" "+Ball.slope+" "+Ball.yIntercept);
		// Check collision with upper boundary 
		if(nextY<=0){
			Ball.yIntercept = (int) ((Ball.currentY * Ball.slope + Ball.currentX)/Ball.slope);
			Ball.slope = -(1.0/Ball.slope);
			/*
			Ball.slope *= -1;
			Ball.yIntercept *= -1;
			*/
		}
		
		// Check collision with right boundary
		if(nextX>=boardSize.width){
			Ball.forward = !Ball.forward;
			Ball.yIntercept = (int) ((Ball.currentY * Ball.slope + Ball.currentX)/Ball.slope);
			Ball.slope = -(1.0/Ball.slope);
		}
		
		// Check collision with lower boundary
		if(nextY>=boardSize.height){
			Ball.yIntercept = (int) ((Ball.currentY * Ball.slope + Ball.currentX)/Ball.slope);
			Ball.slope = -(1.0/Ball.slope);
			/*
			Ball.slope *= -1;
			Ball.yIntercept = 2*(Ball.currentY - Ball.yIntercept) + Ball.yIntercept;
			*/
		}
		
		// Check collision with left boundary
		if(nextX<=0){
			Ball.forward = !Ball.forward;
			Ball.yIntercept = (int) ((Ball.currentY * Ball.slope + Ball.currentX)/Ball.slope);
			Ball.slope = -(1.0/Ball.slope);
		}
		
		
		if(Math.abs(Ball.slope) > 1){
			Ball.BALLSPEED = 20;
		} else {
			Ball.BALLSPEED = 10;
		}
			
	}
	
	

	//public void updateBallPath(direction, slope, x, y); // m is slope of line. A recursive function to keep updating ball position along the path given by line slope, direction decides ball is going left or right. Based on that inc / dec x for subsequent calculations. This function should itself change path of ball when it collides with boundary / sliders.
	//public void updateSlider(slider_number, x, y); called when message from network received / self slider changes.

}
