package br.natanael.android.olx.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import br.natanael.android.olx.R;
import br.natanael.android.olx.helper.ConfiguracaoFirebase;

public class CadastroActivity extends AppCompatActivity {

    private Button buttonAcesso;
    private TextView editCadastroEmail,editCadastroSenha;
    private Switch switchAcesso;
    private ProgressBar progressBar;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        inicializarComponentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        buttonAcesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editCadastroEmail.getText().toString();
                String senha = editCadastroSenha.getText().toString();
                
                if(!email.isEmpty())
                {
                    if(!senha.isEmpty())
                    {
                        progressBar.setVisibility(View.VISIBLE);

                        if(switchAcesso.isChecked())
                        {
                            //Cadastro
                            autenticacao.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(CadastroActivity.this, "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        String erroExcecao = "";
                                        try {
                                            throw task.getException();
                                        }
                                        catch (FirebaseAuthWeakPasswordException ex)
                                        {
                                            erroExcecao = "Digite uma senha mais forte";
                                        }
                                        catch (FirebaseAuthInvalidCredentialsException ex) {
                                            erroExcecao = "Por favor, digite um e-mail válido";
                                        }
                                        catch (FirebaseAuthUserCollisionException ex){
                                            erroExcecao = "Esta conta já foi cadastrada";
                                        }
                                        catch (Exception ex)
                                        {
                                            erroExcecao = "ao cadastrar usuario: " + ex.getMessage();
                                            ex.printStackTrace();
                                        }

                                        Toast.makeText(CadastroActivity.this, "Erro: " + erroExcecao, Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), AnunciosActivity.class));
                                    }
                                    progressBar.setVisibility(View.GONE);

                                }
                            });


                        }
                        else
                        {

                            //Login
                            autenticacao.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(CadastroActivity.this, "Logado com sucesso", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), AnunciosActivity.class));

                                    }
                                    else
                                    {
                                        Toast.makeText(CadastroActivity.this, "Erro ao logar: " + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                    progressBar.setVisibility(View.GONE);

                                }
                            });

                        }
                    }
                    else
                    {
                        Toast.makeText(CadastroActivity.this, "Preencha a Senha", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(CadastroActivity.this, "Preencha o E-mail", Toast.LENGTH_SHORT).show();
                }


            }
        });
                




    }

    private void inicializarComponentes() {
        buttonAcesso = findViewById(R.id.buttonAcesso);
        editCadastroEmail = findViewById(R.id.editCadastroEmail);
        editCadastroSenha  = findViewById(R.id.editCadastroSenha);
        switchAcesso = findViewById(R.id.switchAcesso);
        progressBar = findViewById(R.id.progressBarCadastrarOuLogar);
        progressBar.setVisibility(View.GONE);
    }
}
