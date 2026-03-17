package br.com.forum_hub.infra.seguranca;

import br.com.forum_hub.domain.perfil.PerfilEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class ConfiguracoesSeguranca {
    @Autowired
    private FiltroTokenAcesso filtroTokenAcesso;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(
                        req -> {
                            req.requestMatchers("/login", "/atualizar-token", "/usuarios/verificar-conta").permitAll();

                            req.requestMatchers(HttpMethod.GET, "/cursos").permitAll();

                            req.requestMatchers(HttpMethod.GET, "/topicos/**").permitAll();

                            req.requestMatchers(HttpMethod.POST, "/topicos")
                                    .hasRole(PerfilEnum.ESTUDANTE.name());

                            req.requestMatchers(HttpMethod.PUT, "/topicos")
                                    .hasRole(PerfilEnum.ESTUDANTE.name());

                            req.requestMatchers(HttpMethod.DELETE, "/topicos/**")
                                    .hasRole(PerfilEnum.ESTUDANTE.name());

                            req.requestMatchers(HttpMethod.PATCH, "/topicos/{idTopico}/repostas/**")
                                    .hasAnyRole(PerfilEnum.INSTRUTOR.name(), PerfilEnum.ESTUDANTE.name());

                            req.requestMatchers(HttpMethod.PATCH, "/topicos/**")
                                    .hasRole(PerfilEnum.MODERADOR.name());

                            req.requestMatchers(HttpMethod.PATCH, "/usuarios/adicionar-perfil/**")
                                    .hasRole(PerfilEnum.ADMIN.name());

                            req.anyRequest().authenticated();
                        })
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(this.filtroTokenAcesso, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder encriptador(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        String hierarquia = "ROLE_ADMIN > ROLE_MODERADOR\n"+
                "ROLE_MODERADOR > ROLE_INSTRUTOR\n"+
                "ROLE_MODERADOR > ROLE_ESTUDANTE";
        return RoleHierarchyImpl.fromHierarchy(hierarquia);
    }
}