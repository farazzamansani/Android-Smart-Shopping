package au.edu.utas.smartshopping;
//Faraz
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class shoppingList extends AppCompatActivity {

    public ViewGroup root;

    @Override
    public void onResume() {
        super.onResume();
        Spinner SortOption = (Spinner) findViewById(R.id.sort_filter);
        String sortMode = SortOption.getSelectedItem().toString();
        root = (ViewGroup) findViewById(R.id.taskListRoot);
        root.removeAllViewsInLayout();
        populate(sortMode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        DisplayMetrics metrics = getBaseContext().getResources().getDisplayMetrics();
        float ratio = ( (float)metrics.heightPixels / (float)metrics.widthPixels
        );
        Log.d("ratio is", toString().valueOf(ratio));
        //if 1.661111 is 5x
        // 1.64444445 is pixel
        if (ratio>1.65) {
            ScrollView viewy=(ScrollView) findViewById(R.id.adjustme);
Log.d("this ran","ran");
          // viewy.getLayoutParams().height=400;
            final float scale = getResources().getDisplayMetrics().density;
            int dpheightinpx  = (int) (450 * scale);
           viewy.getLayoutParams().height=dpheightinpx;
        }
        if (ratio>1.8) {
            ScrollView viewy=(ScrollView) findViewById(R.id.adjustme);
            Log.d("this ran","ran");
            // viewy.getLayoutParams().height=400;
            final float scale = getResources().getDisplayMetrics().density;
            int dpheightinpx  = (int) (550 * scale);
            viewy.getLayoutParams().height=dpheightinpx;
        }

        final Spinner SortOption = (Spinner) findViewById(R.id.sort_filter);
        SortOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String sortMode = SortOption.getSelectedItem().toString();
                SharedPreferences.Editor editor = getSharedPreferences("sortby", MODE_PRIVATE).edit();
                sortMode = sortMode.toLowerCase();

                editor.putString("sortby", sortMode);
                editor.commit();


                Button newTaskButton = (Button) findViewById(R.id.newshoppingfromlistbutton);
                newTaskButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(shoppingList.this, newshopping.class);
                        startActivity(i);
                    }
                });

                Button shoppingdone = (Button) findViewById(R.id.shoppingdone);
                shoppingdone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       //shopping done popup
                        final Dialog dialog =  new Dialog(shoppingList.this);
                        dialog.setContentView(R.layout.listdonepopup);
                        dialog.setTitle("Are you Sure?");
                        TextView errMsg = (TextView)dialog.findViewById(R.id.done_message);
                        //yes
                        Button delYes = (Button)dialog.findViewById(R.id.done_yes);
                        delYes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialog.dismiss();
                                finishlist();
                            }
                        });

                        Button delNo = (Button)dialog.findViewById(R.id.done_no);
                        delNo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }
                });

                Button sharingbutton = (Button) findViewById(R.id.share);
                sharingbutton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        shareIt();
                    }
                });

                root = (ViewGroup) findViewById(R.id.taskListRoot);
                root.removeAllViewsInLayout();
                populate(sortMode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        SharedPreferences prefs = getSharedPreferences("sortby", MODE_PRIVATE);
        String sortMode = prefs.getString("sortby", "name");


        if(sortMode.contains("name"))
            SortOption.setSelection(0);
        if(sortMode.contains("bought") || sortMode.contains("at"))
            SortOption.setSelection(1);
        if(sortMode.contains("pri"))
            SortOption.setSelection(2);

        populate(sortMode);
    }

    private void shareIt() {
//sharing implementation here

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
         //   sharingIntent.setType("text/plain");

            SQLiteDatabase mydatabase = openOrCreateDatabase("Shopping List", MODE_PRIVATE, null);
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Danktable(Title VARCHAR,BoughtAt VARCHAR,Price DOUBLE ,Checkbox INT,buyDate DATE, sortDate VARCHAR,quantity INT,notes VARCHAR,img VARCHAR);");
            Cursor resultSet;
            resultSet = mydatabase.rawQuery("Select * from Danktable ", null);

            int count = resultSet.getCount();
            resultSet.moveToFirst();
            String shareBody = "Smart Shopping List! \n";
            while (count > 0) {
                //example Name: namey, Bought At:Coles, Price: $45, Quantity: 6, Notes : hi, Bought? true
                String name = resultSet.getString(0);
                String boughtat = resultSet.getString(1);
                double pricey = resultSet.getDouble(2);
                boolean bought = (resultSet.getInt(3) == 1) ? true : false;
                int quanty = resultSet.getInt(6);
                String notey = resultSet.getString(7);
                String yesno;
                if (bought == true)
                    yesno = "Yes";
                else
                    yesno = "No";
                shareBody = shareBody + "Name: " + name + ", Bought At: " + boughtat + ", Price: $" + pricey + ", Quantity: " + quanty + ", Notes: " + notey + ", Bought? " + yesno + "\n";

                resultSet.moveToNext();
                count--;
            }


            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Shopping List");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);


        Bitmap screeny=takeScreenshot();
        saveBitmap(screeny);

        File imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot.png");
        Uri uri = Uri.fromFile(imagePath);
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(sharingIntent, "Share Shopping via"));
    }
    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public void saveBitmap(Bitmap bitmap) {
        File imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot.png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }



    public void populate(String SortMode){
        SQLiteDatabase mydatabase = openOrCreateDatabase("Shopping List",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Danktable(Title VARCHAR,BoughtAt VARCHAR,Price DOUBLE ,Checkbox INT,buyDate DATE, sortDate VARCHAR,quantity INT,notes VARCHAR,img VARCHAR);");
        //check sort
        Spinner SortOption = (Spinner) findViewById(R.id.sort_filter);
        SortMode = SortOption.getSelectedItem().toString();
        SortMode = SortMode.toLowerCase();

        Cursor resultSet;
        resultSet = mydatabase.rawQuery("Select * from Danktable ",null);

        if(SortMode.contains("name"))
            resultSet = mydatabase.rawQuery("Select * from Danktable ORDER BY Title, BoughtAt, Price",null);

        if(SortMode.contains("bought"))
            resultSet = mydatabase.rawQuery("Select * from Danktable ORDER BY BoughtAt, Title, Price",null);

        if(SortMode.contains("pri"))
           resultSet = mydatabase.rawQuery("select * from Danktable ORDER BY Price desc, Title desc, BoughtAt desc ",null);

        int count = resultSet.getCount();
        resultSet.moveToFirst();

        while (count > 0){

            String thetitle = resultSet.getString(0);
            String boughtat = resultSet.getString(1);
            double pricey = resultSet.getDouble(2);
            boolean bought = (resultSet.getInt(3) == 1)? true : false;
            String datey = resultSet.getString(4);
            int quanty = resultSet.getInt(6);
            String notey = resultSet.getString(7);
            String imagey=resultSet.getString(8);

        //    Log.d("inflating", thetitle);
            inflateTask(thetitle, boughtat ,datey, pricey, bought, SortMode,quanty,notey,imagey);
            //go next
            resultSet.moveToNext();
            count --;
        }
    }

    public void delete(String titlegiven, String boughtatgiven)
    {
        Log.d("About to delete : " , titlegiven + boughtatgiven);
        SQLiteDatabase mydatabase = openOrCreateDatabase("Shopping List",MODE_PRIVATE,null);

        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Danktable(Title VARCHAR,BoughtAt VARCHAR,Price DOUBLE,Checkbox INT,buyDate DATE, sortDate VARCHAR,quantity INT,notes VARCHAR,img VARCHAR);");
        mydatabase.delete("Danktable","Title='"+titlegiven+"' AND BoughtAt='"+boughtatgiven+"'",null); //deletes row
    }

    public void complete_task(String titlegiven, String boughtatgiven, Boolean checkbox)
    {
        SQLiteDatabase mydatabase = openOrCreateDatabase("Shopping List",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Danktable(Title VARCHAR,BoughtAt VARCHAR,Price DOUBLE,Checkbox INT,buyDate DATE, sortDate VARCHAR,quantity INT,notes VARCHAR,img VARCHAR);");
        if (checkbox==true)
            mydatabase.execSQL("UPDATE Danktable SET Checkbox=1 WHERE Title='"+ titlegiven +"' AND BoughtAt='"+boughtatgiven+"'");
        else
            mydatabase.execSQL("UPDATE Danktable SET Checkbox=0 WHERE Title='"+ titlegiven +"' AND BoughtAt='"+boughtatgiven+"'");
    }

    void inflateTask(final String name, final String boughtat, String buyDate, double pricey, boolean complete, String sortBy, int quanty, String notes,String imglocation){
        //make a task to hold data
        final item item = new item(shoppingList.this);

        //xml layout inflate
        final LinearLayout root = (LinearLayout)findViewById(R.id.taskListRoot);
        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout taskItem;
        taskItem = (LinearLayout) layoutInflater.inflate(R.layout.task_item,root);


        //layout elements set up
        checkChildren(taskItem, item);

        Log.d("checking : " , imglocation);
        if (imglocation.equals("none")) {
        }
        else{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap imagey = BitmapFactory.decodeFile(imglocation, options);
            item.mImageView.setImageBitmap(imagey);
        }
        item.imgloc=imglocation;

        item.name.setText(name);
        item.boughtat.setText("Bought At: " + boughtat);

       item.buyDate.setText("");


        String price = Double.toString(pricey);
        int color = rColor(R.color.normal);

        item.colour = color;
        item.head.setBackgroundColor(color);

        item.price.setText("Price: $" + Double.toString(pricey) );

       item.quantity.setText("Quantity:" +Integer.toString(quanty+1));//plus 1 because array position 0 is 1

        item.notes.setText("Notes: "+notes);


        item.complete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int color = item.colour;
                if (isChecked)
                    color = rColor(R.color.complete);
                item.head.setBackgroundColor(color);
                String boughty = item.boughtat.getText().toString();
                boughty = boughty.substring(11, boughty.length());
                complete_task(item.name.getText().toString(),boughty, isChecked);
            }
        });
        item.complete.setChecked(complete);


        sortBy = sortBy.toLowerCase();

        if (sortBy.contains("name")){
            item.sortby.setText("");
            //item.buyDate.setVisibility(View.GONE);
        }
        if (sortBy.contains("bought") || sortBy.contains("at")){
            item.sortby.setText(boughtat);
            //item.boughtat.setVisibility(View.GONE);
        }
        if (sortBy.contains("price")){
            item.sortby.setText(price);
            //item.price.setVisibility(View.GONE);
        }

        item.body.setVisibility(View.GONE);
        //SHOW HIDE BODY FUNCTION
        item.head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.body.getVisibility() == View.GONE){
                    item.body.setVisibility(View.VISIBLE);
                } else {
                    item.body.setVisibility(View.GONE);
                }
            }
        });

        //edit
        item.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  edit
                editItem(name, boughtat);
            }
        });
