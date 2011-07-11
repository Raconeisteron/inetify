package net.luniks.android.inetify;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Dialogs {
	
	private Dialogs() {
		throw new UnsupportedOperationException("Utility class");
	}
	
	/**
	 * Shows an OK dialog with the given title and message.
	 * @param title
	 * @param message
	 */
	public static void showOKDialog(final Context context, final String title, final String message) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setCancelable(true);
		       
		alert.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.dismiss();
			}
		});
		
		alert.show();
	}
	
	/**
	 * Shows a dialog displaying the given error message.
	 * @param message message to show
	 */
	public static void showErrorDialog(final Context context, final String message) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);

		alert.setCancelable(false);
		alert.setTitle(R.string.error);
		alert.setMessage(message);
		
		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int whichButton) {
				dialog.dismiss();
			}
		});
		
		alert.show();		
	}
	
	/**
	 * Shows a confirmation dialog before running the given runnable.
	 * @param message the message to use in the confirmation text
	 * @param runnable Runnable to run
	 */
	public static void showConfirmDeleteDialog(final Context context, final String message, final Runnable runnable) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);

		alert.setCancelable(true);
		alert.setTitle(R.string.confirm);
		alert.setMessage(message);
		
		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int whichButton) {
				dialog.dismiss();
				runnable.run();
			}
		});
		
		alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int whichButton) {
				dialog.dismiss();
			}
		});
		
		alert.show();		
	}

}
