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
package net.luniks.android.inetify;

import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.text.format.DateFormat;

/**
 * Some static helper methods.
 * 
 * @author torsten.roemer@luniks.net
 */
public class Utils {
	
	private Utils() {
		throw new UnsupportedOperationException("Utility class");
	}

	/**
	 * Returns the given timestamp formatted as date and time for the default locale.
	 * @param context context
	 * @param timestamp timestamp to format
	 * @return String timestamp formatted as date and time
	 */
	public static String getDateTimeString(final Context context, final long timestamp) {
		Date date = new Date(timestamp);
		String dateString = DateFormat.getLongDateFormat(context).format(date);
		String timeString = DateFormat.getTimeFormat(context).format(date);
		return String.format("%s %s", dateString, timeString);
	}
	
	/**
	 * Returns the given timestamp formatted as short date and time for the default locale.
	 * @param context context
	 * @param timestamp timestamp to format
	 * @return String timestamp formatted as date and time
	 */
	public static String getShortDateTimeString(final Context context, final long timestamp) {
		Date date = new Date(timestamp);
		String dateString = DateFormat.getDateFormat(context).format(date);
		String timeString = DateFormat.getTimeFormat(context).format(date);
		return String.format("%s %s", dateString, timeString);
	}
	
	/**
	 * Returns the given amount of meters in rounded yards if the default locale
	 * is US, in rounded meters otherwise.
	 * @param meters
	 * @return int
	 */
	public static int getLocalizedRoundedMeters(final float meters) {
		Locale defaultLocale = Locale.getDefault();
		if(defaultLocale.equals(Locale.US)) {
			return Math.round(meters / 0.9144f);
		} else {
			return Math.round(meters);
		}
	}
	
}