//Photo FUNCTION
        item.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  if(item.isImageFitToScreen) { //enlarge picture, commented out because we want pop up
               //     item.isImageFitToScreen=false;
              //      item.mImageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
              //      item.mImageView.setAdjustViewBounds(true);
              //  }else{
              //      item.isImageFitToScreen=true;
               //     item.mImageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
              //      item.mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
              //  }

                //or pop up

                final Dialog dialog =  new Dialog(shoppingList.this);
                dialog.setContentView(R.layout.popup);


                ImageView imageView = (ImageView) dialog.findViewById(R.id.imageView);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap imagey = BitmapFactory.decodeFile(item.imgloc, options);
                imageView.setImageBitmap(imagey);

                Display display = getWindowManager().getDefaultDisplay();
                int width = display.getWidth();
                int height = display.getHeight();

                imageView.setMinimumWidth(width);
                imageView.setMinimumHeight(height);

                imageView.setMaxWidth(width);
                imageView.setMaxHeight(height);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                //show pop up
                dialog.show();

            }
        });


        //delete buton
        item.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Message = "This will permanently delete the item named:\n" +
                        name + " from your shopping list \n Are you sure?";
                Log.d("Validation Error :", Message);
                final Dialog dialog =  new Dialog(shoppingList.this);
                dialog.setContentView(R.layout.delete);
                dialog.setTitle("WARNING!");
                TextView errMsg = (TextView)dialog.findViewById(R.id.delete_message);
                errMsg.setText(Message);
                //yes
                Button delYes = (Button)dialog.findViewById(R.id.delete_yes);
                delYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //THE DELETION!
                        delete(name, boughtat);
                        item.me.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                });

                Button delNo = (Button)dialog.findViewById(R.id.delete_no);
                delNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
      //  Log.d("done inflate ", item.name.getText().toString());
    }


    int rColor(int resource){
        return ContextCompat.getColor(this, resource);
    }

    void checkChildren(View view, final item item){
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup)view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = viewGroup.getChildAt(i);
                checkChildren(child, item);
            }
        }

        int id = view.getId();
        if (id == R.id.taskItem)
            item.root = view;
        if (id == R.id.head)
            item.head = view;
        if (id == R.id.name)
            item.name = (TextView)view;
        if (id == R.id.sortBy)
            item.sortby = (TextView)view;
        if (id == R.id.body)
            item.body=view;
        if (id == R.id.date)
            item.buyDate=(TextView)view;
        if (id == R.id.boughtat)
            item.boughtat = (TextView)view;
        if (id == R.id.price)
            item.price = (TextView)view;
        if (id == R.id.notes)
            item.notes = (TextView)view;
        if (id == R.id.quantity)
            item.quantity = (TextView)view;
        if (id == R.id.complete)
            item.complete = (Switch)view;
        if (id == R.id.imgview)
            item.mImageView = (ImageView)view;
        //if (id == R.id.buttons)
        if (id == R.id.edit)
            item.edit = (Button)view;
        if (id == R.id.delete)
            item.delete = (Button)view;
        if (id == R.id.taskItem)
            item.me = view;



    }

    void editItem(String name, String boughtat){
    //    Log.d("EditItem", name + boughtat);
        SharedPreferences.Editor edittask = getSharedPreferences("editstore", MODE_PRIVATE).edit();
        edittask.putString("edit?", "Yes");
        edittask.putString("name", name);
        edittask.putString("boughtat", boughtat);
        edittask.commit();

        Intent i = new Intent(shoppingList.this, newshopping.class);
        startActivity(i);
    }

    void finishlist()
    {
        SQLiteDatabase mydatabase = openOrCreateDatabase("Shopping List",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Danktable(Title VARCHAR,BoughtAt VARCHAR,Price DOUBLE ,Checkbox INT,buyDate DATE, sortDate VARCHAR,quantity INT,notes VARCHAR,img VARCHAR);");

        Cursor resultSet;
        resultSet = mydatabase.rawQuery("Select * from Danktable ",null);

        int count = resultSet.getCount();
        resultSet.moveToFirst();

        while (count > 0){
            //do the things
            String thetitle = resultSet.getString(0);
            String boughtat = resultSet.getString(1);
            double pricey = resultSet.getDouble(2);
            boolean complete = (resultSet.getInt(3) == 1)? true : false;
            String datey = resultSet.getString(4);
            int quanty = resultSet.getInt(6);
            String notey = resultSet.getString(7);
            String imagey=resultSet.getString(8);

            //date setup string

            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS historytable(Title VARCHAR,BoughtAt VARCHAR,Price DOUBLE,Checkbox INT,buyDate DATE, sortDate VARCHAR,quantity INT,notes VARCHAR, img VARCHAR);");


            mydatabase.execSQL("INSERT INTO historytable VALUES('"+ thetitle +
                    "','" + boughtat +
                    "','" + pricey +
                    "',0,'" + Calendar.getInstance().getTime() + "','" + "" + "','" + quanty + "','"+notey+"','"+imagey+"');");


            delete(thetitle,boughtat);
            finish();
            startActivity(getIntent());
            //go next
            resultSet.moveToNext();

            count --;
        }

    }
}
