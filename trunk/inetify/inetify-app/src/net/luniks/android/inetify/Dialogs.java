package net.luniks.android.inetify;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

public class Dialogs {
	
	private Dialogs() {
		throw new UnsupportedOperationException("Utility class");
	}
	
	/**
	 * Creates an OK dialog managed by the given activity and id, with the given title and message.
	 * @param activity
	 * @param id
	 * @param title
	 * @param message
	 */
	public static Dialog createOKDialog(final Activity activity, final int id,
			final String title, final String message) {
		AlertDialog.Builder alert = new AlertDialog.Builder(activity);
		
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setCancelable(true);
		       
		alert.setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int whichButton) {
				activity.dismissDialog(id);
			}
		});
		
		return alert.create();
	}
	
	/**
	 * Creates an error dialog managed by the given activity and id, with the given message.
	 * @param message
	 */
	public static Dialog createErrorDialog(final Activity activity, final int id, 
			final String message) {
		AlertDialog.Builder alert = new AlertDialog.Builder(activity);

		alert.setCancelable(false);
		alert.setTitle(R.string.error);
		alert.setMessage(message);
		
		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int whichButton) {
				activity.dismissDialog(id);
			}
		});
		
		return alert.create();		
	}
	
	/**
	 * Creates a confirmation dialog managed by the given activity, with the given title and message,
	 * where both the positive and the negative button just dismiss the dialog.
	 * @param message
	 */
	public static Dialog createConfirmDeleteDialog(final Activity activity, final int id, 
			final String message) {
		AlertDialog.Builder alert = new AlertDialog.Builder(activity);

		alert.setCancelable(true);
		alert.setTitle(R.string.confirm);
		alert.setMessage(message);
		
		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int whichButton) {
				activity.dismissDialog(id);
			}
		});
		
		alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int whichButton) {
				activity.dismissDialog(id);
			}
		});
		
		return alert.create();
	}

}
