package ru.ulstu.tips.moiseev;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;

public class MenuActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.ic_launcher);
	}

	public void menuClick(View v) {
		String tag = (String) v.getTag();
		Intent intent = null;

		if (tag.compareTo("new") == 0)
			intent = new Intent(this, CalculationActivity.class);
		if (tag.compareTo("about") == 0)
			intent = new Intent(this, AboutActivity.class);
		if (tag.compareTo("help") == 0)
			intent = new Intent(this, HelpActivity.class);
		if (tag.compareTo("load") == 0)
			showLoadDialog();
		if (tag.compareTo("exit") == 0)
			this.finish();

		if (intent != null)
			this.startActivity(intent);
	}

	private void showLoadDialog() {
		File sdDir = Environment.getExternalStorageDirectory();
		final File programDir = new File(sdDir.getAbsolutePath() + "/Simplex/");
		if (programDir.mkdir() || programDir.isDirectory()) {
			final String[] files = programDir.list();

			if (files.length > 0) {
				new AlertDialog.Builder(this)
						.setIcon(android.R.drawable.ic_dialog_info)
						.setTitle(
								getResources().getString(
										R.string.title_dialog_load))
						.setItems(files, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								loadCalculation(programDir, files[arg1]);
							}
						}).show();
			} else {
				Toast.makeText(this, this.getResources().getString(R.string.load_files_not_found), Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(this, this.getResources().getString(R.string.load_dir_not_exists), Toast.LENGTH_LONG).show();
		}
	}

	private void loadCalculation(File dir, String file) {
		String fullFileName = dir.getAbsolutePath() + "/" + file;
		Intent intent = new Intent(this, CalculationActivity.class);

		intent.putExtra("loadFile", fullFileName);
		this.startActivity(intent);
	}
}
