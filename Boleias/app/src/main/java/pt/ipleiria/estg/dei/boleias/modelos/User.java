package pt.ipleiria.estg.dei.boleias.modelos;

public class User {


    private String nome;
    private String token;
    private int condutor;
    private int perfil_id;


    public User(String nome, String token, int condutor, int perfil_id)
    {
        this.nome = nome;
        this.token = token;
        this.condutor = condutor;
        this.perfil_id = perfil_id;
    }

    public int getPerfil_id() {
        return perfil_id;
    }



    public String getNome() {
        return nome;
    }



    public String getToken() {
        return token;
    }


    public int getCondutor() {
        return condutor;
    }

}



