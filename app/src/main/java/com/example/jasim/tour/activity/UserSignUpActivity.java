package com.example.jasim.tour.activity;

import android.Manifest;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jasim.tour.R;
import com.example.jasim.tour.database.UserDbSource;
import com.example.jasim.tour.databinding.ActivityUserSignUpBinding;
import com.example.jasim.tour.model.User;

public class UserSignUpActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityUserSignUpBinding binding;
    long userId;
    String imageUrl = "";
    UserDbSource userDbSource;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDbSource = new UserDbSource(this);
        userId = getIntent().getIntExtra("userId", 0);
        if (userId != 0) {
            user = userDbSource.getUser(userId);
            AlertDialog.Builder dialog;
            //checking android version for lollipop
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dialog = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
            } else {
                dialog = new AlertDialog.Builder(this);
            }
            final EditText editText = new EditText(this);
            editText.setGravity(Gravity.CENTER);
            TextView title = new TextView(this);
            title.setText("Your Password Please");
            title.setGravity(Gravity.CENTER);
            title.setTextSize(20);
            dialog.setCustomTitle(title)
                    .setCancelable(false).setView(editText)
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (!editText.getText().toString().trim().equals(user.getPassword())) {
                                        dialogInterface.dismiss();
                                        finish();
                                        Toast.makeText(getApplicationContext(), "Your Password Wrong.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        dialogInterface.dismiss();
                                        mainView();
                                    }

                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            });
            dialog.show();

        } else mainView();


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

    protected void mainView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_sign_up);
        setSupportActionBar(binding.toolbar.toolBar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("User Registration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (userId != 0) {
            String title = user.getUserName().toUpperCase() + " current info:";
            getSupportActionBar().setTitle(title);
            binding.registerName.setText(user.getUserName());
            binding.registerPassword.setText(user.getPassword());
            binding.repeatPassword.setText(user.getPassword());
            binding.registerNumber.setText(user.getMobileNo());
            binding.registerRecoveryData.setText(user.getRecoveryInfo());
            imageUrl = user.getUserImage();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap bitmap = BitmapFactory.decodeFile(imageUrl, options);
            if (bitmap == null)
                bitmap = BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.no_image);
            binding.profileImage.setImageBitmap(bitmap);
            binding.profileImage.setVisibility(View.VISIBLE);
        }
        binding.registerImageAddBtn.setOnClickListener(this);
        binding.registerBtn.setOnClickListener(this);
        binding.registerName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String userName = binding.registerName.getText().toString().trim();
                if (!hasFocus && !userName.equals("")) {
                    if (userDbSource.checkingDuplicate(userName)) {
                        binding.availability.setText(R.string.user_available);
                        binding.availability.setTextColor(Color.BLUE);
                        binding.availability.setError(null);
                        binding.availability.setVisibility(View.VISIBLE);
                        binding.registerBtn.setVisibility(View.VISIBLE);
                    } else {
                        binding.availability.setText(R.string.user_not_available);
                        binding.availability.setTextColor(Color.RED);
                        binding.availability.setError("");
                        binding.availability.setVisibility(View.VISIBLE);
                        binding.registerBtn.setVisibility(View.GONE);
                    }
                }
            }
        });
        binding.repeatPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String password = binding.registerPassword.getText().toString().trim();
                String repeatPassword = binding.repeatPassword.getText().toString().trim();
                if (!b) {
                    if (!password.equals(repeatPassword))
                        binding.repeatPassword.setError("Password do't matched");
                    else binding.repeatPassword.setError(null);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.registerImageAddBtn:
                if (ActivityCompat.checkSelfPermission(UserSignUpActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UserSignUpActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                    return;
                }
                if (!imageUrl.equals("")) {
                    AlertDialog.Builder dialog;
                    //checking android version for lollipop
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        dialog = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
                    } else {
                        dialog = new AlertDialog.Builder(this);
                    }
                    dialog.setMessage("Are you sure you want to override Current Image? ").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            openGallery();
                            dialogInterface.dismiss();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    dialog.show();
                } else openGallery();

                break;

            case R.id.registerBtn:
                String name = binding.registerName.getText().toString().trim();
                String password = binding.registerPassword.getText().toString().trim();
                String repeatPassword = binding.repeatPassword.getText().toString().trim();
                String number = binding.registerNumber.getText().toString().trim();
                String recovery = binding.registerRecoveryData.getText().toString().trim();
                if (name.equals("") || password.length() < 4 || repeatPassword.equals("")
                        || number.length() < 11 || recovery.equals("") ||
                        !password.equals(repeatPassword)) {
                    if (recovery.equals("")) {
                        binding.registerRecoveryData.setError("Recovery info required");
                        binding.registerRecoveryData.requestFocus();
                    }
                    if (number.length() < 11) {
                        binding.registerNumber.setError("Invalid Number");
                        binding.registerNumber.requestFocus();
                    }
                    if (repeatPassword.equals("")) {
                        binding.repeatPassword.setError("Repeat Password Require");
                        binding.repeatPassword.requestFocus();
                    }
                    if (password.length() < 4) {
                        binding.registerPassword.setError("Password too short");
                        binding.registerPassword.requestFocus();
                    }
                    if (name.equals("")) {
                        binding.registerName.setError("Name required");
                        binding.registerRecoveryData.requestFocus();
                    }
                    if (!password.equals(repeatPassword)) {
                        binding.repeatPassword.setError("Password don't matched");
                        binding.repeatPassword.requestFocus();
                    }
                } else {
                    User user = new User(name, password, recovery, imageUrl, number);
                    boolean success = userDbSource.userRegistration(user);
                    if (success) {
                        finish();
                    } else {
                        AlertDialog.Builder dialog;
                        //checking android version for lollipop
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            dialog = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
                        } else {
                            dialog = new AlertDialog.Builder(this);
                        }
                        dialog.setMessage("Registration Error Try Again....").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                            }
                        });
                        dialog.show();
                    }
                }
                break;

            default:
                break;
        }
    }

    private void openGallery() {
        startActivityForResult(new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else Toast.makeText(UserSignUpActivity.this,
                "You Rejects The Permission", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        binding.registerBtn.requestFocus();
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            Uri selectImage = data.getData();
            imageUrl = getRealPathFromURI(selectImage);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap bitmap = BitmapFactory.decodeFile(imageUrl, options);
            binding.profileImage.setImageBitmap(bitmap);
            binding.profileImage.setVisibility(View.VISIBLE);
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] mediaLocationGetter = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, mediaLocationGetter, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

}
