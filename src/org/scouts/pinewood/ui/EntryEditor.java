package org.scouts.pinewood.ui;

import org.scouts.pinewood.derby.*;

import org.eclipse.wb.swt.SWTResourceManager;

import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.beans.BeanProperties;

public class EntryEditor
{
	protected Shell pshell;
	protected Shell shlEdit;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Text txtPackName;
	private ListViewer lvEntries;

	private Pack thePack = Pack.getGlobalPack();
	private Scout editScout;
	private List lstScouts;
	private ScoutEditComposite scoutEditor; 
	
	
	/*
	 * child application model
	 */
	EntryEditor(Shell parent)
	{
		pshell = parent;
	}

	/**
	 * Open the window
	 */
	public void open()
	{
		createContents();
		shlEdit.open();
		shlEdit.layout();
	}

	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents()
	{
		Label lblPack;
		Label lblCubBar;
		Font fLabel = SWTResourceManager.getFont("Microsoft Sans Serif", 9, SWT.BOLD);
		Font fText = SWTResourceManager.getFont("Lucida Sans", 10, SWT.NORMAL);
		Color cEditorBg = SWTResourceManager.getColor(135,140,114);
		
		shlEdit = new Shell(pshell, SWT.CLOSE | SWT.MIN | SWT.RESIZE | SWT.TITLE);

		shlEdit.setBackground(SWTResourceManager.getColor(135,140,114));
		shlEdit.setSize(640, 500);
		shlEdit.setText("Race Entries");

		// configure form layout
		FormLayout formLayout = new FormLayout();
		formLayout.marginTop = 10;
		formLayout.marginRight = 10;
		formLayout.marginLeft = 10;
		formLayout.marginBottom = 10;
		shlEdit.setLayout(formLayout);
		
		lblPack = formToolkit.createLabel(shlEdit, "Pack:", SWT.SHADOW_IN);
		lblPack.setBackground( cEditorBg );
		lblPack.setFont( fLabel );
		FormData fd_lblPack = new FormData();
		fd_lblPack.width = 40;
		fd_lblPack.top = new FormAttachment(0, 10);
		fd_lblPack.left = new FormAttachment(0, 10);
		lblPack.setLayoutData(fd_lblPack);
		formToolkit.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
		
		txtPackName = formToolkit.createText(shlEdit, "New Text", SWT.NONE);
		txtPackName.setFont( fText );
		txtPackName.setForeground(SWTResourceManager.getColor(255, 255, 240));
		txtPackName.setBackground(SWTResourceManager.getColor(71,77,62));
		FormData fd_txtpackName = new FormData();
		fd_txtpackName.top = new FormAttachment(lblPack, -4, SWT.TOP);
		fd_txtpackName.width = 160;
		fd_txtpackName.left = new FormAttachment(lblPack, 0, SWT.RIGHT);
		txtPackName.setLayoutData(fd_txtpackName);

		lblCubBar = new Label(shlEdit, SWT.SHADOW_IN | SWT.CENTER);
		lblCubBar.setBackground(SWTResourceManager.getColor(0, 0, 0));
		lblCubBar.setAlignment(SWT.CENTER);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.height = 125;
		fd_lblNewLabel.width = 582;
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		fd_lblNewLabel.bottom = new FormAttachment(100, -10);
		lblCubBar.setLayoutData(fd_lblNewLabel);
		lblCubBar.setImage(
				SWTResourceManager.getImage(EntryEditor.class, "images/rank_bar.png")
			);

		lvEntries = new ListViewer(shlEdit, SWT.BORDER | SWT.V_SCROLL);
		lstScouts = lvEntries.getList();
		lstScouts.setFont( fText );
		FormData fd_lstScouts = new FormData();
		fd_lstScouts.width = 180;
		fd_lstScouts.top = new FormAttachment(txtPackName, 24);
		fd_lstScouts.bottom = new FormAttachment(lblCubBar, -20);
		fd_lstScouts.left = new FormAttachment(0, 10);
		lstScouts.setLayoutData(fd_lstScouts);

		// detail sub-window for updating scout entry
		Group grpEdit = new Group(shlEdit, SWT.NONE);
		grpEdit.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		grpEdit.setBackground( cEditorBg );
		grpEdit.setFont(SWTResourceManager.getFont("Microsoft Sans Serif", 9, SWT.ITALIC));
		grpEdit.setText("Scout");
		grpEdit.setLayout(null);

		FormData fd_grpScout = new FormData();
		fd_grpScout.top = new FormAttachment(0, 10);
		fd_grpScout.right = new FormAttachment(100, -10);
		fd_grpScout.height = 250;
		fd_grpScout.width = 360;
		grpEdit.setLayoutData(fd_grpScout);
		
		//configure the editor composite
		scoutEditor = new ScoutEditComposite(grpEdit, SWT.NONE);
		scoutEditor.setBounds(10,20,320,190);
		scoutEditor.setBackgroundMode(SWT.INHERIT_NONE);
		scoutEditor.setDens( thePack.getDens() );

		// edit buttons
		Button btnDelete = new Button(grpEdit, SWT.FLAT | SWT.CENTER);
		btnDelete.setText("Delete");
		btnDelete.setBounds(240, 220, 68, 23);
		btnDelete.addSelectionListener( new editScoutListener(editScoutListener.DELETE) );

		Button btnAdd = new Button(grpEdit, SWT.FLAT | SWT.CENTER);
		btnAdd.setText("Add");
		btnAdd.setBounds(40, 220, 68, 23);
		btnAdd.addSelectionListener( new editScoutListener(editScoutListener.ADD) );

		Button btnUpdate = new Button(grpEdit, SWT.FLAT | SWT.CENTER);
		btnUpdate.setText("Update");
		btnUpdate.setBounds(140, 220, 68, 23);
		btnUpdate.addSelectionListener( new editScoutListener(editScoutListener.UPDATE) );
		
		
		
		lvEntries.addSelectionChangedListener( new ScoutSelection() );
		lvEntries.setSorter( new ScoutListSorter() );
		lvEntries.setContentProvider( new ScoutContentProvider() );
		lvEntries.setInput( thePack.getScouts() );

		initDataBindings();

	}

