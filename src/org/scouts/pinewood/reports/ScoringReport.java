package org.scouts.pinewood.reports;


import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.scouts.pinewood.derby.Pack;
import org.scouts.pinewood.derby.Scout;
import org.scouts.pinewood.derby.SternsRace;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
 
/**
 * scoring, results
 * 
 * final results, ordered finishers
 */
public class ScoringReport extends PinewoodReport
{

	/** path to the resulting PDF file */
	public static final String RESULT = BASE
			+ Pack.getGlobalPack().getName() 
			+ " scores.pdf";

	// tabs
	private Chunk tab1 = new Chunk(new VerticalPositionMark(), 10);
	private Chunk tab2 = new Chunk(new VerticalPositionMark(), 40);
	private Chunk tab3 = new Chunk(new VerticalPositionMark(), 150);
	private Chunk tab4 = new Chunk(new VerticalPositionMark(), 220);


	/**
	 * Creates a PDF document.
	 * @param filename the path to the new PDF document
	 * @throws    DocumentException 
	 * @throws    IOException 
	 */
	public void createPdf(Pack packdata)
	{
		// initialize PDF document
		Document document = new Document();
		try
		{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(RESULT));
			
			// set up header, footer events and areas
			writer.setPageEvent( new HeaderFooter() );
			writer.setBoxSize("hpf", new Rectangle(36, 54, 559, 788));
			
			document.open();
			writer.setCompressionLevel(0);
			
			this.writePdf(packdata, document);
		}
		catch (Exception e)
		{
			// don't really care what type
			e.printStackTrace();
		}
		// end report 
		document.close();
	}

	public void writePdf(Pack tp, Document doc) 
					throws DocumentException, MalformedURLException, IOException
	{
		URL urlCar = getClass().getResource("/org/scouts/pinewood/ui/images/derby-car.jpg");
		Image car = Image.getInstance(urlCar);
		car.setAlignment(Image.RIGHT | Image.UNDERLYING);
		car.scalePercent(60);
		doc.add( car );

		// header
		Paragraph title = new Paragraph();
		title.add( new Chunk("Race Results:", TITLE) );
		title.add( new Chunk("   " + tp.getName()) );
		title.setAlignment(Element.ALIGN_LEFT);
		doc.add(title);

		Paragraph line = new Paragraph();
		line.setSpacingBefore(15);
		line.setSpacingAfter(15);
		line.add(
				new LineSeparator(2, 75, null, Element.ALIGN_LEFT, 0)
			);
		doc.add(line);

		// create list of scouts in den
		List scoutlist = new List();
		ListItem scoutitem;
		scoutlist.setListSymbol( new Chunk("") );

		scoutitem = new ListItem( header() );
		scoutlist.add(scoutitem);
		for ( SternsRace.Record rec : SternsRace.getRace().calculateStandings() )
		{
			scoutitem = new ListItem( itemScout(rec) );
			scoutlist.add(scoutitem);
		}

		doc.add(scoutlist);

	}

	private Phrase header()
	{
		Phrase aLabel = new Phrase(); 
		
		aLabel.add( tab1 );
		aLabel.add( new Chunk("Car", HEADER) );
		aLabel.add( tab2 );
		aLabel.add( new Chunk("Racer", HEADER) );
		aLabel.add( tab3 );
		aLabel.add( new Chunk("Speed", HEADER) );
		aLabel.add( tab4 );
		aLabel.add( new Chunk("Time", HEADER) );

		return aLabel;
	}

	private Phrase itemScout(SternsRace.Record rec)
	{
		Phrase aScout = new Phrase(); 
		Scout s;
		String stats;
		
		s = Pack.getScout( rec.scoutId );

		aScout.add( tab1 );
		aScout.add( new Chunk(s.getCarNumber(), NORMAL_BOLD) );
		aScout.add( tab2 );
		aScout.add( new Chunk(s.getLastName(), NORMAL) );
		aScout.add( new Chunk(", " + s.getFirstName(), NORMAL) );
		aScout.add( tab3 );
		aScout.add( new Chunk( rec.speed() , NORMAL) );
		aScout.add( tab4 );
		
		stats = String.format(
					"%5.3f  (%d for %6.3f)", 
					(float) rec.runTime / (float) rec.heats / 1000.0f, 
					rec.heats, 
					(float) rec.runTime / 1000.0f
				);
		aScout.add( new Chunk(stats, NORMAL) );

		return aScout;
	}

}
