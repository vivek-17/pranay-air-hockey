import java.rmi.RemoteException;


public interface RemoteInterface  extends java.rmi.Remote {

	// returns integer value between 0 through 3 that will be the playerID for caller client. Subsequently, he will be attached to corresponding side of table
	public int getPlayerID() throws RemoteException;
	
	// updates the position of slider for player 'playerID' on server. So that other players could read this position
	public void updateSlider(int x, int y, int playerID) throws RemoteException;
	
	// returns slider coordinates for all the players in game
	public Slider[] getSliderPositions() throws RemoteException;
	
	// returns array with x, y coordinates of Ball
	public int[] getBallPosition() throws RemoteException;

	// returns 'true' if server has registered four players. Client threads poll and wait this function to be true
	public boolean hasFourPlayers() throws RemoteException;

	// returns 'true' if player with ID 'playerID' has reached its MAX_GOAL_Limit described in GameConstants file
	public int terminatePlayer(int playerID) throws RemoteException;
}
