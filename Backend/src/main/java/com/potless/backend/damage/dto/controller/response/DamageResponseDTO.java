package com.potless.backend.damage.dto.controller.response;

import com.potless.backend.damage.entity.enums.Status;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "Damage 조회 응답 DTO")
public class DamageResponseDTO {

    @Schema(description = "Damage ID")
    private Long id;
    @Schema(description = "심각도")
    private Integer severity;
    @Schema(description = "X 좌표 (경도)")
    private Double dirX;
    @Schema(description = "Y 좌표 (위도)")
    private Double dirY;
    @Schema(description = "주소")
    private String address;
    @Schema(description = "너비")
    private Double width;
    @Schema(description = "작업 상태")
    private Status status;
    @Schema(description = "지역 구")
    private String area;
    @Schema(description = "지역 동")
    private String location;
    @Schema(description = "Damage 타입")
    private String dtype;
    @Schema(description = "생성 일자")
    private LocalDateTime createdDateTime;
    @Setter
    @Schema(description = "Damage 이미지 리스트")
    private List<ImagesResponseDTO> imagesResponseDTOS;

    @Builder
    @QueryProjection
    public DamageResponseDTO(Long id, Integer severity, Double dirX, Double dirY, String address, Double width, Status status, String area, String location, String dtype, LocalDateTime createdDateTime, List<ImagesResponseDTO> imagesResponseDTOS) {
        this.id = id;
        this.severity = severity;
        this.dirX = dirX;
        this.dirY = dirY;
        this.address = address;
        this.width = width;
        this.status = status;
        this.area = area;
        this.location = location;
        this.dtype = dtype;
        this.createdDateTime = createdDateTime;
        this.imagesResponseDTOS = imagesResponseDTOS;
    }

    @Builder
    public DamageResponseDTO(Long id, Integer severity, Double dirX, Double dirY, String address, Double width, Status status, String area, String location, String dtype, LocalDateTime createdDateTime) {
        this.id = id;
        this.severity = severity;
        this.dirX = dirX;
        this.dirY = dirY;
        this.address = address;
        this.width = width;
        this.status = status;
        this.area = area;
        this.location = location;
        this.dtype = dtype;
        this.createdDateTime = createdDateTime;
    }

}
