package chi.epsilon.epsilon.boozebot;

import android.app.Application;

import com.firebase.client.Firebase;

public class BoozeBotApp extends Application {
    private String currentUser;
    private String isEEX;

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
