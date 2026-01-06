package pt.ipleiria.estg.dei.boleias.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.boleias.modelos.Boleia;
import pt.ipleiria.estg.dei.boleias.modelos.Reserva;
import pt.ipleiria.estg.dei.boleias.modelos.User;
import pt.ipleiria.estg.dei.boleias.modelos.Viatura;

public class JSONParser {




    public static User parserJsonLogin(String response){

        try{
            JSONObject login = new JSONObject(response);
            if(login.getBoolean("success")){
                String nome = login.getString("nomePerfil");
                String token = login.getString("token");
                int condutor = login.getInt("condutor");
                int perfil_id = login.getInt("perfil_id");

                return new User(nome, token, condutor, perfil_id);
            }

        } catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }


    // viaturas
    public static Viatura parserJsonViatura(String response){


        Viatura auxViatura = null;
        try{
            JSONObject viatura = new JSONObject(response);

            int id = viatura.getInt("id");
            String marca = viatura.getString("marca");
            String modelo = viatura.getString("modelo");
            String matricula = viatura.getString("matricula");
            String cor = viatura.getString("cor");
            int perfil_id = viatura.getInt("perfil_id");

            auxViatura = new Viatura(id, marca, modelo, matricula, cor, perfil_id);

        }catch (JSONException e) {
            e.printStackTrace();
        }

        return auxViatura;
    }

    public static ArrayList<Viatura> parserJsonViaturas(JSONArray response){
        ArrayList<Viatura> viaturas = new ArrayList<>();
        try{
            for (int i=0; i< response.length(); i++){

                JSONObject viatura = response.getJSONObject(i);
                int id = viatura.getInt("id");
                String marca = viatura.getString("marca");
                String modelo = viatura.getString("modelo");
                String matricula = viatura.getString("matricula");
                String cor = viatura.getString("cor");
                int perfil_id = viatura.getInt("perfil_id");

                Viatura auxViatura = new Viatura(id, marca, modelo, matricula, cor, perfil_id);

                viaturas.add(auxViatura);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }

        return viaturas;
    }


    // boleias

    public static Boleia parserJsonBoleia(String response){


        Boleia auxBoleia = null;
        try{
            JSONObject boleia = new JSONObject(response);

            int id = boleia.getInt("id");
            String origem = boleia.getString("origem");
            String destino = boleia.getString("destino");
            String data_hora = boleia.getString("data_hora");
            int lugares_disponiveis = boleia.getInt("lugares_disponiveis");
            double preco = boleia.getDouble("preco");
            int viatura_id = boleia.getInt("viatura_id");



            auxBoleia = new Boleia(id, origem, destino, data_hora, lugares_disponiveis, preco, viatura_id);

        }catch (JSONException e) {
            e.printStackTrace();
        }

        return auxBoleia;
    }

    public static ArrayList<Boleia> parserJsonBoleias(JSONArray response){

        ArrayList<Boleia> boleias = new ArrayList<>();

        try{
            for (int i=0; i< response.length(); i++){

                JSONObject boleia = response.getJSONObject(i);

                int id = boleia.getInt("id");
                String origem = boleia.getString("origem");
                String destino = boleia.getString("destino");
                String data_hora = boleia.getString("data_hora");
                int lugares_disponiveis = boleia.getInt("lugares_disponiveis");
                double preco = boleia.getDouble("preco");
                int viatura_id = boleia.getInt("viatura_id");

                Boleia auxBoleia = new Boleia(id, origem, destino, data_hora, lugares_disponiveis, preco, viatura_id);

                boleias.add(auxBoleia);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }

        return boleias;
    }

    public static Reserva parserJsonReserva(String response){


        Reserva auxReserva = null;
        try {
            JSONObject root = new JSONObject(response);

            if (root.has("data")) {
                JSONObject reserva = root.getJSONObject("data");

                int id = reserva.getInt("id");
                String ponto_encontro = reserva.getString("ponto_encontro");

                int contacto = reserva.optInt("contacto", 0);
                if (contacto == 0 && reserva.has("contacto")) {
                    contacto = Integer.parseInt(reserva.getString("contacto"));
                }
                double reembolso = reserva.optDouble("reembolso", 0.0);
                String estado = reserva.getString("estado");
                int perfil_id = reserva.getInt("perfil_id");
                int boleia_id = reserva.getInt("boleia_id");

                auxReserva = new Reserva(id, ponto_encontro, contacto, reembolso, estado, perfil_id, boleia_id);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return auxReserva;
    }

    public static ArrayList<Reserva> parserJsonReservas(JSONArray response){

        ArrayList<Reserva> reservas = new ArrayList<>();

        try{
            for (int i=0; i< response.length(); i++){

                JSONObject reserva = response.getJSONObject(i);

                int id = reserva.getInt("id");
                String ponto_encontro = reserva.getString("ponto_encontro");
                int contacto = reserva.getInt("contacto");
                double reembolso = reserva.getDouble("reembolso");
                String estado = reserva.getString("estado");
                int perfil_id = reserva.getInt("perfil_id");
                int boleia_id = reserva.getInt("boleia_id");

                Reserva auxReserva = new Reserva(id, ponto_encontro, contacto, reembolso, estado, perfil_id, boleia_id);

                reservas.add(auxReserva);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }

        return reservas;
    }



    public static boolean isConnectionInternet(Context context){

        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
