package see.must.mustseeapp.Model;

import android.util.Log;
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


    public String getDescripcion() {return getString("descripcion");
    }

    public void setDescription(String description) {
        put("descripcion",description);
    }

    public void setImage(ParseFile image) {
        put("image", image);
    }

    public void setIcon(ParseFile icon) {
        put("icon", icon);
    }

    public void setSearchKeywords(String searchKeywords) {
        put("searchKeywords", searchKeywords);
    }
}
