package br.tec.suportes.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean sucesso;
    private String mensagem;
    private T dados;

    public static <T> ApiResponse<T> ok(T dados) {
        return new ApiResponse<>(true, null, dados);
    }

    public static <T> ApiResponse<T> ok(String mensagem, T dados) {
        return new ApiResponse<>(true, mensagem, dados);
    }

    public static <T> ApiResponse<T> erro(String mensagem) {
        return new ApiResponse<>(false, mensagem, null);
    }
}
