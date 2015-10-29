package com.yxkang.android.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.yxkang.android.util.SecurityUtil;


public class SecurityActivity extends AppCompatActivity {

    private EditText mEditText1;
    private EditText mEditText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        mEditText1 = (EditText) findViewById(R.id.et_security_before);
        mEditText2 = (EditText) findViewById(R.id.et_security_after);
        findViewById(R.id.bt_security_base64_e).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                base64Encrypt();
            }
        });
        findViewById(R.id.bt_security_base64_d).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                base64Decrypt();
            }
        });
    }

    private void base64Encrypt() {
        String source = mEditText1.getText().toString();
        if (TextUtils.isEmpty(source)) {
            Toast.makeText(this, "待加密的字符串为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        String des = SecurityUtil.encryptBase64(source);
        mEditText2.setText(des);
    }

    private void base64Decrypt() {
        String source = mEditText1.getText().toString();
        if (TextUtils.isEmpty(source)) {
            Toast.makeText(this, "待解密的字符串为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        String des = SecurityUtil.decryptBase64(source);
        mEditText2.setText(des);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_security, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
