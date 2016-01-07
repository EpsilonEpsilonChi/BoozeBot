package chi.epsilon.epsilon.boozebot;

import android.app.Application;

public class BoozeBotApp extends Application {
    private String currentUser;
    private String isEEX;

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public String getCurrentUser() {

        return currentUser;
    }
}
