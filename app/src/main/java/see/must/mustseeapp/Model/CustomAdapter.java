package see.must.mustseeapp.Model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import see.must.mustseeapp.R;

public class CustomAdapter extends ArrayAdapter<InterestPoint>{
    private final Context context;
    private final List<InterestPoint> puntos;
    private Integer[] images;

    //Integer[] imagenes
    public CustomAdapter(Context context, List<InterestPoint> objects ) {
        super(context,R.layout.row_layout, objects);
        this.context = context;
        this.puntos =  objects;
        //this.images = imagenes;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        String inflater = context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater vi;
        vi=(LayoutInflater)getContext().getSystemService(inflater);
        View rowView= vi.inflate(R.layout.row_layout, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.text);

        //ImageView imageView = (ImageView) rowView.findViewById(R.id.pic);
        txtTitle.setText(puntos.get(position).getNombre());

        //imageView.setImageResource(images[position]);
        return rowView;
    }
}