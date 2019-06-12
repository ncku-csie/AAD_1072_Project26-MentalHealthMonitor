package com.mhmc.mentalhealthmonitor;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;

import android.view.KeyEvent;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.mhmc.mentalhealthmonitor.MYSQL.DBConnector;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @BindView(R.id.input_name)
    EditText _nameText;
    @BindView(R.id.input_address)
    EditText _addressText;
    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_mobile)
    EditText _mobileText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.input_reEnterPassword)
    EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup)
    Button _signupButton;
    @BindView(R.id.link_login)
    TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        //按下註冊鈕
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        //返回註冊畫面
        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    //按下返回鍵回到登入畫面
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // 按下的如果是BACK，同时没有重复
            // Finish the registration screen and return to the Login activity
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }

        return super.onKeyDown(keyCode, event);
    }

    //此部分為註冊過程
    public void signup() {
        Log.d(TAG, "Signup");

        if (validate()) {
            _signupButton.setEnabled(false);
            String name = _nameText.getText().toString();
            String account = _addressText.getText().toString();
            String email = _emailText.getText().toString();
            String mobile = _mobileText.getText().toString();
            String password = _passwordText.getText().toString();
            String reEnterPassword = _reEnterPasswordText.getText().toString();


            //檢查帳號是否存在
            // TODO: Implement your own authentication logic here.
            String Rt = Sign.CheckAccount(account);
            if (!Rt.equals(account)) {
                //Call資料庫儲存資料
                Sign.RegistAccount(name,account,password,mobile,email);
                //loading();

                //註冊成功，返回登入畫面
                Toast.makeText(getBaseContext(), "註冊成功，即將返回登入畫面"+account, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

            } else {
                Toast.makeText(getBaseContext(), "帳號已經存在", Toast.LENGTH_LONG).show();
                _signupButton.setEnabled(true);
            }
        } else {
            onSignupFailed();
            return;
        }
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "資料有誤，請詳細檢查", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String account = _addressText.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 4) {
            _nameText.setError("至少4個字");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (account.isEmpty()) {
            _addressText.setError("Enter Valid Address");
            valid = false;
        } else {
            _addressText.setError(null);
        }


        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (mobile.length() != 10) {
            _mobileText.setError("Enter Valid Mobile Number");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("至少4個字");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }

    private Dialog dialog;
    private void loading() {
        dialog = ProgressDialog.show(this,
                "儲存中", "請等待2秒...", true);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed'
                        dialog.dismiss();
                        // onLoginFailed();
                        //progressDialog.dismiss();
                    }
                }, 2000);
    }
}

class Sign {
    //判斷帳號密碼是否存在
    public static String CheckAccount(String account) {
        String DA = "";
        try {
            String result = DBConnector.executeQuery("http://140.116.82.102:8080/app/checkAccount.php?at="+account+"&pw=0");
                /*
                SQL 結果有多筆資料時使用JSONArray
                只有一筆資料時直接建立JSONObject物件
                JSONObject jsonData = new JSONObject(result);
                */

            JSONArray jsonArray = new JSONArray(result);
            JSONObject jsonData = jsonArray.getJSONObject(0);
            DA = jsonData.getString("Account");
                /*
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                if (jsonData.getString("IDC").equals("1")) {
                    jsonData.getString("NAME");//資料欄位名稱
                }
            }*/

        } catch (Exception e) {
            Log.e("log_tag", e.toString());
        }

        return DA;
    }

    public static void RegistAccount(String name, String account, String password, String mobile, String email){
        try {
            Calendar mCal = Calendar.getInstance();
            CharSequence s = DateFormat.format("yyyy-MM-dd kk:mm:ss", mCal.getTime());
            String result = DBConnector.executeQuery("http://140.116.82.102:8080/app/RegistUser.php?at="+account+"&pw="+password+"&name="+name+"&mobile="+mobile+"&email="+email+"");
            //String query = "http://140.116.82.102:8080/app/InsertNewData.php?Account=" + account + "&time=" + s.toString() + "&content=趕緊寫下您在想甚麼&type=0&test_Anger=0&test_Boredom=0&test_Disgust=0&test_Anxiety=0&test_Happiness=3&test_Sadness=0&test_Surprised=3";
            //result = DBConnector.executeQuery(query);
                /*
                SQL 結果有多筆資料時使用JSONArray
                只有一筆資料時直接建立JSONObject物件
                JSONObject jsonData = new JSONObject(result);
                */

                /*
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                if (jsonData.getString("IDC").equals("1")) {
                    jsonData.getString("NAME");//資料欄位名稱
                }
            }*/
        } catch (Exception e) {
            Log.e("log_tag", e.toString());
        }
    }
}