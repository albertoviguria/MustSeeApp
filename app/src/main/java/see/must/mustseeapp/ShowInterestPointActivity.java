package see.must.mustseeapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import see.must.mustseeapp.Model.InterestPoint;

public class ShowInterestPointActivity extends Activity {
    private String id;
    private Bundle bundle;
    private File file;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_point);
        Intent intent = getIntent();
        bundle = intent.getExtras();
        id = bundle.getString("id");

        ParseQuery<InterestPoint> query = ParseQuery.getQuery("InterestPoint");
        query.getInBackground(id, new GetCallback<InterestPoint>() {
            public void done(final InterestPoint aInterestPoint, ParseException e) {
                if (e == null) {
                    ParseFile imagen = (ParseFile) aInterestPoint.get("image");
                    imagen.getDataInBackground(new GetDataCallback() {
                        public void done(byte[] data, ParseException e) {
                        if (e == null) {
                            Log.v("1 imagen con e null", "d hnj");
                            ImageView espacio = findViewById(R.id.pic);
                            TextView titulo = findViewById(R.id.titulo);
                            titulo.setText(aInterestPoint.getNombre());
                            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                            espacio.setImageBitmap(bmp);
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
                            desc.setText( aInterestPoint.getDescripcion());
                        }
                        }
                    });
                } else {
                    // something went wrong
                    Log.v("error query, reason: ", e.getMessage()+id);
                    Toast.makeText(
                        getBaseContext(),
                        "getServerList(): error  query, reason: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void insertarEnHistorial(View view) {
        CheckBox cbox = findViewById(R.id.checkBox);
        cbox.setChecked(!cbox.isChecked());
        ParseQuery<InterestPoint> query = ParseQuery.getQuery("InterestPoint");
        query.getInBackground(id, new GetCallback<InterestPoint>() {
            public void done(InterestPoint aInterestPoint, ParseException e) {
                if (e == null) {
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
