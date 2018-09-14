package org.scouts.pinewood.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.scouts.pinewood.derby.Heat;
import org.scouts.pinewood.derby.Pack;
import org.scouts.pinewood.derby.Round;
import org.scouts.pinewood.derby.Scout;
import org.scouts.pinewood.derby.SternsRace;
import org.scouts.pinewood.reports.*;


import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;
import com.thoughtworks.xstream.XStream;

public class PinewoodUI
{
	protected Display display;
	protected Shell shlPinewoodDerby;
	protected Label lblStatusLine;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			PinewoodUI window = new PinewoodUI();
			window.open();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open()
	{
		display = Display.getDefault();

		Realm.runWithDefault(
				SWTObservables.getRealm(display),
				new pinewoodApp()
			);
	}


	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents()
	{
		shlPinewoodDerby = new Shell( SWT.SHELL_TRIM & (~SWT.RESIZE) );
		shlPinewoodDerby.setBackground(SWTResourceManager.getColor(113, 118, 96));
		shlPinewoodDerby.setSize(550, 400);
		shlPinewoodDerby.setText("Pinewood Derby");
		shlPinewoodDerby.setLayout(null);
		{		
			Monitor primary = display.getPrimaryMonitor();
			Rectangle bounds = primary.getBounds();
			Rectangle rect = shlPinewoodDerby.getBounds();
			int x = bounds.x + (bounds.width - rect.width) / 2;
			int y = bounds.y + (bounds.height - rect.height) / 2;
			shlPinewoodDerby.setLocation(x, y);
		}
		shlPinewoodDerby.setImages(
			new Image[] { 	
				SWTResourceManager.getImage(
						PinewoodUI.class, "images/app_icon.gif"),
				SWTResourceManager.getImage(
						PinewoodUI.class, "images/app_icon.gif")
			}
		);

		Menu menu = new Menu(shlPinewoodDerby, SWT.BAR);
		shlPinewoodDerby.setMenuBar(menu);

		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");

		Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);

		MenuItem mntmLoadPack = new MenuItem(menu_1, SWT.NONE);
		mntmLoadPack.setText("Load Pack");
		mntmLoadPack.addSelectionListener( new actionItemListener(actionItemListener.LOAD) );

		MenuItem mntmScheduleRace = new MenuItem(menu_1, SWT.NONE);
		mntmScheduleRace.setText("Schedule Race");
		mntmScheduleRace.addSelectionListener( new actionItemListener(actionItemListener.SCHEDULE) );

		new MenuItem(menu_1, SWT.SEPARATOR);

		MenuItem mntmExport = new MenuItem(menu_1, SWT.NONE);
		mntmExport.addSelectionListener( new actionItemListener(actionItemListener.EXPORT) );
		mntmExport.setText("Export Race");

		MenuItem mntmImport = new MenuItem(menu_1, SWT.NONE);
		mntmImport.addSelectionListener( new actionItemListener(actionItemListener.IMPORT) );
		mntmImport.setText("Import Race");

		new MenuItem(menu_1, SWT.SEPARATOR);

		MenuItem mntmExit = new MenuItem(menu_1, SWT.NONE);
		mntmExit.addSelectionListener( new actionItemListener(actionItemListener.EXIT) );
		mntmExit.setText("Exit");

		MenuItem mntmReports_1 = new MenuItem(menu, SWT.CASCADE);
		mntmReports_1.setText("Reports");

		Menu menu_3 = new Menu(mntmReports_1);
		mntmReports_1.setMenu(menu_3);

		MenuItem mntmEntries = new MenuItem(menu_3, SWT.NONE);
		mntmEntries.addSelectionListener(new actionItemListener(actionItemListener.ENTRY_RPT) );
		mntmEntries.setText("Entries");

		MenuItem mntmSchedule = new MenuItem(menu_3, SWT.NONE);
		mntmSchedule.addSelectionListener(new actionItemListener(actionItemListener.SCHEDULE_RPT) );
		mntmSchedule.setText("Schedule");

