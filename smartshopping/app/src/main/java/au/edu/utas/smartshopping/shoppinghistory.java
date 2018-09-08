package au.edu.utas.smartshopping;
//Faraz
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.TimePicker;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;


public class shoppinghistory extends AppCompatActivity {

        public ViewGroup root;
    protected static TextView startDate; //date picker
    protected static TextView endDate; //date picker
    public static String startyDate="Mon 1 January 1990";//this is start for filter, 1990 so always show whole list
    public static String endyDate;
    public  Date starterDate;
    public Date enderDate;
    public Spinner SortOption;
    public double tally;
    public BarChart barChart;


        @Override
        public void onResume() {
            super.onResume();
            SortOption = (Spinner) findViewById(R.id.sort_filter);
            String sortMode = SortOption.getSelectedItem().toString();
            root = (ViewGroup) findViewById(R.id.taskListRoot);
            root.removeAllViewsInLayout();
            populate(sortMode);//this populate list
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_shoppinghistory);

            //the date picker
            startDate = (TextView)findViewById(R.id.startdate);
            startDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //when people click date
                    aDatePickerFragment aDatePicker = new aDatePickerFragment();
                    aDatePicker.show(getFragmentManager(), "Select date");

                    //store user filter stuff for permenent store but maybe not use
                    SharedPreferences.Editor editor = getSharedPreferences("filter", MODE_PRIVATE).edit();
                    editor.putString("filterstart", startyDate);
                    editor.putString("filterend", endyDate);
                    editor.commit();



                }
            });
            //the second date picker
            endDate = (TextView)findViewById(R.id.enddate);
            endDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bDatePickerFragment bDatePicker = new bDatePickerFragment();
                    bDatePicker.show(getFragmentManager(), "Select date");

                    populate("date");//this repopulate list


                    /*
                    public void onTimeSet(TimePicker viewy, int hourOfDay, int minute)
                    {
                        // Do something with the time chosen by the user

                        finish();
                        startActivity(getIntent());
                    }
*/


                }
            });

            DisplayMetrics metrics = getBaseContext().getResources().getDisplayMetrics();
            float ratio = ( (float)metrics.heightPixels / (float)metrics.widthPixels
            );
            Log.d("ratio is", toString().valueOf(ratio));
            //if 1.661111 is 5x
            // 1.64444445 is pixel
            if (ratio<1.65) {
                ScrollView viewy=(ScrollView) findViewById(R.id.adjustme);
                Log.d("this ran","ran");
                // viewy.getLayoutParams().height=400;
                final float scale = getResources().getDisplayMetrics().density;
                int dpheightinpx  = (int) (300 * scale);
                viewy.getLayoutParams().height=dpheightinpx;
            }
            if (ratio>1.8) {
                ScrollView viewy=(ScrollView) findViewById(R.id.adjustme);
                Log.d("this ran","ran");
                // viewy.getLayoutParams().height=400;
                final float scale = getResources().getDisplayMetrics().density;
                int dpheightinpx  = (int) (460 * scale);
                viewy.getLayoutParams().height=dpheightinpx;
            }



