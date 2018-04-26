package see.must.mustseeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import see.must.mustseeapp.Model.InterestPoint;

public class ShowInteresPointActivity extends Activity {
    private String id;
    private Bundle bundle;
    private File file;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_point);
        Intent intent = getIntent();
        bundle = intent.getExtras();
        String nombre = bundle.getString("name");
        String descripcion = bundle.getString("descripcion");
        id = bundle.getString("id");

        TextView titulo = findViewById(R.id.titulo);
        titulo.setText(nombre);
        TextView desc = findViewById(R.id.descripcion);
        CheckBox cbox = findViewById(R.id.checkBox);
        cbox.setChecked(false);
        try {
            file = getFileStreamPath("historial.txt");
            List<String> lista;
            if (!file.exists()) {
                file.createNewFile();
            }

            Scanner scan = new Scanner(file);

            String line = scan.nextLine();
            String[] listado = line.split("[|]");
            lista = Arrays.asList(listado);
            scan.close();

            if (lista.contains(id)) {
                cbox.setChecked(true);
            }
        }
        catch(Exception ex){
        }

        desc.setText(descripcion);
    }

    public void insertarEnHistorial(View view) {
        CheckBox cbox = findViewById(R.id.checkBox);
        cbox.setChecked(!cbox.isChecked());
        ParseQuery<InterestPoint> query = ParseQuery.getQuery("InterestPoint");
        query.whereEqualTo("id", id);
        query.findInBackground(new FindCallback<InterestPoint>() {
            public void done(List<InterestPoint> objects, ParseException e) {
                if (e == null) {
                    ArrayAdapter<InterestPoint> todoItemsAdapter = new ArrayAdapter<InterestPoint>(getApplicationContext(), R.layout.content_main, R.id.mapView, objects);
                    if(todoItemsAdapter.getCount()>0){
                        file = getFileStreamPath("historial.txt");
                        try {
                            List<String> lista;
                            if (!file.exists()) {
                                file.createNewFile();
                            }

                            Scanner scan = new Scanner(file);

                            String line = scan.nextLine();
                            String[] listado = line.split("[|]");
                            lista = Arrays.asList(listado);
                            scan.close();

                            if(!lista.contains(id)) {
                                FileOutputStream writer = openFileOutput(file.getName(),  MODE_APPEND | MODE_PRIVATE);
                                id = id.concat("|");
                                writer.write(id.getBytes());
                                writer.close();

                                //display file saved message
                                Toast.makeText(getBaseContext(), "Punto de Interés añadido al Historial!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                FileOutputStream writer = openFileOutput(file.getName(),  MODE_PRIVATE);
                                for (String s : lista) {
                                    if(!s.equals(id)) {
                                        s = s.concat("|");
                                        writer.write(s.getBytes());
                                    }
                                }
                                writer.close();
                                //display file saved message
                                Toast.makeText(getBaseContext(), "Punto de Interés borrado del Historial!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception error) {
                            try {
                                FileOutputStream writer = openFileOutput(file.getName(), MODE_PRIVATE);
                                id = id.concat("|");
                                writer.write(id.getBytes());
                                writer.close();

                                //display file saved message
                                Toast.makeText(getBaseContext(), "Punto de Interés añadido al Historial!", Toast.LENGTH_SHORT).show();
                            }
                            catch(Exception ex){}

                            //display file saved message
                            Toast.makeText(getBaseContext(), "Punto de Interés añadido al Historial!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Log.v("error reason: no existe","");
                    }
                }
                else {
                    Log.v("error query, reason: " + e.getMessage(), " error");
                }
            }
        });

        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
