package com.celvansystems.projetoamigoanimal.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.activity.MainActivity;
import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.celvansystems.projetoamigoanimal.model.Usuario;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class PerfilUsuarioFragment extends Fragment {

    private View viewFragment;
    private ImageView imvPerfil;
    private TextView txvNomeHumano;
    private TextView txvEmail;
    private TextView txvIdade;
    private TextView txvCidade;
    private TextView txvEstado;
    //private TextView txvPais;
    private TextView txvTelefone;
    private TextView txvSexo;
    private TextView txvResumo;
    private TextView txvSobreMim;
    private TextView txvPerfilHumano;

    public PerfilUsuarioFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        viewFragment = inflater.inflate(R.layout.fragment_perfil_usuario, container, false);

        inicializarComponentes();

        return viewFragment;
    }

    /**
     * configuracoes iniciais
     */
    private void inicializarComponentes() {

        imvPerfil = viewFragment.findViewById(R.id.imageView_perfil);
        txvNomeHumano = viewFragment.findViewById(R.id.textview_nome_humano);
        txvEmail = viewFragment.findViewById(R.id.textView_email_cadastrado);
        txvIdade = viewFragment.findViewById(R.id.textView_idade);
        txvCidade = viewFragment.findViewById(R.id.textView_cidade);
        txvEstado = viewFragment.findViewById(R.id.textView_estado);
        //txvPais = viewFragment.findViewById(R.id.textView_pais);
        txvTelefone = viewFragment.findViewById(R.id.textView_telefone);
        txvSexo = viewFragment.findViewById(R.id.textView_sexo);
        txvResumo = viewFragment.findViewById(R.id.textView_resumo);
        txvResumo.setVisibility(View.INVISIBLE);
        txvSobreMim = viewFragment.findViewById(R.id.txvSobreMim);
        txvSobreMim.setVisibility(View.INVISIBLE);
        txvPerfilHumano = viewFragment.findViewById(R.id.textView_perfil_humano);

        //Preenchendo os campos do Fragment com dados do usuario
        txvEmail.setText(getEmailUsuario());

        Button btnDesativarConta = viewFragment.findViewById(R.id.btnEncerrarConta);

        btnDesativarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDesativarConta = new AlertDialog.Builder(v.getContext());
                alertDesativarConta.setTitle("Tem certeza que deseja desativar sua conta? ");
                alertDesativarConta.setMessage("Esta opção não poderá ser revertida.");

                alertDesativarConta.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        removerContaAtual();
                        dialog.dismiss();
                    }
                });

                alertDesativarConta.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = alertDesativarConta.create();
                alert.show();
            }
        });

        carregarDados();
    }

    private void carregarDados() {

        if(ConfiguracaoFirebase.isUsuarioLogado()){

            DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebase()
                    .child("usuarios");

            usuariosRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot usuarios : dataSnapshot.getChildren()) {

                        if (usuarios != null) {

                            UserInfo user = ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser();
                            //txvEmail.setText(user.getEmail());
                            //txvNomeHumano.setText(user.getDisplayName());
                            //txvTelefone.setText(user.getPhoneNumber());

                            if (Objects.requireNonNull(usuarios.child("id").getValue()).toString().equalsIgnoreCase(Objects.requireNonNull(user).getUid())) {

                                Usuario usuario = new Usuario();

                                usuario.setId(Objects.requireNonNull(ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser()).getUid());

                                if(usuarios.child("nome").getValue() != null) {
                                    usuario.setNome(usuarios.child("nome").getValue().toString());
                                } else {
                                    usuario.setNome("");
                                }

                                if(usuarios.child("telefone").getValue() != null) {
                                    usuario.setTelefone(usuarios.child("telefone").getValue().toString());
                                } else {
                                    usuario.setTelefone("");
                                }

                                if(usuarios.child("foto").getValue() != null) {
                                    usuario.setFoto(usuarios.child("foto").getValue().toString());
                                }

                                if(usuarios.child("pais").getValue() != null) {
                                    usuario.setPais(usuarios.child("pais").getValue().toString());
                                } else {
                                    usuario.setPais("");
                                }

                                if(usuarios.child("uf").getValue() != null) {
                                    usuario.setUf(usuarios.child("uf").getValue().toString());
                                } else {
                                    usuario.setUf("");
                                }

                                if(usuarios.child("cidade").getValue() != null) {
                                    usuario.setCidade(usuarios.child("cidade").getValue().toString());
                                } else {
                                    usuario.setCidade("");
                                }

                                if(usuarios.child("email").getValue() != null) {
                                    usuario.setEmail(usuarios.child("email").getValue().toString());
                                } else {
                                    usuario.setEmail("");
                                }

                                //Preenchimento da tela
                                if(usuario.getNome() != null && !usuario.getNome().equalsIgnoreCase("")) {
                                    txvNomeHumano.setText(usuario.getNome());
                                } else {
                                    txvNomeHumano.setText("[Edite seu perfil]");
                                }
                                if(usuario.getTelefone() != null && !usuario.getTelefone().equalsIgnoreCase("")) {
                                    txvTelefone.setText(usuario.getTelefone());
                                } else {
                                    txvTelefone.setText("[Edite seu perfil]");
                                }
                                /*if(usuario.getPais() != null  && !usuario.getPais().equalsIgnoreCase("")) {
                                    txvPais.setText(usuario.getPais());
                                }*/
                                if(usuario.getUf() != null && !usuario.getUf().equalsIgnoreCase("")) {
                                    txvEstado.setText(usuario.getUf());
                                } else {
                                    txvEstado.setText("[Edite seu perfil]");
                                }
                                if(usuario.getCidade() != null && !usuario.getCidade().equalsIgnoreCase("")) {
                                    txvCidade.setText(usuario.getCidade());
                                } else {
                                    txvCidade.setText("[Edite seu perfil]");
                                }
                                if(usuario.getEmail() != null && !usuario.getEmail().equalsIgnoreCase("")) {
                                    txvEmail.setText(usuario.getEmail());
                                } else {
                                    txvEmail.setText("[Edite seu perfil]");
                                }

                                if(usuario.getFoto() != null) {

                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    /**
     * metodo que exclui o usuario atual e todos os seus anuncios
     */
    private void removerContaAtual() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String usuarioId = ConfiguracaoFirebase.getIdUsuario();

        LoginManager.getInstance().logOut();

        //Deleta usuário
        Objects.requireNonNull(user).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Usuario usuario = new Usuario();
                            usuario.setId(usuarioId);
                            usuario.remover();

                            //deleta todos os anuncios do usuario
                            deletarAnunciosUsuario(usuarioId);

                            Toast.makeText(viewFragment.getContext(), "Usuário excluído com sucesso!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(viewFragment.getContext(), MainActivity.class));
                            Toast.makeText(viewFragment.getContext(), "usuario excluido!", Toast.LENGTH_SHORT).show();
                            Objects.requireNonNull(getActivity()).finish();

                        } else {
                            Log.d("INFO3", "User account not deleted.");
                            Toast.makeText(viewFragment.getContext(), "Falha ao excluir usuário!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * apaga todos os anuncios de um usuario
     *
     * @param usuarioId id
     */
    private void deletarAnunciosUsuario(final String usuarioId) {

        try {
            //excluindo todos os anuncios do usuario removido
            DatabaseReference anunciosPublicosRef = ConfiguracaoFirebase.getFirebase()
                    .child("meus_animais");
            Log.d("INFO29", "passou1");
            anunciosPublicosRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot animal : snapshot.getChildren()) {
                        Log.d("INFO29", "passou2");
                        Animal anuncio = animal.getValue(Animal.class);
                        if (anuncio != null && anuncio.getDonoAnuncio() != null) {
                            Log.d("INFO29", "passou3: " + anuncio.getDonoAnuncio());
                            if (anuncio.getDonoAnuncio().equalsIgnoreCase(usuarioId)) {
                                Log.d("INFO29", "passou4: animal removido: " + anuncio.getIdAnimal());
                                animal.getRef().removeValue();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("INFO29", "passou5: nancelled: " + databaseError.getMessage());
                    throw databaseError.toException();
                }
            });
        } catch (Exception e) {
            Log.d("INFO29", "passou6: excecao!!!: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getEmailUsuario() {
        if (ConfiguracaoFirebase.isUsuarioLogado()) {
            FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
            return Objects.requireNonNull(autenticacao.getCurrentUser()).getEmail();
        }
        return "";
    }
}
