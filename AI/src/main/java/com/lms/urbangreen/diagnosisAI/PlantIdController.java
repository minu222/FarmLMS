package com.lms.urbangreen.diagnosisAI;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PlantIdController {

    // JS가 직접 외부 API 호출(AJAX) 컨트롤러 -> 서비스 부분 필요없음

    // 단순 html 연결
    @GetMapping("/diagnosisPlant")
    public String identificationForm() {
        return "plant_diagnosis";
    }

}