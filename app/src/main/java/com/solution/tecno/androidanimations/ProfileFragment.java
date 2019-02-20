package com.solution.tecno.androidanimations;

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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.cloudinary.Cloudinary;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.utils.ObjectUtils;
import com.solution.tecno.androidanimations.Firebase.Constants;
import com.squareup.picasso.Picasso;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import in.shadowfax.proswipebutton.ProSwipeButton;

import static com.solution.tecno.androidanimations.MainActivity.MY_PERMISSIONS_REQUEST_ACCESS;


public class ProfileFragment extends Fragment {
    ProSwipeButton proSwipeBtn;
    public static final int PICK_IMAGE = 1;
    Context ctx;
    EditText prof_name,prof_user_name,prof_phone,prof_psw,prof_email;
    Credentials cred;
    String base_url=Constants.BASE_URL;
    String user_id;
    AwesomeProgressDialog apd;
    AwesomeSuccessDialog asd;
    AwesomeErrorDialog aed;
    String user_photo="",photo_id="",login_status="";
    CircleImageView prof_photo;
    String photo_path;
    boolean new_photo=false;
    String CLOUDINARY_URL = "cloudinary://285423822327279:0K7-UMpvn21oyqDdKO-xJ_P9_t8@dsdrbqoex";
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
        //create progress dialog
        apd=new AwesomeProgressDialog(ctx)
                .setTitle(R.string.app_name)
                .setMessage("Cargando")
                .setColoredCircle(R.color.dialogInfoBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
                .setCancelable(false);

        //create success dialog
        asd=new AwesomeSuccessDialog(ctx)
                .setTitle(R.string.app_name)
                .setMessage("Listo!")
                .setColoredCircle(R.color.dialogSuccessBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
                .setCancelable(false);

        //create error dialog
        aed=new AwesomeErrorDialog(ctx)
                .setTitle(R.string.app_name)
                .setMessage("Ocurrió un error")
                .setColoredCircle(R.color.dialogErrorBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_error,R.color.white)
                .setCancelable(false);
    }

    public void uploadImage(){
        MediaManager.get().upload(photo_path).callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {
                //delete image
                try{
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    Cloudinary cloudinary=new Cloudinary(CLOUDINARY_URL);
                    Map deleteParams = ObjectUtils.asMap("invalidate", true );
                    cloudinary.uploader().destroy(photo_id,deleteParams );
                }catch (Exception e){
                    Log.d("***",e.toString());
                }
                // your code here
                apd.setMessage("Cargando foto...");
            }
            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {
                // example code starts here
                Double progress = (double) bytes/totalBytes;
                apd.setMessage("Cargando foto..."+Math.round(progress*100)+"%");
                // post progress to app UI (e.g. progress bar, notification)
                // example code ends here
            }
            @Override
            public void onSuccess(String requestId, Map resultData) {
                // your code here
                user_photo=resultData.get("secure_url").toString();
                photo_id=resultData.get("public_id").toString();
                apd.setMessage("Actualizando datos...");
                Picasso.get()
                        .load(user_photo)
                        .resize(200, 200)
                        .centerCrop()
                        .into(prof_photo);

                String name = prof_name.getText().toString();
                String username = prof_user_name.getText().toString();
                String phone = prof_phone.getText().toString();
                String psw = prof_psw.getText().toString();
                String email = prof_email.getText().toString();
                updateUser(user_id,username,phone,name,psw,email);

            }
            @Override
            public void onError(String requestId, ErrorInfo error) {
                // your code here
                apd.hide();
                aed.setMessage(error.getDescription());
                aed.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        aed.hide();
                    }
                }, 3000);
            }
            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
                // your code here
                aed.setMessage(error.getDescription());
                aed.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        aed.hide();
                    }
                }, 3000);
            }})
                .dispatch();
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
                apd.show();
                // user has swiped the btn. Perform your async operation now
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // task success! show TICK icon in ProSwipeButton
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
                            apd.hide();
                            proSwipeBtn.showResultIcon(false);
                            return;
                        }
                        if(name.isEmpty()){
                            prof_name.setError("Complete el nombre");
                            prof_name.requestFocus();
                            apd.hide();
                            proSwipeBtn.showResultIcon(false);
                            return;
                        }
                        if(username.isEmpty()){
                            prof_user_name.setError("Complete el usuario");
                            prof_user_name.requestFocus();
                            apd.hide();
                            proSwipeBtn.showResultIcon(false);
                            return;
                        }
                        if(email.isEmpty()){
                            prof_email.setError("Complete el correo");
                            prof_email.requestFocus();
                            apd.hide();
                            proSwipeBtn.showResultIcon(false);
                            return;
                        }else if(!isValidEmail(email)){
                            prof_email.setError("Ingrese un correo válido");
                            prof_email.requestFocus();
                            apd.hide();
                            proSwipeBtn.showResultIcon(false);
                            return;
                        }
                        if(phone.isEmpty()){
                            prof_phone.setError("Complete su celular");
                            prof_phone.requestFocus();
                            apd.hide();
                            proSwipeBtn.showResultIcon(false);
                            return;
                        }else if(phone.length()<9){
                            prof_phone.setError("Ingrese un número de celular válido");
                            prof_phone.requestFocus();
                            apd.hide();
                            proSwipeBtn.showResultIcon(false);
                            return;
                        }
                        if(psw.isEmpty()){
                            prof_psw.setError("Ingrese contraseña");
                            prof_psw.requestFocus();
                            apd.hide();
                            proSwipeBtn.showResultIcon(false);
                            return;
                        }

                        if(name!="" && username!="" &&
                                phone!="" && psw!="" &&
                                email!="" && isValidEmail(email)){
                            if(new_photo){
                                uploadImage();
                            }else{
                                updateUser(user_id,username,phone,name,psw,email);
                            }
                        }else{
                            apd.hide();
                            proSwipeBtn.showResultIcon(false);
                            return;
                        }
                    }
                }, 1500);
            }
        });
        return v;
    }

    public void updateUser(final String id, final String username,
                           final String phone_number,final String full_name,
                           final String psw,final String email) {

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

                                apd.hide();
                                asd.show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        asd.hide();
                                    }
                                }, 1500);
                                cred.save_credentials(id,full_name,username,phone_number,email,user_photo,login_status);
                                ((FirstActivity)getContext()).updateHeader(full_name,username,user_photo);
                            }else{
                                apd.hide();
                                aed.setMessage("Ocurrió un error");
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        aed.hide();
                                    }
                                }, 3000);
                                proSwipeBtn.showResultIcon(false); // false if task failed
                            }
                        } catch (Exception e) {
                            apd.hide();
                            aed.setMessage(e.getMessage());
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    aed.hide();
                                }
                            }, 3000);
                            proSwipeBtn.showResultIcon(false); // false if task failed
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        apd.hide();
                        aed.setMessage(error.toString());
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                aed.hide();
                            }
                        }, 3000);
                        proSwipeBtn.showResultIcon(false); // false if task failed
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        queue.add(postRequest);
    }

    public void getUserProfile(String user_id) {
        apd.setMessage("Cargando...");
        apd.show();

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
                            apd.hide();
                            asd.show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    asd.hide();
                                }
                            }, 1500);
                        } catch (Exception e) {
                            apd.hide();
                            aed.setMessage(e.getMessage());
                            aed.show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    aed.hide();
                                }
                            }, 3000);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        apd.hide();
                        aed.setMessage(error.toString());
                        aed.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                aed.hide();
                            }
                        },3000);
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
