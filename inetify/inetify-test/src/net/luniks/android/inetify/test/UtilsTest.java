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

import java.util.Locale;

import net.luniks.android.inetify.Utils;
import android.test.AndroidTestCase;
import android.text.format.DateFormat;

public class UtilsTest extends AndroidTestCase {

	public void testGetDateTimeStringUS() {
		
		Locale.setDefault(Locale.US);
		boolean is24HourFormat = DateFormat.is24HourFormat(this.getContext());
		
		String dateTimeString = Utils.getDateTimeString(this.getContext(), 1234567890L);
		
		String expected = "January 15, 1970 7:56";
		if(! is24HourFormat) {
			expected += " AM";
		}
		
		assertEquals(expected, dateTimeString);
		
	}
	
	public void testGetDateTimeStringGERMANY() {
		
		Locale.setDefault(Locale.GERMANY);
		boolean is24HourFormat = DateFormat.is24HourFormat(this.getContext());
		
		String dateTimeString = Utils.getDateTimeString(this.getContext(), 1234567890L);
		
		String expected = "15. Januar 1970 7:56";
		if(! is24HourFormat) {
			expected += " vorm.";
		}
		
		assertEquals(expected, dateTimeString);
		
	}
	
	public void testGetShortDateTimeStringUS() {
		
		Locale.setDefault(Locale.US);
		boolean is24HourFormat = DateFormat.is24HourFormat(this.getContext());
		
		String dateTimeString = Utils.getShortDateTimeString(this.getContext(), 1234567890L);
		
		String expected = "1/15/1970 7:56";
		if(! is24HourFormat) {
			expected += " AM";
		}
		
		assertEquals(expected, dateTimeString);
		
	}
	
	public void testGetShortDateTimeStringGERMANY() {
		
		Locale.setDefault(Locale.GERMANY);
		boolean is24HourFormat = DateFormat.is24HourFormat(this.getContext());
		
		String dateTimeString = Utils.getShortDateTimeString(this.getContext(), 1234567890L);
		
		// This seems to be a bug
		String expected = "1/15/1970 7:56";
		if(! is24HourFormat) {
			expected += " vorm.";
		}
		
		assertEquals(expected, dateTimeString);
		
	}
	
	public void testGetLocalizedRoundedMetersUS() {
		
		Locale.setDefault(Locale.US);
		
		int result = Utils.getLocalizedRoundedMeters(333.3f);
		
		assertEquals(365, result);
	}
	
	public void testGetLocalizedRoundedMetersGERMANY() {
		
		Locale.setDefault(Locale.GERMANY);
		
		int result = Utils.getLocalizedRoundedMeters(333.3f);
		
		assertEquals(333, result);
	}
	
}
