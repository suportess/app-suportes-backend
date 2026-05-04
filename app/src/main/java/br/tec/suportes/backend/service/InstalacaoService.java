package br.tec.suportes.backend.service;

import br.tec.suportes.backend.domain.ConfEmpresa;
import br.tec.suportes.backend.exception.RecursoNaoEncontradoException;
import br.tec.suportes.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstalacaoService {

    @Value("${ngrok.authtoken:}")
    private String ngrokAuthtoken;

    private final UsuarioRepository usuarioRepository;

    /**
     * Gera o conteúdo do docker-compose.yml preenchido com os dados da empresa ativa do usuário.
     * Retorna o YAML como String.
     */
    public String gerarDockerCompose(String auth0Sub) {
        var usuario = usuarioRepository.findByAuth0Sub(auth0Sub)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        ConfEmpresa empresa = Optional.ofNullable(usuario.getEmpresaAtiva())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Nenhuma empresa ativa selecionada."));

        if (empresa.getDsHostPortal() == null || empresa.getDsHostPortal().isBlank())
            throw new RecursoNaoEncontradoException(
                    "A empresa ativa não possui domínio ngrok configurado.");

        // strip https:// or http://
        String domain = empresa.getDsHostPortal().replaceFirst("^https?://", "");
        String apikey = empresa.getApikey();

        return buildYaml(apikey, domain);
    }

    private String buildYaml(String apikey, String domain) {
        return """
# ─────────────────────────────────────────────────────────────────────────────
# Portal — on-premises
#
# Setup:
#   docker compose up -d
#
# Variáveis pré-preenchidas:
#   GATEWAY_API_KEY  / PORTAL_API_KEY  → chave de autenticação desta empresa
#   NGROK_AUTHTOKEN                    → token ngrok da conta Suportes
#   NGROK_DOMAIN                       → domínio reservado para esta empresa
#
# Variáveis opcionais:
#   SERVER_PORT  → porta interna do portal (padrão: 8080)
#   STATUS       → UP | DOWN | MAINTENANCE (padrão: UP)
#
# Para rodar as migrations (uma única vez após subir):
#   docker compose run --rm portal-scripts
# ─────────────────────────────────────────────────────────────────────────────

services:

  portal:
    image: jessebezerra/app-suportes-portal:latest
    container_name: portal
    restart: unless-stopped
    volumes:
      - portal_data:/app/db
    environment:
      DATABASE: portal.db
      DATABASE_PATH: db
      DATABASE_TIMEOUT: 10
      GATEWAY_API_KEY: %s
    healthcheck:
      test: ["CMD-SHELL", "wget -qO /dev/null http://localhost:$${SERVER_PORT:-8080}/status"]
      interval: 30s
      timeout: 5s
      retries: 3
      start_period: 10s

  ngrok:
    image: ngrok/ngrok:latest
    container_name: portal-ngrok
    restart: unless-stopped
    environment:
      NGROK_AUTHTOKEN: %s
    command: http --domain=%s portal:$${SERVER_PORT:-8080}
    depends_on:
      portal:
        condition: service_healthy

  portal-scripts:
    image: jessebezerra/app-suportes-portal-scripts:latest
    container_name: portal-scripts
    restart: "no"
    environment:
      PORTAL_URL: http://portal:$${SERVER_PORT:-8080}
      PORTAL_API_KEY: %s
      PORTAL_HISTORY_FILE: /app/history/portal_scripts_history.json
    volumes:
      - portal_scripts_history:/app/history
    depends_on:
      portal:
        condition: service_healthy

volumes:
  portal_data:
  portal_scripts_history:
""".formatted(apikey, ngrokAuthtoken, domain, apikey);
    }
}
