package br.tec.suportes.backend.dto.empresa;

/**
 * Resposta do endpoint de geração de domínio ngrok.
 * Retorna a empresa atualizada (com dsHostPortal preenchido)
 * e o ID do domínio ngrok (necessário para apagar via API posteriormente).
 */
public record GerarNgrokResponse(EmpresaDTO empresa, String ngrokDomainId) {}
