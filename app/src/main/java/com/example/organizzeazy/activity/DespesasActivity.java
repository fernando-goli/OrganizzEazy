package com.example.organizzeazy.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizzeazy.R;
import com.example.organizzeazy.config.ConfigFirebase;
import com.example.organizzeazy.helper.Base64Custom;
import com.example.organizzeazy.helper.DateCustom;
import com.example.organizzeazy.model.Movimentation;
import com.example.organizzeazy.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private Movimentation movimentation;
    private DatabaseReference firebaseRef = ConfigFirebase.getFirebaseDatabase();
    private FirebaseAuth mAuth = ConfigFirebase.getFirebaseAuth();
    private Double despesaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);


        campoValor = findViewById(R.id.editValor);
        campoCategoria = findViewById(R.id.editCategoria);
        campoData = findViewById(R.id.editData);
        campoDescricao = findViewById(R.id.editDescricao);

        campoData.setText(DateCustom.dataAtual() );
        recuperarDespesas();

    }

    public void salvarDespesa(View view){

        if (validateValues() ){

            movimentation = new Movimentation();
            String data = campoData.getText().toString();
            Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());

            movimentation.setValor( valorRecuperado );
            movimentation.setCategoria( campoCategoria.getText().toString());
            movimentation.setDescricao( campoDescricao.getText().toString());
            movimentation.setData( data );
            movimentation.setTipo( "d" );

            Double despesaAtualizada = despesaTotal + valorRecuperado;
            atualizarDespesa( despesaAtualizada );

            movimentation.salvar( data );

            finish();
        }
    }

    public Boolean validateValues(){

        String textoValor = campoValor.getText().toString();
        String textoData = campoData.getText().toString();
        String textoCategoria = campoCategoria.getText().toString();
        String textoDescricao = campoDescricao.getText().toString();

        if( !textoValor.isEmpty()){
            if ( !textoData.isEmpty() ){
                if ( !textoCategoria.isEmpty()){
                    if ( !textoDescricao.isEmpty()){
                        return true;
                    }else {
                        Toast.makeText(DespesasActivity.this, "Preencha a descric??o", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else {
                    Toast.makeText(DespesasActivity.this, "Preencha a categoria", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else {
                Toast.makeText(DespesasActivity.this, "Preencha a data", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(DespesasActivity.this, "Preencha o valor", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void recuperarDespesas(){
        String emailUser = mAuth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codificarBase64(emailUser);
        DatabaseReference userRef = firebaseRef.child("usuarios").child( idUser );

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue( Usuario.class );
                despesaTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void atualizarDespesa(Double despesa){
        String emailUser = mAuth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codificarBase64(emailUser);
        DatabaseReference userRef = firebaseRef.child("usuarios").child( idUser );

        userRef.child("despesaTotal").setValue(despesa);
    }
}
