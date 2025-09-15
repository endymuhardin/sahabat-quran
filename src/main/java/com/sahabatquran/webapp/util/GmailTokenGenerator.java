package com.sahabatquran.webapp.util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.GmailScopes;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Gmail Token Generator Utility
 *
 * This is a one-time setup utility to generate OAuth2 refresh tokens for Gmail API.
 * Run this locally (not in production) to generate the refresh token that will be
 * used by the application.
 *
 * Steps to generate refresh token:
 * 1. Create OAuth2 credentials in Google Cloud Console
 * 2. Download the credentials.json file
 * 3. Run this utility with the path to credentials.json
 * 4. Complete OAuth flow in browser
 * 5. Copy the refresh token from the output
 * 6. Set the refresh token as environment variable GMAIL_REFRESH_TOKEN
 *
 * Usage: java GmailTokenGenerator /path/to/credentials.json
 */
public class GmailTokenGenerator {

    private static final String APPLICATION_NAME = "Sahabat Quran Gmail Integration";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_SEND);

    /**
     * Main method to run the token generator.
     *
     * @param args Command line arguments (path to credentials.json)
     * @throws IOException If there's an I/O error
     * @throws GeneralSecurityException If there's a security error
     */
    public static void main(String[] args) throws IOException, GeneralSecurityException {
        System.out.println("===========================================");
        System.out.println("Gmail OAuth2 Refresh Token Generator");
        System.out.println("===========================================\n");

        if (args.length < 1) {
            System.out.println("Usage: java GmailTokenGenerator /path/to/credentials.json");
            System.out.println("\nSteps to get credentials.json:");
            System.out.println("1. Go to https://console.cloud.google.com/");
            System.out.println("2. Create or select a project");
            System.out.println("3. Enable Gmail API");
            System.out.println("4. Create OAuth 2.0 Client ID credentials");
            System.out.println("5. Set application type to 'Desktop app'");
            System.out.println("6. Download the credentials.json file");
            System.out.println("7. Run this utility with the path to credentials.json");
            return;
        }

        String credentialsPath = args[0];
        File credentialsFile = new File(credentialsPath);

        if (!credentialsFile.exists()) {
            System.err.println("Error: Credentials file not found: " + credentialsPath);
            return;
        }

        try {
            // Build flow and trigger user authorization request
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Credential credential = getCredentials(httpTransport, credentialsPath);

            System.out.println("\n✅ Authorization successful!");
            System.out.println("\n===========================================");
            System.out.println("IMPORTANT: Save these values securely");
            System.out.println("===========================================\n");

            // Read the credentials.json to get client ID and secret
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new FileReader(credentialsPath));

            System.out.println("Client ID:");
            System.out.println(clientSecrets.getDetails().getClientId());
            System.out.println("\nClient Secret:");
            System.out.println(clientSecrets.getDetails().getClientSecret());
            System.out.println("\nRefresh Token:");
            System.out.println(credential.getRefreshToken());
            System.out.println("\nAccess Token (temporary, will auto-refresh):");
            System.out.println(credential.getAccessToken());

            System.out.println("\n===========================================");
            System.out.println("Environment Variables Configuration");
            System.out.println("===========================================\n");
            System.out.println("Add these to your application.properties or environment:");
            System.out.println("\n# Gmail Configuration");
            System.out.println("gmail.enabled=true");
            System.out.println("gmail.client-id=" + clientSecrets.getDetails().getClientId());
            System.out.println("gmail.client-secret=" + clientSecrets.getDetails().getClientSecret());
            System.out.println("gmail.refresh-token=" + credential.getRefreshToken());
            System.out.println("gmail.notification-email=your-email@gmail.com");

            System.out.println("\nFor environment variables:");
            System.out.println("export GMAIL_CLIENT_ID=\"" + clientSecrets.getDetails().getClientId() + "\"");
            System.out.println("export GMAIL_CLIENT_SECRET=\"" + clientSecrets.getDetails().getClientSecret() + "\"");
            System.out.println("export GMAIL_REFRESH_TOKEN=\"" + credential.getRefreshToken() + "\"");
            System.out.println("export GMAIL_NOTIFICATION_EMAIL=\"your-email@gmail.com\"");

            System.out.println("\n✅ Setup complete! You can now use Gmail API in your application.");

            // Clean up token store
            File tokenFolder = new File(TOKENS_DIRECTORY_PATH);
            if (tokenFolder.exists()) {
                deleteDirectory(tokenFolder);
                System.out.println("\n🧹 Cleaned up temporary token storage.");
            }

        } catch (Exception e) {
            System.err.println("\n❌ Error during authorization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param httpTransport The network HTTP Transport
     * @param credentialsPath Path to credentials.json file
     * @return An authorized Credential object
     * @throws IOException If the credentials.json file cannot be found
     */
    private static Credential getCredentials(final NetHttpTransport httpTransport, String credentialsPath)
            throws IOException {
        // Load client secrets
        InputStream in = new FileInputStream(credentialsPath);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline") // Important for refresh token
                .setApprovalPrompt("force") // Force to get refresh token
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

        System.out.println("\n📌 Opening browser for authorization...");
        System.out.println("If browser doesn't open automatically, visit this URL:");
        System.out.println(flow.newAuthorizationUrl().setRedirectUri(receiver.getRedirectUri()).build());

        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Recursively delete a directory.
     *
     * @param directory Directory to delete
     */
    private static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
}