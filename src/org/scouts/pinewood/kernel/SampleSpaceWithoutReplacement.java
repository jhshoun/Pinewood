package org.scouts.pinewood.kernel;

import java.util.ArrayList;

public class SampleSpaceWithoutReplacement extends SampleSpaceWithReplacement
{

	public SampleSpaceWithoutReplacement(ArrayList<String> newList)
	{
		super(newList);
	}

	public String next()
	{
		String o = super.next();
		data.remove(o);
		return o;
	}
}
