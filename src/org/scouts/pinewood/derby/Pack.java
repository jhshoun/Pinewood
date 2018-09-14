package org.scouts.pinewood.derby;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import org.scouts.pinewood.kernel.AbstractModelObject;

/*
 * Pack
 * 
 * primary model for the pinewood derby application
 * contains the scouts (entries)
 * 
 * [singleton pattern]
 */
@XStreamAlias("pack")
public class Pack extends AbstractModelObject
{

	private static Pack globalPack;

	@XStreamAsAttribute
	private String name;

	private HashMap<String, Scout> scouts = new HashMap<String, Scout>();

	// constructors
	//
	// create an empty pack
	protected Pack()
	{
		this.setName( "the Pack" );
	}
	//
	// create a new pack, with name, den list, and scouts
	protected Pack(String pack, List<Scout> scoutList)
	{
		this.setName( pack );
		this.addScoutGroup( scoutList );
	}

	// singleton accessors
	// global pack management
	public synchronized static Pack getGlobalPack()
	{
		if (globalPack == null)
		{
			globalPack = new Pack();
		}
		return globalPack;
	}
	public static Scout getScout(String carNum)
	{
		return Pack.getGlobalPack().scouts.get( carNum );
	}

	// read text data file and update the global Pack instance
	public synchronized static int readParseFileNamed(String fname)
	{
		BufferedReader flatFile;
		Scout newScout;
		String line;
		String[] fields;	

		// create the new pack
		globalPack = new Pack();
		globalPack.setName( (new File(fname)).getName().replaceFirst("[.][^.]+$", "") );

		// start processing the CSV file
		try
		{

			flatFile = new BufferedReader( new FileReader(fname) );

			while ((line = flatFile.readLine()) != null) {

				fields = line.split("\t");

				newScout = new Scout();
				newScout.setCarNumber( fields[0] );
				newScout.setLastName( fields[1] );
				newScout.setFirstName( fields[2] );
				newScout.setDen( fields[3] );
				newScout.setRank( fields[4] );

				globalPack.addScout( newScout );
			}
			flatFile.close();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();

			globalPack.setName(" ** file open error **");
			return -1;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();

			globalPack.setName(" ** file I/O error **");
			return -1;
		}
		catch (NullPointerException e)
		{
			globalPack.setName("No file selected.");
			return -1;
		}
		catch (Exception e)
		{
			// last chance to identify a problem
			globalPack.setName("File Format Error");
			return -1;
		}

		return 0;
	}



/*

--------------------------------
--------------------------------
----------------------------------------------------
----------------------------------------------------
----------------------------------------------------
reportScoutDistribution
	"displays information regarding lane and opponent assigment"

	| reportText |
	reportText := TextStream on: (String new: 100).
	reportText emphasis: #( #bold #large ).
	reportText nextPutAll: ( 'Race Stats: ', GlobalPack pack ).
	reportText
		nextPut: Character lf;
		nextPut: Character lf.

	reportText
		emphasis: #( #bold #large #underline );
		nextPutAll: 'Scout';
		nextPut: Character tab;
		nextPutAll: 'Lanes';
		nextPut: Character tab;
		nextPutAll: 'Opponents';
		nextPut: Character lf.

	reportText
		emphasis: #( #none ).
	self scouts do: [ :ea | ea reportDistribution: reportText ].

	PinewoodTextView open: (ValueHolder new value: (reportText contents) )
		label: 'Scout Race Distribution'
		icon: (Icon constantNamed: #workspace)
		extent: 400@300
----------------------------------------------------
reportStandings
	"shows current standings"



	results := (self calculateStandingsBy: type).
	results do: [ :r |
		points := r value.
		scout := r key.
		line := ''.
		line := line, (PrintConverter print: (points at: 1) formattedBy: '#####').
		line replaceAll: (Character space) with: $0.
		line := line, '	', scout printString, ' [', scout den, ']'.
		line := line, '	(', (points at: 2) printString, ' for ',  (points at: 3) printString, ')'.
		line := line, '	', ((points at: 2) * 800  / (points at: 3)) printString.
		
		reportText
			emphasis: #( #small );
			nextPutAll: line;
			nextPut: Character lf.
	].

----------------------------------------------------
----------------------------------------------------


 */


	// adds a new scout to the pack
	public void addScout(Scout aScout)
	{
		scouts.put(aScout.getCarNumber(), aScout);
	}
	// removes a scout from the pack
	public void removeScout(String carNum)
	{
		scouts.remove( carNum );
	}

	// adds a group of scouts to the pack
	public void addScoutGroup(List<Scout> theScouts)
	{
		for (Scout e : theScouts )
			this.addScout(e);
	}


	// removes race history for all scouts in pack
	public void clearRaceHistory()
	{
		for ( Scout s : scouts.values() )
			s.clearHistory();
	}

	// default printOn
	public String toString()
	{
		String s;

		s = "Pack: " + this.getName();
		return s;
	}

	/*
	 * accessors
	 */
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}

	// list of all entries
	public List<Scout> getScouts()
	{
		return new ArrayList<Scout>( scouts.values() );
	}
	public void setScouts(List<Scout> theScouts)
	{
		this.addScoutGroup( theScouts );
	}
	
	// list of entries (scouts) from a group (den/pack)
	public List<Scout> getDenScouts(String den)
	{
		List<Scout> dscouts;

		dscouts = new ArrayList<Scout>();
		for (Scout s : scouts.values())
		{
			if (s.getDen().compareTo(den) == 0)
				dscouts.add(s);
		}

		return dscouts;
	}

	// list of the registered dens
	public List<String> getDens()
	{
		HashSet<String> dens = new HashSet<String>();
		List<String> denList = new ArrayList<String>();

		for (Scout s : scouts.values())
		{
			dens.add( s.getDen() );
		}
		denList.addAll(dens);
		Collections.sort( denList );

		return denList;
	}
	

}
