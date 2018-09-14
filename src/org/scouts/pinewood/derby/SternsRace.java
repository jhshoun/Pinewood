package org.scouts.pinewood.derby;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("race")
public class SternsRace
{
	//singleton pattern
	private static SternsRace globalRace;

	public static int NumRounds = 4;

	private int count;					// number of rounds
	private ArrayList<Round> rounds;


	protected SternsRace()
	{
		// nothing to do
	}

	// singleton accessors
	// global race management
	public synchronized static SternsRace getRace()
	{
		if (globalRace == null)
		{
			globalRace = new SternsRace();
		}
		return globalRace;
	}
	public synchronized static void importRace(SternsRace r)
	{
		globalRace = r;
	}

	// routine for calculating speed from int time
	public static String speed(int time)
	{
		return String.format("%5.1f", ( 800000.0d / (double) time ) );
	}
	// routine for calculating speed from float time
	public static String speed(double time)
	{
		return String.format("%5.1f", ( 800000.0d / time ) );
	}
	// routine for calculating average speed
	// from int time & number of rounds
	public static String speed(int time, int rounds)
	{
		return SternsRace.speed( (double) time / rounds );
	}
	

	// answers a collection of all results from the race
	public ArrayList<Result> allResults()
	{
		ArrayList<Result> rlist;

		rlist = new ArrayList<Result>();
		// go through each round
		for (Round r : rounds)
		{
			// go through each heat
			for (Heat h : r)
			{
				// if complete get the results
				if ( h.heatComplete() )
					rlist.addAll( h.results() );
			}
		}	

		return rlist;
	}

	// answers the collection of heats for specified round
	public ArrayList<Heat> heatsForRound(int roundNum)
	{
		return rounds.get(roundNum);
	}

	// schedule the race based on a number of rounds
	public void scheduleRace(int numberOfRounds)
	{
		List<Scout> group;
		Round newRound;

		count = numberOfRounds;
		rounds = new ArrayList<Round>();

		Pack.getGlobalPack().clearRaceHistory();

		for (int i = 0; i < numberOfRounds; ++i)
		{
			group = Pack.getGlobalPack().getScouts();
			newRound = new Round();
			newRound.setNumber( i+1 );
			newRound.scheduleRound( group );
			rounds.add(i, newRound);
		}
	}

	// calculate standings, ordered list based on speed
	// (lowest elapsed time)
	public List<Record> calculateStandings()
	{
		List<Record> standings;

		//	standings array holds stats for each entry
		standings = new ArrayList<Record>();
		for ( Scout s : Pack.getGlobalPack().getScouts() )
		{
			standings.add( new Record(s.getCarNumber()) );
		}

		// iterate through results and update
		for ( Result res : this.allResults() )
		{
			if ( !res.isRun() )
				continue;
			for ( Record rec : standings )
			{
				if (rec.scoutId == res.getScout()){
					rec.heats += 1;
					rec.runTime += res.getTime();
				}
			}
		}

		// order the finishers
		Collections.sort( standings );

		return standings;
	}


	// display string for a race
	public String toString()
	{
		String rep;

		rep = "Pack: " + Pack.getGlobalPack().getName() + " [STERNS]";
		return rep;
	}


	/*
	 * accessors
	 */
	public int getCount()
	{
		return count;
	}
	public void setCount(int count)
	{
		this.count = count;
	}

	public ArrayList<Round> getRounds()
	{
		return rounds;
	}
	public void setRounds(ArrayList<Round> rounds)
	{
		this.rounds = rounds;
	}

	/*
	 * Record
	 * 
	 * holds cumulative stats for a given entry
	 */
	public class Record implements Comparable<Record>
	{
		public String scoutId;
		public int runTime;
		public int heats;

		public Record(String sid)
		{
			scoutId = sid;
			runTime = 0;
			heats = 0;
		}

		@Override
		public int compareTo(Record o)
		{
			return (this.runTime - o.runTime);
		}
		public String speed()
		{
			return SternsRace.speed(runTime, heats);
		}
	}

}
