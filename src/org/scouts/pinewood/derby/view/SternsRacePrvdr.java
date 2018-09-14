package org.scouts.pinewood.derby.view;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.scouts.pinewood.derby.Round;
import org.scouts.pinewood.derby.SternsRace;

/*
 * Sterns Race (viewer implementation)
 */
public class SternsRacePrvdr implements ITreeContentProvider 
{
	private SternsRace model;

	@Override
	public void dispose()
	{
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldRace, Object newRace)
	{
		this.model = (SternsRace) newRace;
	}

	@Override
	public Object[] getElements(Object arg0)
	{
		return model.getRounds().toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement)
	{
		if ( parentElement instanceof Round )
		{
			Round rnd = (Round) parentElement;
			return rnd.toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element)
	{
		return null;
	}

	@Override
	public boolean hasChildren(Object element)
	{
		if ( element instanceof Round )
		{
			return true;
		}
		return false;
	}

}
