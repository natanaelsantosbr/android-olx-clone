package br.natanael.android.olx.helper;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {
    private static DatabaseReference referenciaFirebase;
    private static FirebaseAuth referenciaAutenticacao;
    private static StorageReference referenciaStorage;




    public  static String getIdUsuario() {
        FirebaseAuth auth = getFirebaseAutenticacao();
        return  auth.getCurrentUser().getUid();
    }

    //retorna a referencia do database
    public static DatabaseReference getFirebase() {
        if(referenciaFirebase == null)
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();

        return referenciaFirebase;
    }

    //retorna a instancia do FirebaseAuth
    public static FirebaseAuth getFirebaseAutenticacao() {
        if(referenciaAutenticacao == null)
            referenciaAutenticacao = FirebaseAuth.getInstance();

        return referenciaAutenticacao;
    }

    //retorna a instancia do StorageReference
    public static StorageReference getFirebaseStorage() {
        if(referenciaStorage == null)
            referenciaStorage = FirebaseStorage.getInstance().getReference();

        return referenciaStorage;
    }






}
