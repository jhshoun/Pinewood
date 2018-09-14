package org.scouts.pinewood.reports;

 
import com.itextpdf.text.*;
import com.itextpdf.text.Font.FontFamily;
 
/**
 * base report
 */
public class PinewoodReport
{
	public static final Font NORMAL = new Font(FontFamily.TIMES_ROMAN, 9);
	public static final Font NORMAL_BOLD = new Font(FontFamily.TIMES_ROMAN, 9, Font.BOLD);
	public static final Font HEADER = new Font(FontFamily.HELVETICA, 10, Font.BOLD | Font.UNDERLINE);
	public static final Font TITLE = new Font(FontFamily.HELVETICA, 20, Font.BOLDITALIC);

	/** base path for report files */
	public static final String BASE = 
				System.getenv("USERPROFILE") + "/Desktop/";


}
