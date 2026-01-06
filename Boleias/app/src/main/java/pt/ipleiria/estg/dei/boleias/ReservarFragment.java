package pt.ipleiria.estg.dei.boleias;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import pt.ipleiria.estg.dei.boleias.modelos.Reserva;
import pt.ipleiria.estg.dei.boleias.modelos.Singleton;


public class ReservarFragment extends Fragment {

    private int boleia_id;
    private int perfil_id;
    private EditText etPontoEncontro, etContacto;
    private Button btnConfirmar;

    public ReservarFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reservar, container, false);


        if (getArguments() != null) {
            boleia_id = getArguments().getInt("boleia_id");
            perfil_id = getArguments().getInt("perfil_id");
        }

        etPontoEncontro = view.findViewById(R.id.etPontoEncontro);
        etContacto = view.findViewById(R.id.etContacto);
        btnConfirmar = view.findViewById(R.id.btnConfirmar);

        btnConfirmar.setOnClickListener(v -> {

            String pontoEncontro = etPontoEncontro.getText().toString();
            String contactoStr = etContacto.getText().toString();

            if (pontoEncontro.isEmpty() || contactoStr.isEmpty()) {
                Toast.makeText(getContext(), "Preencha os campos todos.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int contactoInt = Integer.parseInt(contactoStr);

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("DADOS_USER", Context.MODE_PRIVATE);
                String token = sharedPreferences.getString("token", null);

                Reserva novaReserva = new Reserva(
                        0,
                        pontoEncontro,
                        contactoInt,
                        0.0,
                        "Pendente",
                        perfil_id,
                        boleia_id
                );

                Singleton.getInstance(getContext()).adicionarReservaAPI(token, novaReserva, getContext());
                requireActivity().getOnBackPressedDispatcher().onBackPressed();


            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Insira números", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }



}