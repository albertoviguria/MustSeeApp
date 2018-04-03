package see.must.mustseeapp;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;
import see.must.mustseeapp.Model.InterestPoint;
import com.parse.Parse;
import com.parse.ParseObject;

public class TravelPointsApplication extends Application {


    public List<InterestPoint> pointList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(InterestPoint.class);

        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("myAppId")
                .clientKey("empty")
                .server("https://mustseeapp.herokuapp.com/parse/")
                .build());


    }




}

