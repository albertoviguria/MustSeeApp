package see.must.mustseeapp.Model;

import com.parse.ParseClassName;
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

    @Override
    public String toString() {
        return this.getNombre()+" "+this.getLatitud()+" "+this.getLongitud();
    }
}
