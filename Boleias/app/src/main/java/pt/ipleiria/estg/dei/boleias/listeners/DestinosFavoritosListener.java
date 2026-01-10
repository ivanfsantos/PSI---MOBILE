package pt.ipleiria.estg.dei.boleias.listeners;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.boleias.modelos.DestinoFavorito;

public interface DestinosFavoritosListener {
    void onRefreshListaDestinosFavoritos(ArrayList<DestinoFavorito> listaDestinosFavoritos);

}