//this for the graph
            barChart = (BarChart) findViewById(R.id.graph);

            ArrayList<BarEntry> barEntries = new ArrayList<>();
            barEntries.add(new BarEntry(44f,0));
            barEntries.add(new BarEntry(84f,1));
            BarDataSet barDataSet = new BarDataSet(barEntries,"Dates");

            ArrayList<String> theDates = new ArrayList<>();
            theDates.add("April");
            theDates.add("May");

            BarData theData = new BarData(theDates,barDataSet);
            barChart.setData(theData);

            final Calendar c = Calendar.getInstance();
            final Calendar b = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            b.set(1990, 1, 1);
            c.set(year, month, day+1);

            SimpleDateFormat formatter = new SimpleDateFormat("EEE d MMM yyyy", Locale.getDefault());//this how date show
            endyDate = formatter.format(c.getTime());
            endDate.setText(formatter.format(c.getTime()));
            startyDate=formatter.format(b.getTime());
            startDate.setText(formatter.format(b.getTime()));

           SortOption = (Spinner) findViewById(R.id.sort_filter);
            SortOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                    String sortMode = SortOption.getSelectedItem().toString();
                    SharedPreferences.Editor editor = getSharedPreferences("historysortby", MODE_PRIVATE).edit();
                    sortMode = sortMode.toLowerCase();
                    editor.putString("historysortby", sortMode);
                    editor.commit();

                    root = (ViewGroup) findViewById(R.id.taskListRoot);
                    root.removeAllViewsInLayout();
                    populate(sortMode);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                    String sortMode = SortOption.getSelectedItem().toString();
                    SharedPreferences.Editor editor = getSharedPreferences("historysortby", MODE_PRIVATE).edit();
                    sortMode = sortMode.toLowerCase();
                    editor.putString("historysortby", sortMode);
                    editor.commit();
                    root = (ViewGroup) findViewById(R.id.taskListRoot);
                    root.removeAllViewsInLayout();
                    populate(sortMode);

                }

            });
            SharedPreferences prefs = getSharedPreferences("historysortby", MODE_PRIVATE);

            String sortMode = prefs.getString("historysortby", "date");


            if(sortMode.contains("name"))//check shared what sort we use
                SortOption.setSelection(0);
            if(sortMode.contains("bought") || sortMode.contains("at"))
                SortOption.setSelection(1);
            if(sortMode.contains("pri"))
                SortOption.setSelection(2);
            if(sortMode.contains("buy") || sortMode.contains("date"))
                SortOption.setSelection(3);


            SharedPreferences prefs2 = getSharedPreferences("filter", MODE_PRIVATE);
            String sortdate1 = prefs2.getString("filterstart", "Mon 1 Jan 1990");//start at 1990
            String sortdate2 = prefs2.getString("filterend", "Thursday 8 May 2018");//this will change to curant date



            populate(sortMode);//populate the list
        }



        public void populate(String SortMode){
            //this do populate put database into list to see
            SQLiteDatabase mydatabase = openOrCreateDatabase("Shopping List",MODE_PRIVATE,null);
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS historytable(Title VARCHAR,BoughtAt VARCHAR,Price DOUBLE ,Checkbox INT,buyDate DATE, sortDate VARCHAR,quantity INT,notes VARCHAR,img VARCHAR);");
            //check  the sort mode
            Spinner SortOption = (Spinner) findViewById(R.id.sort_filter);
            SortMode = SortOption.getSelectedItem().toString();
            //make lower case easy to check same
            SortMode = SortMode.toLowerCase();
            Cursor resultSet;
            resultSet = mydatabase.rawQuery("Select * from historytable ",null);
//do different order in query for diferent sort
            if(SortMode.contains("name"))
                resultSet = mydatabase.rawQuery("Select * from historytable ORDER BY Title, BoughtAt, Price",null);
            if(SortMode.contains("bought"))
                resultSet = mydatabase.rawQuery("Select * from historytable ORDER BY BoughtAt, Title, Price",null);
            if(SortMode.contains("pri"))
                resultSet = mydatabase.rawQuery("select * from historytable ORDER BY Price desc, Title desc, BoughtAt desc ",null);
            if(SortMode.contains("buy"))
                resultSet = mydatabase.rawQuery("select * from historytable ORDER BY buyDate asc, Title desc, BoughtAt desc ",null);

            int count = resultSet.getCount();
            resultSet.moveToFirst();
tally=0;


            ArrayList<BarEntry> barEntries = new ArrayList<>();
int []months= new int[12];//make array for each month
            for (int i=0; i<months.length; i++)
            {
                months[i]=0;
            }

            while (count > 0){//loop go for each item
                //get values from sql
                String shopname = resultSet.getString(0);
                String boughtat = resultSet.getString(1);
                double pricey = resultSet.getDouble(2);
                boolean bought = (resultSet.getInt(3) == 1)? true : false;
                String datey = resultSet.getString(4);
                int quanty = resultSet.getInt(6);
                String notey = resultSet.getString(7);
                String imagey=resultSet.getString(8);

tally=tally+pricey;
                //format the date string

                //put in the view
                //    Log.d("About to inflate", thetitle);

                ParsePosition posy = new ParsePosition(0);
                SimpleDateFormat simpledateformat = new SimpleDateFormat("EEE MMM d HH:mm:ss zz yyyy");
                Date buyDate = simpledateformat.parse(datey,posy );
                ParsePosition posy2 = new ParsePosition(0);
                SimpleDateFormat simpledateformat2 = new SimpleDateFormat("EEE d MMM yyyy");
                starterDate=simpledateformat2.parse(startyDate,posy2);
                ParsePosition posy3 = new ParsePosition(0);
                enderDate=simpledateformat2.parse(endyDate,posy3);
                ParsePosition posy4 = new ParsePosition(0);
                String substr=datey.substring(0,10);
                    if(buyDate.after(starterDate)) {
                        if(buyDate.before(enderDate)) {
                            inflateTask(shopname, boughtat, substr, pricey, bought, SortMode, quanty, notey, imagey);
                           //graph stuff
//add price to total for month
if (datey.contains("Jan"))
    months[0]=months[0]+(int)pricey;
    if (datey.contains("Feb"))
        months[1]=months[1]+(int)pricey;
        if (datey.contains("Mar"))
            months[2]=months[2]+(int)pricey;
            if (datey.contains("Apr"))
                months[3]=months[3]+(int)pricey;
                if (datey.contains("May"))
                    months[4]=months[4]+(int)pricey;
                    if (datey.contains("Jun"))
                        months[5]=months[5]+(int)pricey;
                        if (datey.contains("Jul"))
                            months[6]=months[6]+(int)pricey;
                            if (datey.contains("Aug"))
                                months[7]=months[7]+(int)pricey;
                                if (datey.contains("Sep"))
                                    months[8]=months[8]+(int)pricey;
                                    if (datey.contains("Oct"))
                                        months[9]=months[9]+(int)pricey;
                                        if (datey.contains("Nov"))
                                            months[10]=months[10]+(int)pricey;
                                            if (datey.contains("Dec"))
                                                months[11]=months[11]+(int)pricey;



                        }
                    }
                //go to the next one
                resultSet.moveToNext();
                count --;
            }
            TextView tallytf = (TextView)findViewById(R.id.tallytf);
           tallytf.setText("Total Spending in Chosen Time Period:"+ (int) tally);



            BarDataSet barDataSet = new BarDataSet(barEntries,"Spent");

           /*
            for (int i=0; i<months.length; i++) {
                barEntries.add(new BarEntry(months[i], i));
            }
            */
           int iteration=0;
            for (int i=0; i<months.length; i++) {

               if (months[i]!=0) {
                   barEntries.add(new BarEntry(months[i], iteration));
                   iteration++;
               }
            }
            //show month if anything price that month
Log.d("month may",Integer.toString(months[4]));
            Log.e("got to here ","got to here");
            ArrayList<String> theDates = new ArrayList<>();
            if(months[0]!=0)
            theDates.add("Jan");
            if(months[1]!=0)
            theDates.add("Feb");
            if(months[2]!=0)
            theDates.add("Mar");
            if(months[3]!=0)
            theDates.add("Apr");
            if(months[4]!=0)
            theDates.add("May");
           if(months[5]!=0)
            theDates.add("Jun");
            if(months[6]!=0)
            theDates.add("Jul");
            if(months[7]!=0)
            theDates.add("Aug");
           if(months[8]!=0)
            theDates.add("Sep");
            if(months[9]!=0)
           theDates.add("Oct");
            if(months[10]!=0)
            theDates.add("Nov");
            if(months[11]!=0)
            theDates.add("Dec");



            BarData theData = new BarData(theDates,barDataSet);

            barChart.setData(theData);
            barChart.getAxisRight().setStartAtZero(true);
            barChart.getAxisLeft().setStartAtZero(true);
barChart.setDescription("");

        }

        public void delete(String titlegiven, String boughtatgiven)
        {
            Log.d("deleting  " , titlegiven + boughtatgiven);
            SQLiteDatabase mydatabase = openOrCreateDatabase("Shopping List",MODE_PRIVATE,null);
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS historytable(Title VARCHAR,BoughtAt VARCHAR,Price DOUBLE,Checkbox INT,buyDate DATE, sortDate VARCHAR,quantity INT,notes VARCHAR,img VARCHAR);");
            mydatabase.delete("historytable","Title='"+titlegiven+"' AND BoughtAt='"+boughtatgiven+"'",null); //deletes row

            root = (ViewGroup) findViewById(R.id.taskListRoot);
            root.removeAllViewsInLayout();
            populate("date");
        }

        void inflateTask(final String name, final String boughtat, String buyDate, double pricey, boolean complete, String sortBy, int quanty, String notes,String imglocation){
            //make a task to hold data
            final historyitem item = new historyitem(au.edu.utas.smartshopping.shoppinghistory.this);

            //xml layout inflate
            final LinearLayout[] root = {(LinearLayout) findViewById(R.id.taskListRoot)};
            LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final LinearLayout taskItem;
            taskItem = (LinearLayout) layoutInflater.inflate(R.layout.history_item, root[0]);


            //layout elements set up
            checkChildren(taskItem, item);

            Log.d(" checking " , imglocation);
            if (imglocation.equals("none")) {
            }
            else{
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap imagey = BitmapFactory.decodeFile(imglocation, options);
                item.mImageView.setImageBitmap(imagey);
            }
            item.imgloc=imglocation;
            //can use now
            item.boughtat.setText("Bought At: " + boughtat);
            item.name.setText(name);

            item.buyDate.setText("");

            String price = Double.toString(pricey);
            int color = rColor(R.color.normal);

            item.colour = color;
            item.head.setBackgroundColor(color);

            item.price.setText("Price: $" + Double.toString(pricey) );

            item.quantity.setText("Quantity:" +Integer.toString(quanty+1));//plus 1 because array position 0 is 1

            item.notes.setText("Notes: "+notes);


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
            if (sortBy.contains("buy")){
                item.sortby.setText(buyDate);
                //item.price.setVisibility(View.GONE);
            }

            item.body.setVisibility(View.GONE);
            //show
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





//Photo function
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

                    final Dialog dialog =  new Dialog(au.edu.utas.smartshopping.shoppinghistory.this);
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
                    //show the pop up
                    dialog.show();

                }
            });




            //delete buton
            item.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //cconfirm
                    String Message = "This will permanently delete the item named:\n" +
                            name + " from your shopping list \n Are you sure?";
                    Log.d("Validation Error :", Message);
                    final Dialog dialog =  new Dialog(au.edu.utas.smartshopping.shoppinghistory.this);
                    dialog.setContentView(R.layout.delete);
                    dialog.setTitle("WARNING!");
                    TextView errMsg = (TextView)dialog.findViewById(R.id.delete_message);
                    errMsg.setText(Message);
                    //yes
                    Button delYes = (Button)dialog.findViewById(R.id.delete_yes);
                    delYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //delete
                            delete(name, boughtat);
                            item.me.setVisibility(View.GONE);
                            dialog.dismiss();
                        }
                    });
                    //no
                    Button delNo = (Button)dialog.findViewById(R.id.delete_no);
                    delNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    //show pop up
                    dialog.show();

                }
            });
            //edit button
            item.buyagain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(" clicked buy again ","");
                    editItem(name, boughtat);
                }
            });
            //  Log.d("inflated", item.name.getText().toString());
        }


        int rColor(int resource){
            //for complete color change
            return ContextCompat.getColor(this, resource);
        }

        void checkChildren(View view, final historyitem item){
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup)view;
                //how many children
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
            if (id == R.id.imgview)
                item.mImageView = (ImageView)view;
            //if (id == R.id.buttons) //dont need?
            if (id == R.id.buyagain)
                item.buyagain = (Button)view;
            if (id == R.id.delete)
                item.delete = (Button)view;
            if (id == R.id.taskItem)
                item.me = view;



        }

        void editItem(String name, String boughtat){
            //    Log.d("edit the item", name + boughtat);
            SharedPreferences.Editor edittask = getSharedPreferences("newitem", MODE_PRIVATE).edit();
            //store data for open in new item screen
            edittask.putString("newitem?", "Yes");
            Log.d(" new item stored ","newthing");
            edittask.putString("name", name);
            edittask.putString("boughtat", boughtat);
            edittask.commit();


            Intent i = new Intent(au.edu.utas.smartshopping.shoppinghistory.this, newshopping.class);
           startActivity(i);
        }

    public class aDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        final Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE d MMM yyyy", Locale.getDefault());
        SimpleDateFormat sortFormater = new SimpleDateFormat("d %m yyyy", Locale.getDefault());

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }
        public void onDateSet(DatePicker view, int year, int month, int day) {
            c.set(year, month, day);
            startyDate = formatter.format(c.getTime());
            startDate.setText(formatter.format(c.getTime()));
            int total = day + (month * 100) + (year * 1500);


            root = (ViewGroup) findViewById(R.id.taskListRoot);
            root.removeAllViewsInLayout();
            populate("date");

        }
    }

    public class bDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        final Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE d MMM yyyy", Locale.getDefault());
        SimpleDateFormat sortFormater = new SimpleDateFormat("d %m yyyy", Locale.getDefault());

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }
        public void onDateSet(DatePicker view, int year, int month, int day) {
            c.set(year, month, day);
            endyDate = formatter.format(c.getTime());
            endDate.setText(formatter.format(c.getTime()));
            int total = day + (month * 100) + (year * 1500);

            root = (ViewGroup) findViewById(R.id.taskListRoot);
            root.removeAllViewsInLayout();
            populate("date");


        }



    }



}
