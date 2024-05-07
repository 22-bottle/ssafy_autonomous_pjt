from fastapi import APIRouter, HTTPException
from fastapi.responses import JSONResponse, StreamingResponse
from typing import List
from services.augmentation_service import color_inverse
from services.calculate_service import calculate_object_scale
from fastapi import FastAPI, File, UploadFile
from fastapi.responses import StreamingResponse
import io
from fastapi.responses import FileResponse
import logging


rec = APIRouter(
    tags=["detection"],
    responses={404: {"description": "Page Not found"}},
)


# 2차 탐지, 1차적으로 탐지된 포트홀을 2차 검증 
@rec.post("/detection", status_code=200, 
            summary="포트홀 2차 탐지 및 위험도 분석 요청 API",
            response_description="2차 탐지로 확인된 포트홀 이미지 데이터, 결과 메시지 반환 ", 
            description="재탐지된 결과에 대해서는 탐지된 이미지 데이터와 위험도 관련 정보를 함께 전송, 아니라면 결과와 메시지만 반환합니다.")
async def detection_confirm(
    image_data: UploadFile = File(...),
    label_data: UploadFile = File(...)
):
    image = await image_data.read()
    label = await label_data.read()

    logging.info("파일 받음요")
    # result = await color_inverse(image, label)
    processed_image_path = await color_inverse(image, label)
    if processed_image_path is None:
        raise HTTPException(status_code=200, detail="이미지 컬러값 변환 처리에 실패하였습니다.") 
    
    # 2차 탐지에 성공했다면, 다음으로 빨간박스가 포함된 원본 이미지를 통해 위험도 분석 거침
    # calculated_result = calculate_object_scale(image)

    def iterfile():
        with open(processed_image_path, mode="rb") as file_like:
            yield from file_like

    return StreamingResponse(iterfile(), media_type="image/jpeg")

    # response = StreamingResponse(iterfile(), media_type="image/jpeg")
    # return {"message": "처리가 성공적으로 완료되었습니다.", "data": response}
