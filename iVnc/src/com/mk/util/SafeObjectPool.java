/**
 * Copyright (C) 2009 Michael A. MacDonald
 */
package com.mk.util;

/**
 * Synchronized object pool
 * @author Michael A. MacDonald
 *
 */
public abstract class SafeObjectPool<R> extends ObjectPool<R> {

	/* (non-Javadoc)
	 * @see com.antlersoft.util.ObjectPool#release(com.antlersoft.util.ObjectPool.Entry)
	 */
	@Override
	public synchronized void release(com.mk.util.ObjectPool.Entry<R> entry) {
		super.release(entry);
	}

	/* (non-Javadoc)
	 * @see com.antlersoft.util.ObjectPool#reserve()
	 */
	@Override
	public synchronized com.mk.util.ObjectPool.Entry<R> reserve() {
		return super.reserve();
	}

}
