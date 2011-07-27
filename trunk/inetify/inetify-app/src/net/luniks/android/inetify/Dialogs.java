package net.luniks.android.inetify;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;

/**
 * Utility class to create dialogs.
 * 
 * @author torsten.roemer@luniks.net
 */
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
	 * @return Dialog
	 */
	public static Dialog createOKDialog(final Activity activity, final int id,
			final String title, final String message) {
		AlertDialog.Builder alert = new AlertDialog.Builder(activity);
		
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setCancelable(true);
		       
		alert.setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int whichButton) {
				dismissDialogSafely(activity, id);
			}
		});
		
		return alert.create();
	}
	
	/**
	 * Creates a context dialog managed by the given activity and id, items and listener
	 * @param activity
	 * @param id
	 * @param items
	 * @param listener
	 * @return Dialog
	 */
	public static Dialog createContextDialog(final Activity activity, final int id,
			final CharSequence[] items, final DialogInterface.OnClickListener listener) {
		AlertDialog.Builder alert = new AlertDialog.Builder(activity);
		alert.setTitle(activity.getString(R.string.dialog_default_title));
		alert.setItems(items, listener);
		
		return alert.create();
	}
	
	/**
	 * Creates an error dialog managed by the given activity and id, with the given message.
	 * @param activity
	 * @param id
	 * @param message
	 * @return Dialog
	 */
	public static Dialog createErrorDialog(final Activity activity, final int id, 
			final String message) {
		AlertDialog.Builder alert = new AlertDialog.Builder(activity);

		alert.setCancelable(false);
		alert.setTitle(R.string.error);
		alert.setMessage(message);
		
		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int whichButton) {
				dismissDialogSafely(activity, id);
			}
		});
		
		return alert.create();		
	}
	
	/**
	 * Creates a confirmation dialog managed by the given activity, with the given title and message,
	 * the given listener passed to the positive button and the negative button dismissing the dialog.
	 * @param activity
	 * @param id
	 * @param message
	 * @param listener
	 * @return Dialog
	 */
	public static Dialog createConfirmDeleteDialog(final Activity activity, final int id, 
			final String message, final DialogInterface.OnClickListener listener) {
		AlertDialog.Builder alert = new AlertDialog.Builder(activity);

		alert.setCancelable(true);
		alert.setTitle(activity.getString(R.string.dialog_default_title));
		alert.setMessage(message);
		
		alert.setPositiveButton(R.string.ok, listener);
		
		alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int whichButton) {
				dismissDialogSafely(activity, id);
			}
		});
		
		return alert.create();
	}
	
	/**
	 * Dismisses the dialog with the given id managed by the given activity, catching
	 * and logging the IllegalArgumentException thrown if the dialog was never shown.
	 * @param activity
	 * @param id
	 */
	public static void dismissDialogSafely(final Activity activity, final int id) {
		try {
			activity.dismissDialog(id);
		} catch(IllegalArgumentException e) {
			// No reason to crash the app just because the dialog was never shown?
			Log.d(Inetify.LOG_TAG, String.format("Dismissed dialog never shown with id %s: %s", id, e.getMessage()));
		}
	}
	
	/**
	 * Removes the dialog with the given id managed by the given activity, catching
	 * and logging the Exception thrown if the dialog was never shown.
	 * @param activity
	 * @param id
	 */
	public static void removeDialogSafely(final Activity activity, final int id) {
		try {
			activity.removeDialog(id);
		} catch(Exception e) {
			// No reason to crash the app just because the dialog was never shown?
			Log.d(Inetify.LOG_TAG, String.format("Removed dialog never shown with id %s: %s", id, e.getMessage()));
		}
	}

}
