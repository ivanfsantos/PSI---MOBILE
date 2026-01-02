package pt.ipleiria.estg.dei.boleias.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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



    public static boolean isConnectionInternet(Context context){

        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
