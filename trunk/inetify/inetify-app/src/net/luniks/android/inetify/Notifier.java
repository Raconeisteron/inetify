package net.luniks.android.inetify;

public interface Notifier {
	
	/**
	 * Creates the notification using the given INotificationManager, based on the
	 * given TestInfo. Cancels existing notifications if info is null.
	 * @param info
	 */
	void inetify(TestInfo info);

}
