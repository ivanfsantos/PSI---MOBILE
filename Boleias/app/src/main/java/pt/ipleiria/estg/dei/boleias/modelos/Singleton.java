package pt.ipleiria.estg.dei.boleias.modelos;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pt.ipleiria.estg.dei.boleias.MenuMainActivity;
import pt.ipleiria.estg.dei.boleias.R;
import pt.ipleiria.estg.dei.boleias.listeners.LoginListener;
import pt.ipleiria.estg.dei.boleias.listeners.ViaturaListener;
import pt.ipleiria.estg.dei.boleias.listeners.ViaturasListener;
import pt.ipleiria.estg.dei.boleias.utils.JSONParser;

public class Singleton {

    private static Singleton instance = null;

    private ArrayList<Viatura> viaturas;
    private BDHelper boleiasBD = null;


    private static RequestQueue volleyQueue;

    private String mUrlApiLogin = "http://192.168.1.75/PROJETOS/boleias/web/PSI-WEB/boleias/backend/web/api/auth";
    private String mUrlApiViatura = "http://192.168.1.75/PROJETOS/boleias/web/PSI-WEB/boleias/backend/web/api/viatura";

    private LoginListener loginListener;
    private ViaturaListener viaturaListener;
    private ViaturasListener viaturasListener;

    public Singleton(Context context)
    {
        viaturas = new ArrayList<>();
        boleiasBD = new BDHelper(context);
    }


    public static synchronized Singleton getInstance(Context context){
        if(instance == null){
            instance = new Singleton(context);
            volleyQueue = Volley.newRequestQueue(context);
        }

        return instance;
    }


    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    public void setViaturasListener(ViaturasListener viaturasListener) {
        this.viaturasListener = viaturasListener;
    }

