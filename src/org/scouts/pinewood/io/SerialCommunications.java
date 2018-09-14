package org.scouts.pinewood.io;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Serial Communications
 * 
 * this implementation makes use of the
 * SerialPortEventListener to avoid polling
 * 
 */
public class SerialCommunications
{
	private int baudRate = 19200;
	private boolean eventDrivenRead = false;
	private CommPortIdentifier portId;
	private CommPort commPort;
	private SerialPort serialPort;
	
	private InputStream in;
	private OutputStream out;

//	private Queue<String> fifo;
	
	private SerialWriter runWriter;
//	private Thread readThread;
	private Thread writeThread;

	public SerialCommunications()
	{
		super();
	}

	// close the serial connection
	public void disconnect() 
	{
		// remove the write thread
		runWriter.halt();
		synchronized (runWriter){
			runWriter.notify();
		}

		serialPort.close();
	}

	// open serial connection
	public int connect(String portName) 
	{

		try {
			// init the port, check status
			portId = CommPortIdentifier.getPortIdentifier(portName);
			if ( portId.isCurrentlyOwned() ) {
				System.out.println("Error: Port " + portName + " is currently in use");
				return -1;
			}

			// try to open
			commPort = portId.open("PINEWOOD", 1000);

		}
		catch (NoSuchPortException e){
			System.out.println("Error: No such port.");
			return -1;
		}
		catch (PortInUseException e1) {
			System.out.println("Error: Port " + portName + " is currently in use");
			return -1;
		}

		// wrong port type?		
		if ( !(commPort instanceof SerialPort) ){
			commPort.close();
			System.out.println("Error: Serial I/O only.");
			return -1;
		}

		try {
			// time to get 'serious'
			serialPort = (SerialPort) commPort;
			serialPort.setSerialPortParams(
								baudRate, 
								SerialPort.DATABITS_8,
								SerialPort.STOPBITS_1, 
								SerialPort.PARITY_NONE
							);

			in = serialPort.getInputStream();
			out = serialPort.getOutputStream();

			// initialize the writer object thread
			// writer posts messages to serial output stream
			runWriter = new SerialWriter(out);
			try {
				// set up writer -> serial port
				writeThread = new Thread( runWriter, "PWSerialIO" );
				writeThread.start();
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			// initialize the input reader
			// event based to move messages into queue
			if ( eventDrivenRead ) {
				this.attachSerialEventListener();
			}	

		}
		catch (Exception e) {
			commPort.close();
		}
		return 0;
	}

	// send string over serial connection
	public int send(String msg) 
	{
		synchronized (runWriter) {
			runWriter.writeMessage( msg );
			runWriter.notify();
		}
		return 0;
	}

	// get any pending messages from queue
//	public String receive()
//	{
//		String s;
//
//		s = fifo.poll();
//		return s;
//	}

	// attach event read receiver
	public int attachSerialEventListener() 
	{
//		fifo = new ConcurrentLinkedQueue<String>();
		try {
//			serialPort.addEventListener( new SerialEventReader( this.in, fifo) );
			serialPort.addEventListener( new SerialEventReader( this.in) );
			serialPort.notifyOnDataAvailable(true);
		}
		catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}


	/*
	 * accessors
	 */
	public void setBaudRate(int baudRate)
	{
		this.baudRate = baudRate;
	}
	public void setEventRead(boolean event)
	{
		this.eventDrivenRead = event;
	}


	/*
	 * class support functions
	 * access to the available comm ports
	 */
	// list of available ports
	public static List<String> listPorts()
	{
		String portName;
		List<String> ports;
		
		ports = new ArrayList<String>();
		
		@SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
		while ( portEnum.hasMoreElements() )
		{
			CommPortIdentifier portId = portEnum.nextElement();
			portName = portId.getName();
			portName += " - " +  getPortTypeName(portId.getPortType());

			// check and see if port is available, mark with ***
			if ( portId.getPortType() == CommPortIdentifier.PORT_SERIAL )
			{
				if ( portId.isCurrentlyOwned() )
					portName += " ***";
				try {
					CommPort aPort = portId.open("PSCAN", 50);
					aPort.close();
				}
				catch (PortInUseException e) {
					portName += " ***";
				}
				catch (Exception e) {
					portName += " ERR";
				}

			}

			ports.add( portName );
		}
		
		return ports;
	}

	// answer string for port type
	static String getPortTypeName ( int portType )
	{
		switch ( portType )
		{
			case CommPortIdentifier.PORT_I2C:
				return "I2C";
			case CommPortIdentifier.PORT_PARALLEL:
				return "Parallel";
			case CommPortIdentifier.PORT_RAW:
				return "Raw";
			case CommPortIdentifier.PORT_RS485:
				return "RS485";
			case CommPortIdentifier.PORT_SERIAL:
				return "Serial";
			default:
				return "unknown";
		}
	}

}
