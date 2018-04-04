package see.must.mustseeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class NewInteresPointActivity extends Activity {
    int position;
    String name;
    Bundle bundle;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_point);
        Intent intent = getIntent();
        bundle = intent.getExtras();
    }

    public void saveLocation(View view) {
        EditText editText = findViewById(R.id.edit_nombre);
        String name = editText.getText().toString();
        bundle.putString("name", name);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
