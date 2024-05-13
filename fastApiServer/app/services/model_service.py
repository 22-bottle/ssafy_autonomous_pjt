import torch
import cv2
import os
import numpy as np
from ultralytics import YOLO
from PIL import Image
import logging
from services.estimate_service import calcPotholeDan, calcPotholeWidth
from fastapi import APIRouter, HTTPException

def model_2th_detection(image):

    model = YOLO('./model/pre_processed_model.pt')
    # 감지 실행
    results = model.predict(task="detect", source=image, stream=False, conf=0.5, hide_labels=True, hide_conf=True)

    folder_name = 'model_result/result1'
    os.makedirs(folder_name, exist_ok=True)
    base_filename = os.path.basename(image)

    danger_max = -1
    i = 1

    # 결과에서 감지된 객체 처리
    for result in results:
        if result.boxes:
            for box in result.boxes:
                filename = os.path.join(folder_name, f"{base_filename}_{i}.png")
                result.save(filename=filename)
                i += 1
                # 탐지된 박스가 여러개일 경우를 고려
                box_x, box_y, box_width, h = box.xywh[0]
                pothole_width = calcPotholeWidth(box_y=box_y, box_width=box_width)
                dan_result = calcPotholeDan(pothole_width=pothole_width, box_x=box_x, box_y=box_y, box_width=box_width)
                if(dan_result > danger_max):
                    danger_max = dan_result

        else:
            raise HTTPException(status_code=200, detail="포트홀 2차 탐지 결과 발견하지 못했습니다.") 
        
    logging.info("Objects detected:", i)
    return danger_max

# model_2th_detection('./app/origin3.jpg')