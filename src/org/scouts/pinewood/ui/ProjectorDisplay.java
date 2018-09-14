package org.scouts.pinewood.ui;

import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;

import org.scouts.pinewood.derby.Heat;
import org.scouts.pinewood.derby.Pack;
import org.scouts.pinewood.derby.SternsRace;


public class ProjectorDisplay
{
	protected Shell pshell;
	protected Shell shlDisplay;

	// UI components
	private LaneComposite l1;
	private LaneComposite l2;
	private LaneComposite l3;
	private LaneComposite l4;


	/*
	 * child application model
	 */
	ProjectorDisplay(Shell parent)
	{
		pshell = parent;
	}

	/**
	 * display management
	 */
	// open window
	public void open()
	{
		createContents();
		shlDisplay.open();
		shlDisplay.layout();
	}
	// close window
	public void close()
	{
		if ( !shlDisplay.isDisposed() )
			shlDisplay.close();
	}


	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents()
	{
		boolean full = false;
		int opts;
		
		// get available monitors,
		// position on secondary if possible
		Monitor[] monitors = pshell.getDisplay().getMonitors();
		opts = (monitors.length > 1) ? SWT.RESIZE : SWT.RESIZE | SWT.TITLE;
		shlDisplay = new Shell(pshell, opts);
		shlDisplay.addListener(
				SWT.Traverse, 
				new Listener()
				{
					public void handleEvent(Event e) {
						if (e.detail == SWT.TRAVERSE_ESCAPE)
							e.doit = false;
					}
				}
		);
		
		if (monitors.length > 1){
			Monitor secondary = shlDisplay.getDisplay().getMonitors()[1];
			shlDisplay.setLocation(secondary.getClientArea().x+10, secondary.getClientArea().y+10);
			shlDisplay.setMaximized(true);
			full = true;
		}
		else
			shlDisplay.setSize(800, 500);
		shlDisplay.setBackground(SWTResourceManager.getColor(0, 0, 0));
		shlDisplay.setLayout(new FillLayout(SWT.VERTICAL));

		// add the lane displays
		l1 = new LaneComposite(shlDisplay, SWT.NONE, full);
//		l1.setVisible( false );
		l2 = new LaneComposite(shlDisplay, SWT.NONE, full);
		l2.setVisible( false );
		l3 = new LaneComposite(shlDisplay, SWT.NONE, full);
		l3.setVisible( false );
		l4 = new LaneComposite(shlDisplay, SWT.NONE, full);
		l4.setVisible( false );
	}

	// show the racers in their lanes	
	public void entries(Heat ht)
	{
		LaneComposite[] lanes = { l1, l2, l3, l4 };
		String sn;
		int lc = 0;
		
		for (LaneComposite l : lanes )
		{
			sn = ht.getLane(lc).getScout();
			if (sn == null)
				l.setVisible(false);
			else {
				l.setStart(Pack.getScout(sn).toString(), sn, ""+(lc+1) );
				l.setVisible(true);
			}
			lc += 1;
		}

	}

	// show cars in motion (blurred)	
	public void racing()
	{
		LaneComposite[] lanes = { l1, l2, l3, l4 };

		for (LaneComposite l : lanes )
			l.setRacing();
	}

	// show finishing position, speed -- highlight winner	
	public void standings(Heat ht)
	{
		LaneComposite[] lanes = { l1, l2, l3, l4 };
		String sn;
		String position;
		String speed;
		int lc = 0;

		for (LaneComposite l : lanes )
		{
			sn = ht.getLane(lc).getScout();
			if (sn != null) {
				
				switch ( ht.getLane(lc).getPosition() )
				{
					case 1:
						position = "1st";
						break;
					case 2:
						position = "2nd";
						break;
					case 3:
						position = "3rd";
						break;
					case 4:
						position = "4th";
						break;
					default:
						position = "DNF";
				}
				speed = SternsRace.speed( ht.getLane(lc).getTime() );
				l.setFinish( position, speed, (ht.getLane(lc).getPosition() == 1) );
			}
			lc += 1;
		}
	}

}
