package org.scouts.pinewood.ui;

import org.scouts.pinewood.derby.Heat;
import org.scouts.pinewood.derby.Pack;
import org.scouts.pinewood.derby.Result;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class HeatComposite extends Composite
{
	private Label[] lblLanes = new Label[ Heat.defaultNumberofLanes ];
	private Label[] lblCarNo = new Label[ Heat.defaultNumberofLanes ];
	private Text[] txtScouts = new Text[ Heat.defaultNumberofLanes ];
	private Text[] txtTime = new Text[ Heat.defaultNumberofLanes ];
	private Text[] txtPosition = new Text[ Heat.defaultNumberofLanes ];

	private Heat currentHeat;
	
	Font fLabel = SWTResourceManager.getFont("Microsoft Sans Serif", 9, SWT.NORMAL);
	Font fText = SWTResourceManager.getFont("Lucida Sans", 9, SWT.NORMAL);
	Color cWhite = SWTResourceManager.getColor(SWT.COLOR_WHITE);
	Color cCyan = SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN);


	public HeatComposite(Composite parent, int style)
	{
		super(parent, SWT.BORDER);
		setBackground( cCyan );
		setLayout(null);
		
		Label bar1 = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		bar1.setBounds(10, 45, 360, 2);
		Label bar2 = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		bar2.setBounds(10, 95, 360, 2);
		Label bar3 = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		bar3.setBounds(10, 145, 360, 2);

		Label bar4 = new Label(this, SWT.SEPARATOR | SWT.VERTICAL);
		bar4.setBounds(60, 10, 2, 180);

		for (int i = 0; i < Heat.defaultNumberofLanes; ++i)
		{
			
			// lane label
			lblLanes[i] = new Label(this, SWT.NONE);
			lblLanes[i].setForeground( cWhite );
			lblLanes[i].setBackground( cCyan );
			lblLanes[i].setFont( fLabel );
			lblLanes[i].setBounds(10, 10 + (i*50), 50, 20);
			lblLanes[i].setText("Lane " + (i+1));

			// car number
			lblCarNo[i] = new Label(this, SWT.NONE | SWT.CENTER);
			lblCarNo[i].setForeground( cWhite );
			lblCarNo[i].setBackground( cCyan );
			lblCarNo[i].setFont( fLabel );
			lblCarNo[i].setBounds(70, 10 + (i*50), 30, 20);
			lblCarNo[i].setText("##");

			// entry names
			txtScouts[i] = new Text(this, SWT.BORDER | SWT.SINGLE);
			txtScouts[i].setEditable(false);
			txtScouts[i].setFont(fText);
			txtScouts[i].setBounds(110, 10 + (i*50), 130, 20);

			// actual time reported from gate
			txtTime[i] = new Text(this, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
			txtTime[i].setFont(fText);
			txtTime[i].setBounds(260, 10 + (i*50), 70, 20);

			txtPosition[i] = new Text(this, SWT.BORDER | SWT.SINGLE | SWT.CENTER);
			txtPosition[i].setEditable(false);
			txtPosition[i].setFont(fText);
			txtPosition[i].setBounds(350, 10 + (i*50), 20, 20);
		}

	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}


	// assigns the heat being run
	// loads detail into UI
	public void setHeat(Heat theHeat)
	{
		currentHeat = theHeat;
		
		for (int i = 0; i < Heat.defaultNumberofLanes; ++i)
		{
			String sid;
			Result r = currentHeat.getLane(i);
			
			sid = r.getScout();
			if (sid == null){
				lblCarNo[i].setText("##");
				txtScouts[i].setText("--");
				txtTime[i].setText("--");
				txtPosition[i].setText( "0" );

				txtScouts[i].setVisible(false);
				txtTime[i].setVisible(false);
			}
			else {
				lblCarNo[i].setText("#" + sid);
				txtScouts[i].setText( 
								Pack.getScout(sid).getFirstName() + " "
								+ Pack.getScout(sid).getLastName()
						);
				txtTime[i].setText("" + r.getTime());
				txtPosition[i].setText( "" + r.getPosition() );

				txtScouts[i].setVisible(true);
				txtTime[i].setVisible(true);
			}
			
		}
	}

	// 
	// puts the time strings into the display
	public void loadResults(String[] stimes)
	{
		for (int i = 0; i < Heat.defaultNumberofLanes; ++i)
		{
			txtTime[i].setText( stimes[i] );
		}
	}

	// 
	// loads detail into UI
	public void postResults()
	{
		int[] times = new int[4];
		
		// get times from screen
		for (int i = 0; i < Heat.defaultNumberofLanes; ++i)
		{
			try {
				times[i] = Integer.parseInt( txtTime[i].getText() );
			}
			catch (Exception e){
				times[i] = 20000;
			}
		}
		currentHeat.setFinishTimes( times );

		for (int i = 0; i < Heat.defaultNumberofLanes; ++i)
		{
			String sid;
			Result r = currentHeat.getLane(i);
			
			sid = r.getScout();
			if (sid == null){
				txtTime[i].setText("30000");
				txtPosition[i].setText("-");
			}
			else {
				txtTime[i].setText( "" + r.getTime() );
				txtPosition[i].setText( "" + r.getPosition() );
			}
			txtTime[i].setVisible(true);
			
		}
	}

}
