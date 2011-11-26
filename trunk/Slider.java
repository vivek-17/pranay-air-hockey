//Has to thread safe
public class Slider {
private volatile int  xposition;
private  volatile int yposition;
//Once fixed ,length of the slider will not change.
private  final int length;
//Once fixed ,slider id will not change.
private final  int sliderid;
private static int slidercount=0;
//==================================================================================
public Slider(int xposition,int yposition,int length,int sliderid){
	if(slidercount<4){
		this.xposition=xposition;
		this.yposition=yposition;
		this.sliderid=sliderid;
		slidercount++;
		this.length=length;
	}else{
		this.xposition=-1;
		this.yposition=-1;
		this.sliderid=-1;
		this.length=-1;
	}
}
//====================================================================================
public void moveSlider(int mouseposition){
	/*Check id of the slider
	 * if 1 and 3 --->Need to move on y axis.
	 * if 2 and 4 --->Need to move on x-axis.
	 * slider should only move when mouse lies in the (slider position + length of the slider and its width if any)
	 * Also slider should not move if the slider is at the corner of the board
	 */
	if(sliderid==1||sliderid==3){
		if( (mouseposition+length)<600 &&(mouseposition)>0){
			yposition=mouseposition;
			System.out.println("hello: "+ mouseposition);
		}
	}
	if(sliderid==2 || sliderid==4){
		if((mouseposition+length)<600 &&(mouseposition)>0){
			xposition=mouseposition;
		}
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
return length;
}
//=========================================================================================
int returnCollisionType(int ballx,int bally){
	/*
	 * Find the balls collision with the slider.
	 * Divide the slider in 5 parts,call them region 1,2,3,4,5.
	 * Return the region number in which the ball collides.
	 * If no collision has taken place during the call return -1.
	 */
	if(sliderid==1){
		if(((ballx-5)<=xposition)){
		if(yposition <= bally && bally <= yposition+9){
			return 1;
		}else if(yposition+9<bally && bally<=yposition+18){
			return 2;
		}else if(yposition+18<bally &&  bally <=yposition+22){
			return 3;
		}else if(yposition+22<bally&& bally <=yposition+31){
			return 4;
		}
		else{
			return 5;
		}
		}
	}else if(sliderid==3){
		if(((ballx+5)>=xposition)){
			if(yposition <= bally && bally <= yposition+9){
				return 1;
			}else if(yposition+9<bally && bally<=yposition+18){
				return 2;
			}else if(yposition+18<bally &&  bally <=yposition+22){
				return 3;
			}else if(yposition+22<bally&& bally <=yposition+31){
				return 4;
			}
			else{
				return 5;
			}
			}
	}else if(sliderid==2){
	if(((bally-5)<=yposition)){
		if(xposition <= ballx && ballx <= xposition+9){
			return 1;
		}else if(xposition+9<ballx && ballx<=xposition+18){
			return 2;
		}else if(xposition+18<ballx &&  ballx <=xposition+22){
			return 3;
		}else if(xposition+22<ballx&& ballx <=xposition+31){
			return 4;
		}
		else{
			return 5;
		}
		}
}
	else if(sliderid==4){
		if(((bally+5)>=yposition)){
			if(xposition <= ballx && ballx <= xposition+9){
				return 1;
			}else if(xposition+9<ballx && ballx<=xposition+18){
				return 2;
			}else if(xposition+18<ballx &&  ballx <=xposition+22){
				return 3;
			}else if(xposition+22<ballx&& ballx <=xposition+31){
				return 4;
			}
			else{
				return 5;
			}
			}
	}
	//-1 is returned that means no collision took place.
	return -1;
}
}
