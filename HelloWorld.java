
import java.applet.Applet;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.net.InetAddress;

//No need to extend JApplet, since we don't add any components;
//we just paint.
public class Main extends Applet implements Runnable{

    static Slider sliders[] = new Slider[4];
	int ballCoordinates[]; // array of size 3; Values = BallX, BallY, BALLSPEED
	RemoteInterface stub;
    int playerID;
    int RMIregPort;
	InetAddress HostName;
	private volatile boolean hasFourPlayers;
	
    public void init() {
    
    	// Set size of board
    	setSize(GameConstants.boardSize);
    	
    	//HostName = InetAddress.getByAddress(127.0.0.1);
		RMIregPort = 42224;
		try 
		{
	        Registry registry = LocateRegistry.getRegistry("localHost", RMIregPort);
	        stub = (RemoteInterface) registry.lookup("pranay");
	        
	    } catch (Exception e) {
	        System.err.println("Client exception: " + e.toString());
	        e.printStackTrace();
	    }
	    
       	//ball = new Ball(200,200, 0.5, 100);
       	//Ball goes from left to right slanted upwards: (200, 500, -0.5, 600);
       	//Ball goes left to bottom downwards: (200,200, 0.5, 100);//getHeight()/2, getWidth()/2);
        
       	//sliders[1] = new Slider(30, 30, 2);
       	
       	try {
			playerID = stub.getPlayerID();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GameConstants.setupGoalCoordinates(); /// for testing only
    }

    public void start() {
		Thread th = new Thread (this);
		th.start ();        
    }

    public void stop() {
    }

    public void destroy() {
    }
    
    public boolean mouseMove( Event evt, int x, int y)
	{
		// Set message....
    	if(hasFourPlayers){
			System.out.println("mousex: "+x+" "+"mousey: "+y);
	    	sliders[playerID].moveSlider(x, y);
	    	try {
				stub.updateSlider(x, y, playerID);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        repaint();
    	}
		// Signal we have handled the event
		return true;
	}

	public void run ()
	{
		int goalCount = 0;
		
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		
		// Barrier for four players to register at server
		try {
			while(!stub.hasFourPlayers()){
				try	{
					Thread.sleep (500);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		while (goalCount < GameConstants.MAXGoalCount)
		{
			try {
				sliders = stub.getSliderPositions();
				ballCoordinates = stub.getBallPosition();
				goalCount = stub.terminatePlayer(playerID);
				
				hasFourPlayers = true;
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			repaint();

			try
			{
				Thread.sleep (ballCoordinates[2]);
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
        
    // Draw boundary with goals
        
        if(GameConstants.goalCoordinates != null){
	        // Upper Boundary
	        g.setColor(GameConstants.boundaryColor);
	        g.fill3DRect(0, 0, GameConstants.goalCoordinates[1][0], GameConstants.boundaryWidth, true);
	        g.setColor(GameConstants.boundaryColor);
	        g.fill3DRect(GameConstants.goalCoordinates[1][1], 0, GameConstants.boardSize.width - GameConstants.goalCoordinates[1][1], GameConstants.boundaryWidth, true);
	        
	        //g.fill3DRect(GameConstants.boardSize.width, 0, GameConstants.boundaryWidth,  true);
	        // right boundary
	        g.setColor(GameConstants.boundaryColor);
	        g.fill3DRect(GameConstants.boardSize.width-GameConstants.boundaryWidth, 0, GameConstants.boundaryWidth, GameConstants.boardSize.height, true);
	        g.setColor(Color.WHITE);
	        g.fillRect(GameConstants.boardSize.width-GameConstants.boundaryWidth, GameConstants.goalCoordinates[2][2], GameConstants.boundaryWidth, GameConstants.goalSize);
	        
	        // left boundary
	        g.setColor(GameConstants.boundaryColor);
	        g.fill3DRect(0, 0, GameConstants.boundaryWidth, GameConstants.boardSize.height, true);
	        g.setColor(Color.WHITE);
	        g.fillRect(0, GameConstants.goalCoordinates[0][2], GameConstants.boundaryWidth, GameConstants.goalSize);
	        
	        //g.fill3DRect(0, 0, GameConstants.boundaryWidth, GameConstants.goalCoordinates[0][2], true);
	        //g.fill3DRect(0, 0, GameConstants.boundaryWidth, GameConstants.goalCoordinates[0][2], true);
	       
	        // Bottom boundary
	        g.setColor(GameConstants.boundaryColor);
	        g.fill3DRect(0, GameConstants.boardSize.height - GameConstants.boundaryWidth, GameConstants.boardSize.width, GameConstants.boundaryWidth, true);
	        g.setColor(Color.WHITE);
	        g.fillRect(GameConstants.goalCoordinates[3][0], GameConstants.boardSize.height - GameConstants.boundaryWidth, GameConstants.goalSize, GameConstants.boundaryWidth);
        }
        
    // Draw a circle for ball
        g.setColor(Color.BLUE);
        if(hasFourPlayers){
	        g.fillArc(ballCoordinates[0] - GameConstants.radius, ballCoordinates[1] - GameConstants.radius, GameConstants.DIAMETER, GameConstants.DIAMETER, 0, 360);
	        g.fillRect(sliders[0].returnXSliderPosition(), sliders[0].returnYSliderPosition(), Slider.Width, Slider.Height);
	        g.fillRect(sliders[1].returnXSliderPosition(), sliders[1].returnYSliderPosition(), Slider.Height, Slider.Width);
	        g.fillRect(sliders[2].returnXSliderPosition(), sliders[2].returnYSliderPosition(), Slider.Width, Slider.Height);
	        g.fillRect(sliders[3].returnXSliderPosition(), sliders[3].returnYSliderPosition(), Slider.Height, Slider.Width);
        }
    }
}
