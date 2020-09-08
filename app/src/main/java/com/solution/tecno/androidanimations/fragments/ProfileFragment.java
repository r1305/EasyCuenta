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
import android.os.StrictMode;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.solution.tecno.androidanimations.Firebase.Constants;
import com.solution.tecno.androidanimations.R;
import com.solution.tecno.androidanimations.activities.FirstActivity;
import com.solution.tecno.androidanimations.activities.FirstActivity;
import com.solution.tecno.androidanimations.utils.Credentials;
import com.solution.tecno.androidanimations.utils.Utils;
import com.solution.tecno.androidanimations.utils.ViewDialog;
import com.squareup.picasso.Picasso;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import in.shadowfax.proswipebutton.ProSwipeButton;


public class ProfileFragment extends Fragment {
    ProSwipeButton proSwipeBtn;
    public static final int PICK_IMAGE = 1;
    Context ctx;
    ViewDialog viewDialog;
    EditText prof_name,prof_user_name,prof_phone,prof_psw,prof_email;
    Credentials cred;
    String base_url=Constants.BASE_URL;
    String user_id;
    String user_photo="",photo_id="",login_status="";
    CircleImageView prof_photo;
    String photo_path;
    boolean new_photo=false;
    public static int MY_PERMISSIONS_REQUEST_ACCESS= 1;
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
        viewDialog = new ViewDialog((FirstActivity)ctx);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);;
        cred = new Credentials(ctx);
        user_id = cred.getUserId();
        login_status = cred.getLoginStatus();
        proSwipeBtn = v.findViewById(R.id.profile_btn_save);
        prof_name = v.findViewById(R.id.profile_et_name);
        prof_user_name = v.findViewById(R.id.profile_et_user);
        prof_phone = v.findViewById(R.id.profile_et_phone);
        prof_psw = v.findViewById(R.id.profile_et_password);
        prof_email = v.findViewById(R.id.profile_et_email);
        prof_photo = v.findViewById(R.id.profile_photo);

        prof_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissions();
            }
        });

        getUserProfile(user_id);

        proSwipeBtn.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                // user has swiped the btn. Perform your async operation now
                viewDialog.showDialog();
                String name = prof_name.getText().toString();
                String username = prof_user_name.getText().toString();
                String phone = prof_phone.getText().toString();
                String psw = prof_psw.getText().toString();
                String email = prof_email.getText().toString();
                if(name.isEmpty() && username.isEmpty() && psw.isEmpty() && phone.isEmpty() && email.isEmpty()){
                    prof_name.setError("Complete el nombre");
                    prof_name.requestFocus();
                    prof_user_name.setError("Complete el usuario");
                    prof_user_name.requestFocus();
                    prof_psw.setError("Ingrese contraseña");
                    prof_psw.requestFocus();
                    prof_phone.setError("Ingrese un número celular");
                    prof_phone.requestFocus();
                    prof_email.setError("Ingrese un correo");
                    prof_email.requestFocus();
                    proSwipeBtn.showResultIcon(false);
                    return;
                }
                if(name.isEmpty()){
                    prof_name.setError("Complete el nombre");
                    prof_name.requestFocus();
                    viewDialog.hideDialog(0);
                    new Utils().createAlert(ctx,"Complete el nombre",1);
                    proSwipeBtn.showResultIcon(false);
                    return;
                }
                if(username.isEmpty()){
                    prof_user_name.setError("Complete el usuario");
                    prof_user_name.requestFocus();
                    viewDialog.hideDialog(0);
                    new Utils().createAlert(ctx,"Complete el usuario",1);
                    proSwipeBtn.showResultIcon(false);
                    return;
                }
                if(email.isEmpty()){
                    prof_email.setError("Complete el correo");
                    prof_email.requestFocus();
                    viewDialog.hideDialog(0);
                    new Utils().createAlert(ctx,"Complete el correo",1);
                    proSwipeBtn.showResultIcon(false);
                    return;
                }else if(!isValidEmail(email)){
                    prof_email.setError("Ingrese un correo válido");
                    prof_email.requestFocus();
                    viewDialog.hideDialog(0);
                    new Utils().createAlert(ctx,"Ingrese un correo válido",1);
                    proSwipeBtn.showResultIcon(false);
                    return;
                }
                if(phone.isEmpty()){
                    prof_phone.setError("Complete su celular");
                    prof_phone.requestFocus();
                    viewDialog.hideDialog(0);
                    new Utils().createAlert(ctx,"Complete su celular",1);
                    proSwipeBtn.showResultIcon(false);
                    return;
                }else if(phone.length()<9){
                    prof_phone.setError("Ingrese un número de celular válido");
                    prof_phone.requestFocus();
                    viewDialog.hideDialog(0);
                    new Utils().createAlert(ctx,"Ingrese un número de celular válido",1);
                    proSwipeBtn.showResultIcon(false);
                    return;
                }
                if(psw.isEmpty()){
                    prof_psw.setError("Ingrese contraseña");
                    prof_psw.requestFocus();
                    viewDialog.hideDialog(0);
                    new Utils().createAlert(ctx,"Ingrese contraseña",1);
                    proSwipeBtn.showResultIcon(false);
                    return;
                }

                if(!name.equals("") && !username.equals("") &&
                        !phone.equals("") && !psw.equals("") &&
                        !email.equals("") && isValidEmail(email)){
                    updateUser(user_id,username,phone,name,psw,email);
                }else{
                    viewDialog.hideDialog(0);
                    new Utils().createAlert(ctx,"Complete los datos",1);
                    proSwipeBtn.showResultIcon(false);
                }
            }
        });
        return v;
    }

    public void updateUser(final String id, final String username,
                           final String phone_number,final String full_name,
                           final String psw,final String email) {
        viewDialog.showDialog();
        RequestQueue queue = Volley.newRequestQueue(ctx);
        if(user_photo==""){
            user_photo= Constants.BASE_PHOTO;
        }
        String params="?id="+id+
                "&username="+Uri.encode(username)+
                "&phone="+Uri.encode(phone_number)+
                "&name="+Uri.encode(full_name)+
                "&psw="+Uri.encode(psw)+
                "&email="+email+
                "&photo="+Uri.encode(user_photo)+
                "&photo_id="+photo_id
                ;

        String url = base_url+"updateUser.php"+params;
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if(response.equals("true")){
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                                Fragment fr=ProfileFragment.newInstance();
                                fragmentTransaction.replace(R.id.container,fr);
                                fragmentTransaction.commit();

                                viewDialog.hideDialog(1.5);
                                cred.save_credentials(id,full_name,username,phone_number,email,user_photo,login_status);
                                ((FirstActivity)getContext()).updateHeader(full_name,username,user_photo);
                            }else{
                                viewDialog.hideDialog(0);
                                proSwipeBtn.showResultIcon(false); // false if task failed
                            }
                        } catch (Exception e) {
                            viewDialog.hideDialog(0);
                            new Utils().createAlert(ctx,e.getMessage(),1);
                            proSwipeBtn.showResultIcon(false); // false if task failed
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        viewDialog.hideDialog(0);
                        new Utils().createAlert(ctx,error.getMessage(),1);
                        proSwipeBtn.showResultIcon(false); // false if task failed
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        queue.add(postRequest);
    }

    public void getUserProfile(String user_id) {
        viewDialog.showDialog();

        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?id="+Integer.parseInt(user_id);
        String url = base_url+"getUserProfile.php"+params;
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONParser jp = new JSONParser();
                        try {
                            JSONArray ja=(JSONArray)jp.parse(response);
                            for(int i=0;i<ja.size();i++){
                                JSONObject item=(JSONObject)ja.get(i);
                                String name=item.get("full_name").toString();
                                String user=item.get("username").toString();
                                String psw=item.get("clave").toString();
                                String phone=item.get("phone_number").toString();
                                String email=item.get("email").toString();
                                String url = item.get("profile_photo").toString();
                                photo_id = item.get("photo_id").toString();
                                if(url!="" && !url.isEmpty()){
                                    Picasso.get()
                                            .load(url)
                                            .resize(200, 200)
                                            .centerCrop()
                                            .into(prof_photo);
                                }

                                prof_name.setText(name);
                                prof_user_name.setText(user);
                                prof_psw.setText(psw);
                                prof_phone.setText(phone);
                                prof_email.setText(email);

                                ((FirstActivity)getContext()).updateHeader(name,user,url);
                            }
                            viewDialog.hideDialog(0);
                        } catch (Exception e) {
                            viewDialog.hideDialog(0);
                            new Utils().createAlert(ctx,e.getMessage(),1);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        viewDialog.hideDialog(0);
                        new Utils().createAlert(ctx,error.getMessage(),1);
                    }
                }
        );
        queue.add(postRequest);
    }

    public final static boolean isValidEmail(CharSequence target) {
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
}
