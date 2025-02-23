package br.natanael.android.olx.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.le.AdvertiseData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.lang.UProperty;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskara.widget.MaskEditText;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.natanael.android.olx.R;
import br.natanael.android.olx.helper.ConfiguracaoFirebase;
import br.natanael.android.olx.helper.Permissoes;
import br.natanael.android.olx.model.Anuncio;
import dmax.dialog.SpotsDialog;

public class CadastrarAnuncioActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTitulo,editDescricao;
    private CurrencyEditText editValor;
    private Button btnSalvarAnuncio;
    private MaskEditText editTelefone;
    private ImageView imageCadastro1, imageCadastro2,imageCadastro3;
    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaURLFotos = new ArrayList<>();
    private Spinner spinnerEstado, spinnerCategoria;
    private Anuncio anuncio;
    private AlertDialog dialog;


    private String[] permissoes = new String[] {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    private StorageReference storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        Permissoes.validarPermissoes(permissoes, this, 1);


        configuracoesInicias();

        inicializarComponentes();

        carregarDadosSpinner();
    }

    private void configuracoesInicias() {
        storage = ConfiguracaoFirebase.getFirebaseStorage();
    }

    private void carregarDadosSpinner() {
        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, estados);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapter);

        String[] categoria = getResources().getStringArray(R.array.categoria);
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, categoria);
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapterCategoria);



    }

    private void inicializarComponentes() {
        editTitulo = findViewById(R.id.editTitulo);
        editDescricao = findViewById(R.id.editDescricao);
        editValor = findViewById(R.id.editValor);
        btnSalvarAnuncio = findViewById(R.id.btnSalvarAnuncio);
        editTelefone = findViewById(R.id.editTelefone);
        imageCadastro1 = findViewById(R.id.imageCadastro1);
        imageCadastro2 = findViewById(R.id.imageCadastro2);
        imageCadastro3 = findViewById(R.id.imageCadastro3);
        spinnerEstado = findViewById(R.id.spinnerEstado);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);

        imageCadastro1.setOnClickListener(this);
        imageCadastro2.setOnClickListener(this);
        imageCadastro3.setOnClickListener(this);

        Locale locale = new Locale("pt", "BR");
        editValor.setLocale(locale);
    }

    public void  validarDadosAnuncio(View view) {
        anuncio = configurarAnuncio();
        String valor = String.valueOf(editValor.getRawValue());
        String fone = "";

        if(editTelefone.getUnMasked() != null)
            fone = editTelefone.getUnMasked();

        if(listaFotosRecuperadas.size() != 0)
        {
            if(!anuncio.getEstado().isEmpty() && !anuncio.getEstado().equals("Estado"))
            {
                if(!anuncio.getCategoria().isEmpty() && !anuncio.getCategoria().equals("Categoria"))
                {
                    if(!anuncio.getTitulo().isEmpty())
                    {
                        if(!valor.isEmpty() & !valor.equals("0"))
                        {
                            if(!anuncio.getTelefone().isEmpty() && fone.length() >=10)
                            {
                                if(!anuncio.getDescricao().isEmpty())
                                {
                                    salvarAnuncio();
                                }
                                else
                                    exibirMensagemErro("Preencha o campo Descrição");
                            }
                            else
                                exibirMensagemErro("Preencha o campo Telefone, digite no minimo 10 caracteres");
                        }
                        else
                            exibirMensagemErro("Preencha o campo valor!");
                    }
                    else
                        exibirMensagemErro("Preencha o campo Titulo");
                }
                else
                    exibirMensagemErro("Preencha o campo Categoria!");
            }
            else
                exibirMensagemErro("Preencha o campo estado!");
        }
        else
            exibirMensagemErro("Selecione ao menos uma foto!");
    }

    private void exibirMensagemErro(String mensagem ){
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    public void salvarAnuncio(){
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Salvando Anúncios")
                .setCancelable(false)
                .build();

        dialog.show();

        int tamanhoDaLista = listaFotosRecuperadas.size();

        for (int i = 0; i < listaFotosRecuperadas.size(); i++)
        {
            String urlImagem = listaFotosRecuperadas.get(i);

            salvarFotoStorage(urlImagem, tamanhoDaLista, i );
        }

    }

    private void salvarFotoStorage(String urlImagem, final int totalDeFotos, int i) {

        final StorageReference imagemRef = storage.child("imagens")
                .child("anuncios")
                .child(anuncio.getIdAnuncio())
                .child("imagem"+i);


        UploadTask uploadTask = imagemRef.putFile(Uri.parse(urlImagem));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String urlConvetida = task.getResult().toString();
                        listaURLFotos.add(urlConvetida);

                        if(totalDeFotos == listaURLFotos.size()){
                            anuncio.setFotos(listaURLFotos);
                            anuncio.salvar();
                            dialog.dismiss();
                            finish();
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                exibirMensagemErro("Falha ao fazer upload");
                e.printStackTrace();

            }
        });

    }

    private Anuncio configurarAnuncio() {
        String estado = spinnerEstado.getSelectedItem().toString();
        String categoria = spinnerCategoria.getSelectedItem().toString();
        String titulo = editTitulo.getText().toString();
        String valor = editValor.getText().toString();
        String telefone = editTelefone.getText().toString();
        String descricao = editDescricao.getText().toString();

        Anuncio anuncio = new Anuncio();
        anuncio.setEstado(estado);
        anuncio.setCategoria(categoria);
        anuncio.setTitulo(titulo);
        anuncio.setValor(valor);
        anuncio.setTelefone(telefone);
        anuncio.setDescricao(descricao);

        return  anuncio;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults)
        {
            if(permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitas as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imageCadastro1:
                escolherImagem(1);
                break;
            case R.id.imageCadastro2:
                escolherImagem(2);
                break;
            case R.id.imageCadastro3:
                escolherImagem(3);
                break;
        }
    }

    public void escolherImagem(int requestCode) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
        {
            //Recuperar imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //Configurar imagem no ImageView
            if(requestCode == 1)
                imageCadastro1.setImageURI(imagemSelecionada);
            else if(requestCode == 2)
                imageCadastro2.setImageURI(imagemSelecionada);
            else if(requestCode == 3)
                imageCadastro3.setImageURI(imagemSelecionada);

            listaFotosRecuperadas.add(caminhoImagem);
        }
    }
}
