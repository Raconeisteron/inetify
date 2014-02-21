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
package net.luniks.android.test.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.luniks.android.interfaces.INotificationManager;
import android.app.Notification;

public class NotificationManagerMock implements INotificationManager {
	
	private List<Integer> cancelledIds = new ArrayList<Integer>();
	private Map<Integer, Notification> notifications = new HashMap<Integer, Notification>();

	public void cancel(final int id) {
		this.cancelledIds.add(id);
	}

	public void notify(final int id, final Notification notification) {
		this.notifications.put(id, notification);
	}
	
	public void reset() {
		cancelledIds.clear();
		notifications.clear();
	}

	public List<Integer> getCancelledIds() {
		return cancelledIds;
	}

	public Map<Integer, Notification> getNotifications() {
		return notifications;
	}

}
