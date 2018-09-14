package org.scouts.pinewood.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.wb.swt.SWTResourceManager;


/*
 * this holds projector display for a single lane, 
 * shows the lane #, entry, speed, finishing position
 * with graphic of car relative to field
 */
public class LaneComposite extends Composite
{
	private Group grpLanes;
	private CLabel lblScout;
	private CLabel lblSpeed;
	private CLabel lblPosition;
	private Canvas canvasCarGraphic;
	
	private static Image im0 = SWTResourceManager.getImage(PinewoodUI.class, "images/cars/racer_x.png");
	private static Image im1 = SWTResourceManager.getImage(PinewoodUI.class, "images/cars/racer_b.png");
	private static Image im2 = SWTResourceManager.getImage(PinewoodUI.class, "images/cars/racer_r.png");
	private static Image im3 = SWTResourceManager.getImage(PinewoodUI.class, "images/cars/racer_y.png");
	private static Image im9 = SWTResourceManager.getImage(PinewoodUI.class, "images/cars/racer_blur.png");
	
	private boolean first = false;
	private boolean finish = false;
	private boolean racing = false;
	
	//ui components
	private Font fLabel;
	private Font fText;
	private Color cWhite = SWTResourceManager.getColor(SWT.COLOR_WHITE);
	private Color cBlack = SWTResourceManager.getColor(SWT.COLOR_BLACK);
//	private Color cdCyan = SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN);
//	private Color cGreen = SWTResourceManager.getColor(SWT.COLOR_GREEN);
	private Color cdGreen = SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN);
	private Image myCar;

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents sub-classing of SWT components
	}

	public LaneComposite(Composite parent, int style, boolean full)
	{
		super(parent, SWT.NONE);
		myCar = im0;
		this.createContent(parent, style, full);
	}
	
	private void createContent(Composite parent, int style, boolean full)
	{
		int fsize = 24;
		
		if (full)
			fsize = 32;

		// TODO ----
		// calculate the best font size
//		fLabel = SWTResourceManager.getFont("Microsoft Sans Serif", 24, SWT.NORMAL | SWT.BOLD);
		fLabel = SWTResourceManager.getFont("Arial Black", fsize, SWT.NORMAL);
		fText = SWTResourceManager.getFont("Microsoft Sans Serif", 12, SWT.BOLD | SWT.ITALIC);

		// set lane composite layout
		setLayout(new FillLayout(SWT.HORIZONTAL));

		// container for lane data
		grpLanes = new Group(this, SWT.NONE);
		grpLanes.setFont( fText );
		grpLanes.setText("Lane #");
		grpLanes.setLayout(new FormLayout());

		// finish position
		lblPosition = new CLabel(grpLanes, SWT.BORDER | SWT.CENTER);
		lblPosition.setFont( fLabel );
		lblPosition.setText("--");
		// -----
		FormData fd_lblPosition = new FormData();
		fd_lblPosition.top = new FormAttachment(0, 5);
		fd_lblPosition.bottom = new FormAttachment(40, 5);
		fd_lblPosition.left = new FormAttachment(0, 5);
		fd_lblPosition.right = new FormAttachment(15, -10);
		lblPosition.setLayoutData(fd_lblPosition);

		// finishing speed
		lblSpeed = new CLabel(grpLanes, SWT.BORDER | SWT.CENTER);
		lblSpeed.setFont(fLabel);
		lblSpeed.setText( "0.0" );
		// -----
		FormData fd_lblSpeed = new FormData();
		fd_lblSpeed.top = new FormAttachment(0, 5);
		fd_lblSpeed.bottom = new FormAttachment(lblPosition, 0, SWT.BOTTOM);
		fd_lblSpeed.left = new FormAttachment(75, 10);
		fd_lblSpeed.right = new FormAttachment(100, -5);
		lblSpeed.setLayoutData(fd_lblSpeed);

		// driver name
		lblScout = new CLabel(grpLanes, SWT.BORDER | SWT.CENTER);
		lblScout.setFont(fLabel);
		lblScout.setText("driver");
		// -----
		FormData fd_lblScout = new FormData();
		fd_lblScout.top = new FormAttachment(0, 5);
		fd_lblScout.bottom = new FormAttachment(lblPosition, 0, SWT.BOTTOM);
		fd_lblScout.left = new FormAttachment(lblPosition, 10, SWT.RIGHT);
		fd_lblScout.right = new FormAttachment(lblSpeed, -10, SWT.LEFT);
		lblScout.setLayoutData(fd_lblScout);
		
		// car graphic
		canvasCarGraphic = new Canvas(grpLanes, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
		canvasCarGraphic.addListener( SWT.Paint, new carListener() );
		// -----
		FormData fd_lblCarGraphic = new FormData();
		fd_lblCarGraphic.top = new FormAttachment(lblPosition, 5, SWT.BOTTOM);
		fd_lblCarGraphic.bottom = new FormAttachment(100, -5);
		fd_lblCarGraphic.left = new FormAttachment(0, 5);
		fd_lblCarGraphic.right = new FormAttachment(100, -5);
		canvasCarGraphic.setLayoutData(fd_lblCarGraphic);
	}

	// populate the labels
	public void setStart(String scout, String carNo, String lane)
	{
		this.finish = false;
		this.first = false;
		this.racing = false;

		// pick a car color
		// put a random car in the field
		switch ( (int)(Math.random() * 3) )
		{
			case 0:
				myCar = im1;
				break;
			case 1:
				myCar = im2;
				break;
			default:
				myCar = im3;
				break;
		}

		grpLanes.setText("Lane " + lane);
		lblScout.setText("#" + scout);
		lblSpeed.setText("--");
		lblPosition.setText("");
		
		colorize( cBlack, cWhite);
//		lblPosition.setVisible(false);

		canvasCarGraphic.redraw();
	}

	// race mode
	public void setRacing()
	{
		this.finish = false;
		this.first = false;
		this.racing = true;

		canvasCarGraphic.redraw();
	}

	// post finish results, highlight winning lane
	public void setFinish(String position, String speed, boolean isFirst)
	{
		this.first = isFirst;
		this.finish = true;
		this.racing = false;

		lblPosition.setText( position );
		lblPosition.setVisible(true);
		lblSpeed.setText( speed );

		if (isFirst){
			colorize( cWhite, cdGreen );
		}
		else {
			colorize( cBlack, cWhite );
		}
		canvasCarGraphic.redraw();
	}

	// post finish results, highlight winning lane
	private void colorize(Color fg, Color bg)
	{
		lblScout.setBackground( bg );
		lblScout.setForeground( fg );
		lblPosition.setBackground( bg );
		lblPosition.setForeground( fg );
		lblSpeed.setBackground( bg );
		lblSpeed.setForeground( fg );
	}


	/*
	 * inner classes, listeners
	 */
	private class carListener implements Listener
	{

		@Override
		public void handleEvent(Event e)
		{
			GC gc = e.gc;
			Rectangle rect = canvasCarGraphic.getClientArea();			
			Device device = gc.getDevice();
			Pattern pattern = null;
			Image theCar;

			if (racing)
				theCar = im9;
			else
				theCar = myCar;

			if (first){
				pattern = new Pattern(device, 0, 0, 
						rect.width, rect.height,
						cdGreen, cWhite);
			}
			else {
				pattern = new Pattern(device, 0, 0, 
						rect.width, rect.height,
						cWhite, cBlack);
			}
			gc.setBackgroundPattern(pattern);
			gc.fillRectangle(rect);
			if (pattern != null)
				pattern.dispose();
			
//			gc.setAntialias(SWT.ON);
//			gc.setInterpolation(SWT.HIGH);

			// scale image size based on available display
			int th, tw;
			int zoom;

			th = rect.height;
			tw = (int) Math.round( (th + 0f) / theCar.getBounds().height * theCar.getBounds().width );
			zoom = rect.width - tw - 5;
			if (finish){
				float sp;
				float offset;

				sp = Float.parseFloat( lblSpeed.getText() );
				offset = (sp - 150f) / 70f;
				if (offset > 0)
					zoom -= (int) (offset * zoom);
			}
			// ready to draw the car position
			gc.drawImage(theCar, 
							0, 0, theCar.getBounds().width, theCar.getBounds().height,
							zoom, 0, tw, th);
		}
	}

}
