package Potless.Backend.damage.dto.service.response;

import Potless.Backend.damage.dto.service.response.kakao.Document;
import Potless.Backend.damage.dto.service.response.kakao.Meta;
import lombok.*;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoMapApiResponseDTO {

    private Meta meta;
    private List<Document> documents;

}
