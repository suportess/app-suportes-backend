-- Adiciona o host do portal MV à configuração de empresa
ALTER TABLE public.conf_empresa
    ADD COLUMN IF NOT EXISTS ds_host_portal VARCHAR(255);
