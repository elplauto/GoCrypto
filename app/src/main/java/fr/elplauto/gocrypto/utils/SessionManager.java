package fr.elplauto.gocrypto.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import fr.elplauto.gocrypto.LoginActivity;
import fr.elplauto.gocrypto.MainActivity;
import fr.elplauto.gocrypto.R;

public class SessionManager {

    private static final String TAG = "SesionManager";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    private static SessionManager instance;

    private SessionManager(Context context) {
        this.context = context;
        String pref_file_name = context.getString(R.string.preference_file_key);
        this.pref = context.getSharedPreferences(pref_file_name, Context.MODE_PRIVATE);
        this.editor = pref.edit();
    }

    public static SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public void createLoginSession(String username) {
        Log.d(TAG, "username");
        editor.putBoolean("isLoggedIn", true);
        editor.putString("username", username);
        editor.commit();
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public void logout() {
        editor.remove("isLoggedIn");
        editor.remove("username");
        editor.commit();
    }

    public String getUsername() {
        return pref.getString("username", "");
    }

    public boolean isLoggedIn() {
        return pref.getBoolean("isLoggedIn", false);
    }

    public void checkLogin() {
        if(!this.isLoggedIn()){
            Intent i = new Intent(context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

}
