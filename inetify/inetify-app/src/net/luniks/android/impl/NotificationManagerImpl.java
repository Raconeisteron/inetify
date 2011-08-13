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
package net.luniks.android.impl;

import net.luniks.android.interfaces.INotificationManager;
import android.app.Notification;
import android.app.NotificationManager;

/**
 * Implementation of INotificationManager.
 * @see android.app.NotificationManager
 * 
 * @author torsten.roemer@luniks.net
 */
public class NotificationManagerImpl implements INotificationManager {
	
	private final NotificationManager notificationManager;
	
	public NotificationManagerImpl(final NotificationManager notificationManager) {
		this.notificationManager = notificationManager;
	}

	public void cancel(final int id) {
		this.notificationManager.cancel(id);
	}

	public void notify(final int id, final Notification notification) {
		this.notificationManager.notify(id, notification);
	}

}
