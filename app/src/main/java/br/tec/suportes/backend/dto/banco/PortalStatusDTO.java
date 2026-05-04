package br.tec.suportes.backend.dto.banco;

/**
 * Retorno de GET /status no portal ngrok da empresa.
 *
 * @param portalAtivo    true quando o portal responde status == "UP"
 * @param status         valor literal retornado pelo portal ("UP", "DOWN", etc.)
 * @param uptime         tempo em que o portal está no ar (string de duração Go)
 * @param bancoCadastrado true quando a chave "oracle-prod" já está registrada no portal
 */
public record PortalStatusDTO(
        boolean portalAtivo,
        String  status,
        String  uptime,
        boolean bancoCadastrado
) {}
