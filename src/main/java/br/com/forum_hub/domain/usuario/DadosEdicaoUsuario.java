package br.com.forum_hub.domain.usuario;

import jakarta.validation.constraints.NotBlank;

public record DadosEdicaoUsuario(
        @NotBlank String nomeUsuario,
        @NotBlank String miniBiografia,
        @NotBlank String biografia) {
}