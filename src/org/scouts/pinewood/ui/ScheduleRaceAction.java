package org.scouts.pinewood.ui;


import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import org.scouts.pinewood.derby.SternsRace;

public class ScheduleRaceAction
{
	private Shell shell;

	// common constructor
	public ScheduleRaceAction(Shell s)
	{
		shell = s;
	}

	// file dialog
	public void run()
	{
		SternsRace newRace;
		
		int count;

		// create dialog
		InputDialog dialog = new InputDialog(shell, 
									"Race Length", 
									"Number of Race Rounds?", 
									Integer.toString(SternsRace.NumRounds), 
									null
								);
		if ( dialog.open() != Window.OK)
		{
			System.out.println("schedule canceled");
		}

		try {
			count = Integer.parseInt( dialog.getValue() );
			// let's build the schedule and tell the pack
			newRace = SternsRace.getRace();
			newRace.scheduleRace( count );
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
