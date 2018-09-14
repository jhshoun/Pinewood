package org.scouts.pinewood.ui;

import org.scouts.pinewood.derby.*;
import org.scouts.pinewood.derby.view.*;
import org.scouts.pinewood.io.SerialCommunications;
import org.scouts.pinewood.kernel.*;

import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;

public class RaceControl
{
	//singleton
	private static RaceControl control;
	
	protected Shell pshell;
	protected Shell shlRace;

	// UI components
	private Label lblStatusLine;
	private Combo portCombo;
	private Button cbGateConnect;
	private HeatComposite heatDetail;
	private StyledText commMessages;
	private TreeViewer raceViewer;
	private Tree raceTree;
	private Button btnProjectorDisplay;
	private ProjectorDisplay projector;

	
	// style support
	private Font fLabel;
//	private Font fText;
	private Font fMonitor;
	private Color cEditorBg;
	private Color cWhite;
//	private Color cCyan;
	private Color cGreen;

	// model components
	private Pack thePack;
//	private SternsRace theRace;
	private Gate theGate;
	private Heat currentHeat;
	
	private boolean expando = false; 		// lame, but too lazy to sort out load events
	
	/*
	 * child application model
	 */
	RaceControl(Shell parent)
	{
		pshell = parent;
		control = this;
	}

	public static RaceControl master()
	{
		return control;
	}
	
	/**
	 * Open the window
	 */
	public void open()
	{
		createContents();
		shlRace.open();
		shlRace.layout();
	}

	public void setPackRace(Pack p, SternsRace r)
	{
		thePack = p;
//		theRace = r;
	}

	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents()
	{

		fLabel = SWTResourceManager.getFont("Microsoft Sans Serif", 9, SWT.BOLD);
//		fText = SWTResourceManager.getFont("Lucida Sans", 10, SWT.NORMAL);
		fMonitor = SWTResourceManager.getFont("Lucida Sans", 8, SWT.NORMAL);
		cEditorBg = SWTResourceManager.getColor(135,140,114);
		cWhite = SWTResourceManager.getColor(SWT.COLOR_WHITE);
//		cCyan = SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN);
		cGreen = SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN);
	
		shlRace = new Shell(pshell, SWT.CLOSE | SWT.MIN | SWT.RESIZE | SWT.TITLE);

		shlRace.setBackground(SWTResourceManager.getColor(135,140,114));
		shlRace.setSize(700, 550);
		shlRace.setText("Race Control");
		shlRace.setLayout(null);

		// close the gate connection automatically when
		// window is closed
		// TODO -- might be handled with GATE singleton
		shlRace.addListener(
					SWT.Close, 
					new Listener()
					{
						public void handleEvent(Event event) {
							if (theGate != null){
								theGate.close();
								theGate = null;
							}
							event.doit = true;
						}
					}
			);
		// trap ESC from closing window
		shlRace.addListener(
					SWT.Traverse, 
					new Listener()
					{
						public void handleEvent(Event e) {
							if (e.detail == SWT.TRAVERSE_ESCAPE)
								e.doit = false;
						}
					}
			);


		// status line, used for tracking COMM messages
		// with the gate
		lblStatusLine = new Label(shlRace, SWT.SHADOW_NONE);
		lblStatusLine.setAlignment(SWT.LEFT);
		lblStatusLine.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblStatusLine.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblStatusLine.setBounds(0, 508, 692, 15);
		lblStatusLine.setText("Let's Race!");

