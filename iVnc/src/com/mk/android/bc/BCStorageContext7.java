/**
 * Copyright (c) 2011 Michael A. MacDonald
 */
package com.mk.android.bc;

import java.io.File;

import android.content.Context;

import android.os.Environment;

import com.mk.imVNC.MainConfiguration;

/**
 * @author Michael A. MacDonald
 *
 */
public class BCStorageContext7 implements IBCStorageContext {

	/* (non-Javadoc)
	 * @see com.mk.android.bc.IBCStorageContext#getExternalStorageDir(android.content.Context, java.lang.String)
	 */
	public File getExternalStorageDir(MainConfiguration context, String type) {
		File f = Environment.getExternalStorageDirectory();
		f = new File(f, "Android/data/com.mk.imVNC/files");
		if (type != null)
			f=new File(f, type);
		f.mkdirs();
		return f;
	}

}
