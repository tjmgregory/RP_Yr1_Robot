                                                                                                                                                                                                                                                                                                                                                                                                                         
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import rp.config.RobotConfigs;
import rp.config.WheeledRobotConfiguration;

//Main class
public class Initiator {


	public static void main(String[] args){
		
		//robot speed is subject to change
		final double speed = 0.18;
		//Follows a straight black line
		Behavior line = new LineFollow((WheeledRobotConfiguration) RobotConfigs.CASTOR_BOT,new LightSensor(SensorPort.S1),new LightSensor(SensorPort.S4),speed);
		//Detects junctions and makes one move per junction
		Behavior junction = new JunctionDetection((WheeledRobotConfiguration) RobotConfigs.CASTOR_BOT,new LightSensor(SensorPort.S1),new LightSensor(SensorPort.S4),speed);
		
		Arbitrator arby = new Arbitrator(new Behavior[] {line,junction});
		arby.start();
	}
}

