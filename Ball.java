import java.awt.Dimension;


public class Ball {
	static int currentX, currentY, yIntercept;
	static int DIAMETER = 20, radius = 10, BALLSPEED = 10; // both radius and diameter are needed to save calculation in paint method
	static double slope = 1;
	static boolean forward = true;
	final int hole_size = 200;
	final Dimension boardSize = new Dimension(600, 600);

	public Ball(){
		currentX = currentY = 100;
	}

	public Ball(int x, int y, double slope, int yIntercept){
		currentX = x; currentY = y;
		slope = slope;
		yIntercept = yIntercept;
	}
	
	public synchronized void move(){
		//y = mx + c
		handleCollisions();
		if(forward){
			currentX++;
		} else {
			currentX--;
		}
		currentY = (int) (slope * currentX) + yIntercept;

	}
	
	public synchronized int getX(){
		return currentX;
	}
	
	public synchronized int getY(){
		return currentY;
	}
	
	
	// Check for collision and set balls slope, direction and yIntercept accordingly
	public void handleCollisions(){
		int nextX, nextY; // next coordinates of ball
		
		if(forward){
			nextX = currentX + 1;
		} else {
			nextX = currentX - 1;
		}
		
		nextY = (int) (slope * nextX) + yIntercept;

		System.out.println(forward+" "+ currentX+" "+currentY+" "+slope+" "+yIntercept);
		
		 
		// TODO: Check collision with sliders
		int sliderCollision = 0;
		for(int i=0; i<4; i++){
			sliderCollision= Main.sliders[i].returnCollisionType(nextX, nextY);
			// Check if collision detected
			if(sliderCollision != -1)
				break;
		}
		if(sliderCollision != -1){
			if(sliderCollision<3){
				// send ball reverse on current path
				forward = !forward;
				
			} else if(sliderCollision<5){
				// send as per collision with boundary
				forward = !forward;
				yIntercept = (int) ((currentY * slope + currentX)/slope);
				slope = -(1.0/slope);
			}
		}
		// TODO: Check if ball enters goal
		
		
		// Checking collisions with board boundary
		// Check collision with upper boundary 
		if(nextY<=0){
			yIntercept = (int) ((currentY * slope + currentX)/slope);
			slope = -(1.0/slope);
		}
		
		// Check collision with right boundary
		if(nextX>=boardSize.width){
			forward = !forward;
			yIntercept = (int) ((currentY * slope + currentX)/slope);
			slope = -(1.0/slope);
		}
		
		// Check collision with lower boundary
		if(nextY>=boardSize.height){
			yIntercept = (int) ((currentY * slope + currentX)/slope);
			slope = -(1.0/slope);
		}
		
		// Check collision with left boundary
		if(nextX<=0){
			forward = !forward;
			yIntercept = (int) ((currentY * slope + currentX)/slope);
			slope = -(1.0/slope);
		}
		
		
		if(Math.abs(slope) > 1){
			BALLSPEED = 20;
		} else {
			BALLSPEED = 10;
		}
			
	}

}
