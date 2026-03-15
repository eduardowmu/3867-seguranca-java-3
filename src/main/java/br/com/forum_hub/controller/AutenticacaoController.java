package br.com.forum_hub.controller;

import br.com.forum_hub.domain.autenticacao.DadosLogin;
import br.com.forum_hub.domain.autenticacao.DadosRefreshToken;
import br.com.forum_hub.domain.autenticacao.DadosToken;
import br.com.forum_hub.domain.autenticacao.TokenService;
import br.com.forum_hub.domain.usuario.Usuario;
import br.com.forum_hub.domain.usuario.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AutenticacaoController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<DadosToken> efetuarLogin(@RequestBody @Valid DadosLogin dadosLogin) {
        var autenticationToken = new UsernamePasswordAuthenticationToken(dadosLogin.email(), dadosLogin.senha());
        Authentication authentication = this.authenticationManager.authenticate(autenticationToken);
        String tokenAcesso = this.tokenService.gerarToken((Usuario)authentication.getPrincipal());
        String refreshToken = this.tokenService.gerearRefreshToken((Usuario)authentication.getPrincipal());
        return ResponseEntity.ok(new DadosToken(tokenAcesso, refreshToken));
    }

    @PostMapping("/atualizar-token")
    public ResponseEntity<DadosToken> atualizarToken(@RequestBody @Valid DadosRefreshToken dadosRefreshToken) {
        var refreshToken = dadosRefreshToken.refreshToken();
        Long idUsuario = Long.valueOf(tokenService.verificarToken(refreshToken));
        Usuario usuario = this.usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "Usuario de ID %d não encontrado", idUsuario)));

        String tokenAcesso = this.tokenService.gerarToken(usuario);
        String tokenAtualizacao = this.tokenService.gerearRefreshToken(usuario);
        return ResponseEntity.ok(new DadosToken(tokenAcesso, tokenAtualizacao));
    }
}