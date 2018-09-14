package org.scouts.pinewood.reports;

 
import java.io.FileOutputStream;
import java.io.IOException;

import org.scouts.pinewood.derby.Heat;
import org.scouts.pinewood.derby.Pack;
import org.scouts.pinewood.derby.Round;
import org.scouts.pinewood.derby.SternsRace;
 
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
 
/**
 * entry reports
 * 
 * list of race race entries, grouped by den/pack
 */
public class ScheduleReport extends PinewoodReport
{

	/** path to the resulting PDF file */
	public static final String RESULT = BASE
			+ Pack.getGlobalPack().getName() 
			+ " schedule.pdf";


	/**
	 * Creates a PDF document.
	 * @param filename the path to the new PDF document
	 * @throws    DocumentException 
	 * @throws    IOException 
	 */
	public void createPdf(Pack packdata)
	{
		// initialize PDF document
		Document document = new Document( PageSize.LETTER.rotate() );
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
					throws DocumentException, IOException
	{
		
		Paragraph title = new Paragraph();
		title.add( new Chunk("Race Entries:", TITLE) );
		title.add( new Chunk("   " + tp.getName()) );
		title.setAlignment(Element.ALIGN_LEFT);
		doc.add(title);

		// make sure there is a schedule
		if ( SternsRace.getRace().getRounds() == null )
			return;
		
		// loop over rounds
		for (Round round : SternsRace.getRace().getRounds() )
		{
			doc.add( new Paragraph( String.format("Round %d", round.getNumber()), HEADER) );
			doc.add( around(round) );
		}

	}

	private PdfPTable around(Round r) throws DocumentException
	{
		PdfPTable tbl;
		PdfPCell cell;

		// init table
		tbl = new PdfPTable(5);
		tbl.setSpacingBefore(8);
		tbl.setSpacingAfter(5);

		tbl.setWidths(new int[]{2, 3, 3, 3, 3});

		tbl.setKeepTogether(true);
		tbl.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
		tbl.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

		cell = new PdfPCell(new Phrase( "" ));
		cell.setGrayFill( 0.75f );
		tbl.addCell(cell);

		cell = new PdfPCell(new Phrase("- 1 -"));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setGrayFill( 0.90f );
		tbl.addCell(cell);
		cell = new PdfPCell(new Phrase("- 2 -"));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setGrayFill( 0.90f );
		tbl.addCell(cell);
		cell = new PdfPCell(new Phrase("- 3 -"));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setGrayFill( 0.90f );
		tbl.addCell(cell);
		cell = new PdfPCell(new Phrase("- 4 -"));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setGrayFill( 0.90f );
		tbl.addCell(cell);

		// go through all the heats for the round
		for ( Heat h : r )
		{
			cell = new PdfPCell(new Phrase( h.toString(), NORMAL ));
			tbl.addCell(cell);

			// get entry from each lane
			// TODO make lane count dynamic
			for (int i = 1; i <= Heat.defaultNumberofLanes; ++i)
			{
				String s;
				s = h.resultInLane(i).getScout();
				
				if (s == null)
					cell = new PdfPCell(new Phrase( "---", NORMAL ));
				else
					cell = new PdfPCell(new Phrase( Pack.getScout(s).toString(), NORMAL ));
				cell.setPadding( 5.0f );
				tbl.addCell(cell);
			}
		}

		return tbl;
	}

}
