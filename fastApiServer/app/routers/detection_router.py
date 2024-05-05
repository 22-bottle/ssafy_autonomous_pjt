from fastapi import APIRouter, HTTPException
from fastapi.responses import JSONResponse, StreamingResponse
from typing import List
from services.augmentation_service import color_inverse
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
            summary="포트홀 2차 탐지 요청 API(현재는 전처리과정만 포함)",
            response_description="2차 탐지로 확인된 포트홀 이미지 데이터, 결과 메시지 반환 ", 
            description="재탐지된 결과에 대해서는 탐지된 이미지 데이터를 함께 전송, 아니라면 결과와 메시지만 반환합니다.")
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
    
    def iterfile():
        with open(processed_image_path, mode="rb") as file_like:
            yield from file_like

    return StreamingResponse(iterfile(), media_type="image/jpeg")

    # response = StreamingResponse(iterfile(), media_type="image/jpeg")
    # return {"message": "처리가 성공적으로 완료되었습니다.", "data": response}
