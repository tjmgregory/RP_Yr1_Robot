package Networking;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import Objects.Sendable.Move;
import Objects.Sendable.RobotInfo;
import Objects.Sendable.SendableObject;
import Objects.Sendable.SingleTask;

public class Client {

	private ClientReceiver receiver;
	
	private UUID name;
	
	public Client(String[] args) {
		name = UUID.randomUUID();
		
		System.out.println("Waiting for Bluetooth connection...");
		BTConnection connection = Bluetooth.waitForConnection();
		System.out.println("OK!");

		DataInputStream in = connection.openDataInputStream();
		DataOutputStream out = connection.openDataOutputStream();
		
		ClientSender.setStream(out);
    	receiver = new ClientReceiver(in);  
	    receiver.start();
	    
	    // Ronan(name);
	    // Luyobmir(whatever he needs);
	    // Lyuobmir.start();
	    
	    RobotInfo info = new RobotInfo(name, new Point(1,1));
	    try {
			ClientSender.send(info);
		} catch (IOException e) {
			out("Failed to send name");
		}
	    
 		while (receiver.isAlive()) {			
 			
 		   	// Goes through all available instructions and executes them.
 			SendableObject comm = null;
 			
 		    while((comm = receiver.popCommand()) != null)
 		    {
 		    	if(comm instanceof Move){
 		    		// Do blah
 		    	}
 		    	else if(comm instanceof SingleTask){
 		    		// Do blah
 		    	}
 		    }
 		}
     	out("MyClient no longer running");
	}

	public static void main(String[] args) {
		new Client(args);
	}
	
	// ********************** HELPER METHODS *********************

	private void out(Object n) {
		System.out.println(""+n);
	}
}
