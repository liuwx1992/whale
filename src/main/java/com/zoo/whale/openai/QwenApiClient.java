package com.zoo.whale.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;

/**
 * @Date: 2026年 03月 04日
 * @Time:19时:34分
 * @Author: weixin.liu
 * @Version:V1.0
 * sk-79420ba6d26345b3886b1b5ce5e4a971
 */
public class QwenApiClient {
    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
    private static final String API_KEY = "sk-79420ba6d26345b3886b1b5ce5e4a971"; // 替换为你的 API Key

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 简单估算 token 数量（实际应使用 tokenizer，见下文说明）
    public int estimateTokenCount(String text) {
        // 粗略估算：1 token ≈ 4 个英文字符 或 1.5 个中文字符
        return (int) Math.ceil(text.length() / 1.5);
    }

    public String callQwen(String prompt, int maxTokens) throws IOException {
        int promptTokens = estimateTokenCount(prompt);
        if (promptTokens > 6000) {
            throw new IllegalArgumentException("Prompt too long, exceeds token limit.");
        }

        // 控制生成长度，防止超出模型总上下文（如 qwen-max 支持 32768 tokens）
        int maxCompletionTokens = Math.min(maxTokens, 32768 - promptTokens);

        // 构建请求体
        String jsonBody = "{\n" +
                "  \"model\": \"qwen-max\",\n" +
                "  \"input\": {\n" +
                "    \"messages\": [\n" +
                "      {\"role\": \"user\", \"content\": \"" + escapeJson(prompt) + "\"}\n" +
                "    ]\n" +
                "  },\n" +
                "  \"parameters\": {\n" +
                "    \"max_tokens\": " + maxCompletionTokens + "\n" +
                "  }\n" +
                "}";

        Request request = new Request.Builder()
                .url(API_URL)
                .post(RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8")))
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseBody = response.body().string();
            JsonNode root = objectMapper.readTree(responseBody);
            return root.path("output").path("text").asText();
        }
    }

    private String escapeJson(String str) {
        return str.replace("\"", "\\\"").replace("\n", "\\n");
    }

    public static void main(String[] args) throws IOException {
        QwenApiClient client = new QwenApiClient();
        String prompt = "你好，请介绍一下你自己。";
        String result = client.callQwen(prompt, 500);
        System.out.println(result);
    }


}
