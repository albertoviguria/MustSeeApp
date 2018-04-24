package see.must.mustseeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class NewInteresPointActivity extends Activity {
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
        EditText editText1 = findViewById(R.id.edit_descripcion);
        String description = editText1.getText().toString();
        bundle.putString("name", name);
        bundle.putString("description", description);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
