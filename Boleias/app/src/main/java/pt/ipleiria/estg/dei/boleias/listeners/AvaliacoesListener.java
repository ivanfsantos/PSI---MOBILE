package pt.ipleiria.estg.dei.boleias.listeners;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.boleias.modelos.Avaliacao;


public interface AvaliacoesListener {
    void onRefreshListaAvaliacoes(ArrayList<Avaliacao> listaAvaliacoes);

}
