<template>
  <table>
    <thead>
      <tr>
        <th class="delete-column"></th>
        <th>종류</th>
        <th>위험성</th>
        <th>행정동</th>
        <th class="address-column">지번</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="taskDetail in props.task" :key="taskDetail.id">
        <td class="close-cursor" @click="removeTask(taskDetail.taskId)">
          <span class="close-button">&times;</span>
        </td>
        <td>
          {{ taskDetail.status }}
        </td>
        <td class="dangers-column">
          <div class="danger-type" :class="dangerClass(taskDetail.severity)">
            <p>{{ dangerClass2(taskDetail.severity) }}</p>
          </div>
        </td>
        <td>{{ taskDetail.location }}</td>
        <td>{{ taskDetail.address }}</td>
      </tr>
    </tbody>
  </table>
</template>

<script setup>
import { ref } from "vue";
import { patchPothole } from "../../../api/task/taskDetail";
import { useAuthStore } from "@/stores/user";
import { storeToRefs } from "pinia";
import { useSwal } from "../../../composables/useSwal";

const store2 = useAuthStore();
const { accessToken, coordinates } = storeToRefs(store2);
const swal = useSwal();

const props = defineProps({
  task: Object,
});
const emit = defineEmits(["updateDetail"]);

const showAlert = () => {
  swal({
    title: "프로젝트에서 해당 도로 파손데이터가 제외되었습니다",
    icon: "success",
    confirmButtonText: "확인",
    width: "700px",
  });
};

const deletePothole = (potholeId) => {
  const potholeData = ref({
    taskId: potholeId,
    origin: coordinates.value,
  });

  patchPothole(
    accessToken.value,
    potholeData.value,
    (res) => {
      if (res.data.status == "SUCCESS") {
        showAlert();
        // console.log(res.data.message);
        emit("updateDetail");
      }
    },
    (error) => {
      console.log(error);
    }
  );
};

// 위험성 필터링
const dangerClass = (danger) => {
  switch (danger) {
    case 3:
      return "serious";
    case 2:
      return "cautious";
    case 1:
      return "safe";
    default:
      return "";
  }
};

const dangerClass2 = (danger) => {
  switch (danger) {
    case 3:
      return "심각";
    case 2:
      return "주의";
    case 1:
      return "양호";
    default:
      return "";
  }
};

// 작업 리스트 삭제
const removeTask = (id) => {
  deletePothole(id);
};
</script>

<style scoped>
.serious {
  background-color: #f5172d;
}

.cautious {
  background-color: #ffb700bf;
}

.safe {
  background-color: #008a1e75;
}

table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
}

td {
  border-top: 1px solid #dddddda1;
  border-bottom: 1px solid #dddddda1;
  border-left: none;
  border-right: none;
  text-align: center;
  padding: 1.7vh;
  font-size: 1.9vh;
  color: #373737;
  background-color: #f1f1f170;
}

thead th {
  position: sticky;
  top: 0;
  background-color: #d3d5ed;
  z-index: 0;
  padding: 1vh 1vh;
  font-size: 1.7vh;
  color: #6c6c6c;
}

tbody tr:hover {
  background-color: #dddddd44;
}

.danger-column {
  width: 5vw;
  text-align: center;
  white-space: nowrap;
}

.address-column {
  width: 14vw;
  text-align: center;
  white-space: nowrap;
}

.danger-type {
  display: inline-block;
  width: 35px;
  height: 35px;
  border-radius: 100%;
  color: #ffffff;
  font-size: 13px;
  font-weight: bold;
  line-height: 42px;
}

p {
  margin: -3px;
}

.delete-column {
  width: 50px;
  min-width: 50px;
  text-align: center;
  white-space: nowrap;
}

.button {
  padding: 5px 10px;
  background-color: #f0f0f0;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
  height: 37.78px;
}

.button:hover {
  background-color: #e1e1e1;
}

.remove-button {
  cursor: pointer;
}

.close-button {
  color: #cd0404;
  font-weight: bold;
  font-size: 2.8vh;
}

.close-button:hover {
  color: #8f0000;
}

.close-cursor {
  cursor: pointer;
}
</style>
