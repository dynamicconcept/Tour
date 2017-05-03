package com.example.jasim.tour.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.jasim.tour.R;
import com.example.jasim.tour.database.UserDbSource;
import com.example.jasim.tour.databinding.ActivityPasswordRecoveryBinding;

public class PasswordRecoveryActivity extends AppCompatActivity {

    ActivityPasswordRecoveryBinding binding;
    UserDbSource userDbSource;
    String userName;
    String recoveryData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_password_recovery);
        setSupportActionBar(binding.toolbar.toolBar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Password Recovery");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();

    }

    public void recover(View view) {
        userName = binding.userName.getText().toString().trim();
        recoveryData = binding.passwordRecovery.getText().toString().trim();
        if (userName.equals("") || recoveryData.equals("")) {
            if (recoveryData.equals("")) {
                binding.passwordRecovery.setError("Provide Recovery Info");
                binding.passwordRecovery.requestFocus();
            }
            if (userName.equals("")) {
                binding.userName.setError("No User Name");
                binding.userName.requestFocus();
            }
        } else {
            userDbSource = new UserDbSource(this);
            String password = userDbSource.getPassword(userName, recoveryData);
            switch (password){
                case "no@user":
                    binding.userName.setError("User Name Wrong");
                    binding.userName.requestFocus();
                    break;
                case "wrong@data":
                    binding.passwordRecovery.setError("Recovery Info Wrong");
                    binding.passwordRecovery.requestFocus();
                    break;
                default:
                    binding.userPass.setVisibility(View.VISIBLE);
                    binding.userName.setText("");
                    binding.userName.setError(null);
                    binding.passwordRecovery.setText("");
                    binding.passwordRecovery.setError(null);
                    binding.userPass.setText(password);
                    binding.userPass.requestFocus();
                    break;
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
        }
        return (super.onOptionsItemSelected(menuItem));
    }
}
