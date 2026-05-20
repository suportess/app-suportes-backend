package br.tec.suportes.backend.dto.vinculo;

import br.tec.suportes.backend.domain.VinculoProduto;

import java.time.LocalDateTime;

public record VinculoProdutoDTO(
        Long          id,
        String        nmUsuario,
        String        emailUsuario,
        String        nmEmpresa,
        LocalDateTime dtVinculo,
        Integer       qtVinculos
) {
    public static VinculoProdutoDTO of(VinculoProduto v) {
        return new VinculoProdutoDTO(
                v.getId(),
                v.getNmUsuario(),
                v.getEmailUsuario(),
                v.getNmEmpresa(),
                v.getDtVinculo(),
                v.getQtVinculos()
        );
    }
}
