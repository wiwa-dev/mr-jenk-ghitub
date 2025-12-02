package com.buy01.user.config;

import feign.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

@Configuration
public class FeignConfiguration {

    /**
     * Cette configuration crée un client Feign qui ignore la vérification SSL.//
     * Elle utilise le client par défaut de Feign sans dépendances externes.
     * Approprié pour les communications en réseau internes sécurisé.
     */
    @Bean
    public Client feignClient() {
        try {
            // Créer un gestionnaire de confiance qui accepte tous les certificats
            // sans effectuer aucune vérification
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        // Accepte tous les certificats clients sans vérification
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        // Accepte tous les certificats serveurs sans vérification
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
            };

            // Initialiser le contexte SSL avec notre gestionnaire de confiance permissif
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory socketFactory = sslContext.getSocketFactory();

            // Créer un vérificateur de nom d'hôte qui accepte tous les noms
            // Cela permet au certificat d'être pour n'importe quel domaine
            javax.net.ssl.HostnameVerifier hostnameVerifier = (hostname, session) -> true;

            // Retourner le client Feign par défaut configuré avec notre SSL permissif
            return new Client.Default(socketFactory, hostnameVerifier);
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la configuration du client Feign SSL", e);
        }
    }
}