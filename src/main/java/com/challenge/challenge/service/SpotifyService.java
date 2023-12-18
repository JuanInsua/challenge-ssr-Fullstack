package com.challenge.challenge.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Base64;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SpotifyService {

    private final RestTemplate restTemplate;

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret ;

    private final String tokenUrl = "https://accounts.spotify.com/api/token";
    private final String clientMeUrl = "https://api.spotify.com/v1/me";

    public String getAccessToken(String authorizationCode, String redirectUri) {
        // Construye las credenciales codificadas para la solicitud de token
        String credentials = clientId + ":" + clientSecret;
        String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        // Construye los parámetros de la solicitud
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("code", authorizationCode);
        requestBody.add("redirect_uri", redirectUri);

        // Construye los encabezados de la solicitud
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Credentials);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Construye la solicitud
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        // Realiza la solicitud para intercambiar el código por el token de acceso
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

        // Verifica si la solicitud fue exitosa
        if (response.getStatusCode().is2xxSuccessful()) {
            // Obtiene el token de acceso de la respuesta
            return (String) Objects.requireNonNull(response.getBody()).get("access_token");
        } else {
            // Maneja el caso en el que la solicitud no fue exitosa (por ejemplo, manejar errores)
            throw new RuntimeException("Error al intercambiar el código de autorización por el token de acceso de Spotify");
        }
    }
    public String buildRedirectUri() {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/spotify/oauth2/callback/spotify")
                .build()
                .toUriString();
    }

    public String getSpotifyUserProfile(String spotifyAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + spotifyAccessToken);

        ResponseEntity<String> response = restTemplate.exchange(
                clientMeUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            // Manejar errores según sea necesario
            return "Error al obtener la información del perfil de Spotify";
        }
    }

    public String getPlaylistUser(String spotifyAccessToken,String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + spotifyAccessToken);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.spotify.com/v1/users/"+userId+"/playlists",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            // Manejar errores según sea necesario
            return "Error al obtener la información de artistas de Spotify";
        }
    }

    public String getPlaylistItemsById(String spotifyAccessToken,String playlistId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + spotifyAccessToken);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.spotify.com/v1/playlists/"+playlistId+"/tracks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            // Manejar errores según sea necesario
            return "Error al obtener la información de artistas de Spotify";
        }
    }


}

