package com.potless.backend.damage.dto.service.response.count;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "구별 Damage 발생 통계 조회 리스트 DTO")
public class AreaDamageCountForDateListResponseDTO {

    @Schema(description = "구 이름")
    private String areaGu;

    List<AreaDamageCountForDateResponseDTO> list = new ArrayList<>();

    @Builder
    public AreaDamageCountForDateListResponseDTO(String areaGu, List<AreaDamageCountForDateResponseDTO> list) {
        this.areaGu = areaGu;
        this.list = list;
    }
}