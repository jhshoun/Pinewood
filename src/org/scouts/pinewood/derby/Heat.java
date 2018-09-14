package org.scouts.pinewood.derby;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.scouts.pinewood.kernel.SampleSpaceWithoutReplacement;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("heat")
public class Heat
{
	public static int defaultNumberofLanes = 4;

	@XStreamAsAttribute
	private int number;				// heat number
	private String name;			// heat name, round + heat number
	@XStreamAsAttribute
	private int timeFast;
	@XStreamAsAttribute
	private int timeSlow;

	private ArrayList<Result> lanes;


	// constructor
	public Heat()
	{
		super();

		// set up the results
		lanes = new ArrayList<Result>(Heat.defaultNumberofLanes);
		for (int i = 0; i < Heat.defaultNumberofLanes; ++i)
			lanes.add( new Result() );
	}


	/*
	 * main scheduling routine base on bids
	 * from a pool of scouts not in the current round
	 */

	// schedules self using scouts in collection - evenly divide 
	// remaining scouts over final heats
	public void scheduleWith(List<Scout> scouts)
	{
		int entrants;
		SampleSpaceWithoutReplacement lanePool;


		lanePool = new SampleSpaceWithoutReplacement( 
						new ArrayList<String>( Arrays.asList("1", "2", "3", "4") ) 
					);

		entrants = spreadEntrants( scouts );
		for (int i = 0; i < entrants; ++i)
		{
			assignLaneNumber(Integer.parseInt(lanePool.next()),  scouts);
		}

		// have everyone update their opponent list
		this.updateOpponents();
	}

	// selects scout from group to run in lane based on bid
	private void assignLaneNumber(int aLane, List<Scout> scouts)
	{
		int lowBid;
		int currentBid;
		Scout lowBidder;		// scout car number
		SampleSpaceWithoutReplacement pool;
		ArrayList<String> bidders = new ArrayList<String>();

		if ( scouts.isEmpty() )
			return;
		lowBid = 10000;

		for (Scout racer : scouts)
		{
			currentBid = racer.bidOn(this, aLane);
			if (currentBid < lowBid) {
				bidders = new ArrayList<String>();
				bidders.add( racer.getCarNumber() );
				lowBid = currentBid;
			}
			else {
				if (currentBid == lowBid)
					bidders.add( racer.getCarNumber() );
			}
		}
		pool = new SampleSpaceWithoutReplacement(bidders);
		lowBidder = Pack.getScout( pool.next() );

		this.placeScout( lowBidder, aLane);

		scouts.remove( lowBidder );
	}


	// allocate remaining scouts over the 4 lanes
	// keeps from getting a heat with a single car
	private int spreadEntrants(List<Scout> scouts)
	{
		int size = scouts.size();

		if (size == 9) return 3;
		if (size == 6) return 3;
		if (size == 5) return 3;
		if (size > 4) return 4;
		return size;
	}


	// post finishing results
	public void setFinishTimes(int[] times)
	{
		for (int i = 0; i < Heat.defaultNumberofLanes; ++i)
		{
			Result r = this.getLane(i);
			r.setTime( times[i] );
		}
		this.orderFinishers();
	}

	// assign the lane finishing positions based on elapsed time
	// track the faster & slowest times
	public void orderFinishers()
	{
		int timeFast = 100;
		int timeSlow = 0;

		// calculate position by counting number
		// or results faster an slower than me
		for (Result r1 : this.lanes)
		{
			int faster = 0;
			for (Result r2 : this.lanes)
			{
				faster += (r2.getTime() < r1.getTime()) ? 1 : 0;
			}
			r1.setPosition( faster + 1 );

			// set stats
			if ( r1.getTime() < timeFast )
				timeFast = r1.getTime();
			if ( (r1.getTime() > timeSlow) && (r1.getTime() < 10000) )
				timeSlow = r1.getTime();
		}

	}



	// answers the result for the specified lane.
	// creates an empty entry if none present
	public Result resultInLane(int laneNumber)
	{
		Result r;

		r = lanes.get(laneNumber - 1);
		return r;
	}

	// puts the scout in the designated lane
	// tells scout to add laneNumber to his list
	public void placeScout(Scout aScout, int laneNumber)
	{
		aScout.addRaceLane( laneNumber );
		this.resultInLane(laneNumber).setScout(aScout.getCarNumber());
	}


	// answers a collection of the scouts in the heat
	public ArrayList<String> scouts()
	{
		String s;
		ArrayList<String> sl = new ArrayList<String>();

		for ( Result re : this.results() )
		{
			s = re.getScout();
			if (s  != null )
				sl.add( s );
		}

		return sl;
	}

	// tell each scout who he is racing in this heat
	public void updateOpponents()
	{
		for ( String sid : this.scouts() )
		{
			Scout s = Pack.getScout( sid );
			s.addOpponents( this.scouts() );
		}
	}

	// answers a collection of the results
	public ArrayList<Result> results()
	{
		return this.getLanes();
	}

	// answers the lanes 
	// array list of lanes, empty lanes have no entry
	public ArrayList<Result> getLanes()
	{
		return lanes;
	}
	// the result in a lane
	public Result getLane(int i)
	{
		return lanes.get( i );
	}

	// answers the fastest time
	public int first()
	{
		return getTimeFast();
	}
	// answers the fastest time
	public int last()
	{
		return getTimeSlow();
	}

	// tests to see if there are results for all entries - if so the heat is over"
	boolean heatComplete()
	{
		for ( Result r : this.getLanes() )
		{
			if( !r.isRun() )
				return false;
		}
		return true;
	}

	// erases all results for a complete re-run
	public void resetTimes()
	{
		for ( Result re : this.getLanes() )
			re.setTime(30000);
	}


	// display string for a heat
	public String toString()
	{
		String s = "";

		s += this.getName();
		if ( this.heatComplete() )
			s += "  *OVER*' ";
		return s;
	}

	// answers the percentage position in field based on time
	public float pctField(int aTime)
	{
		float r;

		r = (this.last() - aTime) / (this.last() - this.first());
		return r;
	}

	// answer a string showing the heat number and lane assignments
	public String reportHeat()
	{
		String rep = "";

		for ( Result r : this.getLanes() )
		{
			rep += "\t";
			// TODO this really needs to get the print for the entry,
			// not just the entry #
			rep += r.getScout().toString();
		}
		return rep;
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

	public String getName()
	{
		return name;
	}
	public void setName(int roundNo, int heatNo)
	{
		this.name = String.format( "Rnd %d - Heat %d", roundNo, heatNo );
	}


	public int getTimeFast()
	{
		return timeFast;
	}

	public int getTimeSlow()
	{
		return timeSlow;
	}

}
