package pt.ipleiria.estg.dei.boleias;

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

import pt.ipleiria.estg.dei.boleias.modelos.Avaliacao;
import pt.ipleiria.estg.dei.boleias.modelos.Boleia;
import pt.ipleiria.estg.dei.boleias.modelos.Reserva;
import pt.ipleiria.estg.dei.boleias.modelos.Singleton;
import pt.ipleiria.estg.dei.boleias.modelos.Viatura;


public class AvaliarFragment extends Fragment {

    private int boleia_id;
    private EditText etDescricao;
    private Button btnConfirmar;

    public AvaliarFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_avaliar, container, false);

        if (getArguments() != null) {
            boleia_id = getArguments().getInt("boleia_id");
        }

        Boleia boleia = Singleton.getInstance(getContext()).getBoleia(boleia_id);

        etDescricao = view.findViewById(R.id.etDescricao);
        btnConfirmar = view.findViewById(R.id.btnConfirmar);

        btnConfirmar.setOnClickListener(v -> {
            String descricao = etDescricao.getText().toString();

            if (descricao.isEmpty()) {
                Toast.makeText(getContext(), "Preencha a descrição!", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("DADOS_USER", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("token", null);

            int perfilDestinoId = -1;

            if (boleia != null) {
                Viatura viatura = Singleton.getInstance(getContext()).getViatura(boleia.getViatura_id());
                if (viatura != null) {
                    perfilDestinoId = viatura.getPerfil_id();
                }
            }

            if (perfilDestinoId != -1) {
                Avaliacao novaAvaliacao = new Avaliacao(
                        0,
                        descricao,
                        perfilDestinoId
                );

                Singleton.getInstance(getContext()).adicionarAvaliacaoAPI(token, novaAvaliacao, getContext());
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            } else {
                Toast.makeText(getContext(), "Erro: Condutor não encontrado.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}