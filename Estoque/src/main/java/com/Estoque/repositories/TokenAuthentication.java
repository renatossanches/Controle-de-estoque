package com.Estoque.repositories;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class TokenAuthentication {

    // Caminho do arquivo de configuração no diretório home do usuário
    private static final String CONFIG_FILE_PATH = System.getProperty("user.home") + "/application.properties";

    private static final String FIREBASE_API_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=SuaAPIKEYdoFirebase";
    private static final String urlStr = "https://securetoken.googleapis.com/v1/token?key=SuaAPIKEYdoFirebase";  // Substitua pela sua chave da API do Firebase
    private String authToken;
    private String idToken;

    // Método para garantir que o arquivo de configuração exista no diretório do usuário
    private static void ensureFileConfiguration() {
        File configFile = new File(CONFIG_FILE_PATH);

        // Se o arquivo não existe, extraí-lo do JAR ou criá-lo
        if (!configFile.exists()) {
            try (InputStream inputStream = TokenAuthentication.class.getClassLoader().getResourceAsStream("application.properties")) {
                if (inputStream != null) {
                    // Crie o arquivo local a partir do recurso dentro do JAR
                    Files.copy(inputStream, Paths.get(CONFIG_FILE_PATH), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Arquivo de configuração extraído do JAR.");
                } else {
                    // Caso o arquivo não esteja no JAR, crie o arquivo local vazio
                    configFile.createNewFile();
                    System.out.println("Arquivo de configuração criado no diretório do usuário.");
                }
            } catch (IOException e) {
                System.out.println("Erro ao garantir a existência do arquivo de configuração: " + e.getMessage());
            }
        }
    }

    // Método para autenticar com email e senha
    public Map<String, String> authenticateWithEmailAndPassword(String email, String password) throws IOException {
        URL url = new URL(FIREBASE_API_URL);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        // Corpo da requisição em JSON
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("email", email);
        jsonInput.put("password", password);
        jsonInput.put("returnSecureToken", true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInput.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Obtém a resposta
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            JSONObject jsonResponse = new JSONObject(response.toString());
            authToken = jsonResponse.getString("refreshToken");
            idToken = jsonResponse.getString("idToken");

            Map<String, String> tokens = new HashMap<>();
            tokens.put("idToken", idToken);
            tokens.put("refreshToken", authToken);
            return tokens;
        }
    }

    // Método para atualizar o ID token
    public static Map<String, String> updateIdToken(String refreshToken) throws IOException {
        URL url = new URL(urlStr);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // Prepare o corpo da requisição com o refresh token
        String body = "grant_type=refresh_token&refresh_token=" + refreshToken;

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = body.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Obtém a resposta
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            JSONObject jsonResponse = new JSONObject(response.toString());
            String newIdToken = jsonResponse.getString("id_token");

            // Salve o novo ID token junto com o refresh token
            Map<String, String> tokens = new HashMap<>();
            tokens.put("idToken", newIdToken);
            tokens.put("refreshToken", refreshToken);  // O refresh token não muda

            return tokens;
        }
    }

    // Método para salvar tokens no arquivo de configuração
    public static void salveTokenInFile(Map<String, String> tokens) {
        ensureFileConfiguration();  // Garante que o arquivo de configuração existe
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(fis);  // Carrega o arquivo properties existente

            // Atualiza o valor das chaves
            properties.setProperty("auth.token", tokens.get("idToken"));
            properties.setProperty("refresh.token", tokens.get("refreshToken"));

            try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE_PATH)) {
                properties.store(fos, null);  // Grava as alterações no arquivo
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar o token: " + e.getMessage());
        }
    }

    // Método para carregar os tokens do arquivo de configuração
    public static Map<String, String> loadToken() {
        ensureFileConfiguration();  // Garante que o arquivo de configuração exista
        Properties properties = new Properties();
        Map<String, String> tokens = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(fis);  // Carrega o arquivo properties
            String authToken = properties.getProperty("auth.token");
            String refreshToken = properties.getProperty("refresh.token");

            tokens.put("authToken", authToken);
            tokens.put("refreshToken", refreshToken);
        } catch (IOException e) {
            System.out.println("Erro ao carregar o arquivo de configuração: " + e.getMessage());
        }

        return tokens;  // Retorna os tokens ou um mapa vazio se houver erro
    }

    // Método para remover os tokens do arquivo de configuração
    public static void removeToken() {
        ensureFileConfiguration();  // Garante que o arquivo de configuração existe
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(fis);  // Carrega o arquivo properties

            // Remove as propriedades de token
            properties.remove("auth.token");
            properties.remove("refresh.token");

            // Salva as alterações
            try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE_PATH)) {
                properties.store(fos, null);  // Grava as alterações no arquivo
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar ou salvar o arquivo de configuração: " + e.getMessage());
        }
    }

    // Método para verificar se o usuário está logado
    public static boolean isUserLoggedIn() {
        Map<String, String> tokens = loadToken();
        String refreshToken = tokens.get("refreshToken");

        if (refreshToken != null && !refreshToken.isBlank()) {
            try {
                Map<String, String> newTokens = updateIdToken(refreshToken);  // Atualiza o ID token
                salveTokenInFile(newTokens);  // Salva os tokens atualizados
                return true;
            } catch (IOException e) {
                System.out.println("Erro ao atualizar o token: " + e.getMessage());
                return false;  // Caso não consiga atualizar, o usuário não está logado
            }
        }

        return false;  // Não está logado se não houver refresh token
    }

    // Métodos para obter os tokens
    public String getAuthToken() {
        return authToken;
    }

    public void logout() {
        authToken = null;
        idToken = null;
    }
}
