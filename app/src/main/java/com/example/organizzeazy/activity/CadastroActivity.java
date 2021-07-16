package com.example.organizzeazy.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizzeazy.R;
import com.example.organizzeazy.config.ConfigFirebase;
import com.example.organizzeazy.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoEmail, campoNome, campoSenha;
    private Button buttonCadastrar;
    private FirebaseAuth mAuth;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

    campoNome = findViewById(R.id.editNomeCad);
    campoEmail = findViewById(R.id.editEmail);
    campoSenha = findViewById(R.id.editPassw);
    buttonCadastrar  = findViewById(R.id.btnCadastrar);

    buttonCadastrar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //pega informação do editText
            String textNome = campoNome.getText().toString();
            String textEmail = campoEmail.getText().toString();
            String textSenha = campoSenha.getText().toString();

            //validar se os campos foram preenchidos
            if (!textNome.isEmpty() ){
                if (!textEmail.isEmpty() ) {
                    if (!textSenha.isEmpty() ){
                            usuario = new Usuario();
                            usuario.setNome(textNome);
                            usuario.setEmail(textEmail);
                            usuario.setSenha(textSenha);
                            cadastraUsuario();


                    }else{

                        Toast.makeText(CadastroActivity.this, "Preencha o senha", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(CadastroActivity.this, "Preencha o email", Toast.LENGTH_SHORT).show();

                }
            }else{

                Toast.makeText(CadastroActivity.this, "Preencha o nome", Toast.LENGTH_SHORT).show();
            }
        }
    });
    } //onCreate


    public void cadastraUsuario(){

        mAuth = ConfigFirebase.getFirebaseAuth();
        mAuth.createUserWithEmailAndPassword(
            usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CadastroActivity.this, "Sucesso ao cadastrar usuario", Toast.LENGTH_SHORT).show();
                }else{
                    String excecao = "";

                    //tratar exceção no cadastro
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e){
                        excecao = "Digite uma senha mais forte!";
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "Digite uma email valido";
                    } catch (FirebaseAuthUserCollisionException e) {
                        excecao = "Esta conta já foi cadastrada";
                    } catch (Exception e){
                        excecao = "Erro ao cadastrar usuario" + e.getMessage();

                    }
                Toast.makeText(CadastroActivity.this, excecao, Toast.LENGTH_SHORT).show();

                }
            }
        });


    }

}
