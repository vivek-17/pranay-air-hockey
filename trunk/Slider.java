import java.io.Serializable;

//Has to thread safe
public class Slider implements Serializable{
private volatile int  xposition;
private  volatile int yposition;
//Once fixed ,length of the slider will not change.
//Once fixed ,slider id will not change.
private final  int sliderid;
private static int slidercount=0;
static final int Height = 140;
static final int Width = 7;
//==================================================================================
public Slider(int xposition,int yposition,int sliderid){
	if(slidercount<4){
		this.xposition=xposition;
		this.yposition=yposition;
		this.sliderid=sliderid;
		slidercount++;
	}else{
		this.xposition=-1;
		this.yposition=-1;
		this.sliderid=-1;
	}
}
//====================================================================================
public void moveSlider(int x, int y){
	/*Check id of the slider
	 * if 1 and 3 --->Need to move on y axis.
	 * if 2 and 4 --->Need to move on x-axis.
	 * slider should only move when mouse lies in the (slider position + length of the slider and its width if any)
	 * Also slider should not move if the slider is at the corner of the board
	 */
	if(sliderid==0||sliderid==2){
		//if( (mouseposition+Height)<600 &&(mouseposition)>0){
			yposition=y;
			//System.out.println("hello: "+ mouseposition);
		//}
	}
	if(sliderid==1 || sliderid==3){
		//if((mouseposition+Height)<600 &&(mouseposition)>0){
			xposition=x;
		//}
	}
}
//========================================================================================
public int returnXSliderPosition(){
	/*
	 * Return the x position of the slider
	 */
		return xposition;
}
//=========================================================================================
public int returnYSliderPosition(){	
	/*
	 * Return the y position of the slider
	 */
		return yposition;
}
//=========================================================================================
public int returnLength(){
	/*
	 * Return the length of the slider
	 */
return Height;
}
//=========================================================================================
int returnCollisionType(int ballx,int bally){
	/*
	 * Find the balls collision with the slider.
	 * Divide the slider in 5 parts,call them region 1,2,3,4,5.
	 * Return the region number in which the ball collides.
	 * If no collision has taken place during the call return -1.
	 */
	if(sliderid==0){
		if(((ballx-5)<=xposition)){
			if(yposition <= bally && bally <= yposition+Height/2){
				return 1;
			}else if(yposition+Height/2<bally && bally <=yposition+Height){
				return 2;
			}
			else{
				return -1; // No Collision
			}
		}
	}else if(sliderid==2){
		if(((ballx+5)>=xposition)){
			if(yposition <= bally && bally <= yposition+Height/2){
				return 1;
			}else if(yposition+Height/2<bally && bally <=yposition+Height){
				return 2;
			}
			else{
				return -1; // No Collision
			}
		}
	}else if(sliderid==1){
	if(((bally-5)<=yposition)){
		if(xposition <= ballx && ballx <= xposition+Height/2){
			return 1;
		}else if(xposition+Height/2<ballx&& ballx <=xposition+Height){
			return 2;
		}
		else{
			return -1;
		}
	}
}
	else if(sliderid==3){
		if(((bally+5)>=yposition)){
			if(xposition <= ballx && ballx <= xposition+Height/2){
				return 1;
			}else if(xposition+Height/2<ballx&& ballx <=xposition+Height){
				return 2;
			}
			else{
				return -1;
			}
		}
	}
	//-1 is returned that means no collision took place.
	return -1;
}
}
