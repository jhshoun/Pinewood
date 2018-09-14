package org.scouts.pinewood.reports;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.itextpdf.text.*;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

class HeaderFooter extends PdfPageEventHelper
{
	public static final Font HEADER = new Font(FontFamily.HELVETICA, 9, Font.BOLD);	
	public static final Font FOOT = new Font(FontFamily.TIMES_ROMAN, 8, Font.ITALIC);
	private Date now;


	/** header. */
	int pagenumber;

	/**
	 * Initialize one of the headers.
	 */
	public void onOpenDocument(PdfWriter writer, Document document)
	{
		now = new Date();
		HEADER.setColor(BaseColor.WHITE);
		pagenumber = 0;
	}

	/**
	 * Increase the page number.
	 */
	public void onStartPage(PdfWriter writer, Document document)
	{
		pagenumber++;
	}

	/**
	 * Adds the header and the footer.
	 */
	public void onEndPage(PdfWriter writer, Document document)
	{
		SimpleDateFormat formatter;

		formatter = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss z");

		Rectangle rect = writer.getBoxSize("hpf");

		ColumnText.showTextAligned(
				writer.getDirectContent(),
				Element.ALIGN_LEFT,
				new Phrase( formatter.format(now) , FOOT),
				rect.getLeft(), rect.getBottom() - 18, 0
			);
		ColumnText.showTextAligned(
				writer.getDirectContent(),
				Element.ALIGN_RIGHT,
				new Phrase(String.format("Pg %d", pagenumber), FOOT),
				rect.getRight(), rect.getBottom() - 18, 0
			);
	}
}
