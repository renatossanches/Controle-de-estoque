package com.Estoque.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableAsync;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

@Configuration
@EnableAsync
public class FirebaseConfig {
  @Value("${firebase.config.path}")
  private String firebaseConfigPath;
  
  static {
    try {
      disableVerificationSSL();
    } catch (KeyManagementException|NoSuchAlgorithmException|IOException e) {
      e.printStackTrace();
    } 
  }
  
  @Bean
  public FirebaseDatabase firebaseApp() throws IOException {
    InputStream serviceAccount = (new ClassPathResource("adm.json")).getInputStream();
    if (serviceAccount == null)
      throw new IOException("Nfoi possencontrar o arquivo de credenciais: " + this.firebaseConfigPath); 
    FirebaseOptions options = (new FirebaseOptions.Builder()).setCredentials(GoogleCredentials.fromStream(serviceAccount)).setDatabaseUrl("https://ColoqueAquiSeuBancoDeDados-default-rtdb.firebaseio.com").build();
    if (FirebaseApp.getApps().isEmpty())
      FirebaseApp.initializeApp(options); 
    return FirebaseDatabase.getInstance();
  }
  
  @Bean
  public FirebaseAuth firebaseAuth() {
    return FirebaseAuth.getInstance();
  }
  
  @Bean
  public FirebaseMessaging firebaseMessaging() {
    return FirebaseMessaging.getInstance();
  }

    // Método para desabilitar a verificação SSL
    public static void disableVerificationSSL() throws KeyManagementException, IOException, NoSuchAlgorithmException {
        // Cria um TrustManager que não faz verificação de certificado
        TrustManager[] trustAllCertificates = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    // Não faz nada, permitindo qualquer certificado de cliente
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    // Não faz nada, permitindo qualquer certificado de servidor
                }
            }
        };

        // Configura o SSLContext para usar o TrustManager customizado
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCertificates, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Desabilitar a verificação de hostname
        HostnameVerifier allHostsValid = (hostname, session) -> true;
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        // Teste de conexão para garantir que o SSL foi desabilitado
        URL url = new URL("https://api.spring.io");
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.connect();
        //ystem.out.println("Conexão bem-sucedida com a URL: " + url);
    }
}
