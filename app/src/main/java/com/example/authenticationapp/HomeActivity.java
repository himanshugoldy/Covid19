package com.example.authenticationapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.authenticationapp.ml.Model;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

public class HomeActivity extends AppCompatActivity {


    private ImageView imgView;
    private Button select, predict,logout;
    private TextView tv;
    private Bitmap img;
    FirebaseAuth fAuth;

    ActivityResultLauncher<Intent> activityResultLauncherGallery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        imgView = (ImageView) findViewById(R.id.imageView);
        tv = (TextView) findViewById(R.id.textView);
        select = (Button) findViewById(R.id.button);
        predict = (Button) findViewById(R.id.button2);
        logout = (Button) findViewById(R.id.logout);
        fAuth = FirebaseAuth.getInstance();

        activityResultLauncherGallery = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                (ActivityResult result) -> {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Get the url of the image from data
                        Uri selectedImageUri = result.getData().getData();
                        if (null != selectedImageUri) {
                            // update the preview image in the layout
                            imgView.setImageURI(selectedImageUri);
                            try {
                                img = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            predict.setVisibility(View.VISIBLE);
                        }else{
                            Toast.makeText(HomeActivity.this, "Photo not uploaded", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        Toast.makeText(HomeActivity.this, "Photo not uploaded", Toast.LENGTH_SHORT)
                                .show();
                    }
                });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imagePickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncherGallery.launch(imagePickerIntent);
            }
        });

        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                img = Bitmap.createScaledBitmap(img, 224, 224, true);

                try {
                    Model model = Model.newInstance(getApplicationContext());

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
                    TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                    tensorImage.load(img);
                    ByteBuffer byteBuffer = tensorImage.getBuffer();
                    inputFeature0.loadBuffer(byteBuffer);

                    // Runs model inference and gets result.
                    Model.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    // Releases model resources if no longer used.
                    model.close();
                    if (outputFeature0.getFloatArray()[0] < 0.5) {
//                        tv.setText("Result:- You have Covid");
                        tv.setText(Html.fromHtml("<font color= '#FF0000'><b>You Have Covid</b><br></font>"));
                    } else {
//                        tv.setText("Result:- You are Normal");
                        tv.setText(Html.fromHtml("<font color= '#00FF00'><b>You are Normal</b><br></font>"));
                    }

//                    tv.setText(outputFeature0.getFloatArray()[0]+"You have covid");

                } catch (IOException e) {
                    Toast.makeText(HomeActivity.this, "Error!"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                startActivity(new Intent(getApplicationContext(),Login.class));
                Toast.makeText(HomeActivity.this, "Successfully Logged Out", Toast.LENGTH_SHORT).show();
                finish();
            }
        });





    }
}