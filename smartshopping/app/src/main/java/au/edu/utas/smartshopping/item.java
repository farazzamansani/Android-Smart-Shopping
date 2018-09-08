package au.edu.utas.smartshopping;

import android.content.Context;
import android.media.Image;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;


public class item {
    View me;
    TextView name;
    TextView sortby;    //Displays a copy of the value task list is sorted by
    TextView buyDate;
    TextView boughtat;
    TextView price;
    TextView quantity;
    TextView notes;
    Button Image;
    Image pic;
    TextView attached;
    int colour;
    Switch complete;
    Button edit;
    Button delete;
    ImageView mImageView;
    String imgloc;
    boolean isImageFitToScreen;

    //Containers
    View root;          //absolute parent view
    View head;         //contains name and sort by value
    View body;       //expand & collapse area containing data and buttons.

    Context activity;

    public item(Context context){
        activity = context;
        //cant find recource by id in here...

    }
}