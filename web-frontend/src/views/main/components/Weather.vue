<template>
  <div class="main-container" :style="{ backgroundColor: backgroundColor }" v-if="weather">
    <div class="location-box">
      <img class="marker-img" src="../../../assets/icon/location.png" alt="marker" />
      <div class="location">{{ selectedRegionName }}</div>
    </div>
    <div class="info-container">
      <component :is="weatherComponent" class="weather-animation" />
      <div></div>
      <div class="weather-info">
        <div class="temperature">{{ formattedTemperature }}°C</div>
        <div class="cloud">{{ cloudDescription }}</div>
        <div class="wind">풍속: {{ weather.wind.speed }} m/s</div>
      </div>
    </div>
  </div>
  <div class="load-container" v-else><LottieLoading /></div>
</template>
<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useAuthStore } from "@/stores/user";
import { storeToRefs } from "pinia";
import { getWeatherInfo } from "../../../api/weather/weather";
import LottieLoading from "./LottieLoading.vue";
import LottieSunny from "./LottieSunny.vue";
import LottieClouds from "./LottieClouds.vue";
import LottieRain from "./LottieRain.vue";
import LottieSnow from "./LottieSnow.vue";
import LottieThunderstorm from "./LottieThunderstorm.vue";
import LottieFog from "./LottieFog.vue";

const store = useAuthStore();
const { areaId } = storeToRefs(store);

const weather = ref(null);
const selectedRegionName = ref(""); // 지역 이름 저장
const weatherComponent = ref(null);

const coordinates = {
  1: { lat: 36.3511, lon: 127.385, name: "대덕구" }, // 대덕구
  2: { lat: 36.3275, lon: 127.4348, name: "동구" }, // 동구
  3: { lat: 36.3514, lon: 127.4197, name: "중구" }, // 중구
  4: { lat: 36.3623, lon: 127.355, name: "유성구" }, // 유성구
  5: { lat: 36.3515, lon: 127.3868, name: "서구" }, // 서구
  6: { lat: 37.5665, lon: 126.978, name: "서울" }, // 서울 (기타)
};

const backgroundColor = computed(() => {
  if (!weather.value) return "#0095FF";
  switch (weather.value.weather[0].main) {
    case "Clear":
      return "#009ff5";
    case "Clouds":
      return "#83c3e1";
    case "Rain":
      return "#0071B1";
    case "Thunderstorm":
      return "#28728F";
    case "Snow":
      return "#75CCDC";
    case "Mist":
      return "#8ebfc1";
    case "Fog":
      return "#8ebfc1";
    case "Haze":
      return "#78ebfc1";
    default:
      return "#0095FF";
  }
});

// 날씨에 따른 컴포넌트 설정
const updateWeatherComponent = () => {
  if (!weather.value) {
    weatherComponent.value = null;
    return;
  }
  const main = weather.value.weather[0].main;
  switch (main) {
    case "Clear":
      weatherComponent.value = LottieSunny;
      break;
    case "Clouds":
      weatherComponent.value = LottieClouds;
      break;
    case "Rain":
      weatherComponent.value = LottieRain;
      break;
    case "Thunderstorm":
      weatherComponent.value = LottieThunderstorm;
      break;
    case "Snow":
      weatherComponent.value = LottieSnow;
      break;
    case "Mist":
    case "Fog":
    case "Haze":
      weatherComponent.value = LottieFog;
      break;
    default:
      weatherComponent.value = null;
  }
};

onMounted(() => {
  if (areaId.value && coordinates[areaId.value]) {
    const { lat, lon, name } = coordinates[areaId.value];
    selectedRegionName.value = name;
    getWeatherInfo(
      lat,
      lon,
      (response) => {
        weather.value = response.data;
      },
      (error) => {
        console.error("날씨 정보를 불러오는 데 실패했습니다:", error);
      }
    );
  } else {
    console.log("잘못된 areaId 값입니다.");
    selectedRegionName.value = "잘못된 위치";
  }
});

// 온도 포맷팅
const formattedTemperature = computed(() => {
  if (!weather.value) return "";
  const temp = parseFloat(weather.value.main.temp);
  const roundedTemp = temp.toFixed(1);
  // 소수 첫째 자리가 0일 경우 정수 부분만 반환
  return roundedTemp.endsWith(".0") ? Math.round(temp).toString() : roundedTemp;
});

// 구름 양
const cloudDescription = computed(() => {
  const cloudiness = weather.value ? weather.value.clouds.all : 0;
  if (cloudiness <= 10) return "맑음";
  else if (cloudiness <= 25) return "구름 조금";
  else if (cloudiness <= 50) return "구름 약간";
  else if (cloudiness <= 85) return "구름 많음";
  else return "흐림";
});

// 날씨 데이터 변경 감지
watch(weather, updateWeatherComponent);
</script>
<style scoped>
.main-container {
  transition: background-color 0.5s ease;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.255);
  border-radius: 15px;
  padding: 1.5vh;
  display: grid;
  grid-template-rows: 15% 82%;
  gap: 3%;
}
.load-container {
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.255);
  border-radius: 15px;
  background-color: rgba(241, 241, 241, 0.641);
}
.location-box {
  display: flex;
}
.marker-img {
  height: 2.5vh;
}
.location {
  color: white;
  font-size: 1.8vh;
  font-weight: bold;
  margin-left: 5px;
}
.info-container {
  display: grid;
  grid-template-columns: 53% 47%;
}
.weather-img {
  display: flex;
  height: 15vh;
  padding: 0vh 0vh 0vh 1vw;
}
.weather-info {
  color: white;
  margin-left: 20px;
  transform: translateY(-0.5vh);
}
.temperature {
  font-size: 5vh;
  font-weight: bold;
  margin-bottom: 0.8vh;
}
.cloud,
.wind {
  font-size: 1.9vh;
}
</style>
