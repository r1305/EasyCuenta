package com.solution.tecno.androidanimations.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.database.DatabaseReference;
import com.solution.tecno.androidanimations.Firebase.Constants;
import com.solution.tecno.androidanimations.R;
import com.solution.tecno.androidanimations.activities.MainActivity;
import com.solution.tecno.androidanimations.model.Usuario;
import com.solution.tecno.androidanimations.utils.Credentials;
import com.solution.tecno.androidanimations.utils.Preferences;
import com.solution.tecno.androidanimations.utils.Utils;
import com.solution.tecno.androidanimations.utils.ViewDialog;

import de.hdodenhof.circleimageview.CircleImageView;
import in.shadowfax.proswipebutton.ProSwipeButton;

public class ProfileFragment extends Fragment {
    ProSwipeButton proSwipeBtn;
    public static final int PICK_IMAGE = 1;
    Context ctx;
    ViewDialog viewDialog;
    Utils utils;
    EditText prof_name,prof_phone,prof_email;
    Credentials cred;
    String user_id;
    String login_status="";
    CircleImageView prof_photo;
    String photo_path;
    boolean new_photo=false;
    public static int MY_PERMISSIONS_REQUEST_ACCESS= 1;
    Usuario usuario;
    FirebaseUser user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx=this.getContext();
        viewDialog = new ViewDialog((MainActivity)ctx);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        cred = new Credentials(ctx);
        utils = new Utils(ctx);
        user_id = cred.getData(Preferences.USER_ID);
        login_status = cred.getLoginStatus();
        user = FirebaseAuth.getInstance().getCurrentUser();
        proSwipeBtn = v.findViewById(R.id.profile_btn_save);
        prof_name = v.findViewById(R.id.profile_et_name);
        prof_phone = v.findViewById(R.id.profile_et_phone);
        prof_email = v.findViewById(R.id.profile_et_email);
        prof_photo = v.findViewById(R.id.profile_photo);

        setData();

        prof_photo.setOnClickListener(view -> checkPermissions());

        proSwipeBtn.setOnSwipeListener(() -> {
            // user has swiped the btn. Perform your async operation now
            String name = prof_name.getText().toString();
            String phone = prof_phone.getText().toString();
            String email = prof_email.getText().toString();

            if(name.isEmpty()){
                prof_name.setError("Complete el nombre");
                prof_name.requestFocus();
                proSwipeBtn.showResultIcon(false);
                return;
            }

            if(email.isEmpty()){
                prof_email.setError("Complete el correo");
                prof_email.requestFocus();
                proSwipeBtn.showResultIcon(false);
                return;
            }else if(!isValidEmail(email)){
                prof_email.setError("Ingrese un correo válido");
                prof_email.requestFocus();
                proSwipeBtn.showResultIcon(false);
                return;
            }

            if(phone.isEmpty()){
                prof_phone.setError("Complete su celular");
                prof_phone.requestFocus();
                proSwipeBtn.showResultIcon(false);
                return;
            }else if(phone.length()<9){
                prof_phone.setError("Ingrese un número de celular válido");
                prof_phone.requestFocus();
                proSwipeBtn.showResultIcon(false);
                return;
            }

            viewDialog.showDialog("Actualizando perfil...");
            updateProfile();
        });
        return v;
    }

    void setData()
    {
        usuario = new Usuario();
        usuario.setCelular(cred.getData(Preferences.USER_PHONE));
        usuario.setCorreo(cred.getData(Preferences.USER_EMAIL));
        usuario.setNombre(cred.getData(Preferences.USER_NAME));
        usuario.setCelular(cred.getData(Preferences.USER_PHONE));

        prof_email.setText(usuario.getCorreo());
        prof_name.setText(usuario.getNombre());
        prof_phone.setText(usuario.getCelular());
    }

    static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == PICK_IMAGE && data!=null) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            photo_path=picturePath;
            cursor.close();

            prof_photo.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            new_photo=true;

        }
    }

    private void checkPermissions(){
        if (ActivityCompat.checkSelfPermission(ctx,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    MY_PERMISSIONS_REQUEST_ACCESS);
        }else{
            Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            i.setType("image/*");
            startActivityForResult(i, PICK_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case 1 :
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    i.setType("image/*");
                    startActivityForResult(i, PICK_IMAGE);
                } else {
                    checkPermissions();
                }
                break;
        }
    }

    boolean updatedEmail = false;
    boolean updateEmail(String email)
    {
        user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    viewDialog.showDialog(task.getException().getMessage());
                    viewDialog.showFail(task.getException().getMessage());
                    viewDialog.hideDialog(3);
                    updatedEmail = false;
                }else{
                    usuario.setCorreo(email);
                    updatedEmail = true;
                }
            }
        });
        return updatedEmail;
    }

    void updateProfile()
    {
        System.out.println("updateProfile");
        if(!prof_email.getText().toString().equals(usuario.getCorreo())){
            if(!updateEmail(prof_email.getText().toString())) {
                viewDialog.showFail("¡Ocurrió un error al actualizar el perfil!");
                viewDialog.hideDialog(3);
                return;
            }
        }
        System.out.println("same_email");

        usuario.setKey(cred.getData(Preferences.USER_ID));
        usuario.setCelular(prof_phone.getText().toString());
        usuario.setNombre(prof_name.getText().toString());
        DatabaseReference reference = utils.getDatabaseReference(Preferences.FIREBASE_USUARIOS);
        reference.child(cred.getData(Preferences.USER_ID)).setValue(usuario);
        cred.saveData(Preferences.USER_EMAIL,usuario.getCorreo());
        cred.saveData(Preferences.USER_PHONE,usuario.getCelular());
        cred.saveData(Preferences.USER_NAME,usuario.getNombre());
        viewDialog.showSuccess("¡Perfil actualizado!");
        viewDialog.hideDialog(3);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                proSwipeBtn.showResultIcon(true);
                FragmentManager fm = ((MainActivity)ctx).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                Fragment fr= AccountsFragment.newInstance();
                fragmentTransaction.replace(R.id.container,fr);
                fragmentTransaction.commit();
            }
        }, 3000);
    }
}
