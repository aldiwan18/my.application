package ac.id.atmaluhur.mahasiswa.travelku;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;

public class RegisterTwoAct extends AppCompatActivity {
    Button next,add;
    ImageButton back;
    ImageView photo;
    EditText name,bio;

    Uri photo_loc;
    Integer photo_max =1;
    DatabaseReference ref;
    StorageReference stor;

    String USERNAME_KEY = "usernamekey";
    String username_key = "";
    String username_key_new =  "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_two);

        getusernamelocal();
        photo = findViewById(R.id.iv_foto);
        add = findViewById(R.id.bt_up);
        next = findViewById(R.id.bt_next);
        back = findViewById(R.id.ib_back);
        name = findViewById(R.id.et_name);
        bio = findViewById(R.id.et_bio);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findPhoto();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backto = new Intent( RegisterTwoAct.this,RegisterAct.class);
                startActivity(backto);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref = FirebaseDatabase.getInstance().getReference().child("Users").child(username_key_new);
                stor = FirebaseStorage.getInstance().getReference().child("PhotoUsers").child(username_key_new);

                if(photo_loc != null){
                    StorageReference storl = stor.child(System.currentTimeMillis()+ " . "+ getFileExtension(photo_loc));
                    storl.putFile(photo_loc).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String uri_photo = taskSnapshot.getDownloadUrl.toString();
                            ref.getRef().child("url_photo").setValue(uri_photo);
                            ref.getRef().child("nama").setValue(name.getText().toString());
                            ref.getRef().child("bio").setValue(name.getText().toString());
                        }
                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            Intent nextto = new Intent(RegisterTwoAct.this, LoginAct.class);
                            startActivity(nextto);
                        }
                    });
                }
            }
        });
    }

    String getFileExtension (Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mtm = MimeTypeMap.getSingleton();
        return mtm.getExtensionFromMimeType(cr.getType(uri));
    }
    public void findPhoto(){
        Intent pic = new Intent();
        pic.setType("image/*");
        pic.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(pic, photo_max);
    }
    public void getusernamelocal(){
        SharedPreferences sp = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
        username_key_new = sp.getString(username_key,"");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == photo_max&& requestCode ==RESULT_OK && data !=null && data.getData() !=null){
            photo_loc = data.getData();

            Picasso.with(this).load(photo_loc).centerCrop().fit().into(photo);
        }
    }
}
