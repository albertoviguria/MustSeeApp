package see.must.mustseeapp.Model;

import com.parse.GetDataCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("InterestPoint")
public class InterestPoint extends ParseObject{

    public InterestPoint() {
    }

    public double getLatitud() {
        return  getDouble("latitud");
    }

    public void setLatitud(double latitud) {
        put("latitud",latitud);
    }

    public double getLongitud() {
       return getDouble("longitud");
    }

    public void setLongitud(double longitud) {
        put("longitud",longitud);
    }

    public String getNombre() {return getString("nombre");
    }

    public void setNombre(String nombre) {
        put("nombre",nombre);
    }

    public ParseFile getImage() {
        ParseFile imagen = (ParseFile)get("image");
        imagen.getDataInBackground(new GetDataCallback() {
            public void done(byte[] data, ParseException e) {
                if (e == null) {
                    // data has the bytes for the resume
                } else {
                    // something went wrong
                }
            }
        });
        return imagen;
    }

    public void setImage(ParseFile image) { put("image", image); }
    @Override
    public String toString() {
        return this.getNombre()+" "+this.getLatitud()+" "+this.getLongitud();
    }

}
