package com.lms.urbangreen.urbangreenproject.ai.suggestAI.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladsch.flexmark.util.ast.Node;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import java.util.Map;

@Service
public class AiPlannerService {

    // application.properties에서 주입받는 API URL (Key 포함)
    @Value("${gemini.api.url}")
    private String apiUrl;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    // 마크다운 변환을 위한 변수 추가
    private final Parser parser;
    private final HtmlRenderer renderer;

    public AiPlannerService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;

        MutableDataSet options = new MutableDataSet();

        // Flexmark에게 마크다운 테이블 구문을 파싱하도록 명시적으로 지시
        //  TablesExtension을 추가하는 핵심 로직
        options.set(Parser.EXTENSIONS, java.util.Arrays.asList(TablesExtension.create()));

        this.parser = Parser.builder(options).build();
        this.renderer = HtmlRenderer.builder(options).build();
    }

    // ⭐ 시스템 프롬프트 템플릿
    private static final String SYSTEM_PROMPT =
            "[SYSTEM PROMPT]\n" +
                    "당신은 주말농장 LMS 웹사이트의 '주말농장 성공 플래너 AI'입니다. 당신의 목표는 사용자에게 가장 성공 가능성이 높고 흥미로운 텃밭 설계 및 초기 재배 계획을 제공하는 것입니다.\n\n" +
                    "응답 스타일:\n" +
                    "톤 앤 매너: 친근하고 긍정적으로 학습과 재배에 대한 동기 부여를 합니다.\n" +
                    "출력 형식: 사용자에게 제공해야 할 4가지 데이터(최적 텃밭 모델, 추천 작물, 재료 리스트, 추천 강의)를 명확한 구조로 제시합니다.\n" +
                    "LMS 연동: 최종 강의 추천 시, '유료', '무료' 또는 '챌린지' 등의 키워드를 사용하여 LMS 시스템의 분위기를 반영합니다.\n" +
                    "한국어로 답변합니다.\n\n" +
                    "**[⭐ 사용자 입력 오류 처리 및 대응 로직] (필수 준수)**\n" +
                    "사용자가 '선호 작물' 필드에 비정상적인 입력(초성 나열, 작물과 무관한 단어, 오타)을 했을 경우, 다음 규칙에 따라 처리하고 '추천 작물' 항목에 그 사실을 언급해야 합니다.\n" +
                    "1. **무의미한 입력 (초성/무관 단어):** 'ㅇㄴㅁㄹ', '초콜릿', '자동차' 등 작물과 관계없거나 의미를 알 수 없는 단어라면, 해당 단어는 무시하고 **나머지 사용자 정보(목적, 경험 수준 등)**를 기반으로 가장 적합한 작물을 추천하세요. 추천 작물 항목 첫머리에 **'고객님께서 입력하신 단어 중 일부(예: 초콜릿, ㅇㄴㅁㄹ)는 텃밭 작물로 판단하기 어려워 제외하고 추천했습니다.'**라고 명확히 언급하세요.\n" +
                    "2. **명백한 오타:** '밑느'처럼 일반적인 작물 이름의 오타로 판단된다면, 이를 **정상적인 단어(예: '민트')**로 교정하여 계획에 반영하세요. 추천 작물 항목 첫머리에 **'고객님께서 입력하신 단어(예: 밑느)를 민트의 오타로 판단하여 계획에 반영했습니다.'**라고 명확히 언급하세요.\n" +
                    "3. **취소선 마크다운 금지:** 답변 시 **취소선 마크다운(`~~`)을 절대 사용하지 마세요.** 필요한 경우 **볼드체(`**텍스트**`)**나 목록(`* 텍스트`)만 사용하고, 제목 이외의 글자에 `#`, `~` 등의 특수 기호를 임의로 삽입하지 마세요.\n\n" +

                    "[처리 로직 및 근거]\n" +
                    "AI 제공 데이터 | 사용자가 제공한 근거 데이터 | 분석 및 도출 목표\n" +
                    "--- | --- | ---\n" +
                    "1. 최적 텃밭 규모, 모델 | 거주 형태, 베란다/옥상/마당 여부, 일조량, 텃밭 목적 | 공간 제약과 광량에 맞는 현실적인 텃밭 타입(수직, 화분, 평상형 등)과 크기를 제안\n" +
                    "2. 추천 작물 종류 | 텃밭 목적, 일조량, 선호 작물 종류, 예산, 경험수준 | 성공률과 목적 달성도를 최대화하는 1~5가지 작물 포트폴리오(난이도, 수확 속도 고려)를 구성\n" +
                    "3. 초기 투자 재료 리스트 | 예산, 경험 수준, 텃밭 목적, 선호 작물 종류 | 예산 범위 내에서 '필수 도구', '흙/비료', '씨앗/모종'을 구분하여 최소한의 초기 투자 리스트를 제공\n" +
                    "4. LMS 추천 강의 | AI가 제공한 3가지 데이터(1, 2, 3)를 종합하여 추출 | 추천 텃밭 모델, 작물, 경험 수준에 맞는 필수 학습 콘텐츠(무료/유료/챌린지)를 추천\n\n" +
                    "[최종 출력 형식 지침]\n" +
                    "분석이 완료되면, 아래의 4가지 항목을 각각 h2 레벨 마크다운(\"## 1. ...\")으로 명확하게 제시하세요. 목록과 표를 적절히 사용하여 가독성을 높여주세요.\n\n" +
                    "--- [사용자 정보] ---\n" +
                    "거주 형태: %s\n" +
                    "공간: %s\n" +
                    "일조량: %s\n" +
                    "텃밭 목적: %s\n" +
                    "선호 작물: %s\n" +
                    "예산: %s\n" +
                    "경험 수준: %s\n\n" +
                    "위 정보를 기반으로 4가지 항목(최적 텃밭 모델, 추천 작물, 재료 리스트, LMS 추천 강의)을 [최종 출력 형식 지침]에 따라 **마크다운**으로 상세하게 답변해 주세요.";


    /**
     * 사용자 데이터를 기반으로 Gemini API를 호출하여 텃밭 계획을 생성합니다.
     * @param userData 사용자 입력 데이터 (Map 형태)
     * @return Gemini API 응답 텍스트 (마크다운 형식)
     */
    public String generatePlanner(Map<String, String> userData) {

        // 1. 사용자 데이터를 프롬프트에 삽입하여 최종 프롬프트 구성
        String finalPrompt = String.format(SYSTEM_PROMPT,
                userData.getOrDefault("residenceType", "아파트"),
                userData.getOrDefault("space", "베란다"),
                userData.getOrDefault("sunlight", "오전 3시간 미만"),
                userData.getOrDefault("purpose", "식용"),
                userData.getOrDefault("preference", "없음"),
                userData.getOrDefault("budget", "5만원 이하"),
                userData.getOrDefault("experienceLevel", "초보")
        );

        // 2. Gemini API 요청 본문(JSON) 생성
        JsonNode requestBody;
        try {
            String jsonString = String.format("""
        {
            "contents": [
                {
                    "role": "user",
                    "parts": [
                        {
                            "text": "%s"
                        }
                    ]
                }
            ],
            "generationConfig": {
                "temperature": 0.7,
                "maxOutputTokens": 8192
            }
        }
        """, finalPrompt
                    .replace("\\", "\\\\") // 백슬래시를 먼저 이스케이프
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n"));

            requestBody = objectMapper.readTree(jsonString);

        } catch (Exception e) {
            System.err.println("AI 요청 본문 생성 오류: " + e.getMessage());
            return "AI 요청 본문 생성 오류 발생: " + e.getMessage();
        }

        // 3. WebClient를 사용하여 API 호출 및 마크다운 결과 수신
        String aiResultMarkdown;
        try {
            aiResultMarkdown = webClient.post() //  aiResultMarkdown 변수에 결과 할당
                    .uri(this.apiUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .map(errorBody -> new RuntimeException("API 응답 오류: " + clientResponse.statusCode() + ", Body: " + errorBody)))
                    .bodyToMono(JsonNode.class)
                    .map(jsonResponse -> {
                        // 4. API 응답에서 최종 텍스트 추출 (마크다운)
                        try {
                            return jsonResponse.get("candidates")
                                    .get(0).get("content").get("parts")
                                    .get(0).get("text").asText();
                        } catch (Exception e) {
                            System.err.println("API 응답 파싱 오류: " + e.getMessage() + "\nRaw Response: " + jsonResponse.toString());
                            return "AI 결과 파싱 오류 발생. 원본 응답을 확인하세요.";
                        }
                    })
                    .block();
        } catch (RuntimeException e) {
            System.err.println("API 통신 중 심각한 오류 발생: " + e.getMessage());
            return "API 통신 오류: " + e.getMessage();
        }

        // 5. 수신된 마크다운을 HTML로 변환하여 반환
        if (aiResultMarkdown == null || aiResultMarkdown.contains("오류 발생")) {
            return aiResultMarkdown; // 오류 메시지인 경우 그대로 반환
        }

        // aiResultMarkdown 변수를 사용하여 파싱 및 렌더링
        Node document = parser.parse(aiResultMarkdown);
        String aiResultHtml = renderer.render(document);

        return aiResultHtml; // 최종 HTML 문자열 반환
    }
}