package com.challenge.challenge.controller;

import com.challenge.challenge.service.SpotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/spotify")
@RequiredArgsConstructor
public class SpotifyController {

    @Value("${spotify.client.id}")
    private String clientId;
    private final SpotifyService spotifyService;
    @GetMapping("/authorize")
    public ResponseEntity<Void> authorize() {
        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest
                .authorizationCode()
                .clientId(clientId)
                .authorizationUri("https://accounts.spotify.com/authorize")
                .redirectUri(spotifyService.buildRedirectUri()) // Tu URI de redirección registrada en Spotify
                .scope("user-read-private", "user-read-email", "playlist-read-private", "user-read-playback-state"
                        , "user-read-currently-playing", "user-read-recently-played")
                .state(OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI)
                .attributes(attr -> attr.put("key", "value"))
                .build();

        String redirectUrl = authorizationRequest.getAuthorizationRequestUri();

        return ResponseEntity
                .status(302)
                .header("Location", redirectUrl)
                .build();
    }
    @GetMapping("/oauth2/callback/spotify")
    public ResponseEntity<String> spotifyCallback(@RequestParam("code") String authorizationCode,
                                                  @RequestParam("state") String state,
                                                  Principal principal) {
        // Lógica para intercambiar el authorizationCode por el token de acceso
        String redirectUri = spotifyService.buildRedirectUri(); // Obtén la URI de redirección
        String spotifyAccessToken = spotifyService.getAccessToken(authorizationCode, redirectUri);

        return ResponseEntity.ok(spotifyAccessToken);
    }

    @GetMapping("/me")
    public ResponseEntity<String> getSpotifyUserProfile(
            @RequestParam(name = "access_token", required = true) String spotifyAccessToken) {
        try {
            // Lógica para obtener el perfil de Spotify usando el token de acceso
            String userProfile = spotifyService.getSpotifyUserProfile(spotifyAccessToken);
            //GENERAR DTO USERPROFILE
            return ResponseEntity.ok(userProfile);
        } catch (RuntimeException re) {
            re.printStackTrace();
            return ResponseEntity.badRequest().body(re.getMessage());
        }
    }
    @GetMapping("/playlists")
    public ResponseEntity<String> getPlaylistUser(
            @RequestParam(name = "access_token", required = true) String spotifyAccessToken
            ,@RequestParam(name="userId",required = true) String userId) {

        try {
            // Lógica para obtener el playlists de Spotify usando el token de acceso
            String artists = spotifyService.getPlaylistUser(spotifyAccessToken,userId);
            //GENERAR DTO ARTISTAS
            return ResponseEntity.ok(artists);
        } catch (RuntimeException re) {
            re.printStackTrace();
            return ResponseEntity.badRequest().body(re.getMessage());
        }
    }
    @GetMapping("/playListItems")
    public ResponseEntity<String> getPlaylistItems(
            @RequestParam(name = "access_token", required = true) String spotifyAccessToken
            ,@RequestParam(name="userId",required = true) String playlistId) {
        try {
            // Lógica para obtener el playListItems de Spotify usando el token de acceso
            String playlistItems = spotifyService.getPlaylistItemsById(spotifyAccessToken,playlistId);
            //GENERAR DTO ITEMSPLAYLIST
            return ResponseEntity.ok(playlistItems);
        } catch (RuntimeException re) {
            re.printStackTrace();
            return ResponseEntity.badRequest().body(re.getMessage());
        }
    }

}



