package pt.ipleiria.estg.dei.boleias.modelos;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

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
import pt.ipleiria.estg.dei.boleias.listeners.AvaliacaoListener;
import pt.ipleiria.estg.dei.boleias.listeners.AvaliacoesListener;
import pt.ipleiria.estg.dei.boleias.listeners.BoleiaListener;
import pt.ipleiria.estg.dei.boleias.listeners.BoleiasListener;
import pt.ipleiria.estg.dei.boleias.listeners.DestinoFavoritoListener;
import pt.ipleiria.estg.dei.boleias.listeners.DestinosFavoritosListener;
import pt.ipleiria.estg.dei.boleias.listeners.LoginListener;
import pt.ipleiria.estg.dei.boleias.listeners.ReservaListener;
import pt.ipleiria.estg.dei.boleias.listeners.ReservasListener;
import pt.ipleiria.estg.dei.boleias.listeners.ViaturaListener;
import pt.ipleiria.estg.dei.boleias.listeners.ViaturasListener;
import pt.ipleiria.estg.dei.boleias.utils.JSONParser;

public class Singleton {

    private static Singleton instance = null;

    private BDHelper boleiasBD = null;

    private ArrayList<Viatura> viaturas;
    private ArrayList<Boleia> boleias;
    private ArrayList<Boleia> boleiasFechadas;
    private ArrayList<Reserva> reservas;
    private ArrayList<Avaliacao> avaliacoes;
    private ArrayList<DestinoFavorito> destinosFavoritos;



    private static RequestQueue volleyQueue;


    private String mUrlApiLogin;
    private String mUrlApiViatura;
    private String mUrlApiBoleia;
    private String mUrlApiReserva;
    private String mUrlApiAvaliacao;
    private String mUrlApiDestinoFavorito;


    private LoginListener loginListener;
    private ViaturaListener viaturaListener;
    private ViaturasListener viaturasListener;
    private BoleiaListener boleiaListener;
    private BoleiasListener boleiasListener;
    private ReservaListener reservaListener;
    private ReservasListener reservasListener;
    private AvaliacaoListener avaliacaoListener;
    private AvaliacoesListener avaliacoesListener;
    private DestinoFavoritoListener destinoFavoritoListener;
    private DestinosFavoritosListener destinosFavoritosListener;


    public Singleton(Context context)
    {
        boleiasBD = new BDHelper(context);
        viaturas = new ArrayList<>();
        boleias = new ArrayList<>();
        reservas = new ArrayList<>();
        avaliacoes = new ArrayList<>();
        destinosFavoritos = new ArrayList<>();

        SharedPreferences prefs = context.getSharedPreferences("CONFIGS", Context.MODE_PRIVATE);
        String ipGuardado = prefs.getString("IP_SERVIDOR", "192.168.1.75");
        configurarUrls(ipGuardado);
    }

    public void configurarUrls(String ip) {
        String path;

        if (ip.equals("192.168.1.75")) {
            path = "/PROJETOS/boleias/web/PSI-WEB/boleias/backend/web/api";
        } else {
            path = "/PSI---WEB/boleias/backend/web/api";
        }

        String baseUrl = "http://" + ip + path;

        this.mUrlApiLogin = baseUrl + "/auth";
        this.mUrlApiViatura = baseUrl + "/viatura";
        this.mUrlApiBoleia = baseUrl + "/boleia";
        this.mUrlApiReserva = baseUrl + "/reserva";
        this.mUrlApiAvaliacao = baseUrl + "/avaliacao";
        this.mUrlApiDestinoFavorito = baseUrl + "/destino-favorito";
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

    public void setBoleiasListener(BoleiasListener boleiasListener) {
        this.boleiasListener = boleiasListener;
    }

    public void setBoleiaListener(BoleiaListener boleiaListener) {
        this.boleiaListener = boleiaListener;
    }

    public void setReservasListener(ReservasListener reservasListener) {
        this.reservasListener = reservasListener;
    }

    public void setReservaListener(ReservaListener reservaListener) {
        this.reservaListener = reservaListener;
    }

    public void setAvaliacoesListener(AvaliacoesListener avaliacoesListener) {
        this.avaliacoesListener = avaliacoesListener;
    }

    public void setAvaliacaoListener(AvaliacaoListener avaliacaoListener) {
        this.avaliacaoListener = avaliacaoListener;
    }

    public void setDestinosFavoritosListener(DestinosFavoritosListener destinosFavoritosListener) {
        this.destinosFavoritosListener = destinosFavoritosListener;
    }

    public void setDestinoFavoritoListener(DestinoFavoritoListener destinoFavoritoListener) {
        this.destinoFavoritoListener = destinoFavoritoListener;
    }

    // login
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

    // singleton viaturas
    public void getAllViaturasAPI(final Context context, String tokenAPI) {
        if (!JSONParser.isConnectionInternet(context)) {
            Toast.makeText(context, R.string.erro_ligacao_internet, Toast.LENGTH_SHORT).show();
        }
        else {
        String url = mUrlApiViatura + "?access-token=" + tokenAPI;

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
                    }

                });

