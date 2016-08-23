package com.ik9.beacon.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.beacon.ik9.ik9beacon.utilities.Device;

public class ProfileRepository {
    private SQLiteDatabase db;
    private static final String TABLE_NAME = "profile";
    private Context _context;

    public ProfileRepository(Context context) {
        try {
            _context = context;
            Database auxDb = new Database(_context);
            db = auxDb.getWritableDatabase();
        } catch (SQLException ex) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setMessage("Erro ao criar conexão: " + ex.getMessage());
            alertDialog.setNeutralButton("OK", null);
            alertDialog.show();
        }
    }

    public boolean Insert(ProfileEntity profile) {
        boolean result;

        try {
            ContentValues contentValues = new ContentValues();

            contentValues.put("birth", profile.getBirth());
            contentValues.put("city", profile.getCity());
            contentValues.put("email", profile.getEmail());
            contentValues.put("gender", profile.getGender());
            contentValues.put("name", profile.getName());
            contentValues.put("phone", profile.getPhone());
            contentValues.put("photo", profile.getUriPhoto());
            contentValues.put("state", profile.getState());
            contentValues.put("device_id", Device.GetMAC(_context));

            result = (db.insert(TABLE_NAME, null, contentValues) > 0);
        } catch (SQLiteException ex) {
            result = false;
        }

        return result;
    }

    public boolean Update(ProfileEntity profile) {
        boolean result;

        try {
            ContentValues contentValues = new ContentValues();

            contentValues.put("birth", profile.getBirth());
            contentValues.put("city", profile.getCity());
            contentValues.put("email", profile.getEmail());
            contentValues.put("gender", profile.getGender());
            contentValues.put("name", profile.getName());
            contentValues.put("photo", profile.getUriPhoto());
            contentValues.put("state", profile.getState());
            contentValues.put("device_id", Device.GetMAC(_context));

            result = (db.update(TABLE_NAME, contentValues, "_id = ?", new String[]{profile.getID()}) > 0);
        } catch (SQLiteException ex) {
            result = false;
        }

        return result;
    }

    public boolean Delete(ProfileEntity profile) {
        boolean result;

        try {
            result = (db.delete(TABLE_NAME, "_id = ?", new String[]{profile.getID()}) > 1);
        } catch (SQLiteException ex) {
            result = false;
        }

        return result;
    }

    public ProfileEntity Create(String name, String birth, String gender, String email, String city, String state, String phone, String photo, String device_id, int id){

        ProfileEntity profileEntiry = new ProfileEntity();

        profileEntiry.setName(name);
        profileEntiry.setBirth(birth);
        profileEntiry.setGender(gender);
        profileEntiry.setEmail(email);
        profileEntiry.setCity(city);
        profileEntiry.setState(state);
        profileEntiry.setPhone(phone);
        profileEntiry.setUriPhoto(photo);
        profileEntiry.setDevice_id(device_id);
        profileEntiry.setID(id);

        return profileEntiry;
    }

    public ProfileEntity Search() {
        ProfileEntity profileEntity = new ProfileEntity();

        try {
            String[] columns = new String[]{"_id", "birth", "city", "email", "gender", "name", "phone", "photo", "state", "device_id"};
            //String selection; // o mesmo que o WHERE
            //String[] args; // os valores de selection
            Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null, null);

            int indexBirthColumn = cursor.getColumnIndex("birth");
            int indexCityColumn = cursor.getColumnIndex("city");
            int indexDeviceIdColumn = cursor.getColumnIndex("device_id");
            int indexEmailColumn = cursor.getColumnIndex("email");
            int indexGenderColumn = cursor.getColumnIndex("gender");
            int indexIdColumn = cursor.getColumnIndex("_id");
            int indexNameColumn = cursor.getColumnIndex("name");
            int indexPhoneColumn = cursor.getColumnIndex("phone");
            int indexPhotoColumn = cursor.getColumnIndex("photo");
            int indexStateColumn = cursor.getColumnIndex("state");

            if (cursor.getCount() > 0) {

                if(cursor.moveToFirst()) {

                    while (cursor.isAfterLast() == false) {

                        profileEntity = this.Create(
                                cursor.getString(indexNameColumn),
                                cursor.getString(indexBirthColumn),
                                cursor.getString(indexGenderColumn),
                                cursor.getString(indexEmailColumn),
                                cursor.getString(indexCityColumn),
                                cursor.getString(indexStateColumn),
                                cursor.getString(indexPhoneColumn),
                                cursor.getString(indexPhotoColumn),
                                cursor.getString(indexDeviceIdColumn),
                                cursor.getInt(indexIdColumn)
                        );

                        cursor.moveToNext();
                    }
                }
            }
        }
        catch(SQLiteException ex)
        {
            Toast.makeText(_context, ex.getMessage(), Toast.LENGTH_LONG);
        }

        return profileEntity;
    }
}
