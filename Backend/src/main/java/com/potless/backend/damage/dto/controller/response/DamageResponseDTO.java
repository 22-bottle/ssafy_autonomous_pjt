package com.potless.backend.damage.dto.controller.response;

import com.potless.backend.damage.entity.enums.Status;
import com.potless.backend.damage.entity.road.ImageEntity;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DamageResponseDTO {

    private Long id;
    private Integer severity;
    private Double dirX;
    private Double dirY;
    private String address;
    private String roadName;
    private Double width;
    private Status status;
    private String area;
    private String location;
    private List<ImagesResponseDTO> imagesResponseDTOS;
    private String dtype;

    @Builder
    @QueryProjection
    public DamageResponseDTO(Long id, Integer severity, Double dirX, Double dirY, String address, String roadName, Double width, Status status, String area, String location, List<ImagesResponseDTO> imagesResponseDTOS, String dtype) {
        this.id = id;
        this.severity = severity;
        this.dirX = dirX;
        this.dirY = dirY;
        this.address = address;
        this.roadName = roadName;
        this.width = width;
        this.status = status;
        this.area = area;
        this.location = location;
        this.imagesResponseDTOS = imagesResponseDTOS;
        this.dtype = dtype;
    }

    @Builder
    public DamageResponseDTO(Long id, Integer severity, Double dirX, Double dirY, String address, String roadName, Double width, Status status, String area, String location, String dtype) {
        this.id = id;
        this.severity = severity;
        this.dirX = dirX;
        this.dirY = dirY;
        this.address = address;
        this.roadName = roadName;
        this.width = width;
        this.status = status;
        this.area = area;
        this.location = location;
        this.dtype = dtype;
    }

    public void setImagesResponseDTOS(List<ImageEntity> imageEntities) {
        this.imagesResponseDTOS = imageEntities.stream()
                .map(image -> new ImagesResponseDTO(image.getId(), image.getUrl(), image.getOrder()))
                .collect(Collectors.toList());
    }
}