		MenuItem mntmStandings = new MenuItem(menu_3, SWT.NONE);
		mntmStandings.addSelectionListener(new actionItemListener(actionItemListener.RESULT_RPT) );
		mntmStandings.setText("Results");

		MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
		mntmHelp.setText("Help");

		Menu menu_2 = new Menu(mntmHelp);
		mntmHelp.setMenu(menu_2);

		MenuItem mntmAbout = new MenuItem(menu_2, SWT.NONE);
		mntmAbout.addSelectionListener( new aboutItemListener() );
		mntmAbout.setText("About");

		lblStatusLine = new Label(shlPinewoodDerby, SWT.SHADOW_NONE);
		lblStatusLine.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblStatusLine.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblStatusLine.setBounds(0, 341, 542, 13);
		lblStatusLine.setText("Let's Race!");

		Label lblCar = new Label(shlPinewoodDerby, SWT.NONE);
		lblCar.setBounds(217, 42, 300, 225);
		lblCar.setImage(
				SWTResourceManager.getImage(
						PinewoodUI.class, "images/pinewood_car.png")
			);

		Group grpRaceControl = new Group(shlPinewoodDerby, SWT.NONE);
		grpRaceControl.setBounds(31, 53, 115, 185);
		grpRaceControl.setText("Race Control");
		grpRaceControl.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		grpRaceControl.setBackground(SWTResourceManager.getColor(133, 138, 133));
		grpRaceControl.setFont(SWTResourceManager.getFont("Microsoft Sans Serif", 9, SWT.ITALIC));

		Button btnLoad = new Button(grpRaceControl, SWT.FLAT | SWT.CENTER);
		btnLoad.addSelectionListener(new actionItemListener(actionItemListener.LOAD) );
		btnLoad.setBounds(23, 26, 68, 23);
		btnLoad.setText("Load Pack");

		Button btnEdit = new Button(grpRaceControl, SWT.FLAT | SWT.CENTER);
		btnEdit.addSelectionListener(new editItemListener() );
		btnEdit.setBounds(23, 67, 68, 23);
		btnEdit.setText("Edit");

		Button btnSchedule = new Button(grpRaceControl, SWT.FLAT | SWT.CENTER);
		btnSchedule.addSelectionListener( new actionItemListener(actionItemListener.SCHEDULE) );
		btnSchedule.setBounds(23, 108, 68, 23);
		btnSchedule.setText("Schedule");

