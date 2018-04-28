package see.must.mustseeapp.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
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
    private List<byte[]> images;

    public CustomAdapter(Context context, List<InterestPoint> objects, List<byte[]> imagenes) {
        super(context,R.layout.show_historial, objects);
        this.context = context;
        this.puntos =  objects;
        this.images = imagenes;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        String inflater = context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater vi;
        vi=(LayoutInflater)getContext().getSystemService(inflater);
        View rowView= vi.inflate(R.layout.row_layout, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.text);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.pic);
        txtTitle.setText(puntos.get(position).getNombre());
        Log.v("posicion",String.valueOf(position));
        if(images.size()!=0) {
            Bitmap bmp = BitmapFactory.decodeByteArray(images.get(position), 0, images.get(position).length);
            imageView.setImageBitmap(bmp);
        }

        return rowView;
    }
}