import java.awt.Dimension;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicBoolean;


public class Server implements ServerInterface {
	// shared by all clients. Synchronized by object synchronization
		private Slider sliders[];
		private int[] goalCount; // Tells number of goals committed on each player
		private Ball ball;
		private int peerCount;
	
	// Shared only between Main Thread and BallThread on server
		private AtomicBoolean shutdown; // set to 'true' if all four players have terminated
		private Object lock; // Used for synchronization between Main Thread and BallThread
		private BallThread ballThread;
	
	// Default Constructor 
	public Server(){
		sliders = new Slider[4];
		sliders[0] = new Slider(30, 30, 0);
		sliders[1] = new Slider(300, 30, 1);
		sliders[2] = new Slider(570, 300, 2);
		sliders[3] = new Slider(300, 570, 3);
		goalCount = new int[4];
		shutdown = new AtomicBoolean(false);
		ball = new Ball();
		ballThread = new BallThread();
		lock = new Object();
		GameConstants.setupGoalCoordinates();
		
	}
	
	public static void main(String args[]){

		int RMIregPort = 24404;
		if(args.length > 0)
			RMIregPort = Integer.parseInt(args[0]);
		
		Server serverObject = new Server();
		
		try{
			// Register the object at RMI registry
			java.rmi.registry.LocateRegistry.createRegistry(RMIregPort);
			ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(serverObject, 0);
			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.getRegistry(RMIregPort);
			registry.rebind("pranay", stub);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see ServerInterface#getPlayerID()
	 */
	 synchronized public int getPlayerID(){
		 System.out.println("Player " + peerCount + " has registered.");
		 if(peerCount == 3) 
				ballThread.start();
		return peerCount++;
	}
	
	 /*
	  * (non-Javadoc)
	  * @see ServerInterface#updateSlider(int, int, int)
	  */
	public synchronized void updateSlider(int x, int y, int playerID){
		sliders[playerID].moveSlider(x, y);
	}
	
	/*
	 * (non-Javadoc)
	 * @see ServerInterface#getSliderPositions()
	 */
	public synchronized Slider[] getSliderPositions(){
		return sliders;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ServerInterface#getBallPosition()
	 * returns array with x, y coordinates of Ball and BallSpeed
	 */
	public synchronized int[] getBallPosition(){
		int ballCoordinates[] = new int[3];
		ballCoordinates[0] = ball.getX();
		ballCoordinates[1] = ball.getY();
		ballCoordinates[2] = ball.getBALLSPEED();
		return ballCoordinates;
	}

	/*
	 * returns true if four players have registered
	 */
	public synchronized boolean hasFourPlayers() {
		if(peerCount == 4)
			return true;
		else
			return false;
	}

	/* 
	 * param: playerID, ID of player whose goal count is needed
	 * return value: number of goals occured on playerID
	 * Ball Thread shares access to it, hence synchronized on lock object
 	 */
	public int terminatePlayer(int playerID) {		
		 int retValue;
		 synchronized(lock){
			 retValue = goalCount[playerID];		
		 }
		 return retValue;
	}
	
	/*
	 * param: playerID, ID of player whose goal count is to be incremented
	 * BallThread shares access with Main Thread, hence synchronized on lock object
	 */
	public void incGoalCount(int playerID){
		 synchronized(lock){
			 goalCount[playerID]++;
		 }
	}

	
	// Inner Class to calculate ball's new position recursively at constant intervals
	class BallThread extends Thread{
		
		public void run(){
			int extraSleepTime = 0; // used to induce pause effect after goal is achieved on any player
			Thread.currentThread().setName("Ball Thread");
		
			while(!shutdown.get()){
				extraSleepTime = 0;
				extraSleepTime = ball.move();
				try
				{
					Thread.sleep (ball.getBALLSPEED() + extraSleepTime);
				}
				catch (InterruptedException ex)
				{
					System.out.println("Ball Thread interrupted");
					ex.printStackTrace();					
				}
			}
		}
		
	}


	/*
	 * Inner Class that provides abstraction for Ball attributes and methods
	 * ThreadSafe
	 */
	public class Ball {
		// These variables define a line (path) for ball to move
			private int currentX, currentY, yIntercept;
			private double slope = 1;
			private boolean forward = true;

		/* This defines speed of ball at which ball moves. 
		 * After every BALLSPEED milliseconds, new position for ball is calculated by BallThread
		 */
			private int BALLSPEED = 10;
		
			
		public Ball(){
			ballConfigLeftToRightDown();
		}

		public Ball(int x, int y, double slope, int yIntercept){
			currentX = x; currentY = y;
			this.slope = slope;
			this.yIntercept = yIntercept;
		}
		
		/*
		 * Set of coordinates to set Ball path to move from left edge of screen to bottom edge
		 */
		private void ballConfigLeftToRightDown(){
			currentX = 200; currentY = 200;
			slope = 0.5;
			yIntercept = 100;
			forward = true;
		}
		
		/*
		 * return value: extra sleep time for caller excluding BALLSPEED after which next time move() should be called.
		 */
		public synchronized int move(){
			boolean goalOccured = false;
			//y = mx + c
			goalOccured = handleCollisions();
			if(forward){
				currentX++;
			} else {
				currentX--;
			}
			currentY = (int) (slope * currentX) + yIntercept;
			
			if(goalOccured)
				return 2000;
			else
				return 0;
		}
		
		// return value: current X coordinate of Ball
		public synchronized int getX(){
			return currentX;
		}

		// return value: current Y coordinate of Ball
		public synchronized int getY(){
			return currentY;
		}
		
		
		public synchronized int getBALLSPEED() {
			return BALLSPEED;
		}

		public synchronized void setBALLSPEED(int bALLSPEED) {
			BALLSPEED = bALLSPEED;
		}
		
		/*
		 * Set path for Ball after goal has been achieved on a player
		 */
		private void initializeBallOnGoal() {
			currentX = currentY = 100;
			slope = 1;
			forward = true;
			BALLSPEED = 10;			
		}

		/*
		 * return value: true if goal happened during this calculation, else false
		 * Checks for collision and sets ball's slope, direction and yIntercept accordingly for next move
		 * Collision detection is in following order: Sliders, goal then Boundary
		 * 
		*/
		public synchronized boolean handleCollisions(){
			int nextX, nextY; // next coordinates of ball for which collision is to be tested
			
			if(forward){
				nextX = currentX + 1;
			} else {
				nextX = currentX - 1;
			}
			
			nextY = (int) (slope * nextX) + yIntercept;
 
			// Check collision with sliders
			int sliderCollision = 0;
			for(int i=0; i<4; i++){
				sliderCollision= sliders[i].returnCollisionType(nextX, nextY);
				// Check if collision detected
				if(sliderCollision != -1)
					break;
			}
			if(sliderCollision != -1){
				if(sliderCollision == 1){
					// send ball reverse on current path
					forward = !forward;
					
				} else if(sliderCollision == 2){
					// send as per collision with boundary
					forward = !forward;
					yIntercept = (int) ((currentY * slope + currentX)/slope);
					slope = -(1.0/slope);
				}
			}
			
			// Check if ball enters goal
			// i : 0 - left goal, 1 - upper goal .... clockwise as playerIDs
			for(int i=0; i<4; i++){
				if((GameConstants.goalCoordinates[i][0] < nextX && nextX < GameConstants.goalCoordinates[i][1])
						&& (GameConstants.goalCoordinates[i][2] < nextY && nextY < GameConstants.goalCoordinates[i][3])){
					initializeBallOnGoal();
					incGoalCount(i);		
					System.out.println("Goal on PlayerID " + i);
					return true;
				}				
			}

			
			// Checking collisions with board boundary
			// Check collision with upper boundary 
				if(nextY<=0){
					yIntercept = (int) ((currentY * slope + currentX)/slope);
					slope = -(1.0/slope);
				}
			
			// Check collision with right boundary
				if(nextX>=GameConstants.boardSize.width){
					forward = !forward;
					yIntercept = (int) ((currentY * slope + currentX)/slope);
					slope = -(1.0/slope);
				}
			
			// Check collision with lower boundary
				if(nextY>=GameConstants.boardSize.height){
					yIntercept = (int) ((currentY * slope + currentX)/slope);
					slope = -(1.0/slope);
				}
			
			// Check collision with left boundary
				if(nextX<=0){
					forward = !forward;
					yIntercept = (int) ((currentY * slope + currentX)/slope);
					slope = -(1.0/slope);
				}
			
			// Adjust BallSpeed according to slope to give constant speed for different lines
				if(Math.abs(slope) > 1){
					BALLSPEED = 20;
				} else {
					BALLSPEED = 10;
				}
				
			// If here, goal did not happen	
				return false; 
		}


	}



}
