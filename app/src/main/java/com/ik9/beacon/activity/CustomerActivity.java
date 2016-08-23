package com.ik9.beacon.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ik9.beacon.R;
import com.ik9.beacon.repository.ProfileEntity;
import com.ik9.beacon.repository.ProfileRepository;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static com.ik9.beacon.R.menu.activity_customer_drawer;

public class CustomerActivity extends AppCompatActivity {

    //    private Button btnPhoto;
    private EditText txtBirth;
    private EditText txtCity;
    private EditText txtEmail;
    private EditText txtName;
    private EditText txtPhone;
    private ImageView imgPhoto;
    private Spinner cmbGender;
    private Spinner cmbState;
    private Uri fileUri;

    private ArrayAdapter<CharSequence> adpGender;
    private ArrayAdapter<CharSequence> adpStates;
    private GoogleApiClient client;
    private ProfileEntity profileEntity;
    private ProfileRepository profileRepository;

    private int RotateAngle = 0;
    private String ImageFilePath;

    private final Context context = this;
    private static final boolean NOT_SILHOUETTE = false;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        btnPhoto = (Button) findViewById(R.id.btnPhoto);
        setSupportActionBar(toolbar);

        imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
        imgPhoto.setOnTouchListener(new RotateImage());
        imgPhoto.setImageBitmap(loadSilhouette());

        adpGender = ArrayAdapter.createFromResource(this, R.array.genders, android.R.layout.simple_spinner_item);
        adpGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbGender = (Spinner) findViewById(R.id.cmbGender);
        cmbGender.setAdapter(adpGender);
        cmbGender.setFocusable(true);
        cmbGender.setFocusableInTouchMode(true);
        cmbGender.setOnTouchListener(new HideKeyboard());

        adpStates = ArrayAdapter.createFromResource(this, R.array.states, android.R.layout.simple_spinner_item);
        adpStates.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbState = (Spinner) findViewById(R.id.cmbState);
        cmbState.setAdapter(adpStates);
        cmbState.setFocusable(true);
        cmbState.setFocusableInTouchMode(true);
        cmbState.setOnTouchListener(new HideKeyboard());
        cmbState.setOnFocusChangeListener(new HideKeyboard());

        txtBirth = (EditText) findViewById(R.id.txtBirth);
        txtBirth.setInputType(InputType.TYPE_NULL);
        txtBirth.setOnClickListener(new CallDatePicker());
        txtBirth.setOnFocusChangeListener(new CallDatePicker());

