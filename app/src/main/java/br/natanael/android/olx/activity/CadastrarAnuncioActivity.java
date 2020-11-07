package br.natanael.android.olx.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blackcat.currencyedittext.CurrencyEditText;

import java.text.DecimalFormat;
import java.util.Locale;

import br.natanael.android.olx.R;

public class CadastrarAnuncioActivity extends AppCompatActivity {
    private EditText editTitulo,editDescricao;
    private CurrencyEditText editValor;
    private Button btnSalvarAnuncio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        editTitulo = findViewById(R.id.editTitulo);
        editDescricao = findViewById(R.id.editDescricao);
        editValor = findViewById(R.id.editValor);
        btnSalvarAnuncio = findViewById(R.id.btnSalvarAnuncio);

        Locale locale = new Locale("pt", "BR");
        editValor.setLocale(locale);
    }

    public void salvarAnuncio(View view){

    }
}
