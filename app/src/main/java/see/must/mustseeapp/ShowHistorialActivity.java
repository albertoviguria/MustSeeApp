package see.must.mustseeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import see.must.mustseeapp.Model.CustomAdapter;
import see.must.mustseeapp.Model.InterestPoint;

public class ShowHistorialActivity  extends Activity {
    private ListView lv;
    private List<byte[]> imagenes = new ArrayList<byte[]>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_historial);
        lv = findViewById(R.id.list);

        try{
            List<String> lista;
            File file = getFileStreamPath("historial.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            Scanner scan = new Scanner(file);

            Log.v("Pre Lectura", "entrada 2");
            String line = scan.nextLine();
            String[] listado = line.split("[|]");
            lista = Arrays.asList(listado);

            Log.v("line leida", String.valueOf(lista.size()));
            Log.v("Post lectura", "salida");
            scan.close();

            ParseQuery<InterestPoint> query = ParseQuery.getQuery("InterestPoint");
            query.whereContainedIn("id",lista);
            query.findInBackground(new FindCallback<InterestPoint>() {
                public void done(final List<InterestPoint> objects, ParseException e) {
                    if (e == null) {
                        for (InterestPoint object : objects) {
                            ParseFile applicantResume = (ParseFile)object.get("image");
                            applicantResume.getDataInBackground(new GetDataCallback() {
                                public void done(byte[] data, ParseException e) {
                                    if (e == null) {
                                        Log.v("1 imagen con e null","d hnj");
                                        imagenes.add(data);
                                        if(imagenes.size() == objects.size()){
                                            final CustomAdapter todoItemsAdapter = new CustomAdapter(ShowHistorialActivity.this, objects, imagenes);
                                            lv.setAdapter(todoItemsAdapter);
                                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    InterestPoint item = (InterestPoint) lv.getItemAtPosition(position);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("id", item.getId());
                                                    Intent intent = new Intent(getApplicationContext(), ShowInteresPointActivity.class);
                                                    intent.putExtras(bundle);
                                                    startActivityForResult(intent, 1);
                                                }
                                            });
                                        }
                                    } else {
                                        Log.v("No hay nada","hdn");
                                        //imamgenes.add("no_image.png");
                                    }
                                }
                            });
                        }
                    } else {
                        Log.v("error query, reason: " + e.getMessage(), "getServerList()");
                        Toast.makeText(
                                getBaseContext(),
                                "getServerList(): error  query, reason: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
