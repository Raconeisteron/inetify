package net.luniks.android.inetify;

/**
 * Interface for a class creating notifications based on the TestInfo given to inetify(TestInfo).
 * 
 * @author dode@luniks.net
 */
public interface Notifier {
	
	/**
	 * Creates the notification using the given INotificationManager, based on the
	 * given TestInfo. Cancels existing notifications if info is null.
	 * @param info
	 */
	void inetify(TestInfo info);

}
