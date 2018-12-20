package com.example.gusarisna.pratikum.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gusarisna.pratikum.R;
import com.example.gusarisna.pratikum.data.model.BasicRes;
import com.example.gusarisna.pratikum.data.model.GantiProfilRes;
import com.example.gusarisna.pratikum.data.model.Postingan;
import com.example.gusarisna.pratikum.data.model.User;
import com.example.gusarisna.pratikum.data.remote.APIService;
import com.example.gusarisna.pratikum.data.remote.ApiUtils;
import com.example.gusarisna.pratikum.data.remote.FilePath;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GantiFotoProfil extends AppCompatActivity {

    @BindView(R.id.foto_profil_cl)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.user_fotoprofil)
    CircleImageView fotoProfil;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nama_file)
    TextView fileGambar;
    ProgressDialog pDialog;
    APIService mAPIService;
    SharedPreferences userPrefs;
    Postingan postingan;
    String apiToken;
    User user;
    String BASE_URL_IMAGE;
    String URL_UPLOAD;

    Uri selectedImage;
    String mediaPath;
    String postPath;

    //Image request code
    private int REQUEST_PICK_IMAGE = 1;
    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ganti_foto_profil);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle("Edit Profil");
        setUserAndToken();
        BASE_URL_IMAGE = ApiUtils.BASE_URL + "foto_profil/";
        URL_UPLOAD = ApiUtils.BASE_URL + "api/user/foto_profil";

        initDialog();
        refreshFotoProfil();
        requestStoragePermission();
        mAPIService = ApiUtils.getAPIService();
    }

    public void refreshFotoProfil(){
        Log.d("DEVELOP", user.getFotoProfil());
        BASE_URL_IMAGE = ApiUtils.BASE_URL + "foto_profil/";
        if(!user.getFotoProfil().equals("default.png")){
            String url = BASE_URL_IMAGE + user.getFotoProfil();
            Glide.with(this)
                    .load(url)
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.ic_user_round).centerCrop())
                    .into(fotoProfil);
        }
    }

    public void setUserAndToken(){
        userPrefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        user = new User();
        user.setId(userPrefs.getInt("userId", 0));
        user.setNama(userPrefs.getString("userNama", ""));
        user.setEmail(userPrefs.getString("userEmail", ""));
        user.setFotoProfil(userPrefs.getString("userFotoProfil", ""));
        apiToken = userPrefs.getString("apiToken", "");
    }

    protected void initDialog() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Sedang Memproses..");
        pDialog.setCancelable(true);
    }


    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }

    @OnClick(R.id.btn_select)
    public void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), REQUEST_PICK_IMAGE);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                // Get the Image from data
                Uri selectedImage = data.getData();

                mediaPath = FilePath.getPath(this, selectedImage);
                // Set the Image in ImageView for Previewing the Media
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                fotoProfil.setImageBitmap(bitmap);

                postPath = mediaPath;
                fileGambar.setText(postPath);
            }
        }
    }

    public String getPath(Uri uri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = getContentResolver().query(uri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @OnClick(R.id.btn_upload)
    public void uploadFile() {
        if (postPath == null || postPath.equals("")) {
            Toast.makeText(this, "please select an image ", Toast.LENGTH_LONG).show();
            return;
        } else {
            showpDialog();

            // Map is used to multipart the file using okhttp3.RequestBody
            Map<String, RequestBody> map = new HashMap<>();
            File file = new File(postPath);

            // Parsing any Media type file
            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            map.put("img\"; filename=\"" + file.getName() + "\"", requestBody);

            mAPIService.gantiFotoProfil("Bearer " + apiToken, map).enqueue(new Callback<GantiProfilRes>() {
                @Override
                public void onResponse(Call<GantiProfilRes> call, Response<GantiProfilRes> response) {
                    hidepDialog();
                    if(response.body().isStatus()){

                        SharedPreferences userPrefs = getSharedPreferences("user", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = userPrefs.edit();
                        editor.putString("userFotoProfil", response.body().getFotoProfil());
                        editor.apply();

                        Log.d("DEVELOP",response.body().getFotoProfil() );

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(GantiFotoProfil.this);
                        alertBuilder.setMessage("Foto Profil Berhasil Diganti")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                        AlertDialog alertDialog = alertBuilder.create();
                        alertDialog.show();
                    } else {
                        Toast.makeText(getApplicationContext(), response.body().getPesan(), Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<GantiProfilRes> call, Throwable t) {
                    hidepDialog();
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }
}
