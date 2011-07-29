package net.luniks.android.inetify;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView.BufferType;

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
	 * Creates an OK dialog managed by the given activity with the given id, 
	 * with the given title and message.
	 * @param activity
	 * @param id
	 * @param title
	 * @param message
	 * @return AlertDialog
	 */
	public static AlertDialog createOKDialog(final Activity activity, final int id,
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
	 * Creates a context dialog managed by the given activity with the given id, 
	 * with the given items and listener.
	 * @param activity
	 * @param id
	 * @param items
	 * @param listener
	 * @return AlertDialog
	 */
	public static AlertDialog createContextDialog(final Activity activity, final int id,
			final CharSequence[] items, final DialogInterface.OnClickListener listener) {
		AlertDialog.Builder alert = new AlertDialog.Builder(activity);
		alert.setTitle(activity.getString(R.string.dialog_default_title));
		alert.setItems(items, listener);
		
		return alert.create();
	}
	
	/**
	 * Creates an error dialog managed by the given activity with the given id, 
	 * with the given message.
	 * @param activity
	 * @param id
	 * @param message
	 * @return AlertDialog
	 */
	public static AlertDialog createErrorDialog(final Activity activity, final int id, 
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
	 * Creates a confirmation dialog managed by the given activity with the given id, 
	 * with the given message and the given listener passed to the positive button
	 * and the negative button dismissing the dialog.
	 * @param activity
	 * @param id
	 * @param message
	 * @param listener
	 * @return AlertDialog
	 */
	public static AlertDialog createConfirmDialog(final Activity activity, final int id, 
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
	 * Creates an input dialog managed by the given activity with the given id, 
	 * with the given message and the given listener passed to the positive button
	 * and the negative button dismissing the dialog.
	 * @param activity
	 * @param id
	 * @param message
	 * @param listener
	 * @return InputDialog
	 */
	public static InputDialog createInputDialog(final Activity activity, final int id, 
			final String message, final DialogInterface.OnClickListener listener) {
		InputDialog dialog = new InputDialog(activity);
		
		dialog.setCancelable(true);
		dialog.setTitle(activity.getString(R.string.dialog_default_title));
		dialog.setMessage(message);
		
		dialog.setButton(AlertDialog.BUTTON_POSITIVE, activity.getString(R.string.ok), listener);
		
		dialog.setButton(AlertDialog.BUTTON_NEGATIVE, activity.getString(R.string.cancel), 
				new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int whichButton) {
				dismissDialogSafely(activity, id);
			}
		});
		
		return dialog;
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
	
	/**
	 * An AlertDialog with an EditText allowing to enter text.
	 * 
	 * @author torsten.roemer@luniks.net
	 */
	public static class InputDialog extends AlertDialog {
		
		private static final String STATE_BUNDLE_KEY_INPUT_TEXT = "inputText";

		private final EditText input;
		
		protected InputDialog(Context context) {
			super(context);
			input = new EditText(this.getContext());
		}
		
		public void setInputText(final String text) {
			if(text != null) {
				this.input.setText(text, BufferType.NORMAL);
			}
		}
		
		public String getInputText() {
			return String.valueOf(input.getText());
		}
		
		@Override
		public Bundle onSaveInstanceState() {
			Bundle bundle = super.onSaveInstanceState();
			bundle.putString(STATE_BUNDLE_KEY_INPUT_TEXT, String.valueOf(input.getText()));
			return bundle;
		}

		@Override
		protected void onCreate(final Bundle savedInstanceState) {
			this.setView(input, 10, 0, 10, 20);
			
			super.onCreate(savedInstanceState);

			if(savedInstanceState != null) {
				String text = savedInstanceState.getString(STATE_BUNDLE_KEY_INPUT_TEXT);
				if(text != null) {
					this.input.setText(text, BufferType.NORMAL);
				}
			}
			
			this.setOnCancelListener(new OnCancelListener() {
				public void onCancel(final DialogInterface dialog) {
					((InputDialog)dialog).setInputText("");
				}
			});
			
			this.setOnDismissListener(new OnDismissListener() {
				public void onDismiss(final DialogInterface dialog) {
					((InputDialog)dialog).setInputText("");
				}
			});
		}
	}

}
