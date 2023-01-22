package com.example.geoQuiz.geoQuizUI;

import android.content.Context;
import android.widget.Toast;

/**
 * toaster for creating user notifications
 */
public class NotificationToaster {

    private Context ctx;

    /**
     * creates a Toaster object with context of depending activity
     * @param ctx
     */
    public NotificationToaster(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * prints msg to screen
     * @param msg
     */
    public void notifyUserToast(String msg) {
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(this.ctx, msg, duration);
        toast.show();
    }

}
