package org.scouts.pinewood.derby.view;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.SWTResourceManager;
import org.scouts.pinewood.derby.Heat;
import org.scouts.pinewood.derby.Round;
import org.scouts.pinewood.ui.PinewoodUI;

public class SternsRaceLabel extends StyledCellLabelProvider
{
	private static final Image CAR = getImage("FormulaOneCar.png");
	private static final Image FLAG = getImage("FinishFlag.png");


	public void update(ViewerCell cell)
	{
		Object element = cell.getElement();
		StyledString text = new StyledString();
		
		if (element instanceof Round)
		{
			Round rnd = (Round) element;

			text.append( rnd.toString() );
			cell.setImage(FLAG);
			text.append(" (" + rnd.size() + ") ", StyledString.COUNTER_STYLER);
		}

		else 			// must be a heat
		{
			Heat ht = (Heat) element;
			text.append( ht.toString() );
			cell.setImage(CAR);
		}

		cell.setText(text.toString());
		cell.setStyleRanges(text.getStyleRanges());
		super.update(cell);
	}


	// helper Method to load the images
	private static Image getImage(String file)
	{
		return SWTResourceManager.getImage(
				PinewoodUI.class, "/org/scouts/pinewood/ui/images/" + file);
	}

}