        txtCity = (EditText) findViewById(R.id.txtCity);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtName = (EditText) findViewById(R.id.txtName);
        txtPhone = (EditText) findViewById(R.id.txtPhone);

//        btnPhoto.setFocusable(true);
//        btnPhoto.setFocusableInTouchMode(true);
//        btnPhoto.requestFocus();
//        btnPhoto.setOnClickListener(new OpenPhotoChooser());

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        profileRepository = new ProfileRepository(context);
        loadCustomerData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(activity_customer_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnSave:
                confirmSave();
                return true;
            case R.id.btnPhotoChooser:
                showPhotoChooser();
                return true;
            case R.id.btnUpdateData:
                releaseControls(true);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        ImageFilePath = "";

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 4;
                    Bitmap photo = BitmapFactory.decodeFile(fileUri.getPath(), options);

                    ImageFilePath = fileUri.getPath();

                    imgPhoto.setVisibility(View.GONE);
                    imgPhoto.setVisibility(View.VISIBLE);
                    imgPhoto.setImageBitmap(getCircleBitmap(photo, NOT_SILHOUETTE));
                    imgPhoto.setRotation(analyseImage(photo));
                } catch (Exception ex) {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            fileUri = data.getData();
            ImageFilePath = fileUri.getPath();

            try {
                Bitmap photo = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);

                imgPhoto.setVisibility(View.GONE);
                imgPhoto.setVisibility(View.VISIBLE);
                imgPhoto.setImageBitmap(getCircleBitmap(photo, NOT_SILHOUETTE));
                imgPhoto.setRotation(analyseImage(fileUri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Customer Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    private Bitmap getCircleBitmap(Bitmap bitmap, boolean isSilhouette) {

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        bitmapWidth = bitmapWidth > bitmapHeight ? bitmapHeight : bitmapWidth;
        bitmapHeight = bitmapHeight > bitmapWidth ? bitmapWidth : bitmapHeight;

        final Bitmap output = Bitmap.createBitmap(bitmapWidth,
                bitmapHeight, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmapWidth, bitmapHeight);
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        //int left = isSilhouette ? 0 : -150;
        int left;
        left = isSilhouette ? 0 :
                bitmap.getWidth() > bitmap.getHeight() ?
                        Math.round((bitmap.getWidth() - bitmap.getHeight()) / 2) * -1 : 0;

        int top;
        top = isSilhouette ? 0 :
                bitmap.getWidth() < bitmap.getHeight() ?
                        Math.round((bitmap.getHeight() - bitmap.getWidth()) / 2) * -1 : 0;

        canvas.drawBitmap(bitmap, left, top, paint);

        bitmap.recycle();

        return output;
    }

    private Bitmap loadSilhouette() {
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_account_circle_grey_300_48dp);
        return getCircleBitmap(bitmap, !NOT_SILHOUETTE);
    }

    private File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private float analyseImage(Bitmap bitmap) {
        float result = 0;

        try {
            ExifInterface exifInterface = new ExifInterface(ImageFilePath);

            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            switch (orientation) {
                case ExifInterface.ORIENTATION_UNDEFINED:
                    result = 0;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    result = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    result = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    result = 270;
                    break;
            }
        } catch (Exception ex) {
            Toast.makeText(context, "Falha ao analisar a image.", Toast.LENGTH_LONG).show();
        }

        return result;
    }

    private float analyseImage(Uri uri) {
        String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.ORIENTATION};

        Cursor cursor = getContentResolver().query(uri, columns, null, null, null);

        if (cursor == null) {
            return 0;
        }

        cursor.moveToFirst();

        int orientationColumnIndex = cursor.getColumnIndex(columns[1]);
        int orientation = cursor.getInt(orientationColumnIndex);

        return orientation;
    }

    public String getImagePath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private void callCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private void callGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Selecione a foto"), PICK_IMAGE_REQUEST);
    }

