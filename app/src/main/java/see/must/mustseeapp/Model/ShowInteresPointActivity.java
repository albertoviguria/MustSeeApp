package see.must.mustseeapp.Model;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import see.must.mustseeapp.R;

public class ShowInteresPointActivity extends Activity {

    Bundle bundle;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_point);
        Intent intent = getIntent();
        bundle = intent.getExtras();
        String nombre = bundle.getString("name");
        String descripcion = bundle.getString("descripcion");

        TextView titulo = findViewById(R.id.titulo);
        titulo.setText(nombre);
        TextView desc = findViewById(R.id.descripcion);
        desc.setText(descripcion);
    }

    public void insertarEnHistorial(View view) {
       //Rellenar
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
