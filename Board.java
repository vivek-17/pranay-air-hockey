import java.awt.Dimension;


public class Board {
	final int hole_size = 200;
	final static Dimension boardSize = new Dimension(600, 600);
    int slider_positions[][];
	
	//public void updateBallPath(direction, slope, x, y); // m is slope of line. A recursive function to keep updating ball position along the path given by line slope, direction decides ball is going left or right. Based on that inc / dec x for subsequent calculations. This function should itself change path of ball when it collides with boundary / sliders.
	//public void updateSlider(slider_number, x, y); called when message from network received / self slider changes.

}
