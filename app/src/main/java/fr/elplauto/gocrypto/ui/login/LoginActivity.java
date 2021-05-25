package fr.elplauto.gocrypto.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import fr.elplauto.gocrypto.R;
import fr.elplauto.gocrypto.api.LoginService;
import fr.elplauto.gocrypto.model.LoginStatus;
import fr.elplauto.gocrypto.utils.SessionManager;


public class LoginActivity extends AppCompatActivity implements LoginService.LoginServiceCallbackListener {

    EditText usernameEditText;
    EditText pwdEditText;
    SessionManager sessionManager;
    Button loginBtn;
    LoginService.LoginServiceCallbackListener self = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = SessionManager.getInstance(getApplicationContext());
        usernameEditText = findViewById(R.id.username);
        pwdEditText = findViewById(R.id.password);
        loginBtn = findViewById(R.id.login);

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkEnableButton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pwdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkEnableButton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String username = usernameEditText.getText().toString();
            String password = pwdEditText.getText().toString();
            LoginService.loginOrRegister(getApplicationContext(), self, username, password);
            }
        });

    }

    void checkEnableButton() {
        boolean enable = !usernameEditText.getText().toString().equals("") && !pwdEditText.getText().toString().equals("");
        loginBtn.setEnabled(enable);
    }

    @Override
    public void onLoginServiceCallback(LoginStatus loginStatus) {
        if (loginStatus.getIsLoggedIn()) {
            sessionManager.createLoginSession(loginStatus.getUsername());
        } else {
            LoginActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Incorrect password", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}