        volleyQueue.add(request);
        }
    }

    public void getAllViaturasPerfilAPI(final Context context, String tokenAPI, String perfil_id) {
        if (!JSONParser.isConnectionInternet(context)) {
            Toast.makeText(context, R.string.erro_ligacao_internet, Toast.LENGTH_SHORT).show();
        }
        else {
            String url = mUrlApiViatura + "/index-perfil?access-token=" + tokenAPI + "&perfil_id=" + perfil_id;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                JSONArray jsonArray = response.getJSONArray("data");
                                viaturas = JSONParser.parserJsonViaturas(jsonArray);
                                adicionarViaturasPerfilBD(viaturas);

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
                        }

                    });

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
        if (viatura == null) return;

        final Viatura v = viatura;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Viatura auxViatura = boleiasBD.adicionarViaturaBD(v);
                if (auxViatura != null){
                    viaturas.add(auxViatura);
                }
            }
        }).start();
    }

    public void adicionarViaturasPerfilBD(ArrayList<Viatura> viaturas) {
        if (viaturas == null || viaturas.isEmpty()) return;

        int perfilId = viaturas.get(0).getPerfil_id();

        boleiasBD.removerViaturasPerfilBD(perfilId);

        for (Viatura v : viaturas) {
            boleiasBD.adicionarViaturaBD(v);
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

    public ArrayList<Viatura> getViaturasPerfilBD(int perfilId) {

        return boleiasBD.getAllViaturasPerfilBD(perfilId);
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


    // singleton boleias
    public void getAllBoleiasAPI(final Context context, String tokenAPI) {
        if (!JSONParser.isConnectionInternet(context)) {
            Toast.makeText(context, R.string.erro_ligacao_internet, Toast.LENGTH_SHORT).show();
        }
        else {
            String url = mUrlApiBoleia + "?access-token=" + tokenAPI;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                JSONArray jsonArray = response.getJSONArray("data");
                                JSONArray jsonArrayFechadas = response.getJSONArray("boleias_fechadas");


                                boleias = JSONParser.parserJsonBoleias(jsonArray, 0);
                                boleiasFechadas = JSONParser.parserJsonBoleias(jsonArrayFechadas, 1);

                                java.util.HashMap<Integer, Boleia> mapaBoleias = new java.util.HashMap<>();

                                for (Boleia b : boleias) mapaBoleias.put(b.getId(), b);

                                for (Boleia f : boleiasFechadas) mapaBoleias.put(f.getId(), f);

                                boleias.clear();
                                boleias.addAll(mapaBoleias.values());


                                adicionarBoleiasBD(boleias);

                                if (boleiasListener != null) {
                                    boleiasListener.onRefreshListaBoleias(boleias);
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
                            Toast.makeText(context, "API Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });

            volleyQueue.add(request);
        }

    }

    public void adicionarBoleiaAPI(String tokenAPI, final Boleia boleia, final Context context){
        if(!JSONParser.isConnectionInternet(context)) {
            Toast.makeText(context, R.string.erro_ligacao_internet, Toast.LENGTH_SHORT).show();
        } else{
            String token = "?access-token=" + tokenAPI;
            StringRequest request = new StringRequest(Request.Method.POST, mUrlApiBoleia + token, new Response.Listener<String>() {

                @Override
                public void onResponse(String s) {
                    Boleia boleia = JSONParser.parserJsonBoleia(s);
                    adicionarBoleiaBD(boleia);
                    if (boleiaListener != null){
                        boleiaListener.onRefreshDetalhes(MenuMainActivity.ADD);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        try {
                            String responseBody = new String(volleyError.networkResponse.data, "UTF-8");
                            JSONObject jsonObject = new JSONObject(responseBody);

                            // This retrieves the 'errors' array from your Yii2 return statement
                            if (jsonObject.has("errors")) {
                                String validationErrors = jsonObject.get("errors").toString();
                                Log.e("YII_VALIDATION", validationErrors);
                                Toast.makeText(context, "Validation Fail: " + validationErrors, Toast.LENGTH_LONG).show();
                            } else {
                                String message = jsonObject.optString("message", "Unknown Server Error");
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("DEBUG", "Error parsing 500 response", e);
                        }
                    }

                }
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();

                    params.put("origem", boleia.getOrigem());
                    params.put("destino", boleia.getDestino());
                    params.put("data_hora", boleia.getData_hora());
                    params.put("lugares_disponiveis", boleia.getLugares_disponiveis()+"");
                    params.put("preco", boleia.getPreco()+"");
                    params.put("viatura_id", boleia.getViatura_id()+"");

                    return params;
                }
            };
            volleyQueue.add(request);
        }
    }

    public void editarBoleiaAPI(String tokenAPI, final Boleia boleia, final Context context){
        if (!JSONParser.isConnectionInternet(context)) {
            Toast.makeText(context, R.string.erro_ligacao_internet, Toast.LENGTH_SHORT).show();
        }
        else {
            String token = "?access-token=" + tokenAPI;
            StringRequest request = new StringRequest(Request.Method.PUT, mUrlApiBoleia + "/" + boleia.getId() + token, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    editarBoleiaBD(boleia);
                    if (boleiaListener != null){
                        boleiaListener.onRefreshDetalhes(MenuMainActivity.EDIT);
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

                    params.put("origem", boleia.getOrigem());
                    params.put("destino", boleia.getDestino());
                    params.put("data_hora", boleia.getData_hora());
                    params.put("lugares_disponiveis", boleia.getLugares_disponiveis()+"");
                    params.put("preco", boleia.getPreco()+"");
                    params.put("viatura_id", boleia.getViatura_id()+"");

                    return params;
                }
            };
            volleyQueue.add(request);
        }

    }



    public void removerBoleiaAPI(String tokenAPI, final Boleia boleia, final Context context){
        if(!JSONParser.isConnectionInternet(context)) {
            Toast.makeText(context, R.string.erro_ligacao_internet, Toast.LENGTH_SHORT).show();
        } else {

            String token = "?access-token=" + tokenAPI;
            StringRequest request = new StringRequest(Request.Method.DELETE, mUrlApiBoleia + "/" + boleia.getId() + token, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    removerBoleiaBD(boleia.getId());
                    if (boleiaListener != null){
                        boleiaListener.onRefreshDetalhes(MenuMainActivity.DEL);
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



    public void adicionarBoleiaBD(Boleia boleia){

        if(boleia == null) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Boleia auxBoleia = boleiasBD.adicionarBoleiaBD(boleia);
                if (auxBoleia != null){
                    boleias.add(auxBoleia);
                }
            }
        }).start();

    }

    public void adicionarBoleiasBD(ArrayList<Boleia> boleias) {
        boleiasBD.removerAllBoleiasBD();
        for (Boleia b : boleias) {
            boleiasBD.adicionarBoleiaBD(b);
        }
    }

    public void editarBoleiaBD(Boleia boleia){
        Boleia b =  getBoleia(boleia.getId());
        if (b != null){
            boleiasBD.editarBoleiaBD(boleia);
        }
    }

    public void removerBoleiaBD(int idBoleia){
        Boleia b = getBoleia(idBoleia);
        if (b != null) {
            if (boleiasBD.removerBoleiaBD(b.getId())) {
                viaturas.remove(b);
            }
        }
    }

    public Boleia getBoleia(int idBoleia) {

        for (Boleia boleia: boleias) {
            if (boleia.getId()==idBoleia){
                return boleia;
            }
        }
        return null;
    }

    public ArrayList<Boleia> getBoleiasBD() {
        boleias = boleiasBD.getAllBoleiasBD();
        return new ArrayList<>(boleias);
    }

    // singleton reservas
    public void getAllReservasAPI(final Context context, String tokenAPI, String perfil_id) {
        if (!JSONParser.isConnectionInternet(context)) {
            Toast.makeText(context, R.string.erro_ligacao_internet, Toast.LENGTH_SHORT).show();
        }
        else {
            String url = mUrlApiReserva + "?access-token=" + tokenAPI + "&perfil_id=" + perfil_id;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                JSONArray jsonArray = response.getJSONArray("data");
                                reservas = JSONParser.parserJsonReservas(jsonArray);
                                adicionarReservasBD(reservas);

                                if (reservasListener != null) {
                                    reservasListener.onRefreshListaReservas(reservas);
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
                            Toast.makeText(context, "API Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });

            volleyQueue.add(request);
        }
    }


    public void getAllReservasCondutorAPI(final Context context, String tokenAPI, String boleia_id) {
        if (!JSONParser.isConnectionInternet(context)) {
            Toast.makeText(context, R.string.erro_ligacao_internet, Toast.LENGTH_SHORT).show();
        }
        else {
            String url = mUrlApiReserva + "/reservas" + "?access-token=" + tokenAPI + "&boleia_id=" + boleia_id;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                JSONArray jsonArray = response.getJSONArray("data");
                                reservas = JSONParser.parserJsonReservas(jsonArray);
                                adicionarReservasCondutorBD(reservas);

                                if (reservasListener != null) {
                                    reservasListener.onRefreshListaReservas(reservas);
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
                            Toast.makeText(context, "API Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });

            volleyQueue.add(request);
        }
    }

    public void verificarReservasBoleiaAPI(final Context context, String tokenAPI, int boleiaId) {
        String url = mUrlApiReserva + "/reservas?access-token=" + tokenAPI + "&boleia_id=" + boleiaId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("data");
                        boolean temReservas = jsonArray.length() > 0;

                        if (reservasListener != null) {
                            ArrayList<Reserva> lista = JSONParser.parserJsonReservas(jsonArray);
                            reservasListener.onRefreshListaReservas(lista);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> { });
        volleyQueue.add(request);
    }




    public void validarReservasAPI(final Context context, final String tokenAPI, final String boleia_id) {
        if (!JSONParser.isConnectionInternet(context)) {
            Toast.makeText(context, R.string.erro_ligacao_internet, Toast.LENGTH_SHORT).show();
        } else {
            String url = mUrlApiReserva + "/validar?access-token=" + tokenAPI + "&boleia_id=" + boleia_id;

            StringRequest request = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    try {
                        JSONObject response = new JSONObject(s);
                        boolean success = response.getBoolean("success");

                        if (success) {
                            getAllReservasCondutorAPI(context, tokenAPI, boleia_id);

                            if (reservaListener != null) {
                                reservaListener.onRefreshDetalhes(MenuMainActivity.EDIT);
                            }
                            Toast.makeText(context, "Reservas validadas com sucesso", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String responseBody = new String(error.networkResponse.data, "UTF-8");
                            JSONObject jsonObject = new JSONObject(responseBody);

                            if (jsonObject.has("errors")) {
                                String validationErrors = jsonObject.get("errors").toString();
                                Log.e("YII_VALIDATION", validationErrors);
                                Toast.makeText(context, "Validation Fail: " + validationErrors, Toast.LENGTH_LONG).show();
                            } else {
                                String message = jsonObject.optString("message", "Unknown Server Error");
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("DEBUG", "Error parsing 500 response", e);
                        }
                    }
                }
            });

            volleyQueue.add(request);
        }
    }




    public void adicionarReservaAPI(String tokenAPI, final Reserva reserva, final Context context){
        if(!JSONParser.isConnectionInternet(context)) {
            Toast.makeText(context, R.string.erro_ligacao_internet, Toast.LENGTH_SHORT).show();
        } else {

            String token = "?access-token=" + tokenAPI;
            StringRequest request = new StringRequest(Request.Method.POST, mUrlApiReserva + token, new Response.Listener<String>() {

                @Override
                public void onResponse(String s) {
                    Reserva reserva = JSONParser.parserJsonReserva(s);
                    adicionarReservaBD(reserva);
                    if (reservaListener != null){
                        reservaListener.onRefreshDetalhes(MenuMainActivity.ADD);
                    }
                    Toast.makeText(context, "Reserva guardada com sucesso", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        try {
                            String responseBody = new String(volleyError.networkResponse.data, "UTF-8");
                            JSONObject jsonObject = new JSONObject(responseBody);

                            if (jsonObject.has("errors")) {
                                String validationErrors = jsonObject.get("errors").toString();
                                Log.e("YII_VALIDATION", validationErrors);
                                Toast.makeText(context, "Validation Fail: " + validationErrors, Toast.LENGTH_LONG).show();
                            } else {
                                String message = jsonObject.optString("message", "Unknown Server Error");
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("DEBUG", "Error parsing 500 response", e);
                        }
                    }

                }
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();

                    params.put("ponto_encontro", reserva.getPonto_encontro());
                    params.put("contacto", reserva.getContacto()+"");
                    params.put("perfil_id", reserva.getPerfil_id()+"");
                    params.put("boleia_id", reserva.getBoleia_id()+"");

                    return params;
                }
            };
            volleyQueue.add(request);
        }
    }



    public void removerReservaAPI(String tokenAPI, final Reserva reserva, final Context context){
        if(!JSONParser.isConnectionInternet(context)) {
            Toast.makeText(context, R.string.erro_ligacao_internet, Toast.LENGTH_SHORT).show();
        } else {

            String token = "?access-token=" + tokenAPI;
            StringRequest request = new StringRequest(Request.Method.DELETE, mUrlApiReserva + "/" + reserva.getId() + token, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    removerReservaBD(reserva.getId());
                    if (reservaListener != null){
                        reservaListener.onRefreshDetalhes(MenuMainActivity.DEL);
                    }
                    Toast.makeText(context, "Reserva removida com sucesso", Toast.LENGTH_SHORT).show();

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




    public void adicionarReservaBD(Reserva reserva){

        if(reserva == null) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Reserva auxReserva = boleiasBD.adicionarReservaBD(reserva);
                if (auxReserva != null){
                    reservas.add(auxReserva);
                }
            }
        }).start();

    }

    public void adicionarReservasBD(ArrayList<Reserva> reservas) {
        boleiasBD.removerAllReservasBD();
        for (Reserva r : reservas) {
            boleiasBD.adicionarReservaBD(r);
        }
    }

    public void adicionarReservasCondutorBD(ArrayList<Reserva> reservas) {
        if (reservas == null || reservas.isEmpty()) return;

        int boleia_id = reservas.get(0).getBoleia_id();

        boleiasBD.removerReservasPerfilBD(boleia_id);

        for (Reserva r : reservas) {
            boleiasBD.adicionarReservaBD(r);
        }
    }



    public void removerReservaBD(int idReserva){
        Reserva r = getReserva(idReserva);
        if (r != null) {
            if (boleiasBD.removerReservaBD(r.getId())) {
                reservas.remove(r);
            }
        }
    }

    public Reserva getReserva(int idReserva) {

        for (Reserva reserva: reservas) {
            if (reserva.getId()==idReserva){
                return reserva;
            }
        }
        return null;
    }

    public ArrayList<Reserva> getReservasBD() {
        reservas = boleiasBD.getAllReservasBD();
        return new ArrayList<>(reservas);
    }

    public void getAllAvaliacoesAPI(final Context context, String tokenAPI, String perfil_id) {
        if (!JSONParser.isConnectionInternet(context)) {
            Toast.makeText(context, R.string.erro_ligacao_internet, Toast.LENGTH_SHORT).show();
        }
        else {
            String url = mUrlApiAvaliacao + "?access-token=" + tokenAPI + "&perfil_id=" + perfil_id;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                JSONArray jsonArray = response.getJSONArray("data");
                                avaliacoes = JSONParser.parserJsonAvaliacoes(jsonArray);
                                adicionarAvaliacoesBD(avaliacoes);

                                if (avaliacoesListener != null) {
                                    avaliacoesListener.onRefreshListaAvaliacoes(avaliacoes);
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
                        }

                    });

            volleyQueue.add(request);
        }
    }

    public void adicionarAvaliacaoAPI(String tokenAPI, final Avaliacao avaliacao, final Context context){
        if(!JSONParser.isConnectionInternet(context)) {
            Toast.makeText(context, R.string.erro_ligacao_internet, Toast.LENGTH_SHORT).show();
        } else{
            String token = "?access-token=" + tokenAPI;
            StringRequest request = new StringRequest(Request.Method.POST, mUrlApiAvaliacao + token, new Response.Listener<String>() {

                @Override
                public void onResponse(String s) {
                    Avaliacao avaliacao = JSONParser.parserJsonAvaliacao(s);
                    adicionarAvaliacaoBD(avaliacao);
                    if (avaliacaoListener != null){
                        avaliacaoListener.onRefreshDetalhes(MenuMainActivity.ADD);
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

                    params.put("descricao", avaliacao.getDescricao());
                    params.put("perfil_id", avaliacao.getPerfil_id() + "");

                    return params;
                }
            };
            volleyQueue.add(request);
        }
    }


    public void adicionarAvaliacaoBD(Avaliacao avaliacao){
        if(avaliacao == null)return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Avaliacao auxAvaliacao = boleiasBD.adicionarAvaliacaoBD(avaliacao);
                if (auxAvaliacao != null){
                    avaliacoes.add(auxAvaliacao);
                }
            }
        }).start();

    }


    public void adicionarAvaliacoesBD(ArrayList<Avaliacao> avaliacoes) {
        boleiasBD.removerAllAvaliacoesBD();
        for (Avaliacao a : avaliacoes) {
            boleiasBD.adicionarAvaliacaoBD(a);
        }
    }


    public ArrayList<Avaliacao> getAvaliacoesBD() {
        avaliacoes = boleiasBD.getAllAvaliacoesBD();
        return new ArrayList<>(avaliacoes);
    }




    public void getAllDestinosFavoritosAPI(final Context context, String tokenAPI, String perfil_id) {
        if (!JSONParser.isConnectionInternet(context)) {
            Toast.makeText(context, R.string.erro_ligacao_internet, Toast.LENGTH_SHORT).show();
        }
        else {
            String url = mUrlApiDestinoFavorito + "?access-token=" + tokenAPI + "&perfil_id=" + perfil_id;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                JSONArray jsonArray = response.getJSONArray("data");
                                destinosFavoritos = JSONParser.parserJsonDestinosFavoritos(jsonArray);
                                adicionarDestinosFavoritosBD(destinosFavoritos);

                                if (destinosFavoritosListener != null) {
                                    destinosFavoritosListener.onRefreshListaDestinosFavoritos(destinosFavoritos);
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
                            Toast.makeText(context, "API Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });

            volleyQueue.add(request);
        }
    }

    public void adicionarDestinoFavoritoAPI(String tokenAPI, final DestinoFavorito destinoFavorito, final Context context){
        if(!JSONParser.isConnectionInternet(context)) {
            Toast.makeText(context, R.string.erro_ligacao_internet, Toast.LENGTH_SHORT).show();
        } else {

            String token = "?access-token=" + tokenAPI;
            StringRequest request = new StringRequest(Request.Method.POST, mUrlApiDestinoFavorito + token, new Response.Listener<String>() {

                @Override
                public void onResponse(String s) {
                    DestinoFavorito destinoFavorito = JSONParser.parserJsonDestinoFavorito(s);
                    adicionarDestinoFavoritoBD(destinoFavorito);
                    if (destinoFavoritoListener != null){
                        destinoFavoritoListener.onRefreshDetalhes(MenuMainActivity.ADD);
                    }
                    Toast.makeText(context, "Boleia guardada na watchlist", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        try {
                            String responseBody = new String(volleyError.networkResponse.data, "UTF-8");
                            JSONObject jsonObject = new JSONObject(responseBody);

                            if (jsonObject.has("errors")) {
                                String validationErrors = jsonObject.get("errors").toString();
                                Log.e("YII_VALIDATION", validationErrors);
                                Toast.makeText(context, "Validation Fail: " + validationErrors, Toast.LENGTH_LONG).show();
                            } else {
                                String message = jsonObject.optString("message", "Unknown Server Error");
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("DEBUG", "Error parsing 500 response", e);
                        }
                    }

                }
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();

                    params.put("boleia_id", destinoFavorito.getBoleia_id()+"");
                    params.put("perfil_id", destinoFavorito.getPerfil_id()+"");


                    return params;
                }
            };
            volleyQueue.add(request);
        }
    }



    public void removerDestinoFavoritoAPI(String tokenAPI, final DestinoFavorito destinoFavorito, final Context context){
        if(!JSONParser.isConnectionInternet(context)) {
            Toast.makeText(context, R.string.erro_ligacao_internet, Toast.LENGTH_SHORT).show();
        } else {

            String token = "?access-token=" + tokenAPI;
            StringRequest request = new StringRequest(Request.Method.DELETE, mUrlApiDestinoFavorito + "/" + destinoFavorito.getId() + token, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    removerDestinoFavoritoBD(destinoFavorito.getId());
                    if (destinoFavoritoListener != null){
                        destinoFavoritoListener.onRefreshDetalhes(MenuMainActivity.DEL);
                    }
                    Toast.makeText(context, "Boleia removida da watchlist com sucesso", Toast.LENGTH_SHORT).show();

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

    public void removerDestinoFavoritoBD(int idReserva){
        Reserva r = getReserva(idReserva);
        if (r != null) {
            if (boleiasBD.removerReservaBD(r.getId())) {
                reservas.remove(r);
            }
        }
    }

    public DestinoFavorito getDestinoFavorito(int idDestinoFavorito) {

        for (DestinoFavorito destinoFavorito: destinosFavoritos) {
            if (destinoFavorito.getId()==idDestinoFavorito){
                return destinoFavorito;
            }
        }
        return null;
    }

    public void adicionarDestinoFavoritoBD(DestinoFavorito destinoFavorito){

        if(destinoFavorito == null) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                DestinoFavorito auxDestinoFavorito = boleiasBD.adicionarDestinoFavoritoBD(destinoFavorito);
                if (auxDestinoFavorito != null){
                    destinosFavoritos.add(auxDestinoFavorito);
                }
            }
        }).start();

    }


    public void adicionarDestinosFavoritosBD(ArrayList<DestinoFavorito> destinosFavoritos) {
        boleiasBD.removerAllDestinosFavoritosBD();
        for (DestinoFavorito d : destinosFavoritos) {
            boleiasBD.adicionarDestinoFavoritoBD(d);
        }
    }


}
