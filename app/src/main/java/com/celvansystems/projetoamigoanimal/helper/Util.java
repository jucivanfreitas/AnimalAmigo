package com.celvansystems.projetoamigoanimal.helper;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.activity.ComentariosActivity;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.celvansystems.projetoamigoanimal.model.Comentario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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

import static android.content.Context.NOTIFICATION_SERVICE;

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

    public static void configuraNotificacoes(final Context ctx, final Animal anuncio){

        final DatabaseReference comentRef = ConfiguracaoFirebase.getFirebase()
                .child("meus_animais")
                .child(anuncio.getIdAnimal())
                .child("comentarios");

        comentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (anuncio.getDonoAnuncio().equalsIgnoreCase(ConfiguracaoFirebase.getIdUsuario())) {

                    Comentario coment = new Comentario();
                    int size = anuncio.getListaComentarios().size() - 1;
                    String texto = anuncio.getListaComentarios().get(size).getTexto();
                    coment.setTexto(texto);

                    int sizeComentsNotificacoes = Util.comentariosNotificacoes.size();
                    if ((sizeComentsNotificacoes == 0 || !Util.comentariosNotificacoes.get(sizeComentsNotificacoes-1).equalsIgnoreCase(texto))
                            && !anuncio.getDonoAnuncio().equalsIgnoreCase(ConfiguracaoFirebase.getIdUsuario())) {
                        createNotificationMessage(ctx, ctx.getString(R.string.novo_comentario), coment.getTexto(), anuncio);

                        //ultimoComentario = texto;
                        Util.comentariosNotificacoes.add(texto);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private static void createNotificationMessage(Context ctx, String Title, String Msg, Animal anuncio) {

        int id = 15;
        Intent intent = new Intent(ctx, ComentariosActivity.class);
        intent.putExtra("anuncioSelecionado", anuncio);
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, intent, 0);

        Notification.Builder b = new Notification.Builder(ctx);

        NotificationChannel mChannel = null;

        b.setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(Title)
                .setTicker(Title)
                .setContentText(Msg)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(contentIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel("cid", "name", NotificationManager.IMPORTANCE_HIGH);
            b.setChannelId("cid");
            mChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build());
        }

        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(id, b.build());
    }
}
