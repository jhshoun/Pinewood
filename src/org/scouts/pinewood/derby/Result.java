package org.scouts.pinewood.derby;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("result")
public class Result
{
	@XStreamAsAttribute
	private int position = 0;		// finishing position
	@XStreamAsAttribute
	private String scout;			// entry
	private int time = 30000;		// runtime in milliseconds


	// compares which result is first
	// used for ordering
	public boolean compare(Result re)
	{
		return ( this.getTime() <= re.getTime() );
	}

	// has this result been run?
	// if less than 18 seconds then no
	public boolean isRun()
	{
		if (scout == null)
			return true;

		return ( this.getTime() <= 18000 );
	}

	// display string for a result
	@Override
	public String toString()
	{
		String[] suffixes = {"#st", "#nd", "#rd", "#th", "#th", "#th", "#th", "#th", "#th"};
		String rep;

		rep = Integer.toString( this.getPosition() );

		if ( this.getPosition() == 0 )
			rep += "##";
		else
			rep += suffixes[ this.getPosition() ];

		rep +=  "  - " + Integer.toString( this.getTime() );
		return rep;
	}


	// accessors
	public int getPosition()
	{
		return position;
	}
	public void setPosition(int position)
	{
		this.position = position;
	}

	public String getScout()
	{
		return scout;
	}
	public void setScout(String scout)
	{
		this.scout = scout;
	}

	public int getTime()
	{
		return time;
	}
	public void setTime(int time)
	{
		this.time = time;
	}

}
