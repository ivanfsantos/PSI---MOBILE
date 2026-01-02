package pt.ipleiria.estg.dei.boleias.listeners;

public interface LoginListener {
    void onValidateLogin(final String nome, final String token, final int condutor, final int perfil_id);
}