	/******************************************************************************
	 * inner classes
	 */

	/* list view components */
	// list sorter
	class ScoutListSorter extends ViewerSorter
	{
		public int compare(Viewer viewer, Object obj1, Object obj2)
		{
			return ((Scout) obj1).compareTo( (Scout) obj2 );
		}
	}
	// change, selection listener
	class ScoutSelection implements ISelectionChangedListener
	{
		public void selectionChanged(SelectionChangedEvent event)
		{
			// update the den lookup (it may have changed)
			scoutEditor.setDens( thePack.getDens() );

			// push current selection to editor
			IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			editScout = (Scout) selection.getFirstElement();
			scoutEditor.setScout( new Scout(editScout) );
		}
	}

	// gets scout list from pack
	class ScoutContentProvider implements IStructuredContentProvider
	{
		@SuppressWarnings("rawtypes")
		public Object[] getElements(Object inputElement)
		{
			return ((java.util.List) inputElement).toArray();
		}

		public void dispose() {}
		
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
				
		}
	}


	/*
	 * actions listeners
	 */

	// generic listener, target action type set when
	// the listener is initialized
	class editScoutListener implements SelectionListener 
	{
		public static final int ADD = 1;
		public static final int UPDATE = 2;
		public static final int DELETE = 3;
		private int eType;
		
		public editScoutListener(int type)
		{
			super();
			eType = type;
		}
		public void widgetSelected(SelectionEvent event)
			{ this.editScout(); }
		public void widgetDefaultSelected(SelectionEvent event)
			{ this.editScout(); }
		private void editScout()
		{
			Scout cScout;

			cScout = scoutEditor.getScout();
			switch (eType)
			{
				case ADD:
					thePack.addScout( cScout );
					lvEntries.setInput( thePack.getScouts() );
					break;
				case UPDATE:
					editScout.copy( cScout );
					lvEntries.refresh();
					break;
				case DELETE:
					// create empty scout record
					scoutEditor.setScout( new Scout() );
					thePack.removeScout( cScout.getCarNumber() );
					lvEntries.setInput( thePack.getScouts() );
					break;
			}
		}
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTxtNewTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtPackName);
		IObservableValue nameThePackObserveValue = BeanProperties.value("name").observe(thePack);
		bindingContext.bindValue(observeTextTxtNewTextObserveWidget, nameThePackObserveValue, null, null);
		//
		return bindingContext;
	}
}
