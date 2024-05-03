import { localAxios } from "@/util/http-commons";

const local = localAxios();

// 포트홀 전체 리스트 조회(검색 쿼리 사용)
const getPotholeList = async (accessToken, queryParams, success, fail) => {
  await local
    .get(`/damage`, {
      params: {
        queryParams,
        size: 10,
      },
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    })
    .then(success)
    .catch(fail);
};

// 포트홀 정보 상세 조회
const getPotholeDetail = async (accessToken, damageId, success, fail) => {
  await local
    .get(`/damage/${damageId}`, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    })
    .then(success)
    .catch(fail);
};

// 포트홀 정보 삭제
const deletePothole = async (accessToken, damageId, success, fail) => {
  await local
    .put(`/damage/${damageId}`, {
      headers: {
        Authorization: accessToken,
      },
    })
    .then(success)
    .catch(fail);
};

export { getPotholeList, getPotholeDetail, deletePothole };