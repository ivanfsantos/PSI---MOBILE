package pt.ipleiria.estg.dei.boleias.listeners;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.boleias.modelos.Reserva;

public interface ReservasListener {
    void onRefreshListaReservas(ArrayList<Reserva> listaReservas);
}
