package see.must.mustseeapp.Model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("InterestPoint")
public class InterestPoint extends ParseObject{
    public String descripcion;

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

    public String getId() {
        return getString("id");
    }

    public void setId(String id) {
        put("id",id);
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

    @Override
    public String toString() {
        return this.getNombre()+" ";
    }
}
