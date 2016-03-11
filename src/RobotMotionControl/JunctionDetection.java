package RobotMotionControl;
import Networking.ClientSender;
import Objects.Sendable.Move;
import Objects.Sendable.MoveReport;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Behavior;
import lejos.util.Delay;
import rp.config.WheeledRobotConfiguration;
import rp.systems.WheeledRobotSystem;

/**
 * Behaviour for detecting junctions
 */
public class JunctionDetection implements Behavior {
	
	private WheeledRobotSystem system;
	private LightSensor left;
	private LightSensor right;
	private int leftValue;
	private int rightValue;
	private DifferentialPilot pilot;
	private boolean firstJunction=true; 
	//Light value threshold
	private final int threshold = 35;
	//Moves
	private final char LEFT='l';
	private final char RIGHT='r';
	private final char FORWARD='f';
	private final char BACKWARDS='b';
	//Test array 
	//private int[] pattern = {FORWARD,RIGHT,LEFT,LEFT,RIGHT,FORWARD,RIGHT,LEFT,LEFT,FORWARD,FORWARD,LEFT,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,LEFT,FORWARD,LEFT};
	//private int[] pattern = {FORWARD,FORWARD,BACKWARDS,FORWARD,FORWARD,BACKWARDS,FORWARD,FORWARD,BACKWARDS};
	private int[] pattern = {LEFT};
	private int i=0;
	
	private boolean hasCommand = false;
	private Move nextMove;
	

	public JunctionDetection(WheeledRobotConfiguration config,LightSensor left,LightSensor right,double speed){
		this.system= new WheeledRobotSystem(config);
		this.left=left;
		this.right=right;
		pilot=system.getPilot();
		pilot.setTravelSpeed(speed);
	}
	/**
	 * Generates calibrated values
	 * On a black line = 45
	 * Not on a black line = 35
	 */
	private void generateLightValues(){
		leftValue=left.getLightValue();
		rightValue=(int)(right.getLightValue()*0.8);
		if(leftValue>39)
			leftValue=45;
		else
			leftValue=35;
		if(rightValue>39)
			rightValue=45;
		else
			rightValue=35;
		//LCD.drawString(leftValue+" "+rightValue, 0, 0);
	}
	@Override
	public boolean takeControl() {		
		generateLightValues();
		if(leftValue==threshold && rightValue==threshold){
			//if(!firstJunction){
				sendReport(true);
			//}
		//	else
		//		firstJunction=false;
			return true;
			
		}
		return false;
	}
	//Rotates at 180 degrees
	private void moveBackwards(){
		moveLeft();
		moveLeft();
	}
	//Turns left
	private void moveLeft(){
		pilot.rotate(128);
	}
	//Turns right
	private void moveRight(){
		pilot.rotate(-128);
	}
	//Continues going forward
	private void moveForward(){
		pilot.forward();
	}
	//Executes a move
	private void executeMove(int move){
		if(move==LEFT)
			moveLeft();
		else if(move==RIGHT)
			moveRight();
		else if(move==BACKWARDS)
			moveBackwards();
		else
			moveForward();
			
	}
	private MoveReport sendReport(boolean hasMoved){
		LCD.drawString(hasMoved+"", 0, 4);
		 return new MoveReport(hasMoved);
	}

	@Override
	/**	
	 * This is where the magic happens
	 */
	public void action() {
		LCD.drawString("JunctionDetection", 0, 0);
		LCD.drawString(leftValue + " " + rightValue, 0, 2);
		ClientSender.send(new MoveReport(true));
		while(!hasCommand){
			LCD.drawString("its waiting", 0, 6);
			Delay.msDelay(100);
		}
		
		pilot.travel(0.1f);
		
		executeMove(nextMove.getDirection());
		hasCommand = false;
	}
	
	public void giveMove(Move obj){
		nextMove = obj;
		hasCommand = true;
	}

	@Override
	public void suppress() {
		
	}

}