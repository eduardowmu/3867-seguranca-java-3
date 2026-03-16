package br.com.forum_hub.domain.usuario;

import br.com.forum_hub.domain.perfil.PerfilEnum;
import jakarta.validation.constraints.NotNull;

public record DadosPerfil(@NotNull PerfilEnum perfilEnum) {
}