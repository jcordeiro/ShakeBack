package com.jcordeiro.shakeback;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Jon Cordeiro (http://www.github.com/jcordeiro) on 22/06/15.
 */
public class ShakeBack {

    private static ShakeBack instance;

    private static SensorManager sensorManager;
    private static Dialog dialog;

    private static boolean isDialogUp;

    private String PACKAGE_NAME;
    private static Context context;

    /* Shake acceleration values */
    private static float acceleration;
    private static float currentAcceleration;
    private static float lastAcceleration;

    /* Shake sensitivity thresholds */
    public enum ShakeSensitivity {
        EASY,
        NORMAL,
        HARD
    }

    final public int SHAKE_THRESHOLD_EASY = 6;
    final public int SHAKE_THRESHOLD_NORMAL = 12;
    final public int SHAKE_THRESHOLD_HARD = 18;

    /* Default settings */
    final private int SHAKE_THRESHOLD_DEFAULT = SHAKE_THRESHOLD_NORMAL;

    final private String FEEDBACK_EMAIL_ADDRESS_DEFAULT = "";
    private String FEEDBACK_EMAIL_SUBJECT_DEFAULT = ""; // Not final. Value set in constructor below

    final private boolean VIBRATION_ENALBED_DEFAULT = false;
    final private int VIBRATION_DURATION_DEFAULT = 100;

    final private int DIALOG_ICON_ID_DEFAULT = R.drawable.ic_phone_shake_grey;
    final private String DIALOG_TITLE_DEFAULT = "Send feedback to developer?";
    final private String DIALOG_MESSAGE_DEFAULT = "By shaking the device you can send us some feedback and your thoughts about our app. Your feedback helps us improve the app and continue building a better experience for you";

    /* Vibration settings */
    private boolean vibrationEnabled = VIBRATION_ENALBED_DEFAULT;
    private int vibrationDuration = VIBRATION_DURATION_DEFAULT;

    /* Email settings */
    private String feedbackEmailAddress = FEEDBACK_EMAIL_ADDRESS_DEFAULT;
    private String feedbackEmailSubject = null;

    /* Shake sensitivity settings */
    private int shakeThreshold = SHAKE_THRESHOLD_DEFAULT;

    /* Alert dialog settings */
    private int dialogIconId = DIALOG_ICON_ID_DEFAULT;
    private String dialogTitle = DIALOG_TITLE_DEFAULT;
    private String dialogMessage = DIALOG_MESSAGE_DEFAULT;

    /* Handles the shake event */
    private static SensorEventListener sensorListener;

    /**
     * Create a SensorEventListener to handle the shake events using the given context
     */
    public SensorEventListener createSensorListener(final Context newContext) {

        return new SensorEventListener() {

            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

            /* Calculate acceleration */
                lastAcceleration = currentAcceleration;
                currentAcceleration = (float) Math.sqrt((double) (x * x + y * y + z * z));
                float delta = currentAcceleration - lastAcceleration;
                acceleration = (acceleration * 0.9f) + delta; // perform low-cut filter

                if (acceleration > instance.shakeThreshold) {

                /* Don't show the dialog if it's already on the screen */
                    if (!isDialogUp) {
                        displayEmailPrompt();

                    /* Vibrates the devices if setting for vibration is enabled */
                        if (instance.vibrationEnabled) {
                            Vibrator v = (Vibrator) newContext.getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(instance.vibrationDuration);
                        }
                    }
                }
            }

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
    }

    /* Default constructor */
    public ShakeBack() {
    }

    /* Constructor */
    public ShakeBack(Context newContext) {
        instance = new ShakeBack();

        context = newContext;
        sensorManager = (SensorManager) newContext.getSystemService(Context.SENSOR_SERVICE);
        sensorListener = createSensorListener(newContext);
        acceleration = 0.00f;
        activate();

        /* If you are planning to support your app on different celestial bodies,
           you'll have to change these lines. By default only Earth is supported */
        currentAcceleration = SensorManager.GRAVITY_EARTH;
        lastAcceleration = SensorManager.GRAVITY_EARTH;

        PACKAGE_NAME = newContext.getPackageName();

        FEEDBACK_EMAIL_SUBJECT_DEFAULT = "Feedback: " + PACKAGE_NAME;
        instance.feedbackEmailSubject = FEEDBACK_EMAIL_SUBJECT_DEFAULT;
    }

    /* Constructor */
    public ShakeBack(Context newContext, String emailAddress, String emailSubject) {
        this(newContext);

        context = newContext;
        instance.feedbackEmailAddress = emailAddress;
        instance.feedbackEmailSubject = emailSubject;
    }

    protected static ShakeBack initialize(Context newContext) {
        instance = new ShakeBack(newContext);
        return instance;
    }

    /**
     * Sets the email settings
     */
    public static ShakeBack initialize(Context newContext, String emailAddress, String emailSubject) {
        if (instance == null) {
            instance = new ShakeBack(newContext, emailAddress, emailSubject);
            return instance;
        }

        instance.feedbackEmailAddress = emailAddress;
        instance.feedbackEmailSubject = emailSubject;
        return instance;
    }

    /**
     * Registers sensor manager's event listener
     */
    public static void activate() {
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Unregisters sensor manager's event listener
     */
    public static void deactivate() {
        sensorManager.unregisterListener(sensorListener);
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * Starts an intent to let the user send an email
     */
    private static void sendFeedbackEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{instance.feedbackEmailAddress});
            intent.putExtra(Intent.EXTRA_SUBJECT, instance.feedbackEmailSubject);
            context.startActivity(intent);
            dialog.cancel();
        }
    }

    /**
     * Displays the alert dialog on the screen - prompts the user to email their feedback
     */
    private static void displayEmailPrompt() {

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        /* Inflate the dialog's layout and get the views */
        dialog.setContentView(R.layout.dialog_email_prompt);
        ImageView dialogIcon = (ImageView) dialog.findViewById(R.id.shakeback_dialog_icon);
        TextView dialogTitle = (TextView) dialog.findViewById(R.id.shakeback_dialog_title);
        TextView dialogMessage = (TextView) dialog.findViewById(R.id.shakeback_dialog_message);
        Button dismissButton = (Button) dialog.findViewById(R.id.shakeback_dismiss_button);
        Button sendButton = (Button) dialog.findViewById(R.id.shakeback_send_button);

        dialogIcon.setImageResource(instance.dialogIconId);
        dialogTitle.setText(instance.dialogTitle);
        dialogMessage.setText(instance.dialogMessage);

        /* "Dismiss" button  clicked */
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        /* "Send Feedback" button clicked */
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedbackEmail();
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                isDialogUp = false;
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isDialogUp = false;
            }
        });

        dialog.show();
        isDialogUp = true;
    }

    /**
     * Sets whether or not device vibration is enabled
     */
    public ShakeBack setVibrationEnabled(boolean enabled) {
        instance.vibrationEnabled = enabled;
        return instance;
    }

    /**
     * Sets the duration of the vibration in milliseconds
     */
    public ShakeBack setVibrationDuration(int duration) {
        instance.vibrationDuration = duration;
        return instance;
    }

    /**
     * Sets the sensitivity of a shake event being triggered
     */
    public ShakeBack setShakeSensitivity(ShakeSensitivity sensitivity) {

        switch (sensitivity) {
            case EASY:
                instance.shakeThreshold = SHAKE_THRESHOLD_EASY;
                break;
            case NORMAL:
                instance.shakeThreshold = SHAKE_THRESHOLD_NORMAL;
                break;
            case HARD:
                instance.shakeThreshold = SHAKE_THRESHOLD_HARD;
                break;

            default:
                instance.shakeThreshold = SHAKE_THRESHOLD_NORMAL;
                break;
        }

        return instance;
    }

    /**
     * Sets the email address for the feedback email to be sent to
     */
    public ShakeBack setEmailAddress(String emailAddress) {
        instance.feedbackEmailAddress = emailAddress;
        return instance;
    }

    /**
     * Sets the subject line of the feedback email
     */
    public ShakeBack setEmailSubject(String emailSubject) {
        instance.feedbackEmailSubject = emailSubject;
        return instance;
    }

    /**
     * Sets the icon to be used in the dialog using a drawable resource id
     */
    public ShakeBack setDialogIcon(int drawableId) {
        instance.dialogIconId = drawableId;
        return instance;
    }

    /**
     * Sets the title to be used in the dialog
     */
    public ShakeBack setDialogTitle(String title) {
        instance.dialogTitle = title;
        return instance;
    }

    /**
     * Sets the message to be used in the dialog
     */
    public ShakeBack setDialogMessage(String message) {
        instance.dialogMessage = message;
        return instance;
    }
}
