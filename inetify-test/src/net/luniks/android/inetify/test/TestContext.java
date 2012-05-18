/*
 * Copyright 2011 Torsten Römer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.luniks.android.inetify.test;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Looper;
import android.test.mock.MockContext;

public class TestContext extends MockContext {
	
	private final Context context;
	
	private int startServiceCount = 0;
	private Intent startServiceIntent = null;
	
	public TestContext(final Context context) {
		this.context = context;
	}

	public int getStartServiceCount() {
		return startServiceCount;
	}

	public Intent getStartServiceIntent() {
		return startServiceIntent;
	}
	
	@Override
	public ContentResolver getContentResolver() {
		return context.getContentResolver();
	}

	@Override
	public Looper getMainLooper() {
		return context.getMainLooper();
	}

	@Override
	public Resources getResources() {
		return context.getResources();
	}

	@Override
	public Object getSystemService(String name) {
		return context.getSystemService(name);
	}

	@Override
	public ComponentName startService(Intent service) {
		startServiceCount++;
		this.startServiceIntent = service;
		return null;
	}

	@Override
	public String getPackageName() {
		return context.getPackageName();
	}

	@Override
	public SharedPreferences getSharedPreferences(String name, int mode) {
		return context.getSharedPreferences(name, mode);
	}
	
}
