package org.scouts.pinewood.io;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import org.eclipse.swt.widgets.Display;
import org.scouts.pinewood.ui.RaceControl;

/**
 * handles input coming from the serial port,
 * a new line character is treated as the end of a block
 * 
 * --> the block is written to message queue
 *
 */
public class SerialEventReader implements SerialPortEventListener
{
	private InputStream in;
	private byte[] buffer = new byte[1024];
//	private Queue<String> serialQueue;


	// initialize with serial I/O and message queue
	public SerialEventReader(InputStream in)
//	public SerialEventReader(InputStream in, Queue<String> aQ)
	{
		this.in = in;
//		this.serialQueue = aQ;
	}

	public void serialEvent(SerialPortEvent arg0)
	{
		final String message;
		int data;

		try {
			int len = 0;
			while ((data = in.read()) > -1)
			{
				// skip white space
				if (data == 13)
					continue;
				if (data == 10)
					break;
				buffer[len++] = (byte) data;
			}
			
			message = new String(buffer, 0, len);
//			serialQueue.offer( message );

System.out.println( message );

			// i didn't want to do this
			// the race control behaves as a singleton, 
			// accepts a request to update the message queue
			Display.getDefault().syncExec(
			new Runnable()
			{
				public void run()
				{
					RaceControl.master().updateSerialMonitor( message , true);
				}
			}
		);
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
