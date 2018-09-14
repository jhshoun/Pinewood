package org.scouts.pinewood.derby;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/*
 * Round
 * 
 * special collection that holds all heats for a round
 * extends ArrayList, adds integer identifier
 */
@XStreamAlias("round")
public class Round extends ArrayList<Heat>
{
	/* serial ID */
	private static final long serialVersionUID = -178929925096621014L;

	@XStreamAsAttribute
	private int number;


	// schedule the heats for a group of scouts
	public void scheduleRound(List<Scout> scouts)
	{
		Heat aheat;
		int heatNumber = 0;

// does this work?
Collections.shuffle( scouts );

		while ( !scouts.isEmpty() )
		{
			heatNumber += 1;
			aheat = new Heat();
			aheat.setNumber( heatNumber );
			aheat.setName( this.number, heatNumber );
			aheat.scheduleWith( scouts );
			this.add( aheat );
		}
	}


	// display round name	
	public String toString()
	{
		String s;

		s = String.format("Round: %02d", number);
		return s;
	}

	// accessors
	public int getNumber()
	{
		return number;
	}
	public void setNumber(int number)
	{
		this.number = number;
	}

}
