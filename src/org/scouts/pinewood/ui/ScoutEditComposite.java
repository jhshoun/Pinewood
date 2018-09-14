package org.scouts.pinewood.ui;

import java.util.List;

import org.scouts.pinewood.derby.Scout;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class ScoutEditComposite extends Composite
{

	private DataBindingContext m_bindingContext;
	private Scout scout = new Scout();
	private Text carNumberText;
	private Combo denCombo;
	private Text firstNameText;
	private Text lastNameText;
	private Text rankText;
	Font fLabel = SWTResourceManager.getFont("Microsoft Sans Serif", 9, SWT.BOLD);
	Font fText = SWTResourceManager.getFont("Lucida Sans", 10, SWT.NORMAL);
	Color cWhite = SWTResourceManager.getColor(SWT.COLOR_WHITE);
	Color cCyan = SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN);


	public ScoutEditComposite(Composite parent, int style, Scout newScout)
	{
		this(parent, style);
		setScout(newScout);
	}

	public ScoutEditComposite(Composite parent, int style)
	{
		super(parent, SWT.BORDER);
		setBackground( cCyan );
//		setSize(320, 190);
		setLayout(null);

		// car number
		Label label = new Label(this, SWT.NONE);
		label.setForeground( cWhite );
		label.setBackground( cCyan );
		label.setFont( fLabel );
		label.setBounds(15, 70, 58, 13);
		label.setText("Car #:");

		carNumberText = new Text(this, SWT.BORDER | SWT.SINGLE);
		carNumberText.setFont(fText);
		carNumberText.setBounds(15, 90, 84, 21);

		// den select (combo-box)
		Label label_1 = new Label(this, SWT.NONE);
		label_1.setForeground( cWhite );
		label_1.setBackground( cCyan );
		label_1.setFont( fLabel );
		label_1.setBounds(15, 120, 98, 13);
		label_1.setText("Den/Pack");

		denCombo = new Combo(this, SWT.BORDER);
		denCombo.setFont(fText);
		denCombo.setBounds(15, 140, 200, 21);

		// first name
		Label label_2 = new Label(this, SWT.NONE);
		label_2.setForeground( cWhite );
		label_2.setBackground( cCyan );
		label_2.setFont( fLabel );
		label_2.setBounds(15, 20, 51, 13);
		label_2.setText("First:");

		firstNameText = new Text(this, SWT.BORDER | SWT.SINGLE);
		firstNameText.setFont(fText);
		firstNameText.setBounds(15, 40, 112, 21);

		// last name
		Label label_3 = new Label(this, SWT.NONE);
		label_3.setForeground( cWhite );
		label_3.setBackground( cCyan );
		label_3.setFont( fLabel );
		label_3.setBounds(162, 20, 51, 13);
		label_3.setText("Last:");

		lastNameText = new Text(this, SWT.BORDER | SWT.SINGLE);
		lastNameText.setFont(fText);
		lastNameText.setBounds(162, 40, 139, 21);

		// rank
		Label label_4 = new Label(this, SWT.NONE);
		label_4.setFont( fLabel );
		label_4.setForeground( cWhite );
		label_4.setBackground( cCyan );
		label_4.setBounds(140, 70, 40, 13);
		label_4.setText("Rank:");

		rankText = new Text(this, SWT.BORDER | SWT.SINGLE);
		rankText.setFont(fText);
		rankText.setBounds(140, 90, 159, 21);

		if (scout != null)
		{
			m_bindingContext = initDataBindings();
		}
	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	private DataBindingContext initDataBindings()
	{
		IObservableValue carNumberObserveWidget = SWTObservables.observeText(
				carNumberText, SWT.Modify);
		IObservableValue carNumberObserveValue = PojoObservables.observeValue(
				scout, "carNumber");
		IObservableValue denObserveWidget = SWTObservables
				.observeText(denCombo);
		IObservableValue denObserveValue = PojoObservables.observeValue(scout,
				"den");
		IObservableValue firstNameObserveWidget = SWTObservables.observeText(
				firstNameText, SWT.Modify);
		IObservableValue firstNameObserveValue = PojoObservables.observeValue(
				scout, "firstName");
		IObservableValue lastNameObserveWidget = SWTObservables.observeText(
				lastNameText, SWT.Modify);
		IObservableValue lastNameObserveValue = PojoObservables.observeValue(
				scout, "lastName");
		IObservableValue rankObserveWidget = SWTObservables.observeText(
				rankText, SWT.Modify);
		IObservableValue rankObserveValue = PojoObservables.observeValue(scout,
				"rank");
		//
		DataBindingContext bindingContext = new DataBindingContext();
		//
		bindingContext.bindValue(carNumberObserveWidget, carNumberObserveValue,
				null, null);
		bindingContext.bindValue(denObserveWidget, denObserveValue, null, null);
		bindingContext.bindValue(firstNameObserveWidget, firstNameObserveValue,
				null, null);
		bindingContext.bindValue(lastNameObserveWidget, lastNameObserveValue,
				null, null);
		bindingContext.bindValue(rankObserveWidget, rankObserveValue, null,
				null);
		//
		return bindingContext;
	}

	// accessors
	public Scout getScout()
	{
		return scout;
	}
	public void setScout(Scout newScout)
	{
		setScout(newScout, true);
	}
	public void setDens(String[] dens)
	{
		denCombo.setItems( dens );
	}
	public void setDens(List<String> dens)
	{
		if (dens.size() < 1)
			return;

		this.setDens( dens.toArray( new String[1] ) );
	}


	public void setScout(Scout newScout,
			boolean update)
	{
		scout = newScout;
		if (update)
		{
			if (m_bindingContext != null)
			{
				m_bindingContext.dispose();
				m_bindingContext = null;
			}
			if (scout != null)
			{
				m_bindingContext = initDataBindings();
			}
		}
	}

}