		Label lblPack = new Label(shlRace, SWT.BORDER | SWT.CENTER);
		lblPack.setText( thePack.getName() );
		lblPack.setBounds(20, 10, 200, 23);
		lblPack.setBackground( SWTResourceManager.getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW) );
		lblPack.setFont( fLabel );
		
		// heat navigation control
		raceViewer = new TreeViewer(shlRace, SWT.BORDER | SWT.V_SCROLL);
		raceTree = raceViewer.getTree();
		raceTree.setBounds(20, 40, 200, 440);
		
		raceViewer.setContentProvider( new SternsRacePrvdr() );
		raceViewer.setLabelProvider( new SternsRaceLabel() );
		raceViewer.setInput( SternsRace.getRace() );
		raceViewer.expandAll();
		// connect the selection listener
		((Tree) raceViewer.getControl()).addSelectionListener(new treeListener());

		// control remote projector display
		btnProjectorDisplay = new Button(shlRace, SWT.FLAT | SWT.CHECK);
		btnProjectorDisplay.setBackground( cEditorBg );
		btnProjectorDisplay.setBounds(574, 28, 71, 56);
		btnProjectorDisplay.setImage(
				SWTResourceManager.getImage(RaceControl.class, "images/projector_icon.png")
			);
		btnProjectorDisplay.addSelectionListener(new projectorListener() );


		// detail sub-window for monitoring heat results
		Group grpRace = new Group(shlRace, SWT.NONE);
		grpRace.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		grpRace.setBackground( cEditorBg );
		grpRace.setFont(SWTResourceManager.getFont("Microsoft Sans Serif", 9, SWT.ITALIC));
		grpRace.setText("Current Heat");
		grpRace.setLayout(null);
		grpRace.setBounds(245, 180, 420, 300);

		//configure the heat detail composite
		heatDetail = new HeatComposite(grpRace, SWT.NONE);
		heatDetail.setBounds(10,21,400,200);
		heatDetail.setBackgroundMode(SWT.INHERIT_NONE);

		// buttons
		Button btnStartGate = new Button(grpRace, SWT.FLAT | SWT.CENTER);
		btnStartGate.addSelectionListener(new actionItemListener(actionItemListener.START) );
		btnStartGate.setText("Start");
		btnStartGate.setBounds(17, 249, 63, 25);

		Button btnResetGate = new Button(grpRace, SWT.FLAT | SWT.CENTER);
		btnResetGate.addSelectionListener(new actionItemListener(actionItemListener.RESET) );
		btnResetGate.setText("Reset Gate");
		btnResetGate.setBounds(337, 249, 63, 25);

		Button btnGetScore = new Button(grpRace, SWT.FLAT | SWT.CENTER);
		btnGetScore.addSelectionListener(new actionItemListener(actionItemListener.SCORE) );
		btnGetScore.setText("Score");
		btnGetScore.setBounds(177, 249, 63, 25);

		Button btnHalt = new Button(grpRace, SWT.FLAT | SWT.CENTER);
		btnHalt.addSelectionListener(new actionItemListener(actionItemListener.HALT) );
		btnHalt.setText("Stop");
		btnHalt.setBounds(257, 249, 63, 25);

		Button btnResults = new Button(grpRace, SWT.FLAT | SWT.CENTER);
		btnResults.addSelectionListener(new actionItemListener(actionItemListener.RESULTS) );
		btnResults.setText("Results");
		btnResults.setBounds(97, 249, 63, 25);

		// controls to select the I/O port
		// port select (combo-box)
		Label label_1 = new Label(shlRace, SWT.NONE);
		label_1.setForeground( cWhite );
		label_1.setBackground( cEditorBg );
		label_1.setFont( fLabel );
		label_1.setBounds(240, 15, 100, 25);
		label_1.setText("Gate I/O Port");

		portCombo = new Combo(shlRace, SWT.NONE);
		portCombo.setBounds(240, 40, 120, 25);
		portCombo.setItems( SerialCommunications.listPorts().toArray( new String[1] ) );

		commMessages = new StyledText(shlRace, SWT.BORDER | SWT.V_SCROLL);
		commMessages.setFont( fMonitor );
		commMessages.setDoubleClickEnabled(false);
		commMessages.setEditable(false);
		commMessages.setBounds(240, 70, 270, 90);
		commMessages.append("Serial Port\r\n");

		// check box to activate gate serial port
		cbGateConnect = new Button(shlRace, SWT.FLAT | SWT.CHECK);
		cbGateConnect.setText("Gate Connect");
		cbGateConnect.setFont( fLabel );
		cbGateConnect.setBackground( cEditorBg );
		cbGateConnect.setBounds(380, 40, 120, 25);
		cbGateConnect.addSelectionListener(new actionItemListener(actionItemListener.GATE) );

	}


	// update the status line
	private void updateStatus(String text)
	{
		lblStatusLine.setText( text );
	}

	// add message to the serial I/O window
	public void updateSerialMonitor(String m, boolean highlight)
	{
		int end;
		StyleRange sr;

		commMessages.append( m );
		end = commMessages.getCharCount();

		sr = new StyleRange();
		sr.start = end - m.length();
		sr.length = m.length();
		if (highlight){
			sr.foreground = cWhite;
			sr.background = cGreen;
			sr.fontStyle = SWT.ITALIC;
			commMessages.append( "\r\n");
		}
		else{
			sr.fontStyle = SWT.BOLD;
		}

		commMessages.setStyleRange(sr);
		commMessages.setTopIndex(commMessages.getLineCount());

		// passes the message to the result processor
		try {
			if (m.charAt(0) == '<')
				this.postRaceResults( m );
		}
		catch (Exception e)	{
			// trap exceptions
			System.out.println("message string error");
			//e.printStackTrace();
		}
	}

	// post the gate timings to the UI
	private void postRaceResults(String m)
	{
		String raw;
		String[] results;

		raw = m.substring(1, m.length()-1);
		// return if this isn't numeric
		if ( !Character.isDigit( raw.charAt(1) ) )
			return;
		
		// make sure there are 4 time results
		results = raw.split(" ");
		if (results.length != 4)
			return;
		
		heatDetail.loadResults( results );
	}
	
	private void score()
	{
		heatDetail.postResults();
		raceViewer.refresh(true);
	
		if (projector != null)
			projector.standings( currentHeat );

		updateStatus( "Finished." );
	}



	/******************************************************************************
	 * inner classes
	 */

	/*
	 * Projector Display Listener
	 * 
	 * opens the projection display window
	 */
	class projectorListener implements SelectionListener 
	{
		public void widgetSelected(SelectionEvent event)
			{ this.projectorControl(); }
		public void widgetDefaultSelected(SelectionEvent event)
			{ this.projectorControl(); }
		private void projectorControl()
		{

			// checked, then open the display
			if (btnProjectorDisplay.getSelection()) {
				lblStatusLine.setText("Projector On");
				projector = new ProjectorDisplay(shlRace);
				projector.open();			
			}
			// unchecked, close it down
			else {
				lblStatusLine.setText("Projector Off");
				projector.close();
				projector = null;
			}
		}
	}

	/*
	 * generic button listener
	 * 
	 * calls action routine based on initialization
	 */
	class actionItemListener implements SelectionListener 
	{
		public static final int START = 1;
		public static final int GATE = 2;
		public static final int SCORE = 3;
		public static final int RESET = 4;
		public static final int HALT = 5;
		public static final int RESULTS = 6;
		private int eType;

		public actionItemListener(int type)
		{
			super();
			eType = type;
		}

		public void widgetSelected(SelectionEvent event)
			{ this.theAction(); }
		public void widgetDefaultSelected(SelectionEvent event)
			{ this.theAction(); }
		private void theAction()
		{
			switch (eType)
			{
				// reset the race
				case RESET:
					heatDetail.loadResults( new String[] {"30000", "30000", "30000", "30000"} );
					if (theGate == null){
						updateStatus( "No GATE comm selected." );
						break;
					}
					theGate.reset();
					updateStatus( "Reset." );
					break;

				// start the race
				case START:
					if (theGate == null){
						updateStatus( "No GATE comm selected." );
						break;
					}
					theGate.start();
					updateStatus( "Start ..." );

					// fast blur cars
					if (projector != null)
						projector.racing();
					break;

				// stop running race
				case HALT:
					if (theGate == null){
						updateStatus( "No GATE comm selected." );
						break;
					}
					theGate.halt();
					updateStatus( "" );
					break;

				// gets gate connection and initializes
				case GATE:
					// get selected port name
					int i = portCombo.getSelectionIndex();
					String pname;

					if (i < 0) {
						cbGateConnect.setSelection(false);
						updateStatus("No comm port selected");
						return;
					}
					// checked, then connect the gate
					if (cbGateConnect.getSelection()) {
						pname = portCombo.getItem( i );
						theGate = new Gate( pname.split(" ")[0] );
						theGate.connect( RaceControl.this );
						if ( theGate.isOK() ){
							updateStatus("Port " + pname + " connected.");
							theGate.reset();
						}
						else {
							cbGateConnect.setSelection(false);
							updateStatus("Gate communication error.");
						}
					}
					// unchecked, drop the connection
					else {
						if (theGate == null)
							break;
						theGate.close();
						theGate = null;
						updateStatus("Gate communication closed.");
					}
					break;

				// get the results
				case RESULTS:
					if (theGate != null)
						theGate.results();
					break;

					// get the results
				case SCORE:
					try {
						score();
					}
					catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;

				default:
					// nothing
			}
			return;
		}

	}

	// hands selected heat to the control component	
	class treeListener extends SelectionAdapter
	{
		@Override
		public void widgetSelected(SelectionEvent e)
		{
			TreeItem item = (TreeItem) e.item;

			// deal with a single heat
			if (item.getData() instanceof Heat) {
				String lanes = "";

				currentHeat = (Heat) item.getData();
				heatDetail.setHeat( currentHeat );

				// if the projector is on, show the entries
				if (projector != null)
					projector.entries( currentHeat );

				// pass slots to gate
				if (theGate == null){
					updateStatus( "No GATE comm selected." );
					return;
				}
				for (int i = 0; i < Heat.defaultNumberofLanes; ++i)
				{
					Result r = currentHeat.getLanes().get(i);
					if (r.getScout() == null)
						lanes += "0";
					else
						lanes += "1";
				}
				theGate.init(lanes);
				
				updateStatus( "Ready." );
			}

			// manage a round expand, collapse
			else {
				if (item.getItemCount() > 0) {
					heatDetail.setHeat( new Heat() );
					// this deals with the initial page load exception
					if ( expando )
						item.setExpanded(!item.getExpanded());
					else
						expando = true;
				}
				updateStatus( "-----" );
			}
		}
	}
}
