-- =============================================================
-- V7 — Adiciona coluna TIPO à tabela USUARIO
--
-- OPERADOR: usuário padrão, somente pode operar se vinculado a
--           ao menos uma empresa. Não pode gerenciar configurações.
-- =============================================================

ALTER TABLE usuario
    ADD COLUMN IF NOT EXISTS tipo VARCHAR(20) NOT NULL DEFAULT 'OPERADOR';

COMMENT ON COLUMN usuario.tipo IS 'Tipo de acesso: OPERADOR (padrão — restrito a empresas vinculadas)';