		Button btnRace = new Button(grpRaceControl, SWT.FLAT | SWT.CENTER);
		btnRace.addSelectionListener(new raceItemListener() );
		btnRace.setBounds(23, 149, 68, 23);
		btnRace.setText("Race");

	}


	/*
	 * 
	 */
	class pinewoodApp implements Runnable
	{
		public void run()
		{
			// main SWT processing loop
			createContents();
			shlPinewoodDerby.open();
			shlPinewoodDerby.layout();
			while (!shlPinewoodDerby.isDisposed())
			{
				if (!display.readAndDispatch())
				{
					display.sleep();
				}
			}
		}
	}
	
	/*
	 * generic listener
	 * 
	 * calls action routine based on initialization
	 */
	class actionItemListener implements SelectionListener 
	{
		public static final int LOAD = 1;
		public static final int SCHEDULE = 2;
		public static final int EXIT = 3;
		public static final int IMPORT = 20;
		public static final int EXPORT = 21;
		public static final int ENTRY_RPT = 10;
		public static final int SCHEDULE_RPT = 11;
		public static final int RESULT_RPT = 12;
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
			try {
				this.performAction();
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		private void performAction()
		{
			Class[] names;

			names = new Class[]{ 
							Scout.class, 
							Pack.class, 
							SternsRace.class,
							Round.class,
							Heat.class,
							Result.class
					};

			switch (eType)
			{
				// calls file dialog to load the pack data from text
				case LOAD:
					lblStatusLine.setText("Loading pack data ...");
					new OpenPackAction(shlPinewoodDerby).run();
					lblStatusLine.setText("Data loaded.");
					break;

				// calls dialog to get number of rounds,
				// then initializes and schedules the Sterns race 
				case SCHEDULE:
					lblStatusLine.setText("Scheduling race ...");
					new ScheduleRaceAction(shlPinewoodDerby).run();
					lblStatusLine.setText("Ready.");
					break;

				// the reports
				case ENTRY_RPT:
					{
						EntryReport rep = new EntryReport();
						rep.createPdf( Pack.getGlobalPack() );
					}
					break;
				case SCHEDULE_RPT:
					{
						ScheduleReport rep = new ScheduleReport();
						rep.createPdf( Pack.getGlobalPack() );
					}
					break;
				case RESULT_RPT:
					{
						ScoringReport rep = new ScoringReport();
						rep.createPdf( Pack.getGlobalPack() );
					}
					break;


				// XML import, export
				case IMPORT:
					{
						// use XStream serializer
						File result;
						XStream xs;

						try {
							Pack thePack = Pack.getGlobalPack();

							xs = new XStream();
							xs.processAnnotations( names );

							// get pack data first
							result = new File(PinewoodReport.BASE + "XS_pack.xml");
							Pack p = (Pack) xs.fromXML(result);

							thePack.setName( p.getName() );
							thePack.setScouts( p.getScouts() );

							// load the exported race results
							result = new File(PinewoodReport.BASE + "XS_race.xml");
							SternsRace.importRace( (SternsRace) xs.fromXML(result) );

						}
						catch (Exception e) {
							// TODO
						}

					}
					break;

				case EXPORT:
					try {
						// use XStream xml persistence
						File result;
						FileOutputStream fos;

						XStream xstream = new XStream();
						xstream.processAnnotations( names );

						result = new File(PinewoodReport.BASE + "XS_pack.xml");
						fos = new FileOutputStream( result );
						xstream.toXML(Pack.getGlobalPack(), fos);
						fos.close();

						result = new File(PinewoodReport.BASE + "XS_race.xml");
						fos = new FileOutputStream( result );
						xstream.toXML(SternsRace.getRace(), fos);
						fos.close();
					}
					catch (Exception e) {
						// TODO deal with later
						System.out.println(e);
					}
					break;

				// last call
				case EXIT:
					shlPinewoodDerby.close();
					display.dispose();

				default:
					// nothing
			}
		}
	}


	/*
	 * Edit Pack Listener
	 * 
	 * opens editor page for pack data
	 */
	class editItemListener implements SelectionListener 
	{
		public void widgetSelected(SelectionEvent event)
			{ this.editPack(); }
		public void widgetDefaultSelected(SelectionEvent event)
			{ this.editPack(); }
		private void editPack()
		{
			lblStatusLine.setText("Editing pack data ...");

			EntryEditor redit = new EntryEditor(shlPinewoodDerby);
			redit.open();			
		}
	}

	/*
	 * Race Control Listener
	 * 
	 * opens main race management page
	 */
	class raceItemListener implements SelectionListener 
	{
		public void widgetSelected(SelectionEvent event)
			{ this.raceControl(); }
		public void widgetDefaultSelected(SelectionEvent event)
			{ this.raceControl(); }
		private void raceControl()
		{
			lblStatusLine.setText("Go, go, go ...");

			RaceControl rcontrol = new RaceControl(shlPinewoodDerby);
			rcontrol.setPackRace(
						Pack.getGlobalPack(), 
						SternsRace.getRace()
					);
			rcontrol.open();
		}
	}


	

	/*
	 * ABOUT Listener
	 */
	class aboutItemListener implements SelectionListener 
	{
		public void widgetSelected(SelectionEvent event)
			{ this.closeApp(); }
		public void widgetDefaultSelected(SelectionEvent event)
			{ this.closeApp(); }
		private void closeApp()
		{
//			shlPinewoodDerby.close();
			
			
			
			
			
			// pack serialization test			
			try {
				FileOutputStream fileOut = new FileOutputStream("C:/Documents and Settings/q766769/Desktop/pack.dat");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject( Pack.getGlobalPack() );
				out.close();
				fileOut.close();
			} catch ( IOException i ) {
				i.printStackTrace();
			}
			
			
			
			
			
			
			
			
			
			
			
			
//			display.dispose();
		}
	}
}
