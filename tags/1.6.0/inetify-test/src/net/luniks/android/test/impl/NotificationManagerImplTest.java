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
