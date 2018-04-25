package see.must.mustseeapp.Model;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import see.must.mustseeapp.R;

public class CustomAdapter extends ArrayAdapter<String>{
    private final Activity context;
    private final String[] puntos;
    private Integer[] imageId;

    public CustomAdapter(Activity context,String[] puntos, Integer[] imageId) {
        super(context, R.layout.row_layout, puntos);
        this.context = context;
        this.puntos = puntos;
        this.imageId = imageId;
    }

    public void set_imageId(Integer[] imageId){
        this.imageId = imageId;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.row_layout, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.text);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.pic);
        txtTitle.setText(puntos[position]);

        imageView.setImageResource(imageId[position]);
        return rowView;
    }
}