package br.tec.suportes.backend.dto.produto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class CadastroProdutoRequest {

    // ── Descrições ────────────────────────────────────────────────────────────
    @NotBlank
    @JsonProperty("ds_produto")
    private String dsProduto;

    @JsonProperty("ds_comercial")
    private String dsComercial;

    @JsonProperty("ds_especificacao")
    private String dsEspecificacao;

    // ── Controles ─────────────────────────────────────────────────────────────
    @NotBlank
    @Pattern(regexp = "^[SN]$")
    @JsonProperty("sn_lote")
    private String snLote = "S";

    @NotBlank
    @Pattern(regexp = "^[SN]$")
    @JsonProperty("sn_validade")
    private String snValidade = "S";

    @NotBlank
    @Pattern(regexp = "^[SN]$")
    @JsonProperty("sn_medicamento")
    private String snMedicamento = "S";

    @NotBlank
    @Pattern(regexp = "^[SN]$")
    @JsonProperty("sn_consignado")
    private String snConsignado = "N";

    @NotBlank
    @Pattern(regexp = "^[AMF]$")
    @JsonProperty("tp_sexo")
    private String tpSexo = "A";

    // ── Classificação ─────────────────────────────────────────────────────────
    @NotNull
    @JsonProperty("cd_especie")
    private Integer cdEspecie;

    @NotNull
    @JsonProperty("cd_classe")
    private Integer cdClasse;

    @NotNull
    @JsonProperty("cd_sub_cla")
    private Integer cdSubCla;

    @NotBlank
    @JsonProperty("ds_sub_cla")
    private String dsSubCla;

    // ── Unidade ───────────────────────────────────────────────────────────────
    @NotBlank
    @JsonProperty("cd_unidade")
    private String cdUnidade;

    // ── Opcionais ─────────────────────────────────────────────────────────────
    @JsonProperty("cd_tip_ativ")
    private Long cdTipAtiv;

    @JsonProperty("cd_pro_fat")
    private String cdProFat;

    @JsonProperty("cd_pro_fat_sus")
    private String cdProFatSus;

    @JsonProperty("cd_procedimento_sus")
    private String cdProcedimentoSus;

    @JsonProperty("cd_fornecedor_principal")
    private Long cdFornecedorPrincipal;

    // ── Empresas ─────────────────────────────────────────────────────────────
    @NotEmpty
    @JsonProperty("empresas")
    private List<Integer> empresas;
}

