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
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

import see.must.mustseeapp.Model.CustomAdapter;
import see.must.mustseeapp.Model.InterestPoint;

public class SearchActivity extends Activity{
        private ListView lv;

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.search);
            lv = findViewById(R.id.list);
        }

        public void search(View view) {
            EditText editText = findViewById(R.id.edit_search);
            String name = editText.getText().toString();
            ParseQuery<InterestPoint> query = ParseQuery.getQuery("InterestPoint");
            query.whereContains("nombre", name);
            query.findInBackground(new FindCallback<InterestPoint>() {
                public void done(List<InterestPoint> objects, ParseException e) {
                    if (e == null) {
                        //ArrayAdapter todoItemsAdapter = new ArrayAdapter<InterestPoint>(getApplicationContext(), R.layout.row_layout, R.id.text, objects);
                        Integer[] images={};
                        CustomAdapter todoItemsAdapter = new CustomAdapter(SearchActivity.this, objects);
                        lv.setAdapter(todoItemsAdapter);
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                InterestPoint item = (InterestPoint) lv.getItemAtPosition(position);
                                Bundle bundle = new Bundle();
                                bundle.putString("name", item.getNombre());
                                bundle.putString("id",item.getId());
                                bundle.putString("descripcion", item.getDescripcion());
                                Intent intent = new Intent(getApplicationContext(), ShowInteresPointActivity.class);
                                intent.putExtras(bundle);
                                startActivityForResult(intent, 1);
                            }
                        });
                    } else {
                        Log.v("error query, reason: " + e.getMessage(), "getServerList()");
                        Toast.makeText(
                                getBaseContext(),
                                "getServerList(): error  query, reason: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
}
