/**
 * Copyright (C) 2012 Iordan Iordanov
 * Copyright (C) 2010 Michael A. MacDonald
 *
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this software; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
 * USA.
 */

package com.mk.imVNC;

import com.mk.imVNC.MostRecentBean;
import com.mk.imVNC.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * @author Michael A. MacDonald
 *
 */
class IntroTextDialog extends Dialog {

	private PackageInfo packageInfo;
	private VncDatabase database;
	
	static IntroTextDialog dialog = null;
	
	static void showIntroTextIfNecessary(Activity context, VncDatabase database, boolean force) {
		PackageInfo pi;
		try {
			pi = context.getPackageManager().getPackageInfo("com.mk.imVNC", 0);
		}
		catch (PackageManager.NameNotFoundException nnfe) {
			return;
		}
		MostRecentBean mr = androidVNC.getMostRecent(database.getReadableDatabase());
		if (dialog == null && (force || mr == null || mr.getShowSplashVersion() != pi.versionCode)) {
				dialog = new IntroTextDialog(context, pi, database);
				dialog.show();
			}
		}
	
	/**
	 * @param context -- Containing dialog
	 */
	private IntroTextDialog(Activity context, PackageInfo pi, VncDatabase database) {
		super(context);
		setOwnerActivity(context);
		packageInfo = pi;
		this.database = database;
	}

	/* (non-Javadoc)
	 * @see android.app.Dialog#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro_dialog);
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		StringBuilder sb = new StringBuilder(getContext().getResources().getString(R.string.intro_title));
		setTitle(sb);
		sb.delete(0, sb.length());
		sb.append(getContext().getResources().getString(R.string.intro_header));
		sb.append(getContext().getResources().getString(R.string.intro_text));
		sb.append("\n");
		sb.append(getContext().getResources().getString(R.string.intro_version_text));
		TextView introTextView = (TextView)findViewById(R.id.textIntroText);
		introTextView.setText(Html.fromHtml(sb.toString()));
		introTextView.setMovementMethod(LinkMovementMethod.getInstance());
		((Button)findViewById(R.id.buttonCloseIntro)).setOnClickListener(new View.OnClickListener() {

			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick(View v) {
				showAgain(true);
			}
			
		});
		((Button)findViewById(R.id.buttonCloseIntroDontShow)).setOnClickListener(new View.OnClickListener() {

			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick(View v) {
				showAgain(false);
			}
			
		});

	}

	/* (non-Javadoc)
	 * @see android.app.Dialog#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getOwnerActivity().getMenuInflater().inflate(R.menu.intro_dialog_menu,menu);
		// Disabling Manual/Wiki Menu item as the original does not correspond to this project anymore.
		/*
		menu.findItem(R.id.itemOpenDoc).setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Utils.showDocumentation(getOwnerActivity());
				dismiss();
				return true;
			}
		});
		*/
		menu.findItem(R.id.itemClose).setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				showAgain(true);
				return true;
			}
		});
		menu.findItem(R.id.itemDontShowAgain).setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				showAgain(false);
				return true;
			}
		});
		return true;
	}

	private void showAgain(boolean show) {
		SQLiteDatabase db = database.getWritableDatabase();
		MostRecentBean mostRecent = androidVNC.getMostRecent(db);
		if (mostRecent != null) {
			int value = -1;
			if (!show) {
				value = packageInfo.versionCode;
			}
			mostRecent.setShowSplashVersion(value);
			mostRecent.Gen_update(db);
		}
		dismiss();
	}
}
