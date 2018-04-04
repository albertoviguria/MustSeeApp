package see.must.mustseeapp.Model;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class TravelPointsApplication extends Application {


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
