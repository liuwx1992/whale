package com.zoo.whale.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * 定制化产品输出层级 JSON 生成工具。
 * 产品code -> id/cell -> 时间切片 -> 机构分组 -> 输出字段
 * 配置 JSON 只描述输出结构，不包含统计值，因此所有 value 统一生成为字符串 "1"。
 */
public final class CustomizedProductOutputJsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String MOCK_VALUE = "1";
    private static final String MOCK_CODE_VALUE = "00";
    private static final String MOCK_SW = "999333_20260920143459_23453A4E0";

    private CustomizedProductOutputJsonUtil() {
    }

    /**
     * 根据定制化产品配置 JSON 生成层级示例输出 JSON 字符串。
     *
     * @param configJson 产品配置 JSON 字符串
     * @return 格式化后的层级示例输出 JSON 字符串，所有 value 均为字符串 "1"
     * @throws IllegalArgumentException 配置为空、JSON 格式非法或层级配置缺失时抛出
     */
    public static String generateOutputJson(String configJson) {
        if (StringUtils.isBlank(configJson)) {
            throw new IllegalArgumentException("configJson 不能为空");
        }

        // 解析配置，并保证顶层必须是 JSON 对象。
        JsonNode config;
        try {
            config = MAPPER.readTree(configJson);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("configJson 不是合法的 JSON", e);
        }

        if (config == null || !config.isObject()) {
            throw new IllegalArgumentException("configJson 必须是 JSON 对象");
        }

        // 校验生成输出层级必需的配置项。
        JsonNode codeNode = config.get("code");
        String code = codeNode == null ? null : codeNode.asText();
        if (StringUtils.isBlank(code)) {
            throw new IllegalArgumentException("config.code 不能为空");
        }

        JsonNode timeSlicesNode = config.get("timeSlices");
        if (timeSlicesNode == null || !timeSlicesNode.isArray() || timeSlicesNode.isEmpty()) {
            throw new IllegalArgumentException("config.timeSlices 必须是非空 JSON 数组");
        }
        for (JsonNode timeSlice : timeSlicesNode) {
            if (timeSlice == null || !timeSlice.isTextual() || StringUtils.isBlank(timeSlice.asText())) {
                throw new IllegalArgumentException("config.timeSlices 中的元素必须是非空字符串");
            }
        }

        JsonNode orgGroupsNode = config.get("orgGroups");
        if (orgGroupsNode == null || !orgGroupsNode.isObject() || orgGroupsNode.isEmpty()) {
            throw new IllegalArgumentException("config.orgGroups 必须是非空 JSON 对象");
        }
        Iterator<Map.Entry<String, JsonNode>> orgGroupFields = orgGroupsNode.fields();
        while (orgGroupFields.hasNext()) {
            Map.Entry<String, JsonNode> orgGroupField = orgGroupFields.next();
            if (StringUtils.isBlank(orgGroupField.getKey())) {
                throw new IllegalArgumentException("config.orgGroups 的分组 key 不能为空");
            }

            JsonNode orgValues = orgGroupField.getValue();
            if (orgValues == null || !orgValues.isArray() || orgValues.isEmpty()) {
                throw new IllegalArgumentException("config.orgGroups." + orgGroupField.getKey() + " 必须是非空 JSON 数组");
            }
            for (JsonNode orgValue : orgValues) {
                if (orgValue == null || !orgValue.isTextual() || StringUtils.isBlank(orgValue.asText())) {
                    throw new IllegalArgumentException("config.orgGroups." + orgGroupField.getKey() + " 中的元素必须是非空字符串");
                }
            }
        }

        JsonNode fieldsNode = config.get("fields");
        if (fieldsNode == null || !fieldsNode.isArray() || fieldsNode.isEmpty()) {
            throw new IllegalArgumentException("config.fields 必须是非空 JSON 数组");
        }
        for (JsonNode field : fieldsNode) {
            if (field == null || !field.isTextual() || StringUtils.isBlank(field.asText())) {
                throw new IllegalArgumentException("config.fields 中的元素必须是非空字符串");
            }
        }

        // 构建外层响应结构；示例报文中所有 value 都固定为字符串 "1"。
        ObjectNode result = MAPPER.createObjectNode();
        result.put("swift_number", MOCK_SW);
        result.put("code", MOCK_CODE_VALUE);

        ObjectNode flagNode = MAPPER.createObjectNode();
        flagNode.put("flag_" + code.toLowerCase(Locale.ROOT), MOCK_VALUE);
        result.set("Flag", flagNode);

        // 按 code -> id/cell -> timeSlice -> orgGroup -> field 逐层构建产品输出结构。
        ObjectNode productNode = MAPPER.createObjectNode();
        result.set(code, productNode);

        ObjectNode idNode = MAPPER.createObjectNode();
        ObjectNode cellNode = MAPPER.createObjectNode();
        productNode.set("id", idNode);
        productNode.set("cell", cellNode);

        for (JsonNode timeSliceNode : timeSlicesNode) {
            ObjectNode idTimeSliceNode = MAPPER.createObjectNode();
            ObjectNode cellTimeSliceNode = MAPPER.createObjectNode();
            idNode.set(timeSliceNode.asText(), idTimeSliceNode);
            cellNode.set(timeSliceNode.asText(), cellTimeSliceNode);

            orgGroupFields = orgGroupsNode.fields();
            while (orgGroupFields.hasNext()) {
                String orgGroup = orgGroupFields.next().getKey();
                ObjectNode idOrgGroupNode = MAPPER.createObjectNode();
                ObjectNode cellOrgGroupNode = MAPPER.createObjectNode();
                idTimeSliceNode.set(orgGroup, idOrgGroupNode);
                cellTimeSliceNode.set(orgGroup, cellOrgGroupNode);

                for (JsonNode fieldNode : fieldsNode) {
                    String field = fieldNode.asText();
                    idOrgGroupNode.put(field, MOCK_VALUE);
                    cellOrgGroupNode.put(field, MOCK_VALUE);
                }
            }
        }

        try {
            // 返回格式化 JSON，便于外部团队直接查看和核对层级。
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("生成输出 JSON 失败", e);
        }
    }
}
