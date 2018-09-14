package org.scouts.pinewood.derby;

//import java.io.Serializable;
import java.util.Collection;

import org.apache.commons.collections.Bag;
import org.apache.commons.collections.bag.HashBag;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/*
 * SCOUT
 * 
 * holds data for single entry, 
 * also tracks the lanes run and opponents used
 * to bid for open slots
 */
@XStreamAlias("scout")
public class Scout
{

	@XStreamAsAttribute
	private String carNumber = "00"; 		// this is the primary identifier
	private String firstName = "---";
	private String lastName = "---";
	private String den = "---";
	private String rank = "---";

	private HashBag lanes;
	private HashBag opponents;

	// default constructor
	public Scout()
	{
		this.clearHistory();
	}

	// clone constructor
	public Scout(Scout another)
	{
		this.copy( another );
		this.clearHistory();
	}

	// updater, copies values from another scout into
	// the current one
	public void copy(Scout another)
	{
		this.carNumber = another.carNumber;
		this.firstName = another.firstName;
		this.lastName = another.lastName;
		this.den = another.den;
		this.rank = another.rank;
	}

	public void clearHistory()
	{
		lanes = new HashBag();
		opponents = new HashBag(); 
	}

	// manage the opponents collection
	public Bag opponents()
	{
		return opponents;
	}

	public void addOpponent(String scout)
	{
		opponents.add( scout );
	}

	public void addOpponents(Collection<String> scouts)
	{
		// the scout is in the heat list,
		// needs to exclude self from bidding
		opponents.addAll( scouts );
	}

	// lane management
	public void addRaceLane(int l)
	{
		lanes.add( new Integer(l) );	
	}

	// bid
	// this controls how opponents and lane assignments are done
	//
	// answers a bid representing the desire to race specified opponents
	// in a given lane - low bid wins
	public int bidOn(Heat ht, int l)
	{
		int bid;

		// increase bid 10 for every time raced in the lane
		bid = lanes.getCount( new Integer(l) ) * 10;

		// increase bid for each time one of the opponents was raced
		for (String ea : ht.scouts())
		{
//			bid += Math.pow( 5, this.opponents.getCount(ea) );
			if ( !ea.equals(carNumber) ) {
				bid += this.opponents.getCount(ea);
			}
		}

		return bid;
	}


	// gives lane, opponent distribution stats for this entry
	public String reportDistribution()
	{
		String report;
		int numOpponents, totOpponents;
		float pctOpponents;

		report = "\t";

		for (Object s : lanes.uniqueSet().toArray() )
		{
			report += String.format("  -  %2d", lanes.getCount( (Integer) s ) );
		}

		numOpponents = this.opponents.uniqueSet().size();
		totOpponents = Pack.getGlobalPack().getScouts().size();
		pctOpponents = (numOpponents * 100) / (totOpponents - 1);
		report += String.format("\t  %3d   ( %5.1f )\n", numOpponents, pctOpponents);

		return report;
	}

	// display label for scout object
	public String toString()
	{
		String s;

		s = this.getCarNumber() + " - ";
		s += this.getFirstName() + " " + this.getLastName();

		return s;
	}

	// sort method
	public int compareTo(Scout other)
	{
		int rc;

		rc = this.getLastName().compareToIgnoreCase( other.getLastName() );
		if (rc == 0)
			rc = this.getFirstName().compareToIgnoreCase( other.getFirstName() );

		return rc;
	}

	/*
	 * accessors
	 */
	public String getCarNumber()
	{
		return carNumber;
	}
	public void setCarNumber(String carNumber)
	{
		this.carNumber = carNumber;
	}

	public String getFirstName()
	{
		return firstName;
	}
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getDen()
	{
		return den;
	}
	public void setDen(String den)
	{
		this.den = den;
	}

	public String getRank()
	{
		return rank;
	}
	public void setRank(String rank)
	{
		this.rank = rank;
	}


}
