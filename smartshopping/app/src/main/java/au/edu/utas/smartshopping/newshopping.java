package au.edu.utas.smartshopping;
//faraz
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.AbstractWindowedCursor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class newshopping extends AppCompatActivity {

    public static String buyDate;
    public static String sortDate;
    LinearLayout root;
    TextView name;
    protected static TextView BuyDate; //date picker
    String buyDateOriginal;
    TextView boughtat;
    TextView price;
    TextView notes;
    Spinner quantity;
    Button done;
    Button camera;
    public boolean editMode;
    public boolean historyitem;
    public String taskNameToEdit;
    public String boughtatToEdit;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView mImageView;
    Bitmap imageBitmap;
    String imglocation ;
    int imageset=0;
    int filefrom=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_shopping);


        root = (LinearLayout)findViewById(R.id.newTaskRoot);

        root.setBackgroundColor(Color.WHITE);
        name = (TextView)findViewById(R.id.et_newName);
       quantity=(Spinner) findViewById(R.id.spinnerquantity);

        boughtat = (TextView)findViewById(R.id.et_newBoughtat);
        price = (TextView)findViewById(R.id.et_newprice);
        mImageView=(ImageView) findViewById(R.id.imgview);
        notes=(TextView)findViewById(R.id.et_notes);


        done = (Button)findViewById(R.id.btn_saveNewtask);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    validateInput();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        camera = (Button)findViewById(R.id.btn_img);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //THE CONFIRMATION DIALOG
                String Message = "Retreive Image from camera or from file?";
                Log.d("Validation Error :", Message);
                final Dialog dialog =  new Dialog(newshopping.this);
                dialog.setContentView(R.layout.camera);
                dialog.setTitle("Image");
                TextView errMsg = (TextView)dialog.findViewById(R.id.camera_message);
                errMsg.setText(Message);

                Button camera = (Button)dialog.findViewById(R.id.Camera);
                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //THE DELETION!
                        dispatchTakePictureIntent();
                        filefrom=1;
                        dialog.dismiss();
                    }
                });
                Button file = (Button)dialog.findViewById(R.id.fromfile);
                file.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takeFromOther("smart");
                        dialog.dismiss();
                        filefrom=2;
                    }
                });
                //show
                dialog.show();


                //   dispatchTakePictureIntent();
                //  takeFromOther("smart");
            }
        });



        //check if edit mode
        SharedPreferences prefs = getSharedPreferences("editstore", MODE_PRIVATE);

        String prefEdit = prefs.getString("edit?", "No");
        editMode = (prefEdit.equals("Yes")) ? true : false;
        if(editMode) {
            taskNameToEdit = prefs.getString("name", null);
            boughtatToEdit = prefs.getString("boughtat", null);

            SharedPreferences.Editor edittask = getSharedPreferences("editstore", MODE_PRIVATE).edit();
            edittask.putString("edit?", "No");
            edittask.commit();
            read();
        } else {
         //   edit mode false do nothing
        }
        SharedPreferences prefs2 = getSharedPreferences("newitem", MODE_PRIVATE);
        String pref2edit=prefs2.getString("newitem?","No");
        historyitem=(pref2edit.equals("Yes")) ? true : false;
        Log.d(" this thing ",prefs2.getString("newitem?","No"));
        if(historyitem){
            taskNameToEdit = prefs2.getString("name", null);
            boughtatToEdit = prefs2.getString("boughtat", null);
            SharedPreferences.Editor edittask2 = getSharedPreferences("newitem", MODE_PRIVATE).edit();
            edittask2.putString("newitem?", "No");
            edittask2.commit();
            Log.d(" read new starting ","hi");
            readnew();
        }

    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
         //  mImageView.setImageBitmap(imageBitmap);

            saveimage();//save the image to the phone storage
            String path = Environment.getExternalStorageDirectory().toString();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap imagey = BitmapFactory.decodeFile(imglocation, options);
            mImageView.setImageBitmap(imagey);
            imageset=1;
        }
        else if (resultCode == RESULT_OK)
        {
            Log.d("got here :", name.getText().toString());
            Uri smart=data.getData();
            if (smart!=null)
                Log.d("smart not null :",smart.toString());
            String realpath= null;
            try {
                realpath = getFilePath(this,smart);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            if (realpath!=null)
             Log.d("realpath not null :", realpath);
            imglocation=realpath;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap imagey = BitmapFactory.decodeFile(realpath, options);
            mImageView.setImageBitmap(imagey);
            imageset=1;

        }
    }


    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    void validateInput() throws ParseException {
        String errorMessage = "";

        if (name.getText().toString().length() < 1) //had to check string length as checking if == """ doesn't always work
            errorMessage = "You must enter a name for your item.";
        if (!editMode && taskExists()){
            errorMessage = "You already have a item with that name and bought at location.";
        }
        if (errorMessage != ""){
            Log.d("Validation Error :", errorMessage);
            final Dialog dialog =  new Dialog(newshopping.this);
            dialog.setContentView(R.layout.error);
            dialog.setTitle("Cant save this item");
            TextView errMsg = (TextView)dialog.findViewById(R.id.error_message);
            errMsg.setText(errorMessage);
            Button errOK = (Button)dialog.findViewById(R.id.error_ok);
            errOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else {

            if(editMode){

                String namey = name.getText().toString();
                String boughty = boughtat.getText().toString();
                String pricy = price.getText().toString();
                String noty=notes.getText().toString();
                int quanty=quantity.getSelectedItemPosition();

                update_task(taskNameToEdit, boughtatToEdit, namey, boughty, pricy,quanty,noty,imglocation);

            } else {

                save();
            }
            finish();

        }
    }

    boolean taskExists(){

        SQLiteDatabase mydatabase = openOrCreateDatabase("Shopping List",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Danktable(Title VARCHAR,BoughtAt VARCHAR,Price DOUBLE,Checkbox INT,buyDate DATE, sortDate VARCHAR,quantity INT,notes VARCHAR, img VARCHAR);");
        Cursor resultSet;
        resultSet = mydatabase.rawQuery("Select * from Danktable WHERE Title='"+name.getText().toString()+"' AND BoughtAt='"+boughtat.getText().toString()+"';",null);
        int count = resultSet.getCount();

        if (count > 0){
            return true;
        }
        return false;
    }

    void save(){
        SQLiteDatabase mydatabase = openOrCreateDatabase("Shopping List",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Danktable(Title VARCHAR,BoughtAt VARCHAR,Price DOUBLE,Checkbox INT,buyDate DATE, sortDate VARCHAR,quantity INT,notes VARCHAR, img VARCHAR);");

        if (imageset!=0) {
            if(filefrom!=2)
            saveimage();//save the image to the phone storage
        }
else
    imglocation="none";
        mydatabase.execSQL("INSERT INTO Danktable VALUES('"+ name.getText() +
                "','" + boughtat.getText() +
                "','" + price.getText() +
                 "',0,'" + Calendar.getInstance().getTime() + "','" + sortDate + "','" + quantity.getSelectedItemPosition() + "','"+notes.getText()+"','"+imglocation+"');");
    }

    public void read(){
        SQLiteDatabase mydatabase = openOrCreateDatabase("Shopping List",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Danktable(Title VARCHAR,BoughtAt VARCHAR,Price DOUBLE,Checkbox INT,buyDate DATE, sortDate VARCHAR,quantity INT,notes VARCHAR, img VARCHAR);");
     //   Cursor resultSet = mydatabase.rawQuery("Select * from Danktable WHERE Title='" + taskNameToEdit +
     //           "' AND BoughtAt='" + boughtatToEdit + "'",null);

       Cursor resultSet = mydatabase.rawQuery("Select * from Danktable WHERE Title='" + taskNameToEdit + "' AND BoughtAt='" + boughtatToEdit + "'",null);

        resultSet.moveToLast();// see the last row

        String thetitle = resultSet.getString(0);
        String boughtaty = resultSet.getString(1);
        String pricy = resultSet.getString(2);
        int quantityint = resultSet.getInt(6);
        String notey = resultSet.getString(7);
        imglocation = resultSet.getString(8);

if (imglocation != "none") {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
    Bitmap imagey = BitmapFactory.decodeFile(imglocation, options);
    mImageView.setImageBitmap(imagey);
}

       // mImageView.setImageBitmap(BitmapFactory.decodeByteArray(imagey, 0, imagey.length));
//        int cmp = resultSet.getInt(3);
       // String datey=resultSet.getString(4);


        name.setText(thetitle);
        boughtat.setText(boughtaty);
        price.setText(pricy);
        quantity.setSelection(quantityint);
        notes.setText(notey);
        //BuyDate.setText(datey);
        changeColor();


    }
    public void readnew(){

        SQLiteDatabase mydatabase = openOrCreateDatabase("Shopping List",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS historytable(Title VARCHAR,BoughtAt VARCHAR,Price DOUBLE,Checkbox INT,buyDate DATE, sortDate VARCHAR,quantity INT,notes VARCHAR,img VARCHAR);");
        //   Cursor resultSet = mydatabase.rawQuery("Select * from Danktable WHERE Title='" + taskNameToEdit +
        //           "' AND BoughtAt='" + boughtatToEdit + "'",null);

        Cursor resultSet = mydatabase.rawQuery("Select * from historytable WHERE Title='" + taskNameToEdit + "' AND BoughtAt='" + boughtatToEdit + "'",null);

        resultSet.moveToLast();// see the last row

        String thetitle = resultSet.getString(0);
        String boughtaty = resultSet.getString(1);
        String pricy = resultSet.getString(2);
        int quantityint = resultSet.getInt(6);
        String notey = resultSet.getString(7);
        imglocation = resultSet.getString(8);

        if (imglocation != "none") {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap imagey = BitmapFactory.decodeFile(imglocation, options);
            mImageView.setImageBitmap(imagey);
        }

        // mImageView.setImageBitmap(BitmapFactory.decodeByteArray(imagey, 0, imagey.length));
//        int cmp = resultSet.getInt(3);
        // String datey=resultSet.getString(4);


        name.setText(thetitle);
        boughtat.setText(boughtaty);
        price.setText(pricy);
        quantity.setSelection(quantityint);
        notes.setText(notey);
        //BuyDate.setText(datey);
        changeColor();


    }

    public void update_task(String titlegiven, String boughtgiven, String newtitle, String newbought, String newprice,int quantgiven,String notesgiven, String imglocgiven)
    {

        int checky=0;


        SQLiteDatabase mydatabase = openOrCreateDatabase("Shopping List",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Danktable(Title VARCHAR,BoughtAt VARCHAR,Price DOUBLE,buyDate DATE, sortDate VARCHAR,quantity INT,notes VARCHAR,img VARCHAR);");

        if(imageset!=0)
        mydatabase.execSQL("UPDATE Danktable SET Title='"+newtitle+"',BoughtAt='"+newbought+"',Price='"+newprice+"',buyDate='"+ Calendar.getInstance().getTime() +"',quantity='"+quantgiven+"',notes='"+notesgiven+"',img='"+imglocgiven+"' WHERE Title='"+ titlegiven +"' AND BoughtAt='"+boughtgiven+"'");
    else
        mydatabase.execSQL("UPDATE Danktable SET Title='"+newtitle+"',BoughtAt='"+newbought+"',Price='"+newprice+"',buyDate='"+ Calendar.getInstance().getTime() +"',quantity='"+quantgiven+"',notes='"+notesgiven+"' WHERE Title='"+ titlegiven +"' AND BoughtAt='"+boughtgiven+"'");

    }

    void changeColor(){
        int color = Color.WHITE;

       root.setBackgroundColor(color);
    }




public void saveimage()
{

    String path = Environment.getExternalStorageDirectory().toString();
    OutputStream fOut = null;
    File file = new File(path,"SmartShopping"+name.getText()+boughtat.getText()+".jpg");
    try {
        fOut = new FileOutputStream(file);
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    }


    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate

    try {
        fOut.flush(); // Not really required
    } catch (IOException e) {
        e.printStackTrace();
    }
    try {
        fOut.close(); // do not forget to close the stream
    } catch (IOException e) {
        e.printStackTrace();
    }

    try {
        Log.d("file name ", file.getName());
        MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
        imglocation=file.getAbsolutePath();
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    }
}

    public void takeFromOther( String title)
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, title),2);



    }


}