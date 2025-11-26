package com.lms.urbangreen.urbangreenproject.ai.suggestAI.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.lms.urbangreen.urbangreenproject.ai.suggestAI.dto.AiPlanner;
import com.lms.urbangreen.urbangreenproject.ai.suggestAI.repository.AiPlannerRepository;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
public class AiPlannerService {

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final AiPlannerRepository aiPlannerRepository;
    private final Parser parser;
    private final HtmlRenderer renderer;

    public AiPlannerService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper, AiPlannerRepository aiPlannerRepository) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
        this.aiPlannerRepository = aiPlannerRepository;

        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create()));
        this.parser = Parser.builder(options).build();
        this.renderer = HtmlRenderer.builder(options).build();
    }

    // ⭐ 시스템 프롬프트 템플릿 (함수 호출 및 링크 생성 지침 강화)
    private static final String SYSTEM_PROMPT =
            "[SYSTEM PROMPT]\n" +
                    "당신은 주말농장 LMS 웹사이트의 '주말농장 성공 플래너 AI'입니다. 당신의 목표는 사용자에게 가장 성공 가능성이 높고 흥미로운 텃밭 설계 및 초기 재배 계획을 제공하는 것입니다.\n\n" +
                    "응답 스타일:\n" +
                    "톤 앤 매너: 친근하고 긍정적으로 학습과 재배에 대한 동기 부여를 합니다.\n" +
                    "출력 형식: 사용자에게 제공해야 할 4가지 데이터(최적 텃밭 모델, 추천 작물, 재료 리스트, 추천 강의)를 명확한 구조로 제시합니다.\n" +
                    "한국어로 답변합니다.\n\n" +
                    "**[⭐ 사용자 입력 오류 처리 및 대응 로직] (필수 준수)**\n" +
                    "사용자가 '선호 작물' 필드에 비정상적인 입력(초성 나열, 작물과 무관한 단어, 오타)을 했을 경우, 다음 규칙에 따라 처리하고 '추천 작물' 항목에 그 사실을 언급해야 합니다.\n" +
                    "1. **무의미한 입력 (초성/무관 단어):** 'ㅇㄴㅁㄹ', '초콜릿', '자동차' 등 작물과 관계없거나 의미를 알 수 없는 단어라면, 해당 단어는 무시하고 **나머지 사용자 정보(목적, 경험 수준 등)**를 기반으로 가장 적합한 작물을 추천하세요. 추천 작물 항목 첫머리에 **'고객님께서 입력하신 단어 중 일부(예: 초콜릿, ㅇㄴㅁㄹ)는 텃밭 작물로 판단하기 어려워 제외하고 추천했습니다.'**라고 명확히 언급하세요.\n" +
                    "2. **명백한 오타:** '밑느'처럼 일반적인 작물 이름의 오타로 판단된다면, 이를 **정상적인 단어(예: '민트')**로 교정하여 계획에 반영하세요. 추천 작물 항목 첫머리에 **'고객님께서 입력하신 단어(예: 밑느)를 민트의 오타로 판단하여 계획에 반영했습니다.'**라고 명확히 언급하세요.\n" +
                    "3. **취소선 마크다운 금지:** 답변 시 **취소선 마크다운(`~~`)을 절대 사용하지 마세요.** 필요한 경우 **볼드체(`**텍스트**`)**나 목록(`* 텍스트`)만 사용하고, 제목 이외의 글자에 `#` 등의 특수 기호를 임의로 삽입하지 마세요.\n\n" +

                    "[처리 로직 및 근거]\n" +
                    "AI 제공 데이터 | 사용자가 제공한 근거 데이터 | 분석 및 도출 목표\n" +
                    "--- | --- | ---\n" +
                    "1. 최적 텃밭 규모, 모델 | 거주 형태, 베란다/옥상/마당 여부, 일조량, 텃밭 목적 | 공간 제약과 광량에 맞는 현실적인 텃밭 타입(수직, 화분, 평상형 등)과 크기를 제안\n" +
                    "2. 추천 작물 종류 | 텃밭 목적, 일조량, 선호 작물 종류, 예산, 경험수준 | 성공률과 목적 달성도를 최대화하는 1~5가지 작물 포트폴리오(난이도, 수확 속도 고려)를 구성\n" +
                    "3. 초기 투자 재료 리스트 | 예산, 경험 수준, 텃밭 목적, 선호 작물 종류 | 예산 범위 내에서 '필수 도구', '흙/비료', '씨앗/모종'을 구분하여 최소한의 초기 투자 리스트를 제공\n" +
                    "4. LMS 추천 강의 | AI가 제공한 3가지 데이터(1, 2, 3)를 종합하여 추출\n **[⭐ 중요] 강의 추천 시 반드시 제공된 [search_lectures] 도구를 사용하여 DB에 존재하는 강의를 찾아야 합니다. 도구 검색 결과가 반환되었다면, 어떤 이유로든 일반적인 텍스트로 대체하지 말고, 반드시 해당 결과를 포함하여 상세한 강의 추천 목록을 마크다운 형식으로 제시해야 합니다.** | 추천 텃밭 모델, 작물, 경험 수준에 맞는 필수 학습 콘텐츠를 추천\n" +
                    "**추천 강의 링크 형식: [강의 제목 보러가기](/lecture/lectureDetail?lectureId={lecture_id})**\n\n" +

                    "**[⭐ 최종 답변 생성 지침] (필수 준수)**\n" +
                    "1. **최종 응답 생성 시, 첫 번째 응답에서 생성했던 '## 1. 최적 텃밭 모델', '## 2. 추천 작물', '## 3. 초기 투자 재료 리스트'의 내용은 그대로 유지하고, 이어서 '## 4. LMS 추천 강의' 항목을 완성해야 합니다.**\n" +
                    "2. 최종 결과는 반드시 이전에 생성된 모든 텍스트와 LMS 강의 추천 목록을 포함하는 **하나의 완전한 마크다운 문서**여야 합니다.\n\n" +

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
     */
    public String generatePlanner(Map<String, String> userData) {
        try {
            // 1. 초기 메시지 구성 (System Prompt + User Data)
            String userMessage = createUserInfoString(userData);

            // 2. 요청 본문 생성 (Tools 포함)
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(
                    Map.of("role", "user", "parts", List.of(Map.of("text", SYSTEM_PROMPT + "\n\n" + userMessage)))
            ));

            // ⭐ Function Declaration (함수 정의)
            requestBody.put("tools", List.of(
                    Map.of("function_declarations", List.of(
                            Map.of(
                                    "name", "search_lectures",
                                    "description", "LMS 데이터베이스에서 키워드로 강의를 검색합니다. 사용자의 관심사나 작물명으로 검색하세요.",
                                    "parameters", Map.of(
                                            "type", "OBJECT",
                                            "properties", Map.of(
                                                    "keyword", Map.of("type", "STRING", "description", "검색할 키워드 (예: 토마토, 텃밭, 기초)"),
                                                    "category", Map.of("type", "STRING", "description", "카테고리 (gardening, field, house 중 택 1 또는 빈값)")
                                            ),
                                            "required", List.of("keyword")
                                    )
                            )
                    ))
            ));

            // 3. 첫 번째 API 호출
            JsonNode initialResponse = callGeminiApi(requestBody);

            // 4. 함수 호출 요청이 있는지 확인
            JsonNode candidates = initialResponse.path("candidates").get(0);
            JsonNode content = candidates.path("content");
            JsonNode partsNode = content.path("parts");

            if (partsNode.isMissingNode() || !partsNode.isArray()) {
                // parts 배열이 없는 경우 (예: content 자체가 단일 텍스트인 경우)
                // 이 상황은 API 응답 구조가 예상과 다름을 의미하므로,
                // 단일 텍스트가 있을 것으로 가정하고 처리하는 방어 로직이 필요합니다.

                // 만약 이 상황이 함수 호출 없이 텍스트만 온 경우라면 아래 else 로직으로 이동해야 합니다.
                // 그러나 MissingNode 오류는 parts가 아예 없을 때 발생하므로,
                // 이 경우에도 단일 텍스트만 있는지 확인해 봅니다.

                if (content.path("text").isTextual()) {
                    // content가 parts 없이 바로 text를 포함하는 경우 (매우 드문 경우)
                    return renderMarkdownToHtml(content.path("text").asText());
                } else {
                    // 예측할 수 없는 응답 구조인 경우
                    System.err.println("Gemini 응답 구조 오류: candidates[0].content에 parts 배열이 없습니다.");
                    return "AI 플래너 생성 중 응답 구조 오류가 발생했습니다.";
                }
            }

            ArrayNode parts = (ArrayNode) partsNode;

            // 함수 호출(Function Call)이 하나라도 있는지 확인
            if (parts.findValue("functionCall") != null) {

                // ArrayNode에서 1차 텍스트 콘텐츠 추출 (for-each 루프 사용)
                String initialText = "";
                for (JsonNode part : parts) { // ArrayNode는 Iterable을 구현하므로 for-each 사용 가능
                    if (part.has("text")) {
                        initialText = part.get("text").asText();
                        break; // 텍스트를 찾았으면 반복문 종료
                    }
                }

                List<Map<String, String>> functionResults = new ArrayList<>();

                // 모든 functionCall을 순회하며 실행 (for-each 루프 사용)
                for (JsonNode part : parts) {
                    if (part.has("functionCall")) {
                        JsonNode functionCall = part.get("functionCall");
                        String functionName = functionCall.get("name").asText();

                        // DB 검색 실행 및 결과 저장
                        String functionResultJson = executeFunction(functionName, functionCall.get("args"));

                        functionResults.add(Map.of(
                                "name", functionName,
                                "response", functionResultJson
                        ));
                    }
                }

                // 5. 함수 결과 전체를 포함하여 두 번째 API 호출
                return sendFunctionResultAndGetFinalResponse(requestBody, content, functionResults, initialText);
            } else {
                // 함수 호출 없이 바로 텍스트가 온 경우 (그대로 렌더링)
                return renderMarkdownToHtml(parts.path(0).path("text").asText());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "AI 플래너 생성 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    // 헬퍼: 사용자 정보 문자열 생성 (키워드 유도 질문 추가)
    private String createUserInfoString(Map<String, String> userData) {
        String userInfo = String.format(
                "--- [사용자 정보] ---\n거주: %s\n공간: %s\n일조량: %s\n목적: %s\n선호작물: %s\n예산: %s\n경험: %s\n\n위 정보를 바탕으로 플래너를 작성하고, 적절한 강의를 도구를 사용해 추천해주세요.",
                userData.getOrDefault("residenceType", "아파트"),
                userData.getOrDefault("space", "베란다"),
                userData.getOrDefault("sunlight", "보통"),
                userData.getOrDefault("purpose", "식용"),
                userData.getOrDefault("preference", "없음"),
                userData.getOrDefault("budget", "5만원"),
                userData.getOrDefault("experienceLevel", "초보")
        );

        // AI가 강의 검색을 위한 키워드를 쉽게 찾도록 유도
        return userInfo + "\n\n현재 계획에 가장 적합한 핵심 키워드(예: 작물명, 난이도, 텃밭 종류)를 2개 이상 선정하여 search_lectures 도구를 호출하세요.";
    }

    // 헬퍼: 실제 Java 메서드 실행 (DB 조회)
    private String executeFunction(String name, JsonNode args) {
        if ("search_lectures".equals(name)) {
            String keyword = args.path("keyword").asText();
            String category = args.path("category").asText("");

            // Repository 호출
            List<Map<String, Object>> lectures = aiPlannerRepository.searchLectures(keyword, category);

            try {
                // DB에서 가져온 강의 리스트 (JSON Array 형태)를 문자열로 반환
                return objectMapper.writeValueAsString(lectures);
            } catch (Exception e) {
                return "[]";
            }
        }
        return "[]";
    }

    // 헬퍼: 함수 결과를 AI에게 보내고 최종 답변 받기 (다중 함수 결과 처리 및 텍스트 조합)
    private String sendFunctionResultAndGetFinalResponse(Map<String, Object> originalRequest, JsonNode assistantContent, List<Map<String, String>> functionResults, String initialText) {
        try {
            // 기존 contents 리스트를 가져옵니다.
            List<Object> contents = new ArrayList<>((List<Object>) originalRequest.get("contents"));

            // 1. 모델의 질문(함수 호출 요청)을 히스토리에 추가 (role: model)
            contents.add(objectMapper.convertValue(assistantContent, Map.class));

            // ⭐ 다중 함수 결과를 담을 List<Map<String, Object>>
            List<Map<String, Object>> functionParts = new ArrayList<>();
            for (Map<String, String> result : functionResults) {
                functionParts.add(
                        Map.of(
                                "functionResponse", Map.of(
                                        "name", result.get("name"),
                                        "response", Map.of("content", result.get("response")) // DB 결과 JSON 문자열을 content에 담음
                                )
                        )
                );
            }

            // 2. 함수 실행 결과 전체를 히스토리에 추가 (role: function)
            Map<String, Object> functionResponsePart = Map.of(
                    "role", "function",
                    "parts", functionParts // 다중 결과를 담은 parts 리스트 사용
            );

            contents.add(functionResponsePart); // 2단계 히스토리 추가

            // 요청 업데이트: 새로운 contents 히스토리로 교체
            originalRequest.put("contents", contents);

            // 3. 최종 호출 (함수 결과 포함)
            JsonNode finalResponse = callGeminiApi(originalRequest);

            // 4. 최종 텍스트 추출 (LMS 추천 링크 포함된 부분)
            String finalLmsText = finalResponse.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

            // 5. ⭐ 1차 텍스트와 2차 LMS 텍스트를 조합
            String combinedText = initialText + "\n" + finalLmsText;

            return renderMarkdownToHtml(combinedText);

        } catch (Exception e) {
            e.printStackTrace();
            return "최종 결과 생성 실패: " + e.getMessage();
        }
    }

    // 헬퍼: WebClient 호출 공통화 (디버깅 코드 제거됨)
    private JsonNode callGeminiApi(Map<String, Object> requestBody) {
        try {
            JsonNode responseNode = webClient.post()
                    .uri(apiUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    // 상태 코드가 4xx/5xx일 때 상세 에러 메시지를 포함하는 RuntimeException 발생
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .map(errorBody -> new RuntimeException("API 응답 오류: " + clientResponse.statusCode() + ", Body: " + errorBody)))
                    .bodyToMono(JsonNode.class)
                    .block();

            return responseNode;

        } catch (RuntimeException e) {
            System.err.println("API 통신 중 심각한 오류 발생: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("JSON 출력 또는 처리 중 오류 발생: " + e.getMessage());
            throw new RuntimeException("JSON 처리 오류", e);
        }
    }

    // 헬퍼: 마크다운 -> HTML 변환
    private String renderMarkdownToHtml(String markdown) {
        if (markdown == null) return "";

        // 1. 마크다운을 HTML로 렌더링
        String html = renderer.render(parser.parse(markdown));

        // 2. ⭐ HTML에서 AI가 생성한 링크를 찾아 JavaScript 함수 호출 로직 주입

        // 정규 표현식: href="/lecture/lectureDetail?lectureId=숫자" 형태의 링크를 찾습니다.
        // 그룹 1: 강의 제목
        // 그룹 2: 강의 ID
        String regex = "<a\\s+href=\"/lecture/lectureDetail\\?lectureId=(\\d+)\">([^<]+)</a>";

        // 대체 문자열: 기존 href 대신 JavaScript 함수 호출을 주입합니다.
        // href를 #으로 변경하고, onclick에 handleCourseClick(ID, '제목', false)를 주입합니다.
        // (AI 플래너의 링크는 항상 구독하지 않은 상태를 가정하고 구독 확인을 시켜야 하므로 isSubscribed는 false로 고정)
        // 강의 제목('그룹 2')에는 따옴표 처리가 필요합니다. (\'는 작은 따옴표 이스케이프)
        String replacement =
                "<a href=\"javascript:void(0);\" " + // href를 무의미하게 변경
                        "onclick=\"handleCourseClick($1, '$2', false); return false;\">" + // JS 함수 호출 주입
                        "$2</a>"; // 강의 제목 ($2) 유지

        // 정규식 치환 적용
        String finalHtml = html.replaceAll(regex, replacement);

        return finalHtml;
    }

    // ai플래너 저장
    public void savePlannerResult(String userId, String plannerName, String content) {
        AiPlanner planner = AiPlanner.builder().userId(userId).plannerName(plannerName).plannerContent(content).build();
        aiPlannerRepository.savePlanner(planner);
    }

    public List<AiPlanner> getSavedPlanners(String userId) {
        return aiPlannerRepository.findAllByUserId(userId);
    }

    public boolean deletePlanner(Long plannerId, String userId) {
        // deletePlanner는 삭제된 행의 개수를 반환합니다. 1이면 성공, 0이면 실패입니다.
        return aiPlannerRepository.deletePlanner(plannerId, userId) > 0;
    }
}