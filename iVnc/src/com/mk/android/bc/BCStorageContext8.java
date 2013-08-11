/**
 * Copyright (c) 2010 Michael A. MacDonald
 */
package com.mk.android.bc;

import java.io.File;

import android.content.Context;

import com.mk.imVNC.MainConfiguration;

/**
 * @author Michael A. MacDonald
 *
 */
class BCStorageContext8 implements IBCStorageContext {

	/* (non-Javadoc)
	 * @see com.mk.android.bc.IBCStorageContext#getExternalStorageDir(android.content.Context, java.lang.String)
	 */
	@Override
	public File getExternalStorageDir(MainConfiguration context, String type) {
		return ((Context)context).getExternalFilesDir(type);
	}

}
