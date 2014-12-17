package ru.ulstu.tips.moiseev;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends ActionBarActivity {
    MenuAdapter menuAdapter;

    String[] menuNames = {
            "Новые вычисления",
            "Загрузить условие",
            "О программе",
            "Помощь",
            "Выход"
    };

    String[] menuTags = {
            "new",
            "load",
            "about",
            "help",
            "exit"
    };

    int[] menuImages = {
            R.drawable.ic_new,
            R.drawable.ic_load,
            R.drawable.ic_about,
            R.drawable.ic_help,
            R.drawable.ic_exit
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

        /*ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.ic_launcher);
        actionBar.setLogo(R.drawable.ic_launcher);*/

        menuAdapter = new MenuAdapter();
        ListView menuListView = (ListView)findViewById(R.id.menuListView);
        menuListView.setAdapter(menuAdapter);
        menuListView.setOnItemClickListener(menuClickListener);
        menuListView.refreshDrawableState();
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

    /* MenuItemClickListener */
    public AdapterView.OnItemClickListener menuClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String tag = (String) view.getTag();
            Intent intent = null;

            if (tag.compareTo("new") == 0)
                intent = new Intent(MenuActivity.this, CalculationActivity.class);
            if (tag.compareTo("about") == 0)
                intent = new Intent(MenuActivity.this, AboutActivity.class);
            if (tag.compareTo("help") == 0)
                intent = new Intent(MenuActivity.this, HelpActivity.class);
            if (tag.compareTo("load") == 0)
                showLoadDialog();
            if (tag.compareTo("exit") == 0)
                MenuActivity.this.finish();

            if (intent != null)
                MenuActivity.this.startActivity(intent);
        }
    };

    /* MenuItem */
    public class MenuItem {
        String menuName;
        int menuImageId;
        String menuTag;
    }

    /* MenuAdapter */
    public class MenuAdapter extends BaseAdapter {

        List<MenuItem> menuList = getDataForListView();

        @Override
        public int getCount() {
            return menuList.size();
        }

        @Override
        public MenuItem getItem(int position) {
            return menuList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null)
            {
                LayoutInflater inflater = (LayoutInflater) MenuActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listitem, parent, false);
            }

            TextView menuItemTitle = (TextView)convertView.findViewById(R.id.menuItemTitle);
            ImageView menuItemImage = (ImageView)convertView.findViewById(R.id.menuItemImage);

            MenuItem menuItem = menuList.get(position);

            menuItemTitle.setText(menuItem.menuName);
            ((RelativeLayout) menuItemTitle.getParent()).setTag(menuItem.menuTag);
            menuItemImage.setImageResource(menuItem.menuImageId);

            return convertView;
        }

        public List<MenuItem> getDataForListView()
        {
            List<MenuItem> resultList = new ArrayList<MenuItem>();

            for(int i = 0; i < menuNames.length; i++)
            {
                MenuItem chapter = new MenuItem();
                chapter.menuName = menuNames[i];
                chapter.menuImageId = menuImages[i];
                chapter.menuTag = menuTags[i];
                resultList.add(chapter);
            }

            return resultList;
        }
    }
}
