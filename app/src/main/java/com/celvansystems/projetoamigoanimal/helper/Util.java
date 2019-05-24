package com.celvansystems.projetoamigoanimal.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.celvansystems.projetoamigoanimal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Util {

    public static List<String> comentariosNotificacoes = new ArrayList<>();

    /**
     * retorna os estados
     *
     * @param ctx contexto
     * @return estados
     */
    public static String[] getEstadosJSON(Context ctx) {
        JSONObject obj;
        JSONArray jaEstados;
        String[] estados = new String[0];
        try {
            obj = new JSONObject(loadJSONFromAsset(ctx));
            jaEstados = obj.getJSONArray("estados");

            if (jaEstados != null) {
                estados = new String[jaEstados.length()];

                for (int i = 0; i < jaEstados.length(); i++) {
                    estados[i] = jaEstados.getJSONObject(i).getString("sigla");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return estados;
    }

    /**
     * pega a lista dos estados
     *
     * @param ctx contexto
     * @return lista de estados
     */
    public static ArrayList<String> getEstadosLista(Context ctx) {

        String[] estados = getEstadosJSON(ctx);
        ArrayList<String> estadosLista = new ArrayList<>();
        estadosLista.add("Todos");
        estadosLista.addAll(Arrays.asList(estados));
        return estadosLista;
    }

    /**
     * carrega o jason dos estados e cidades
     *
     * @param context contexto
     * @return string
     */
    private static String loadJSONFromAsset(Context context) {
        String json;
        try {
            InputStream is = context.getAssets().open("estados-cidades.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "Windows-1252");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    /**
     * usa o arquivo estados-cidades.json localizado na pasta assets
     *
     * @param uf  estado
     * @param ctx contexto
     * @return array de string
     */
    public static String[] getCidadesJSON(String uf, Context ctx) {

        JSONObject obj;
        JSONArray jaEstados;
        JSONArray array = null;

        String[] cidades = new String[0];
        try {
            obj = new JSONObject(loadJSONFromAsset(ctx));
            jaEstados = obj.getJSONArray("estados");

            if (jaEstados != null) {

                for (int i = 0; i < jaEstados.length(); i++) {
                    String sigla = jaEstados.getJSONObject(i).getString("sigla");

                    if (sigla.equalsIgnoreCase(uf)) {
                        array = jaEstados.getJSONObject(i).getJSONArray("cidades");
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (array != null) {

            int len = array.length();
            cidades = new String[len];
            for (int i = 0; i < len; i++) {
                try {
                    cidades[i] = array.getString(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return cidades;
    }

    /**
     * lista de cidades de acordo com o estado
     *
     * @param uf  estado
     * @param ctx contexto
     * @return lista de string
     */
    public static ArrayList<String> getCidadesLista(String uf, Context ctx) {

        String[] cidades = getCidadesJSON(uf, ctx);
        ArrayList<String> cidadesLista = new ArrayList<>();
        cidadesLista.add("Todas");
        cidadesLista.addAll(Arrays.asList(cidades));
        return cidadesLista;
    }

    public static String[] getEspecies(Context ctx) {
        return ctx.getResources().getStringArray(R.array.especies);
    }

    public static ArrayList<String> getEspeciesLista(Context ctx) {

        String[] especies = ctx.getResources().getStringArray(R.array.especies);
        ArrayList<String> especiesLista = new ArrayList<>();
        especiesLista.add("Todas");
        especiesLista.addAll(Arrays.asList(especies));
        return especiesLista;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDataAtualBrasil() {
        // configuraçao da data atual do Brasil
        Calendar cal = Calendar.getInstance();
        return (new SimpleDateFormat("dd/MM/yyyy HH:mm").format(cal.getTime()));
    }

    public static void setSnackBar(View root, String snackTitle) {
        Snackbar snackbar = Snackbar.make(root, snackTitle, Snackbar.LENGTH_LONG);
        snackbar.show();
        View view = snackbar.getView();
        TextView txtv = view.findViewById(android.support.design.R.id.snackbar_text);
        txtv.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    public static boolean validaTexto(String texto) {

        boolean retorno = false;
        if (texto != null) {
            retorno = (!texto.equalsIgnoreCase("")
                    && !texto.trim().equals("null") && texto.trim()
                    .length() > 0 && !texto.isEmpty());
        }
        return retorno;
    }
}