    private void confirmSave() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Confirma salvar os dados ?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                prepareCustomerData();
                save();
            }
        });

        alertDialog.setNegativeButton("Não", null);

        alertDialog.create().show();
    }

    private void loadCustomerData() {
        profileEntity = profileRepository.Search();

        if ((profileEntity.getID() != "0") && (profileEntity.getID() != "") && ((profileEntity.getID() != null))) {

            int index;

            index = adpGender.getPosition(profileEntity.getGender());
            cmbGender.setSelection(index);

            index = adpStates.getPosition(profileEntity.getState());
            cmbState.setSelection(index);

            ImageFilePath = profileEntity.getUriPhoto();

            if (!ImageFilePath.equals("")) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                File imgFile = new File(ImageFilePath);
                Bitmap photo = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
                imgPhoto.setVisibility(View.GONE);
                imgPhoto.setVisibility(View.VISIBLE);
                imgPhoto.setImageBitmap(getCircleBitmap(photo, NOT_SILHOUETTE));
                imgPhoto.setRotation(analyseImage(photo));
            }

            txtBirth.setText(profileEntity.getBirth());
            txtCity.setText(profileEntity.getCity());
            txtEmail.setText(profileEntity.getEmail());
            txtName.setText(profileEntity.getName());
            txtPhone.setText(profileEntity.getPhone());

            releaseControls(false);

            return;
        }

        releaseControls(true);
    }

    private void prepareCustomerData() {

        ImageFilePath = ImageFilePath == "" ? getImagePath(fileUri) : ImageFilePath;

        profileEntity.setBirth(txtBirth.getText().toString());
        profileEntity.setCity(txtCity.getText().toString());
        profileEntity.setEmail(txtEmail.getText().toString());
        profileEntity.setGender(cmbGender.getSelectedItem().toString());
        profileEntity.setName(txtName.getText().toString());
        profileEntity.setPhone(txtPhone.getText().toString());
        profileEntity.setUriPhoto(ImageFilePath);
        profileEntity.setState(cmbState.getSelectedItem().toString());
    }

    private void releaseControls(boolean status) {
        cmbState.setFocusable(status);
        cmbGender.setFocusable(status);
        txtBirth.setFocusable(status);
        txtCity.setFocusable(status);
        txtEmail.setFocusable(status);
        txtPhone.setFocusable(status);
    }

    private void save() {
        if ((profileEntity.getID() == "0") || (profileEntity.getID() == null)) {
            if (profileRepository.Insert(profileEntity)) {
                Toast.makeText(context, "Dados gravados com sucesso.", Toast.LENGTH_LONG).show();
                return;
            }
        } else if ((profileEntity.getID() != "0") || (profileEntity.getID() != null)) {
            if (profileRepository.Update(profileEntity)) {
                Toast.makeText(context, "Dados atualizados com sucesso.", Toast.LENGTH_LONG).show();
                return;
            }
        }

        Toast.makeText(context, "Não foi possível salvar.", Toast.LENGTH_LONG).show();
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DateSetListener(), year, month, day);
        datePickerDialog.show();
    }

    private void showPhotoChooser() {
        AlertDialog.Builder choosePhoneNumber = new AlertDialog.Builder(this);
        String[] items = new String[]{"Câmera", "Galeria"};
        final AlertDialog.Builder builder = choosePhoneNumber.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        callCamera();
                        break;
                    case 1:
                        callGallery();
                        break;
                }

//                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
//                imgPhoto.startAnimation(animation);

                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Sair", null);
        builder.setIcon(R.drawable.ic_menu_camera);
        builder.setTitle("Escolha a unidade:");
        builder.show();
    }

//    private class OpenPhotoChooser implements View.OnClickListener {
//
//        @Override
//        public void onClick(View v) {
//            ShowPhotoChooser();
//        }
//    }

    private class CallDatePicker implements View.OnClickListener, View.OnFocusChangeListener {

        @Override
        public void onClick(View v) {
            showDatePicker();
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(txtBirth.getWindowToken(), 0);

            if (hasFocus) {
                showDatePicker();
            }
        }
    }

    private class DateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            final Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            Date date = calendar.getTime();
            java.text.DateFormat dateFormat = DateFormat.getMediumDateFormat(context);
            String result = dateFormat.format(date);
            txtBirth.setText(result);
        }
    }

    private class HideKeyboard implements View.OnTouchListener, View.OnFocusChangeListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            if (v.getId() == R.id.cmbGender) {
                imm.hideSoftInputFromWindow(cmbGender.getWindowToken(), 0);
                cmbGender.requestFocus();
            } else if (v.getId() == R.id.cmbState) {
                imm.hideSoftInputFromWindow(cmbState.getWindowToken(), 0);
                cmbState.requestFocus();
            }

            return false;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            if (v.getId() == R.id.cmbGender) {
                imm.hideSoftInputFromWindow(cmbGender.getWindowToken(), 0);
                cmbGender.requestFocus();
            } else if (v.getId() == R.id.cmbState) {
                imm.hideSoftInputFromWindow(cmbState.getWindowToken(), 0);
                cmbState.requestFocus();
            }
        }
    }

    private class RotateImage implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (RotateAngle == 360) RotateAngle = 0;

            RotateAngle += 90;

            imgPhoto.setRotation(RotateAngle);
            return false;
        }
    }
}
