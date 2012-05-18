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
package net.luniks.android.test.impl;

import net.luniks.android.impl.NotificationManagerImpl;
import net.luniks.android.interfaces.INotificationManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.test.AndroidTestCase;

public class NotificationManagerImplTest extends AndroidTestCase {
	
	public void testNotificationManagerImpl() {
		
		NotificationManager real = (NotificationManager)this.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
		
		INotificationManager wrapper = new NotificationManagerImpl(real);
		
		Notification notification = new Notification(0, "", 0);
		wrapper.notify(0, notification);
		
		wrapper.cancel(0);
		
		// How to test that notification was created and cancelled?
		
	}

}
