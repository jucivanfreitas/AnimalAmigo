package com.celvansystems.projetoamigoanimal.helper;

import android.content.Context;

import com.celvansystems.projetoamigoanimal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Util {

    /**
     * retorna os estados
     * @param ctx contexto
     * @return estados
     */
    public static String[] getEstadosJSON(Context ctx){
        JSONObject obj;
        JSONArray jaEstados;
        String[] estados = new String[0];
        try {
            obj = new JSONObject(loadJSONFromAsset(ctx));
            jaEstados = obj.getJSONArray("estados");

            if(jaEstados!= null) {
                estados = new String[jaEstados.length()];

                for (int i = 0; i < jaEstados.length(); i++) {
                    estados[i] = jaEstados.getJSONObject(i).getString("sigla");
                }
            }
        } catch (Exception e){e.printStackTrace(); }

        return estados;
    }

    public static ArrayList<String> getEstadosLista(Context ctx){

        String [] estados = getEstadosJSON(ctx);
        ArrayList<String> estadosLista = new ArrayList<>();
        estadosLista.add("Todos");
        for (String s: estados) {
            estadosLista.add(s);
        }
        return estadosLista;
    }

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

    /*
    /* usa o arquivo estados-cidades.json localizado na pasta assets
     */
    public static String[] getCidadesJSON(String uf, Context ctx){

        JSONObject obj;
        JSONArray jaEstados;
        JSONArray array = null;

        String[] cidades = new String[0];
        try {
            obj = new JSONObject(loadJSONFromAsset(ctx));
            jaEstados = obj.getJSONArray("estados");

            if(jaEstados!= null) {

                for(int i = 0; i < jaEstados.length(); i++) {
                    String sigla = jaEstados.getJSONObject(i).getString("sigla");

                    if (sigla.equalsIgnoreCase(uf)) {
                        array = jaEstados.getJSONObject(i).getJSONArray("cidades");
                        break;
                    }
                }
            }

        } catch (Exception e){e.printStackTrace(); }

        if(array != null) {

            int len = array.length();
            cidades = new String[len];
            for (int i=0;i<len;i++){
                try {
                    cidades[i] = array.getString(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return cidades;
    }

    public static ArrayList<String> getCidadesLista(String uf, Context ctx){

        String [] cidades = getCidadesJSON(uf, ctx);
        ArrayList<String> cidadesLista = new ArrayList<>();
        cidadesLista.add("Todas");
        for (String s: cidades) {
            cidadesLista.add(s);
        }
        return cidadesLista;
    }

    public static String[] getEspecies(Context ctx){
        return ctx.getResources().getStringArray(R.array.especies);
    }

    public static ArrayList<String> getEspeciesLista(Context ctx){

        String [] especies = ctx.getResources().getStringArray(R.array.especies);
        ArrayList<String> especiesLista = new ArrayList<>();
        especiesLista.add("Todas");
        for (String s: especies) {
            especiesLista.add(s);
        }
        return especiesLista;
    }

    public static String getDataAtualBrasil(){
        // configuraçao da data atual do Brasil
        Calendar cal = Calendar.getInstance();
        return( new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(cal.getTime()));
    }
}
