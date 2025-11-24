document.addEventListener('DOMContentLoaded', () => {

    // ==============================================
    // 원본 코드의 헤더 스크롤 효과 (JS)
    // ==============================================
    const header = document.getElementById('header');
    if (header) {
        window.addEventListener('scroll', () => {
            if (window.scrollY > 50) {
                header.classList.add('scrolled');
            } else {
                header.classList.remove('scrolled');
            }
        });
    }

    // ==============================================
    // 기존 AI 진단 페이지 스크립트 시작
    // ==============================================

    // 1. 파일 Input 및 상태 표시 요소
    const photoInput = document.getElementById('photoInput');
    const fileNameDisplay = document.getElementById('file-name-display');
    const previewImage = document.getElementById('previewImage');

    // 파일 Input Change 이벤트 리스너
    if (photoInput && fileNameDisplay && previewImage) {
        photoInput.addEventListener('change', function(e) {
            const files = e.target.files;

            // 파일명 표시
            var fileName = files.length > 0 ? files[0].name : '선택된 파일 없음';
            fileNameDisplay.textContent = fileName;

            // 미리보기 업데이트
            if (files.length > 0) {
                const reader = new FileReader();
                reader.onload = function(event) {
                    previewImage.src = event.target.result;
                };
                reader.readAsDataURL(files[0]);
            }
        });
    } else {
        console.error("오류: AI 진단 스크립트의 필수 요소 중 일부가 누락되었습니다.");
    }

    // 로딩 오버레이 요소의 존재 여부 확인
    const overlay = document.getElementById('loading-overlay');
    if (!overlay) {
        console.error("오류: loading-overlay 요소를 HTML에서 찾을 수 없습니다.");
    }

    // ----------------------------------------------
    // AI 진단 페이지 스크립트 끝 (uploadAndIdentifyPlantID 함수는 전역으로 유지)
    // ----------------------------------------------
});


// 로딩 화면 표시/숨김 함수
function toggleLoading(show) {
    const overlay = document.getElementById('loading-overlay');
    if (overlay) {
        if (show) {
            overlay.classList.remove('d-none');
        } else {
            overlay.classList.add('d-none');
        }
    }
}

// 식물 진단 API 호출 및 처리 함수
function uploadAndIdentifyPlantID() {
    const photoInput = document.getElementById("photoInput");
    const resultSection = document.getElementById('result');

    // 1. 파일 선택 여부 확인
    if (!photoInput || photoInput.files.length === 0) {
        alert("사진을 선택해 주세요.");
        return;
    }

    const selectedFile = photoInput.files[0];
    const reader = new FileReader();

    // ⚠️ Plant.ID API Key (보안상 주의 필요)
    const apiKey = "mSdAv442nCQq7qmJGDSUwAIX9dl6yRt38YYwYUG83yKEGLA0tX";
    const latitude = 37.51;
    const longitude = 127.04;
    const health = "all";
    const similarImages = true;
    const language = "ko";
    const details = "common_names,url,description,taxonomy,rank,gbif_id,inaturalist_id,image,synonyms,edible_parts,watering,propagation_methods,treatment,cause";
    const apiUrlPlantID = `https://plant.id/api/v3/identification?details=${details}&language=${language}`;

    reader.onload = function (e) {
        const base64Image = e.target.result.split(',')[1];

        // 2. 로딩 화면 표시 및 결과 섹션 숨김
        toggleLoading(true);
        if (resultSection) {
            resultSection.classList.add('hidden');
        }

        axios
            .post(
                apiUrlPlantID,
                {
                    images: [base64Image],
                    latitude: latitude,
                    longitude: longitude,
                    health: health,
                    similar_images: similarImages
                },
                {
                    headers: {
                        "Api-Key": apiKey,
                        "Content-Type": "application/json"
                    }
                }
            )
            .then(function (response) {
                toggleLoading(false);
                console.log("Response from Plant ID API:", response.data);
                displayPlantIDInfo(response.data, e.target.result);

                if (resultSection) {
                    resultSection.classList.remove('hidden');
                    resultSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
                }
            })
            .catch(function (error) {
                toggleLoading(false);
                let errorMessage = "❌ AI 진단 중 오류가 발생했습니다 ❌";
                if (error.response) {
                    errorMessage = `오류 발생: HTTP Status ${error.response.status}. 잠시 후 다시 시도해 주세요. (크레딧 소진 또는 서버 문제)`;
                } else if (error.message) {
                    errorMessage = `오류: ${error.message}`;
                }
                alert(errorMessage);
                console.error("Error:", error);
                if (resultSection) {
                    resultSection.classList.add('hidden');
                }
            });
    };

    reader.readAsDataURL(selectedFile);
}


