import java.awt.Dimension;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicBoolean;


public class RemoteController implements RemoteInterface {
	private Slider sliders[];
	private int[] goalCount; // Tells number of goals committed on each player
	private Ball ball;
	int peerCount;
	AtomicBoolean shutdown;
	Object lock;
	private BallThread ballThread;
	
	
	public RemoteController(){
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

		int RMIregPort = Integer.parseInt(args[0]);
		RemoteController serverObject = new RemoteController();
		
		try{
			// Register the object at RMI registry
			java.rmi.registry.LocateRegistry.createRegistry(RMIregPort);
			RemoteInterface stub = (RemoteInterface) UnicastRemoteObject.exportObject(serverObject, 0);
			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.getRegistry(RMIregPort);
			registry.rebind("pranay", stub);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	 synchronized public int getPlayerID(){
		 System.out.println(peerCount);
		 if(peerCount == 3) 
				ballThread.start();
		return peerCount++;
	}
	
	public synchronized void updateSlider(int x, int y, int playerID){
		sliders[playerID].moveSlider(x, y);
	}
	
	public synchronized Slider[] getSliderPositions(){
		return sliders;
	}
	
	// returns array with x, y coordinates of Ball
	public synchronized int[] getBallPosition(){
		int ballCoordinates[] = new int[3];
		ballCoordinates[0] = ball.getX();
		ballCoordinates[1] = ball.getY();
		ballCoordinates[2] = ball.getBALLSPEED();
		return ballCoordinates;
	}

	@Override
	public synchronized boolean hasFourPlayers() {
		if(peerCount == 4)
			return true;
		else
			return false;
	}

	@Override
	// Ball Thread shares access to it
	public int terminatePlayer(int playerID) {		
		 int retValue;
		 synchronized(lock){
			 retValue = goalCount[playerID];		
		 }
		 return retValue;
	}
	
	public void incGoalCount(int playerID){
		 synchronized(lock){
			 goalCount[playerID]++;
		 }
	}

	
	// Inner Class to calculate ball's new position recursively
	class BallThread extends Thread{
		public void run(){
			int extraSleepTime = 0;
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


	public class Ball {
		int currentX, currentY, yIntercept;
		static final int DIAMETER = 20, radius = 10; // both radius and diameter are needed to save calculation in paint method
		int BALLSPEED = 10;
		double slope = 1;
		boolean forward = true;
		
		public Ball(){
			ballConfigLeftToRightDown();
		}

		public Ball(int x, int y, double slope, int yIntercept){
			currentX = x; currentY = y;
			this.slope = slope;
			this.yIntercept = yIntercept;
		}
		
		private void ballConfigLeftToRightDown(){
			currentX = 200; currentY = 200;
			slope = 0.5;
			yIntercept = 100;
			forward = true;
		}
		
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
		
		public synchronized int getX(){
			return currentX;
		}
		
		public synchronized int getY(){
			return currentY;
		}
		
		
		public synchronized int getBALLSPEED() {
			return BALLSPEED;
		}

		public synchronized void setBALLSPEED(int bALLSPEED) {
			BALLSPEED = bALLSPEED;
		}
		
		private void initializeBallOnGoal() {
			currentX = currentY = 100;
			slope = 1;
			forward = true;
			BALLSPEED = 10;			
		}

		// Check for collision and set balls slope, direction and yIntercept accordingly
		// returns true if goal happened during this calculation
		public synchronized boolean handleCollisions(){
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
				sliderCollision= sliders[i].returnCollisionType(nextX, nextY);
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
			// i = 0 - left goal, 1 - upper goal .... clockwise as playerIDs
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
			
			
			if(Math.abs(slope) > 1){
				BALLSPEED = 20;
			} else {
				BALLSPEED = 10;
			}
				
			return false; // If here, goal did not happen
		}


	}



}
