package pt.ipleiria.estg.dei.boleias.modelos;

public class DestinoFavorito {

    private int id;
    private int boleia_id;
    private int perfil_id;


    public DestinoFavorito(int id, int boleia_id, int perfil_id){
        this.id = id;
        this.boleia_id = boleia_id;
        this.perfil_id = perfil_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBoleia_id() {
        return boleia_id;
    }

    public void setBoleia_id(int boleia_id) {
        this.boleia_id = boleia_id;
    }

    public int getPerfil_id() {
        return perfil_id;
    }

    public void setPerfil_id(int perfil_id) {
        this.perfil_id = perfil_id;
    }
}
