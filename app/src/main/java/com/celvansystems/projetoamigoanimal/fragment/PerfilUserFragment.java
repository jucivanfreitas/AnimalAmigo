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
import android.widget.Toast;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.activity.AnunciosActivity;
import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PerfilUserFragment extends Fragment {

    private FirebaseAuth autenticacao;

    public PerfilUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        inicializarComponentes();

        return inflater.inflate(R.layout.fragment_perfil_user, container, false);

    }

    private void inicializarComponentes() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        Button btnDesativarConta = getActivity().findViewById(R.id.btnEncerrarConta);

        btnDesativarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDesativarConta = new AlertDialog.Builder(v.getContext());

                alertDesativarConta.setTitle("Tem certeza que deseja desativar sua conta? ");
                alertDesativarConta.setMessage("Esta opção não poderá ser revertida.");

                //final EditText input = new EditText(v.getContext());
                //input.setHint("Digite sua senha");
                //alertDesativarConta.setView(input);

                alertDesativarConta.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: 18/02/2019 codigo para excluir conta do usuario e todos os seus anuncios

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
    }

    /**
     * metodo que exclui o usuario atual
     */
    private boolean removerContaAtual(){
        boolean retorno = false;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String usuarioId = ConfiguracaoFirebase.getIdUsuario();
        Log.d("INFO29", "usuario id: " + usuarioId);
        Task<Void> task = user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            deletarAnunciosUsuario(usuarioId );

                            // TODO: 18/02/2019 inserir lógica para excluir todos os anuncios do usuario excluido

                            //google sign in
                            // TODO: 21/02/2019 verificar se eh conta google ou facebook
                            //revokeAccess();

                            //
                            Log.d("INFO3", "User account deleted.");
                            Toast.makeText(getApplicationContext(), "Usuário excluído com sucesso!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), AnunciosActivity.class));
                            //finish();
                            getActivity().getFragmentManager().popBackStack();


                        } else {
                            Log.d("INFO3", "User account not deleted.");
                            Toast.makeText(getApplicationContext(), "Falha ao excluir usuário!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        if(task.isSuccessful()) {
            retorno = true;
        }
        return retorno;
    }

    private void deletarAnunciosUsuario(final String usuarioId) {

        try {
            //excluindo todos os anuncios do usuario removido
            DatabaseReference anunciosPublicosRef = ConfiguracaoFirebase.getFirebase()
                    .child("meus_animais");
            Log.d("INFO29", "passou1");
            anunciosPublicosRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for(DataSnapshot animal: snapshot.getChildren()){
                        Log.d("INFO29", "passou2");
                        Animal anuncio = animal.getValue(Animal.class);
                        if(anuncio != null && anuncio.getDonoAnuncio()!= null) {
                            Log.d("INFO29", "passou3: "+ anuncio.getDonoAnuncio());
                            if (anuncio.getDonoAnuncio().equalsIgnoreCase(usuarioId)) {
                                Log.d("INFO29", "passou4: animal removido: "+ anuncio.getIdAnimal());
                                animal.getRef().removeValue();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("INFO29", "passou5: nancelled: "+ databaseError.getMessage());
                    throw databaseError.toException();
                }
            });
        } catch (Exception e) {
            Log.d("INFO29", "passou6: excecao!!!: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // TODO: 21/02/2019 corrigir este metodo que tá dando erro no fragment
    //google
    /*private void revokeAccess() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });

    }*/


}
