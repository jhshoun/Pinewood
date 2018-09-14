package org.scouts.pinewood.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.ClosedByInterruptException;

public class SerialWriter implements Runnable
{
	private volatile boolean alive = true;
	private volatile String message;
	private OutputStream out;

	public SerialWriter(OutputStream out)
	{
		this.out = out;
		this.message = "";
	}

	public void writeMessage(String text)
	{
		this.message = String.format("%s\n", text);
	}
	
	public void halt()
	{
		alive = false;
	}
	
	public void run()
	{
		char[] cm;

		while (alive)
		{
			try {
				// send the current text 
				cm = message.toCharArray();
				for (int i = 0; i < cm.length; ++i)
					this.out.write( cm[i] );
				
				// wait for next text
				synchronized (this) {
					this.wait();
				}
			}
			catch (InterruptedException e) {
				// allows to process new message
			}
			catch (ClosedByInterruptException e) {
				alive = false;
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
