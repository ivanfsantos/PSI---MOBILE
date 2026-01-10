package pt.ipleiria.estg.dei.boleias.modelos;

public class Avaliacao {

    private int id;
    private String descricao;
    private int perfil_id;

    public Avaliacao (int id, String descricao, int perfil_id){
        this.id = id;
        this.descricao = descricao;
        this. perfil_id = perfil_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getPerfil_id() {
        return perfil_id;
    }

    public void setPerfil_id(int perfil_id) {
        this.perfil_id = perfil_id;
    }
}
