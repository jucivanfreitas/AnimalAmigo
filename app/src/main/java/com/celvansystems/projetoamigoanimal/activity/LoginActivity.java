package com.celvansystems.projetoamigoanimal.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.celvansystems.projetoamigoanimal.helper.Constantes;
import com.celvansystems.projetoamigoanimal.helper.Util;
import com.celvansystems.projetoamigoanimal.model.Usuario;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Permissao para ler contatos.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private FirebaseAuth authentication;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private AutoCompleteTextView txiNome;
    private TextInputLayout txiLayout;
    private View mProgressView;
    private View mLoginFormView;
    private ImageView mImageBg_color;
    private Switch swtLoginCadastrar;
    private CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private View layout;
    private boolean retorno;
    private Usuario usuarioRetorno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupActionBar();

        authentication = ConfiguracaoFirebase.getFirebaseAutenticacao();

        inicializarComponentes();

        configuraLoginFacebook();

        configuraLoginGoogle();

        //populateAutoComplete();
    }

    private void inicializarComponentes() {

        try {
            mEmailView = findViewById(R.id.txiEmail);
            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);
            mImageBg_color = findViewById(R.id.imageViewbg_color);
            mPasswordView = findViewById(R.id.txiPassword);
            txiNome = findViewById(R.id.txi_nome);
            txiLayout = findViewById(R.id.txt_imput_nome);
            txiLayout.setVisibility(View.INVISIBLE);

            Button btnLogin = findViewById(R.id.btnLogin);
            swtLoginCadastrar = findViewById(R.id.swtLoginCadastrar);
            TextView txvEsqueciSenha = findViewById(R.id.txtEsqueciSenha);

            layout = findViewById(R.id.login_layout);

            TextView txtAnuncios = findViewById(R.id.txtAnuncios);
            txtAnuncios.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            });

            mEmailView.setText("");
            mPasswordView.setText("");

            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                        tentarLogin();
                        return true;
                    }
                    return false;
                }
            });

            swtLoginCadastrar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        txiNome.setVisibility(View.VISIBLE);
                        txiLayout.setVisibility(View.VISIBLE);
                    } else {
                        txiNome.setVisibility(View.INVISIBLE);
                        txiLayout.setVisibility(View.INVISIBLE);
                    }
                }
            });
            txvEsqueciSenha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyboard(LoginActivity.this, mEmailView);
                    if (mEmailView.getText() != null &&
                            !mEmailView.getText().toString().equalsIgnoreCase("") &&
                            mEmailView.getText().toString().contains("@")) {
                        enviarEmailRecuperacao(mEmailView.getText().toString());
                    } else {
                        Util.setSnackBar(layout, "Preencha seu e-mail!");
                    }
                }
            });

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tentarLogin();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enviarEmailRecuperacao(String email) {
        authentication.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            Util.setSnackBar(layout, getString(R.string.email_enviado));

                        } else {
                            Util.setSnackBar(layout, getString(R.string.nao_foi_possivel_email));
                        }
                    }
                });
    }

    /**
     * ação do botao de login do google
     */
    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Constantes.GOOGLE_REQUEST_CODE);
    }

    /**
     * método que configura o login por meio do facebook
     */
    private void configuraLoginFacebook() {

        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = findViewById(R.id.login_button_facebook);
        loginButton.setReadPermissions(Arrays.asList(
                "email", "public_profile"));

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());

                getFbInfo();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
            }
        });
    }

    private void getFbInfo() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {

                            String id = object.getString("id");
                            String first_name = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            String gender = object.getString("gender");
                            String birthday = object.getString("birthday");
                            String image_url = "http://graph.facebook.com/" + id + "/picture?type=large";

                            String email;
                            if (object.has("email")) {
                                email = object.getString("email");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email,gender,birthday"); // id,first_name,last_name,email,gender,birthday,cover,picture.type(large)
        request.setParameters(parameters);
        request.executeAsync();
    }

    /**
     * método que configura o login por meio do google
     */
    private void configuraLoginGoogle() {
        //Google Login
        SignInButton signInButton = findViewById(R.id.login_button_google);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.login_button_google) {
                    signInGoogle();
                }
            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_web_client))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    /**
     * método auxiliar para login por facebook
     *
     * @param token token
     */
    private void handleFacebookAccessToken(final AccessToken token) {

        showProgress(true);

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        authentication.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try {
                            if (task.isSuccessful()) {

                                //direciona para a tela principal
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();

                                Util.setSnackBar(layout, getString(R.string.sucesso_login_facebook));
                            } else {
                                Util.setSnackBar(layout, getString(R.string.falha_login_facebook));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        showProgress(false);
                        mImageBg_color.setVisibility(View.INVISIBLE);
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();

        // Verifica se há conta do google logada.
        /*GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account == null) {
            Log.d("INFO40", "usuario nao logado com google");

        } else {
            Log.d("INFO40", "usuario logado com google");

        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == Constantes.GOOGLE_REQUEST_CODE) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                e.printStackTrace();
            }
            handleSignInResult(task);
        }
    }

    /**
     * método que pega o resultado da tentativa de login do google
     *
     * @param completedTask task
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null)
                Log.d("INFO40", String.format("handle - logado pelo google %s", account.getDisplayName()));
        } catch (ApiException e) {
            Log.d("INFO40", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    /**
     * método usado para fazer login no google com uma credential
     *
     * @param acct conta
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        showProgress(true);
        mImageBg_color.setVisibility(View.GONE);

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        authentication.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = authentication.getCurrentUser();
                            //direciona para a tela principal
                            if (user != null && user.getEmail() != null) {
                                Toast.makeText(LoginActivity.this, getString(R.string.sucesso_login_google) +
                                                user.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                                //Util.setSnackBar();
                                // TODO: 06/03/2019 setSnackBar passando mensagem
                            }
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                        showProgress(false);
                        mImageBg_color.setVisibility(View.INVISIBLE);
                    }
                });
    }


    @SuppressLint("MissingPermission")
    private void tentarLogin() {

        //esconde o teclado
        hideKeyboard(getApplicationContext(), mPasswordView);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        //cadastrando...
        if (swtLoginCadastrar.isChecked()) {

            if (txiNome.getText() != null && !txiNome.getText().toString().equalsIgnoreCase("")) {

                authentication.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    swtLoginCadastrar.setChecked(false);
                                    Util.setSnackBar(layout, getString(R.string.cadastro_realizado));
                                    sendVerificationEmail();

                                    FirebaseUser user = ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser();

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(txiNome.getText().toString()).build();

                                    if (user != null) {
                                        user.updateProfile(profileUpdates);
                                    }

                                    //criando usuario...
                                    Usuario usuario = new Usuario();
                                    usuario.setId(Objects.requireNonNull(task.getResult()).getUser().getUid());
                                    usuario.setNome(txiNome.getText().toString());
                                    usuario.setEmail(email);
                                    usuario.salvar();

                                } else {
                                    String erroExcecao;
                                    try {
                                        throw Objects.requireNonNull(task.getException());
                                    } catch (FirebaseAuthWeakPasswordException e) {
                                        erroExcecao = getString(R.string.digite_senha_forte);
                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        erroExcecao = getString(R.string.digite_email_valido);
                                    } catch (FirebaseAuthUserCollisionException e) {
                                        erroExcecao = getString(R.string.conta_cadastrada);
                                    } catch (FirebaseAuthInvalidUserException e) {
                                        erroExcecao = getString(R.string.usuario_inexistente);
                                    } catch (Exception e) {
                                        erroExcecao = getString(R.string.falha_cadastro_usuario) + e.getMessage();
                                        e.printStackTrace();
                                    }
                                    Util.setSnackBar(layout, getString(R.string.erro) + erroExcecao);
                                }
                            }
                        });
            } else {
                Util.setSnackBar(layout, "Preencha seu nome!");
            }
            //logando...
        } else {
            //direciona para a activity principal
            authentication.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                // TODO: 20/02/2019 habilitar verificacao de e-mail no final. Não deletar o código
                                //if (checkIfEmailVerified()) {

                                //redirecionamento de acordo com o cadastro do usuario (completo ou incompleto)
                                String usuarioId = Objects.requireNonNull(ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser()).getUid();
                                redireciona(usuarioId);

                                /*} else {
                                    Toast.makeText(LoginActivity.this, getString(R.string.email_nao_verificado),
                                            Toast.LENGTH_SHORT).show();
                                    //envia e-mail de verificacao
                                    sendVerificationEmail();
                                }*/
                                // TODO: 20/02/2019 a verificacao de e-mail termina aqui. Não deletar o código
                            } else {
                                Util.setSnackBar(layout, getString(R.string.email_senha_invalido));
                            }
                            showProgress(false);
                            mImageBg_color.setVisibility(View.INVISIBLE);
                        }
                    });
            showProgress(true);
            mImageBg_color.setVisibility(View.VISIBLE);
        }
    }

    private void redireciona(final String usuarioId) {

        DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebase()
                .child("usuarios");

        usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot usuarios : dataSnapshot.getChildren()) {

                    if (usuarios != null) {
                        if (Objects.requireNonNull(usuarios.child("id").getValue()).toString().equalsIgnoreCase(usuarioId)) {

                            if (usuarios.child("loginCompleto").getValue() == null) {

                                Intent intent = new Intent(getApplicationContext(), ComplementoLoginActivity.class);
                                startActivity(intent);

                            } else {

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);

                            }
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // TODO: 06/03/2019 setSnackBar passando mensagem pra Main
    }

    /*private boolean isCadastroCompleto(final String usuarioId) {

        retorno = true;

        DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebase()
                .child("usuarios");

        usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot usuarios : dataSnapshot.getChildren()) {

                    if (usuarios != null) {
                        if (Objects.requireNonNull(usuarios.child("id").getValue()).toString().equalsIgnoreCase(usuarioId)) {

                            if ((usuarios.child("nome").getValue()) == null ||
                                    (usuarios.child("telefone").getValue()) == null ||
                                    (usuarios.child("foto").getValue()) == null ||
                                    (usuarios.child("pais").getValue()) == null ||
                                    (usuarios.child("uf").getValue()) == null ||
                                    (usuarios.child("cidade").getValue()) == null) {

                                retorno = false;

                            }
                            // TODO: 05/03/2019 concluir atributos de usuario apos activity para cadastro de usuario
                            //usuario.setFoto(ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser().getPhotoUrl().toString());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return retorno;
    }*/

    private Usuario getUsuarioDoCadastro(final String usuarioId) {

        DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebase()
                .child("usuarios");


        usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //int size = (int) dataSnapshot.getChildrenCount();
                //atualizaComentarios(size, anuncio, myViewHolder);

                //List<Usuario> usuariosList = new ArrayList<>();
                for (DataSnapshot usuarios : dataSnapshot.getChildren()) {

                    if (usuarios != null) {
                        if (Objects.requireNonNull(usuarios.child("id").getValue()).toString().equalsIgnoreCase(usuarioId)) {

                            usuarioRetorno = new Usuario();

                            usuarioRetorno.setId(Objects.requireNonNull(ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser()).getUid());

                            usuarioRetorno.setNome(Objects.requireNonNull(usuarios.child("nome").getValue()).toString());

                            usuarioRetorno.setNome(Objects.requireNonNull(usuarios.child("telefone").getValue()).toString());

                            usuarioRetorno.setNome(Objects.requireNonNull(usuarios.child("foto").getValue()).toString());

                            usuarioRetorno.setNome(Objects.requireNonNull(usuarios.child("pais").getValue()).toString());

                            usuarioRetorno.setNome(Objects.requireNonNull(usuarios.child("uf").getValue()).toString());

                            usuarioRetorno.setNome(Objects.requireNonNull(usuarios.child("cidade").getValue()).toString());

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return usuarioRetorno;
    }

    private void criarUsuarioFirebase(Usuario usuario) {

    }

    /**
     * firebase envia e-mail de verificação
     */
    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email enviado
                            FirebaseAuth.getInstance().signOut();
                        } else {
                            // email nao enviado
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                        }
                    }
                });
    }

    /**
     * verifica se o e-mail do usuário foi validado pelo firebase
     *
     * @return boolean e-mail verified
     */
    private boolean checkIfEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            return user.isEmailVerified();
        }
        return false;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @SuppressLint("ObsoleteSdkInt")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        try {
            List<String> emails = new ArrayList<>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                emails.add(cursor.getString(ProfileQuery.ADDRESS));
                cursor.moveToNext();
            }
            addEmailsToAutoComplete(emails);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        try {
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(LoginActivity.this,
                            android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

            mEmailView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private interface ProfileQuery {

        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
    }

    private void populateAutoComplete() {
        try {
            if (!mayRequestContacts()) {
                return;
            }
            getLoaderManager().initLoader(0, null, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        try {
            if (requestCode == REQUEST_READ_CONTACTS) {
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    populateAutoComplete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @SuppressLint("ObsoleteSdkInt")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        // Show the Up button in the action bar.
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * esconde teclado
     * */
    public static void hideKeyboard(Context context, View editText) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
/*
            Usuario usuarioTemp = new Usuario();
            usuarioTemp.setEmail(email);
            // TODO: 16/02/2019 pegar dados gps

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            long tempo = 1000; //5 minutos
            float distancia = 30; // 30 metros

            String[] perm = new String[2];
            perm[0] = permission.ACCESS_FINE_LOCATION;
            perm[1] = permission.ACCESS_COARSE_LOCATION;

            if (Permissoes.validarPermissoes(perm, this, 1)) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, tempo, distancia, new LocationListener() {

                    @Override
                    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
                        Toast.makeText(getApplicationContext(), "Status alterado", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onProviderEnabled(String arg0) {
                        Toast.makeText(getApplicationContext(), "Provider Habilitado", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onProviderDisabled(String arg0) {
                        Toast.makeText(getApplicationContext(), "Provider Desabilitado", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onLocationChanged(Location location) {
                        /*TextView numero = (TextView) findViewById(R.id.numero);
                        TextView latitude = (TextView) findViewById(R.id.latitude);
                        TextView longitude = (TextView) findViewById(R.id.longitude);
                        TextView time = (TextView) findViewById(R.id.time);
                        TextView acuracy = (TextView) findViewById(R.id.Acuracy);*/
/*
                        if (location != null) {
                            locs.add(location);
                            numero.setText("Número de posições travadas: " + locs.size());
                            latitude.setText("Latitude: " + location.getLatitude());
                            longitude.setText("Longitude: " + location.getLongitude());
                            acuracy.setText("Precisão: " + location.getAccuracy() + "");

                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            time.setText("Data:" + sdf.format(location.getTime()));

                        }*/
     /*               }
                }, null);
            }


            GPSTracker gps = new GPSTracker(this);

            // GPS: localização do usuário
            if (gps.canGetLocation()) {
                // passa sua latitude e longitude para duas variaveis
                Location local = gps.getLocation();

                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                usuarioTemp.setLatitude(String.valueOf(latitude));
                usuarioTemp.setLongitude(String.valueOf(longitude));

                /*pode-se utilizar o metodo gps.getLocation.distanceTo para calcular distancia entre dois locais*/

// e mostra no Toast
                /*Toast.makeText(getApplicationContext(), "Sua localização - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            }*/
