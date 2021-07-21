package com.example.organizzeazy.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.organizzeazy.adapter.AdapterMovimentacao;
import com.example.organizzeazy.config.ConfigFirebase;
import com.example.organizzeazy.helper.Base64Custom;
import com.example.organizzeazy.model.Movimentation;
import com.example.organizzeazy.model.Usuario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

    private Double despesaTotal = 0.0;
    private Double receitaTotal = 0.0;
    private Double resumoSaldo = 0.0;
    private TextView textoName, textSaldo;

    private MaterialCalendarView calendarView;
    private ValueEventListener valueEventListenerUser;
    private ValueEventListener valueEventListenerMov;
    private RecyclerView recyclerView;
    private AdapterMovimentacao adapterMovimentacao;
    private String mesAnoSelecionado;

    private List<Movimentation> movimentations = new ArrayList<>();
    private DatabaseReference moviRef = ConfigFirebase.getFirebaseDatabase();
    private DatabaseReference firebaseRef = ConfigFirebase.getFirebaseDatabase();
    private FirebaseAuth mAuth = ConfigFirebase.getFirebaseAuth();
    private DatabaseReference userRef;
    private Movimentation movimentation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerSaldo);

        textoName = findViewById(R.id.textName);
        textSaldo = findViewById(R.id.textSaldo);
        calendarView = findViewById(R.id.calendarView);
        configuraCalendarView();
        swipe();

        adapterMovimentacao = new AdapterMovimentacao(movimentations, this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager( layoutManager );
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter( adapterMovimentacao );

    }

    //swipe para deletar movimentação
    public void swipe(){
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //passa position do item para excliur
                excluirMovi( viewHolder );
            }
        };

        new ItemTouchHelper( itemTouch ).attachToRecyclerView( recyclerView );

    }

    public void excluirMovi(RecyclerView.ViewHolder viewHolder){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        //configura alertdialog
        alertDialog.setTitle("Excluir movimentação da conta");
        alertDialog.setMessage("Voce tem certeza que deseja excluir?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int position = viewHolder.getAdapterPosition();
                movimentation = movimentations.get( position );

                String emailUser = mAuth.getCurrentUser().getEmail();
                String idUser = Base64Custom.codificarBase64(emailUser);
                moviRef = firebaseRef.child("movimentation").child( idUser ).child( mesAnoSelecionado );

                moviRef.child( movimentation.getKey() ).removeValue();
                adapterMovimentacao.notifyItemRemoved( position );
                atualizarSaldo();
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(HomeActivity.this, "Cancelado",Toast.LENGTH_SHORT).show();
                //atualiza a lista
                adapterMovimentacao.notifyDataSetChanged();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();

    }

    public void atualizarSaldo(){

        String emailUser = mAuth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codificarBase64(emailUser);
        userRef = firebaseRef.child("usuarios").child( idUser );

        if(movimentation.getTipo().equals("r")){
            receitaTotal = receitaTotal - movimentation.getValor();
            userRef.child("receitaTotal").setValue(receitaTotal);
        } else if ( movimentation.getTipo().equals("d")){
            despesaTotal = despesaTotal - movimentation.getValor();
            userRef.child("despesaTotal").setValue(despesaTotal);
        }
    }

    public void recuperarMov (){
        String emailUser = mAuth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codificarBase64(emailUser);
        moviRef = firebaseRef.child("movimentation").child( idUser ).child( mesAnoSelecionado );

        valueEventListenerMov = moviRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                movimentations.clear();

                for(DataSnapshot dados : snapshot.getChildren()){

                    Movimentation movimentation = dados.getValue(Movimentation.class);
                    movimentation.setKey( dados.getKey() );
                    movimentations.add( movimentation );
                }

                adapterMovimentacao.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void recuperarResumo(){
        String emailUser = mAuth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codificarBase64(emailUser);
        userRef = firebaseRef.child("usuarios").child( idUser );

        valueEventListenerUser = userRef.addValueEventListener(new ValueEventListener() {
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

    //FAB
    public void addReceita(View view){
        startActivity(new Intent(this, ReceitasActivity.class));
    }
    //FAB
    public void addDespesa(View view){
        startActivity(new Intent(this, DespesasActivity.class));
    }

    public void configuraCalendarView(){
        CharSequence[] meses = {"Janeiro","Fevereiro","Março","Abril","Maio","Junho","Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};
        calendarView.setTitleMonths( meses );

        CalendarDay dataAtual = calendarView.getCurrentDate();
        String mesSelecionado = String.format("%02d", (dataAtual.getMonth() + 1) );
        mesAnoSelecionado = mesSelecionado + "" + dataAtual.getYear();

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String mesSelecionado = String.format("%02d", (date.getMonth() + 1) );
                mesAnoSelecionado = mesSelecionado + "" + date.getYear();

                moviRef.removeEventListener(valueEventListenerMov);
                recuperarMov();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarResumo();
        recuperarMov();
    }

    @Override
    protected void onStop() {
        super.onStop();
        userRef.removeEventListener(valueEventListenerUser);
        moviRef.removeEventListener(valueEventListenerMov);
    }


}
