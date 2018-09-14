package org.scouts.pinewood.kernel;

import org.scouts.pinewood.io.SerialCommunications;
import org.scouts.pinewood.ui.RaceControl;

/*
 * Gate
 * 
 * handles all the communication with the finish
 * line gate (RXTX serial library)
 * 
 * this is a singleton (Class method implementation seems lazy)
 */
public class Gate
{
	boolean connectionStatus = false;
	String portName;
	SerialCommunications serial;
	RaceControl ui;
	
	public Gate()
	{
		// TODO Auto-generated constructor stub
	}

	public Gate(String port)
	{
		portName = port;
	}

	// connect to the serial port
	public int connect(RaceControl raceUI)
	{
		int rc;
		
		// keep track of the UI
		this.ui = raceUI;
		
		serial = new SerialCommunications();
		serial.setEventRead( true );
		rc = serial.connect( portName );
		if (rc == 0){
			connectionStatus = true;
		}
		else{
			serial = null;
			connectionStatus = false;
		}
		
		return rc;
	}

	// close the comm port
	public void close()
	{
		serial.disconnect();
		serial = null;
		connectionStatus = false;
	}

	// answers the status of the serial connection
	public boolean isOK()
	{
		return connectionStatus;
	}

	// reset the gate status
	public void reset()
	{
		xmit("RESET");
	}

	// stop a running race, actual
	// msg does not matter, any send
	// stops the gate
	public void halt()
	{
		xmit("HALT");
	}

	// start the race sequence
	public void start()
	{
		xmit("RACE");
	}

	// initialize the gate lanes
	public void init(String lanes)
	{
		xmit("INIT " + lanes);
	}

	// get the results
	public void results()
	{
		xmit("RESULT");
	}

	// gate transaction,
	// grab any results
	public void xmit(String s)
	{
		ui.updateSerialMonitor(s, false);
		serial.send(s);
//		return track();
	}

	// log any serial output to the race
	// control tracking window
//	public String track()
//	{
//		String s;
//		
//		while ( true )
//		{
//			s = serial.receive();
//			if (s == null)
//				break;
//			ui.updateSerialMonitor(s, true);
//		}
//		return s;
//	}

}
