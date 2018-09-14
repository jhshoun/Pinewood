package org.scouts.pinewood.reports;

 
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.scouts.pinewood.derby.Pack;
import org.scouts.pinewood.derby.Scout;
 
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
 
/**
 * entry reports
 * 
 * list of race race entries, grouped by den/pack
 */
public class EntryReport extends PinewoodReport
{

	/** path to the resulting PDF file */
	public static final String RESULT = BASE 
				+ Pack.getGlobalPack().getName() 
				+ " roster.pdf";

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
//		writer.setInitialLeading(32);
			
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
		URL urlCar = getClass().getResource("/org/scouts/pinewood/ui/images/pinewood_car.png");
		Image car = Image.getInstance(urlCar);
		car.setAlignment(Image.RIGHT | Image.UNDERLYING);
		car.scalePercent(50);
		doc.add( car );

		
//		Chunk chunk = new Chunk(
//	              "This is a sentence which is long " + i + ". ");
//	        paragraph.add(chunk);

		Paragraph title = new Paragraph();
		title.add( new Chunk("Race Entries:", TITLE) );
		title.add( new Chunk("   " + tp.getName()) );
		title.setAlignment(Element.ALIGN_LEFT);
		doc.add(title);

		List lstDen = new List(List.UNORDERED);
		lstDen.setListSymbol("");

		// loop over scouts for each den
		for (String den : tp.getDens())
		{
			// create list item for each scout
			ListItem ditem = new ListItem( String.format("Group: %s", den), HEADER );

			// create list of scouts in den
			List scoutlist = new List();
			scoutlist.setListSymbol( new Chunk("") );

			for ( Scout scout : tp.getDenScouts(den) )
			{
				ListItem scoutitem = new ListItem( itemScout(scout) );
				scoutlist.add(scoutitem);
			}
			ditem.add(scoutlist);
			lstDen.add(ditem);
		}
		doc.add(lstDen);

	}

	private Phrase itemScout(Scout s)
	{
		Chunk tab1 = new Chunk(new VerticalPositionMark(), 10);
		Chunk tab2 = new Chunk(new VerticalPositionMark(), 40);
		Chunk tab3 = new Chunk(new VerticalPositionMark(), 150);
		Phrase aScout = new Phrase(); 
		
		aScout.add( tab1 );
		aScout.add( new Chunk(s.getCarNumber(), NORMAL_BOLD) );
		aScout.add( tab2 );
		aScout.add( new Chunk(s.getLastName(), NORMAL) );
		aScout.add( new Chunk(", " + s.getFirstName(), NORMAL) );
		aScout.add( tab3 );
		aScout.add( new Chunk("[ " + s.getRank() + " ]", NORMAL) );

		return aScout;
	}

}
