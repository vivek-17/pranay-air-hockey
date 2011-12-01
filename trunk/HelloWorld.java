
import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;

//No need to extend JApplet, since we don't add any components;
//we just paint.
public class Main extends Applet implements Runnable {

    StringBuffer buffer;
    Ball ball;
    Board board;
    static Slider sliders[] = new Slider[4];
    int [][] sliderPositions;
    int ballx, bally;
    
    public void init() {
    
    	// Set size of board
    	setSize(Board.boardSize);
        
       	ball = new Ball(200,200, 0.5, 100);
       	//Ball goes from left to right slanted upwards: (200, 500, -0.5, 600);
       	//Ball goes left to bottom downwards: (200,200, 0.5, 100);//getHeight()/2, getWidth()/2);
        
       	sliders[1] = new Slider(30, 30, 2);
       	
    	buffer = new StringBuffer();
        addItem("initializing... ");
    }

    public void start() {
		Thread th = new Thread (this);
		th.start ();
        addItem("starting... ");
        
    }

    public void stop() {
        addItem("stopping... ");
    }

    public void destroy() {
        addItem("preparing for unloading...");
    }

    private void addItem(String newWord) {
        System.out.println(newWord);
        buffer.append(newWord);
        repaint();
    }
    
    public boolean mouseMove( Event evt, int x, int y)
	{
		// Set message....
		System.out.println("mousex: "+x+" "+"mousey: "+y);
    	sliders[1].moveSlider(x, y);
        repaint();
	

		// Signal we have handled the event
		return true;
	}

	public void run ()
	{
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

		while (true)
		{
			ball.move();

			repaint();

			try
			{
				Thread.sleep (Ball.BALLSPEED);
			}
			catch (InterruptedException ex)
			{
				// do nothing
			}

			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		}
	}


    public void paint(Graphics g) {
		
	//Draw a Rectangle around the applet's display area.
    	g.setColor(Color.BLACK);
        g.drawRect(0, 0, 
		   getWidth() - 1,
		   getHeight() - 1);
        
       

        
    // Draw a circle for ball
        g.setColor(Color.BLUE);
        g.fillArc(Ball.currentX - Ball.radius, Ball.currentY - Ball.radius, Ball.DIAMETER, Ball.DIAMETER, 0, 360);
        //g.fillRect(sliders[0].returnXSliderPosition(), sliders[0].returnYSliderPosition(), Slider.Width, Slider.Height);
        g.fillRect(sliders[1].returnXSliderPosition(), sliders[1].returnYSliderPosition(), Slider.Height, Slider.Width);
        //g.fillRect(sliders[2].returnXSliderPosition(), sliders[2].returnYSliderPosition(), Slider.Width, Slider.Height);
        //g.fillRect(sliders[3].returnXSliderPosition(), sliders[3].returnYSliderPosition(), Slider.Height, Slider.Width);
	    
    }
}
