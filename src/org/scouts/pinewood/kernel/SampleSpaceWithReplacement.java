package org.scouts.pinewood.kernel;

import java.util.ArrayList;
import java.util.Random;

public class SampleSpaceWithReplacement
{
	ArrayList<String> data;
	int rand = 0;
	
	public SampleSpaceWithReplacement(ArrayList<String> newList)
	{
		data = newList;
	}

	
	public boolean isEmpty()
	{
		//answer whether any items remain to be sampled"
		// ^self size = 0
		if (this.size() == 0)
			return true;
		return false;
	}
	
	public String next()
	{
		/* the next element selected is chosen at random from the
		data collection. the index into the data collection is determined
		by obtaining a random number between 0 and 1, normalizing it
		to be within the range of the size of the data collection */

		if ( this.isEmpty() )
		{
			return null;
		}
		else
		{
			rand = (new Random()).nextInt( this.size() );
			return data.get( rand );
//			return "next"; //^data at: (rand next * data size) truncated + 1 
		}
	}

	
	public ArrayList<String> next(int count)
	{
		/* answers and ordered collection containing anInteger
		number of selections from the data collection */
		
		ArrayList<String> nList = new ArrayList<String>();
		
		for (int i = 0; i < count; ++i)
			nList.add(this.next() );
		
		return nList;	
	}
	
	public void setData(ArrayList<String> newList)
	{
		/* the argument, aSquenceableCollection, is the sample space.
		create a random number generator for sampling from the space */

		data = newList;
	}
	
	public int size()
	{
		return data.size();
	}

}