// 진단 결과를 HTML에 표시하는 함수
function displayPlantIDInfo(plantIdResponse, base64Image) {
    // API 응답 구조에서 필요한 데이터만 추출
    const plantIdDisease = plantIdResponse.result.disease;
    const plantIdIsHealthy = plantIdResponse.result.is_healthy;
    const plantIdIsPlant = plantIdResponse.result.is_plant;

    // 모든 필수 HTML 요소 가져오기
    const previewImage = document.getElementById("previewImage");
    const healthStatusContainer = document.getElementById("plant-health-status-container");
    const diseaseStatusTitle = document.getElementById("disease-status-title");
    const diseaseImageContainer = document.getElementById("plant-similiar-image-with-disease");
    const diseaseNameContainer = document.getElementById("plant-disease-name-container");
    const diseaseProbabilityContainer = document.getElementById("plant-disease-probabilty");
    const diseaseDescriptionContainer = document.getElementById("plant-disease-description");
    const diseaseTreatmentContainer = document.getElementById("plant-disease-treatment");

    // 요소 Null 체크
    if (!previewImage || !healthStatusContainer || !diseaseStatusTitle || !diseaseImageContainer || !diseaseNameContainer || !diseaseProbabilityContainer || !diseaseDescriptionContainer || !diseaseTreatmentContainer) {
        console.error("오류: 결과를 출력하는 데 필요한 HTML 요소(ID) 중 일부가 누락되었습니다. HTML 파일을 확인하세요.");
        return;
    }

    // 초기화
    diseaseNameContainer.innerHTML = '';
    diseaseProbabilityContainer.innerHTML = '';
    diseaseDescriptionContainer.innerHTML = '';
    diseaseTreatmentContainer.innerHTML = '';

    // PREVIEW IMAGE
    previewImage.src = base64Image;

    // 1. 식물 여부 확인
    if (plantIdIsPlant.binary === false) {
        alert("업로드된 이미지는 식물이 아닌 것으로 판단됩니다. 다시 시도해 주세요.");
        document.getElementById('result').classList.add('hidden');
        return;
    }

    // 2. 건강 상태 확인 및 출력
    const plantHealthStatus = plantIdIsHealthy.binary;

    if (plantHealthStatus === true) {
        // A. 건강한 경우
        healthStatusContainer.className = 'alert alert-success text-center';
        healthStatusContainer.innerHTML = '<h4><i class="fas fa-heartbeat"></i> **진단 결과: 건강함**</h4>';

        diseaseStatusTitle.textContent = "식물 상태 상세 정보";
        diseaseImageContainer.src = "https://fakeimg.pl/300x200/?text=Healthy%20Plant%F0%9F%8C%B1&font=lobster";
        diseaseNameContainer.innerHTML = `<p><strong>상태:</strong> 매우 건강합니다. 꾸준히 관리해 주세요.</p>`;
        diseaseDescriptionContainer.innerHTML = `<p>현재 식물은 건강한 상태입니다. 정기적인 관찰과 관수를 통해 최상의 상태를 유지해주세요.</p>`;

    } else {
        // B. 건강하지 않은 경우 (문제 발견)
        const diseaseSuggestion = (plantIdDisease.suggestions && plantIdDisease.suggestions.length > 0)
            ? plantIdDisease.suggestions[0]
            : null;

        healthStatusContainer.className = 'alert alert-danger text-center';

        if (diseaseSuggestion) {
            // 메인 진단 결과에 질병 이름을 포함
            healthStatusContainer.innerHTML = `<h4><i class="fas fa-exclamation-triangle"></i> **진단 결과: ${diseaseSuggestion.name}**</h4>`;
            diseaseStatusTitle.textContent = "병충해 진단 상세 내용";

            // 상세 질병 정보 출력
            diseaseImageContainer.src = diseaseSuggestion.similar_images[0].url;

            // 한글 항목명으로 통일
            diseaseNameContainer.innerHTML = `<p><strong>질병/해충 종류:</strong> ${diseaseSuggestion.name}</p>`;
            // 진단 확률은 백분율로 표시
            diseaseProbabilityContainer.innerHTML = `<p><strong>진단 확률:</strong> ${(diseaseSuggestion.probability.toFixed(4) * 100).toFixed(1)}%</p>`;

            const plantDiseaseDescription = diseaseSuggestion.details.description ? diseaseSuggestion.details.description : "상세 설명이 제공되지 않습니다.";
            diseaseDescriptionContainer.innerHTML = `<p><strong>상세 설명:</strong> ${plantDiseaseDescription}</p>`;

            // 치료/관리 방법 (Treatment)
            const plantDiseaseTreatment = diseaseSuggestion.details.treatment;
            diseaseTreatmentContainer.innerHTML = '<strong>치료 및 관리 방법:</strong>';

            if (Object.keys(plantDiseaseTreatment).length === 0) {
                diseaseTreatmentContainer.innerHTML += `<p>특정 치료 정보가 제공되지 않습니다.</p>`;
            } else {
                for (const key in plantDiseaseTreatment) {
                    if (plantDiseaseTreatment.hasOwnProperty(key)) {
                        let translatedKey;
                        // Treatment 키 한글화
                        switch (key) {
                            case 'biological': translatedKey = '생물학적 방제'; break;
                            case 'chemical': translatedKey = '화학적 처리'; break;
                            case 'prevention': translatedKey = '예방 조치'; break;
                            case 'symptoms': translatedKey = '일반 증상'; break;
                            case 'common_names': translatedKey = '일반 이름'; break;
                            default: translatedKey = key.charAt(0).toUpperCase() + key.slice(1);
                        }

                        const plantDiseaseTreatmentValues = plantDiseaseTreatment[key]
                            .map((value) => `<li>${value}</li>`)
                            .join("");
                        const plantDiseaseTreatmentText = `<div class="mt-2"><strong>- ${translatedKey}:</strong> <ul>${plantDiseaseTreatmentValues}</ul></div>`;
                        diseaseTreatmentContainer.innerHTML += plantDiseaseTreatmentText;
                    }
                }
            }
        } else {
            // 명확한 질병 제안이 없는 경우
            healthStatusContainer.innerHTML = '<h4><i class="fas fa-exclamation-triangle"></i> **진단 결과: 문제 발견 (원인 불명확)**</h4>';
            diseaseStatusTitle.textContent = "병충해 진단 상세 내용";

            diseaseImageContainer.src = "https://fakeimg.pl/300x200/?text=Unknown%20Issue%F0%9F%99%83&font=lobster";
            diseaseNameContainer.innerHTML = `<p><strong>상태:</strong> 원인 불명확. 추가 진단이나 전문가의 도움이 필요합니다.</p>`;
        }
    }

}
