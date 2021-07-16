package com.example.organizzeazy.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail, editSenha;
    private Button buttonEntrar;
    private Usuario usuario;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editPassw);
        buttonEntrar = findViewById(R.id.btnEntrar);

        buttonEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String textEmail = editEmail.getText().toString();
                String textSenha = editSenha.getText().toString();
            if (!textEmail.isEmpty() ) {
                if (!textSenha.isEmpty() ){
                    usuario = new Usuario();
                    usuario.setEmail(textEmail);
                    usuario.setSenha(textSenha);
                    validarlogin();

                }else{

                    Toast.makeText(LoginActivity.this, "Preencha o senha", Toast.LENGTH_SHORT).show();
                }
            }else {

                Toast.makeText(LoginActivity.this, "Preencha o email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    } //onCreate

    public void validarlogin(){

        mAuth = ConfigFirebase.getFirebaseAuth();
        mAuth.signInWithEmailAndPassword(
            usuario.getEmail(),
            usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    //Toast.makeText(LoginActivity.this, "Sucesso ao fazer login", Toast.LENGTH_SHORT).show();
                    openHomeActy();

                }else{
                    String excecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e){
                        excecao = "Digite um email valido!";
                    } catch ( FirebaseAuthInvalidCredentialsException e){
                        excecao = "Senha invalida!";
                    }catch (Exception e){
                        excecao = "Erro ao tentar logar!" + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void openHomeActy(){
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }


}
