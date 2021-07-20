package com.example.organizzeazy.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.organizzeazy.adapter.AdapterMovimentacao;
import com.example.organizzeazy.config.ConfigFirebase;
import com.example.organizzeazy.helper.Base64Custom;
import com.example.organizzeazy.model.Movimentation;
import com.example.organizzeazy.model.Usuario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.organizzeazy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private TextView textoName, textSaldo;
    private DatabaseReference firebaseRef = ConfigFirebase.getFirebaseDatabase();
    private FirebaseAuth mAuth = ConfigFirebase.getFirebaseAuth();
    private DatabaseReference userRef;
    private ValueEventListener valueEventListenerUsuario;
    private RecyclerView recyclerView;
    private AdapterMovimentacao adapterMovimentacao;
    private List<Movimentation> movimentations = new ArrayList<>();

    private Double despesaTotal = 0.0;
    private Double receitaTotal = 0.0;
    private Double resumoSaldo = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.reclyclerSaldo);

        textoName = findViewById(R.id.textName);
        textSaldo = findViewById(R.id.textSaldo);
        calendarView = findViewById(R.id.calendarView);
        configuraCalendarView();

        //adapter
        adapterMovimentacao = new AdapterMovimentacao(movimentations, this);


        //recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager( layoutManager );
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter( adapterMovimentacao );

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarResumo();
    }

    public void recuperarResumo(){
        String emailUser = mAuth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codificarBase64(emailUser);
        userRef = firebaseRef.child("usuarios").child( idUser );

        valueEventListenerUsuario = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Usuario usuario = snapshot.getValue(Usuario.class);
                despesaTotal = usuario.getDespesaTotal();
                receitaTotal = usuario.getReceitaTotal();
                resumoSaldo = receitaTotal - despesaTotal;

                DecimalFormat decimalFormat = new DecimalFormat("0.##");
                String resulFormat = decimalFormat.format(resumoSaldo);

                textoName.setText("Olá, " + usuario.getNome());
                textSaldo.setText("R$: " + resulFormat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //logout in menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair:
                mAuth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addReceita(View view){
        startActivity(new Intent(this, ReceitasActivity.class));
    }

    public void addDespesa(View view){
        startActivity(new Intent(this, DespesasActivity.class));
    }

    public void configuraCalendarView(){
        CharSequence meses[] = {"Jan","Fev","Mar","Abr", "Mai"
            ,"Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
        calendarView.setTitleMonths(meses);

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        userRef.removeEventListener(valueEventListenerUsuario);
    }


}
