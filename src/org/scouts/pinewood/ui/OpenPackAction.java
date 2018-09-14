package org.scouts.pinewood.ui;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

import org.scouts.pinewood.derby.Pack;

public class OpenPackAction
{
	private Shell shell;

	// common constructor
	public OpenPackAction(Shell s)
	{
		shell = s;
	}

	// file dialog
	public void run()
	{
		String fname;
	
		// create dialog
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setFilterNames(new String[] { "Pack Files", "All Files (*.*)" });
		dialog.setFilterExtensions(new String[] { "*.txt", "*.*" });

		dialog.setFilterPath(System.getenv("USERPROFILE") + "/Desktop"); // Windows path
		dialog.setFileName("pact.txt");
		
		fname = dialog.open();
		System.out.println("file selected is: " + fname);

		// init the global pack from file
		Pack.readParseFileNamed( fname );
	}

}
