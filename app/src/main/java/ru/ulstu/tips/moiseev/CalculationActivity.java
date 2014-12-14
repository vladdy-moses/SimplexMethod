package ru.ulstu.tips.moiseev;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import ru.ulstu.tips.Equation;
import ru.ulstu.tips.Inequality;
import ru.ulstu.tips.Inequality.eSign;
import ru.ulstu.tips.Simplex;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class CalculationActivity extends Activity {
	private LinearLayout mainLayout = null;
	private int variablesCount = 0;
	private int inequalitiesCount = 0;
	private Simplex simplex = new Simplex();
	private boolean isLoaded = false;
	private String stringForSaving = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calculation);

		mainLayout = (LinearLayout) this.findViewById(R.id.mainLayout);
		showScreen(0);
		
		Bundle bundle = this.getIntent().getExtras();
		if((bundle != null) && (bundle.containsKey("loadFile")))
			this.loadFromFile(bundle.getString("loadFile"));
	}

	private void loadFromFile(String fileName) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(fileName));
			stringForSaving = br.readLine();
			loadCheckString();
			br.close();
			
			isLoaded = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadCheckString() {
		if(stringForSaving.length() > 0) {
			String[] splittedString = stringForSaving.split(" ");
			if(splittedString.length > 2) {
				int tmpVariablesCount = Integer.valueOf(splittedString[0]);
				int tmpInequalitiesCount = Integer.valueOf(splittedString[1]);
				
				if(splittedString.length == (2 + tmpVariablesCount + tmpInequalitiesCount * (tmpVariablesCount + 2))) {
					this.variablesCount = tmpVariablesCount;
					this.inequalitiesCount = tmpInequalitiesCount;
					
					loadParseFunction(splittedString);
					loadParseInequalities(splittedString);
					showScreen(2);
				}
			}
		}
	}

	private void loadParseFunction(String[] splittedString) {
		double tmpValue;
		Equation eq = new Equation();
		eq.setVariablesCount(variablesCount);
		
		for(int i = 0; i < this.variablesCount; i++) {
			try {
				tmpValue = Double.valueOf(splittedString[2 + i]);
				eq.set(i, tmpValue);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		
		simplex.setResultFunction(eq);
	}
	
	private void loadParseInequalities(String[] splittedString) {
		for(int i = 0 ; i < this.inequalitiesCount; i++)
			loadParseInequality(splittedString, i);
	}

	private void loadParseInequality(String[] splittedString, int index) {
		int offset = 2 + this.variablesCount + index * (this.variablesCount + 2);
		double tmpValue;
		
		Inequality iq = new Inequality();
		iq.setVariablesCount(this.variablesCount);
		
		for(int i = 0; i < this.variablesCount; i++) {
			try {
				tmpValue = Double.valueOf(splittedString[offset + i]);
				iq.set(i, tmpValue);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		
		try {
			tmpValue = Double.valueOf(splittedString[offset + this.variablesCount]);
			iq.setFreeTerm(tmpValue);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		String signString = splittedString[offset + this.variablesCount + 1];

		if (signString.compareTo(">=") == 0)
			iq.setSign(eSign.MORE);
		else if (signString.compareTo("<=") == 0)
			iq.setSign(eSign.LESS);
		
		simplex.addInequality(iq);
	}

	private void showScreen(int num) {
		mainLayout.removeAllViews();

		switch (num) {
		case 0:
			screenCount();
			break;

		case 1:
			screenCoefficients();
			break;

		case 2:
			screenResult();
			break;
		}
	}

	private void screenCount() {
		TextView textView;
		EditText editText;
		Button button;

		textView = new TextView(this);
		textView.setText(this.getResources().getString(
				R.string.calculation_enter_variables_count));
		mainLayout.addView(textView);

		editText = new EditText(this);
		editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		editText.setTag("variables");
		mainLayout.addView(editText);

		textView = new TextView(this);
		textView.setText(this.getResources().getString(
				R.string.calculation_enter_inequalities_count));
		mainLayout.addView(textView);

		editText = new EditText(this);
		editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		editText.setTag("inequalities");
		// editText.setOnEditorActionListener(new OnEditorActionListener() {});
		mainLayout.addView(editText);

		button = new Button(this);
		button.setText(this.getResources().getString(R.string.action_next));
		button.setOnClickListener(checkVariablesEnterListener);
		mainLayout.addView(button);
	}

	private OnClickListener checkVariablesEnterListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			EditText variablesCountText = (EditText) mainLayout
					.findViewWithTag("variables");
			EditText inequalitiesCountText = (EditText) mainLayout
					.findViewWithTag("inequalities");

			if ((variablesCountText != null) && (inequalitiesCountText != null)) {
				try {
					variablesCount = Integer.parseInt(variablesCountText
							.getText().toString());
					inequalitiesCount = Integer.parseInt(inequalitiesCountText
							.getText().toString());
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} finally {
					if ((variablesCount > 0) && (inequalitiesCount > 0)) {
						showScreen(1);
					}
				}
			}
		}
	};

	private void screenCoefficients() {
		TextView textView;
		Button button;
		HorizontalScrollView scrollView;

		textView = new TextView(this);
		textView.setText(this.getResources().getString(
				R.string.calculation_enter_function));
		mainLayout.addView(textView);

		scrollView = new HorizontalScrollView(this);
		scrollView.setTag("scrollFunction");
		mainLayout.addView(scrollView);

		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		buildFunction(linearLayout);
		scrollView.addView(linearLayout);

		textView = new TextView(this);
		textView.setText(this.getResources().getString(
				R.string.calculation_enter_coefficients));
		mainLayout.addView(textView);

		scrollView = new HorizontalScrollView(this);
		scrollView.setTag("scrollInequalities");
		mainLayout.addView(scrollView);

		TableLayout tableLayout = new TableLayout(this);
		buildInequalities(tableLayout);
		scrollView.addView(tableLayout);

		button = new Button(this);
		button.setText(this.getResources().getString(R.string.action_next));
		button.setOnClickListener(checkCoefficientsListener);
		mainLayout.addView(button);
	}

	private void buildFunction(LinearLayout linearLayout) {
		TextView textView;
		EditText editText;
		String variableTag;

		textView = new TextView(this);
		textView.setText("Z = ");
		linearLayout.addView(textView);

		for (int i = 0; i < variablesCount; i++) {
			variableTag = "x" + (i + 1);

			editText = new EditText(this);
			editText.setText("0");
			editText.setTag(variableTag);
			editText.setInputType(InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_FLAG_DECIMAL
					| InputType.TYPE_NUMBER_FLAG_SIGNED);
			linearLayout.addView(editText);

			textView = new TextView(this);
			textView.setText(variableTag);
			linearLayout.addView(textView);

			if (i < variablesCount - 1)
				textView.setText(textView.getText() + " + ");
		}
	}

	private void buildInequalities(TableLayout tableLayout) {
		TableRow tableRow;

		for (int i = 0; i < inequalitiesCount; i++) {
			tableRow = new TableRow(this);
			tableLayout.addView(tableRow);

			buildInequality(tableRow);
		}
	}

	private void buildInequality(TableRow tableRow) {
		TextView textView;
		EditText editText;
		Spinner spinner;
		String variableTag;

		for (int i = 0; i < variablesCount; i++) {
			variableTag = "x" + (i + 1);

			editText = new EditText(this);
			editText.setText("0");
			editText.setTag(variableTag);
			editText.setInputType(InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_FLAG_DECIMAL
					| InputType.TYPE_NUMBER_FLAG_SIGNED);
			tableRow.addView(editText);

			textView = new TextView(this);
			textView.setText(variableTag);
			tableRow.addView(textView);

			if (i < variablesCount - 1)
				textView.setText(textView.getText() + " + ");
		}

		spinner = new Spinner(this);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.inequality_sign,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setTag("sign");
		tableRow.addView(spinner);

		editText = new EditText(this);
		editText.setText("0");
		editText.setTag("result");
		editText.setInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_DECIMAL
				| InputType.TYPE_NUMBER_FLAG_SIGNED);
		tableRow.addView(editText);
	}

	private OnClickListener checkCoefficientsListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			TableRow inequalityRow;

			addInSavingString(variablesCount);
			addInSavingString(inequalitiesCount);

			HorizontalScrollView functionScrollView = (HorizontalScrollView) mainLayout
					.findViewWithTag("scrollFunction");
			LinearLayout functionTable = (LinearLayout) functionScrollView
					.getChildAt(0);
			parseFunction(functionTable);

			HorizontalScrollView inequalitiesScrollView = (HorizontalScrollView) mainLayout
					.findViewWithTag("scrollInequalities");
			TableLayout inequalitiesTable = (TableLayout) inequalitiesScrollView
					.getChildAt(0);

			for (int i = 0; i < inequalitiesCount; i++) {
				inequalityRow = (TableRow) inequalitiesTable.getChildAt(i);
				parseInequality(inequalityRow);
			}
			if (!isLoaded)
				showSaveAlert();
			showScreen(2);
		}
	};

	private void parseFunction(LinearLayout functionLayout) {
		EditText editText;

		Equation eq = new Equation();
		eq.setVariablesCount(variablesCount);

		for (int i = 0; i < variablesCount; i++) {
			editText = (EditText) functionLayout.findViewWithTag("x" + (i + 1));
			try {
				double value = Double
						.parseDouble(editText.getText().toString());
				eq.set(i, value);
				addInSavingString(value);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				addInSavingString(0.0);
			}
		}

		simplex.setResultFunction(eq);
	}

	private void parseInequality(TableRow inequalityRow) {
		EditText editText;
		Spinner spinner;
		Inequality iq = new Inequality();
		iq.setVariablesCount(variablesCount);

		for (int j = 0; j < variablesCount; j++) {
			editText = (EditText) inequalityRow.findViewWithTag("x" + (j + 1));
			try {
				double value = Double
						.parseDouble(editText.getText().toString());
				iq.set(j, value);
				addInSavingString(value);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				addInSavingString(0.0);
			}
		}

		editText = (EditText) inequalityRow.findViewWithTag("result");
		try {
			double value = Double.parseDouble(editText.getText().toString());
			iq.setFreeTerm(value);
			addInSavingString(value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			addInSavingString(0.0);
		}

		spinner = (Spinner) inequalityRow.findViewWithTag("sign");
		String spinnerText = (String) spinner.getSelectedItem();

		if (spinnerText.compareTo(">=") == 0)
			iq.setSign(eSign.MORE);
		else if (spinnerText.compareTo("<=") == 0)
			iq.setSign(eSign.LESS);

		addInSavingString(spinnerText);

		simplex.addInequality(iq);
	}

	private void showSaveAlert() {
		final EditText fileName = new EditText(this);
		fileName.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle(getResources().getString(R.string.app_name))
				.setMessage(getResources().getString(R.string.calculation_save))
				.setPositiveButton(
						getResources().getString(R.string.calculation_save_yes),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								saveCoefficients(fileName.getText().toString());
							}
						})
				.setNegativeButton(
						getResources().getString(R.string.calculation_save_no),
						null)
				.setView(fileName)
				.show();
	}

	private void saveCoefficients(String fileName) {
		String realFileName = (fileName.length() > 0) ? fileName : "result";
		realFileName = realFileName.replace('/', '_');
		
		File sdDir = Environment.getExternalStorageDirectory();
		File programDir = new File(sdDir.getAbsolutePath() + "/Simplex/");
		if (programDir.mkdir() || programDir.isDirectory()) {
			File sdFile = new File(programDir, realFileName);
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
				bw.write(stringForSaving);
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(this, this.getResources().getString(R.string.load_dir_not_exists), Toast.LENGTH_LONG).show();
		}
	}

	private void screenResult() {
		TextView textView;
		HorizontalScrollView scrollView;

		textView = new TextView(this);
		textView.setTextAppearance(this, android.R.style.TextAppearance_Large);
		
		scrollView = new HorizontalScrollView(this);
		scrollView.setTag("scrollFunction");

		try {
			simplex.setFactVariablesCount(variablesCount);
			Equation result = simplex.run();

			textView.setText(this.getResources().getString(
					R.string.calculation_win));
			mainLayout.addView(textView);

			printSolution(result);

			textView = new TextView(this);
			textView.setText(simplex.getLog().get());
			scrollView.addView(textView);
			
			mainLayout.addView(scrollView);
		} catch (Exception e) {
			textView.setText(this.getResources().getString(
					R.string.calculation_fail));
			mainLayout.addView(textView);

			/*textView = new TextView(this);
			textView.setText(e.toString());
			scrollView.addView(textView);*/
			
			//mainLayout.addView(scrollView);
		}
	}

	private void printSolution(Equation eq) {
		TextView textView;
		
		textView = new TextView(this);
		textView.setTextAppearance(this, android.R.style.TextAppearance_Medium);
		textView.setText(String.format(this.getResources().getString(R.string.result_function), eq.getFreeTerm()));	
		mainLayout.addView(textView);
		
		for(int i = 0; i < eq.getVariablesCount(); i++) {
			textView = new TextView(this);
			textView.setTextAppearance(this, android.R.style.TextAppearance_Medium);
			textView.setText(String.format(this.getResources().getString(R.string.result_variable), "x" + (i+1), eq.get(i)));	
			mainLayout.addView(textView);
		}
	}

	private void addInSavingString(String s) {
		stringForSaving += s + " ";
	}

	private void addInSavingString(double d) {
		stringForSaving += String.valueOf(d) + " ";
	}

	private void addInSavingString(int i) {
		stringForSaving += String.valueOf(i) + " ";
	}
}
