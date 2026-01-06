package pt.ipleiria.estg.dei.boleias.modelos;

public class Reserva {

    private int id;
    private String ponto_encontro;
    private int contacto;
    private double reembolso;
    private String estado;
    private int perfil_id;
    private int boleia_id;

    public Reserva(int id, String ponto_encontro, int contacto, double reembolso, String estado, int perfil_id, int boleia_id)
    {
        this.id = id;
        this.ponto_encontro = ponto_encontro;
        this.contacto = contacto;
        this.reembolso = reembolso;
        this.estado = estado;
        this.perfil_id = perfil_id;
        this.boleia_id = boleia_id;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPonto_encontro() {
        return ponto_encontro;
    }

    public void setPonto_encontro(String ponto_encontro) {
        this.ponto_encontro = ponto_encontro;
    }

    public int getContacto() {
        return contacto;
    }

    public void setContacto(int contacto) {
        this.contacto = contacto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getReembolso() {
        return reembolso;
    }

    public void setReembolso(double reembolso) {
        this.reembolso = reembolso;
    }

    public int getPerfil_id() {
        return perfil_id;
    }

    public void setPerfil_id(int perfil_id) {
        this.perfil_id = perfil_id;
    }

    public int getBoleia_id() {
        return boleia_id;
    }

    public void setBoleia_id(int boleia_id) {
        this.boleia_id = boleia_id;
    }
}