    public void setViaturaListener(ViaturaListener viaturaListener) {
        this.viaturaListener = viaturaListener;
    }

    
    public void loginAPI(final String username, final String password, Context context){
        if(!JSONParser.isConnectionInternet(context)) {
            Toast.makeText(context, R.string.erro_ligacao_internet, Toast.LENGTH_SHORT).show();
        } else {
            StringRequest request = new StringRequest(Request.Method.POST, mUrlApiLogin, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    try {
                        JSONObject response = new JSONObject(s);
                        boolean success = response.getBoolean("success");

                        if (success) {

                            User user = JSONParser.parserJsonLogin(s);
                            if (user != null && loginListener != null) {
                                loginListener.onValidateLogin(user.getNome(), user.getToken(),
                                        user.getCondutor(), user.getPerfil_id());
                            }
                        } else {

                            String msg = response.getString("message");
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Erro no formato dos dados", Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    if (volleyError.networkResponse != null){
                        int statusCode = volleyError.networkResponse.statusCode;
                        System.out.println("-->STATUS: " + statusCode);
                    }else{
                        System.out.println("-->Erro: TIMEOUT OU SEM NET");
                    }
                    Toast.makeText(context, volleyError.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("username",username);
                    params.put("password", password);
                    return params;
                }
            };
            volleyQueue.add(request);
        }
    }

    public void getAllViaturasAPI(final Context context, String tokenAPI, String perfil_id) {
        if (!JSONParser.isConnectionInternet(context)) {
            Toast.makeText(context, R.string.erro_ligacao_internet, Toast.LENGTH_SHORT).show();
        }
        else {
        String url = mUrlApiViatura + "?access-token=" + tokenAPI + "&perfil_id=" + perfil_id;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray jsonArray = response.getJSONArray("data");
                            viaturas = JSONParser.parserJsonViaturas(jsonArray);
                            adicionarViaturasBD(viaturas);

                            if (viaturasListener != null) {
                                viaturasListener.onRefreshListaViaturas(viaturas);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If it still goes here, check Logcat for the status code
                        Toast.makeText(context, "API Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });

        volleyQueue.add(request);
        }
    }

    public void editarViaturaAPI(String tokenAPI, final Viatura viatura, final Context context){
        if (!JSONParser.isConnectionInternet(context)) {
            Toast.makeText(context, R.string.erro_ligacao_internet, Toast.LENGTH_SHORT).show();
        }
        else {
            String token = "?access-token=" + tokenAPI;
            StringRequest request = new StringRequest(Request.Method.PUT, mUrlApiViatura + "/" + viatura.getId() + token, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    editarViaturaBD(viatura);
                    if (viaturaListener != null){
                        viaturaListener.onRefreshDetalhes(MenuMainActivity.EDIT);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(context, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();

                    params.put("marca", viatura.getMarca());
                    params.put("modelo", viatura.getModelo());
                    params.put("matricula", viatura.getMatricula());
                    params.put("cor", viatura.getCor());

                    return params;
                }
            };
            volleyQueue.add(request);
        }

    }


    public void adicionarViaturaAPI(String tokenAPI, final Viatura viatura, final Context context){
        if(!JSONParser.isConnectionInternet(context)) {
            Toast.makeText(context, R.string.erro_ligacao_internet, Toast.LENGTH_SHORT).show();
        } else{
                String token = "?access-token=" + tokenAPI;
                StringRequest request = new StringRequest(Request.Method.POST, mUrlApiViatura + token, new Response.Listener<String>() {

                @Override
                public void onResponse(String s) {
                    Viatura viatura = JSONParser.parserJsonViatura(s);
                    adicionarViaturaBD(viatura);
                    if (viaturaListener != null){
                        viaturaListener.onRefreshDetalhes(MenuMainActivity.ADD);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(context, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();

                    params.put("marca", viatura.getMarca());
                    params.put("modelo", viatura.getModelo());
                    params.put("matricula", viatura.getMatricula());
                    params.put("cor", viatura.getCor());
                    params.put("perfil_id", viatura.getPerfil_id() + "");

                    return params;
                }
            };
            volleyQueue.add(request);
        }
    }

    public void removerViaturaAPI(String tokenAPI, final Viatura viatura, final Context context){
        if(!JSONParser.isConnectionInternet(context)) {
            Toast.makeText(context, R.string.erro_ligacao_internet, Toast.LENGTH_SHORT).show();
        } else {

            String token = "?access-token=" + tokenAPI;
            StringRequest request = new StringRequest(Request.Method.DELETE, mUrlApiViatura + "/" + viatura.getId() + token, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    removerViaturaBD(viatura.getId());
                    if (viaturaListener != null){
                        viaturaListener.onRefreshDetalhes(MenuMainActivity.DEL);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(context, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            volleyQueue.add(request);
        }
    }


    public void adicionarViaturaBD(Viatura viatura){
        Viatura auxViatura = boleiasBD.adicionarViaturaBD(viatura);
        if (auxViatura != null){
            viaturas.add(auxViatura);
        }
    }

    public void adicionarViaturasBD(ArrayList<Viatura> viaturas) {
        boleiasBD.removerAllViaturasBD();
        for (Viatura v : viaturas) {
            boleiasBD.adicionarViaturaBD(v);
        }
    }

    public void editarViaturaBD(Viatura viatura){
        Viatura v =  getViatura(viatura.getId());
        if (v != null){
            boleiasBD.editarViaturaBD(viatura);
        }
    }

    public void removerViaturaBD(int idViatura){
        Viatura v = getViatura(idViatura);
        if (v != null) {
            if (boleiasBD.removerViaturaBD(v.getId())) {
                viaturas.remove(v);
            }
        }
    }

    public Viatura getViatura(int idViatura) {

        for (Viatura viatura: viaturas) {
            if (viatura.getId()==idViatura){
                return viatura;
            }
        }
        return null;
    }

    public ArrayList<Viatura> getViaturasBD() {
        viaturas = boleiasBD.getAllViaturasBD();
        return new ArrayList<>(viaturas);
    }

    public boolean matriculaExiste(String matricula, int idViatura) {
        for (Viatura v : viaturas) {
            if (v.getMatricula().equalsIgnoreCase(matricula) && v.getId() != idViatura) {
                return true;
            }
        }
        return false;
    }
}
