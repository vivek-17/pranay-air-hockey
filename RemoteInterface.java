import java.rmi.RemoteException;


public interface RemoteInterface  extends java.rmi.Remote {

	public int getPlayerID() throws RemoteException;
	
	public void updateSlider(int x, int y, int playerID) throws RemoteException;
	
	public Slider[] getSliderPositions() throws RemoteException;
	
	// returns array with x, y coordinates of Ball
	public int[] getBallPosition() throws RemoteException;

	public boolean hasFourPlayers() throws RemoteException;

	public int terminatePlayer(int playerID) throws RemoteException;
}
