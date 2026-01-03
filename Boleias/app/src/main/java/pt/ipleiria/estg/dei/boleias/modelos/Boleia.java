package pt.ipleiria.estg.dei.boleias.modelos;

public class Boleia {

    private int id;
    private String origem;
    private String destino;
    private String data_hora;
    private int lugares_disponiveis;
    private double preco;
    private int viatura_id;

    public Boleia (int id, String origem, String destino, String data_hora, int lugares_disponiveis, double preco, int viatura_id)
    {
        this.id = id;
        this.origem = origem;
        this.destino = destino;
        this.data_hora = data_hora;
        this.lugares_disponiveis = lugares_disponiveis;
        this.preco = preco;
        this.viatura_id = viatura_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getData_hora() {
        return data_hora;
    }

    public void setData_hora(String data_hora) {
        this.data_hora = data_hora;
    }

    public int getLugares_disponiveis() {
        return lugares_disponiveis;
    }

    public void setLugares_disponiveis(int lugares_disponiveis) {
        this.lugares_disponiveis = lugares_disponiveis;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getViatura_id() {
        return viatura_id;
    }

    public void setViatura_id(int viatura_id) {
        this.viatura_id = viatura_id;
    }
}
