package geoimage.ret.geoimage;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class Login extends AppCompatActivity {
    View rootview;
    EditText username;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        rootview = findViewById(R.id.loginlayout);

        ParseUser currentUser = ParseUser.getCurrentUser();
        //sign up
        if (currentUser.getCurrentUser() == null) {

            Button button = (Button) findViewById(R.id.button_submituserpass);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    username = (EditText) findViewById(R.id.editText_username);
                    password = (EditText) findViewById(R.id.editText_password);
                    ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                navigateToActivity();
                            } else {
                                makeSnack("No Login.Create New?");
                            }
                        }
                    });
                }
            });
        } else {
            navigateToActivity();
        }

    }

    void makeSnack(String text) {
        Snackbar.make(rootview, text, Snackbar.LENGTH_INDEFINITE).setAction("Yes", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = new ParseUser();
                user.setUsername(username.getText().toString());
                user.setPassword(password.getText().toString());
                // other fields can be set just like with ParseObject
                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            navigateToActivity();
                        } else {
                            // Sign up didn't succeed. Look at the ParseException
                            // to figure out what went wrong
                        }
                    }
                });
            }
        }).show();
    }

    public void navigateToActivity() {
        startActivity(new Intent(Login.this, Main.class));
        finish();
    }
}
