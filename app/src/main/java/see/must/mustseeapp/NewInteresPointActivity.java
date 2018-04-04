package see.must.mustseeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import static android.app.Activity.RESULT_OK;



public class NewInteresPointActivity extends Activity {
    int position;
    String name;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_display);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        position= bundle.getInt("position");
        name= bundle.getString("name");

        Double latitud = (Double) bundle.getDouble("latitud");
        Double longitud = (Double) bundle.getDouble("longitud");
        //TextView textView =(TextView) findViewById(R.id.textData);
        //textView.setText("Datos actuales \nLatitud: "+latitud.toString()+"\nLongitud: "+longitud.toString());

    }

    public void updatePoint(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        //EditText editText1 = (EditText) findViewById(R.id.edit_latitud);
        //EditText editText2 = (EditText) findViewById(R.id.edit_longitud);
        //String latitud = editText1.getText().toString();
        //String longitud = editText2.getText().toString();
        bundle.putString("name", name);
        //bundle.putString("latitud", latitud);
        //bundle.putString("longitud", longitud);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
