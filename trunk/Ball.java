
public class Ball {
	static int currentX, currentY, yIntercept;
	static int DIAMETER = 20, radius = 10, BALLSPEED = 10; // both radius and diameter are needed to save calculation in paint method
	static double slope = 1;
	static boolean forward = true;
	
	public Ball(){
		currentX = currentY = 100;
	}

	public Ball(int x, int y, double slope, int yIntercept){
		currentX = x; currentY = y;
		Ball.slope = slope;
		Ball.yIntercept = yIntercept;
	}
	
	public void move(){
		//y = mx + c
		Board.handleCollisions();
		if(forward){
			currentX++;
		} else {
			currentX--;
		}
		currentY = (int) (slope * currentX) + yIntercept;

	}

}
