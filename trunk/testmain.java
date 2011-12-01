import java.applet.*;
import java.awt.*;

/*
public class testmain extends Applet{
	private Slider s;
	public testmain(){
		super();
	}
	 public void init() {
	        s=new Slider(300,300,1);
	        this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
	 }

	    public void start() {
	        
	    }

	    public void stop() {
	        
	    }

	    public void destroy() {
	        
	    }
	    
	    public boolean mouseMove( Event evt, int x, int y)
		{
			// Set message....
			System.out.println("mousex: "+x+" "+"mousey: "+y);
	    	s.moveSlider(y);
	        repaint();
		

			// Signal we have handled the event
			return true;
		}
	    public void paint(Graphics g) {
	    	//Draw a Rectangle around the applet's display area.
	    	System.out.println("x: "+s.returnXSliderPosition()+" "+"y: "+s.returnYSliderPosition());
	            g.fillRect(s.returnXSliderPosition(), s.returnYSliderPosition(), 7,40);
	    	//Draw the current string inside the rectangle.
	            
	        }
}
*/