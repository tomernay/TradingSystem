package DataBase.ServiceAccountDetails;

import DataBase.FireBaseConstants;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.io.IOException;

public class Init {
    public static void initializeFirebaseApp() throws IOException {
        FileInputStream serviceAccount = new FileInputStream("DataBase/ServiceAccountDetails/FirebaseDetails.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://traidingsystem-fec38-default-rtdb.firebaseio.com/") // Replace with your Firebase Database URL
                .build();

        FirebaseApp.initializeApp(options);

            FirebaseDatabase.getInstance().getReference(FireBaseConstants.paymentsRoot).child(FireBaseConstants.publicPay).child("y").setValueAsync("v");



    }

}
