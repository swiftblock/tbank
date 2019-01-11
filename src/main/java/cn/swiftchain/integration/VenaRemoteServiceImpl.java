package cn.swiftchain.integration;

import cn.swiftchain.common.BizException;
import cn.swiftchain.common.util.SignUtils;
import cn.swiftchain.integration.vo.*;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Field;

@Service
@Slf4j
public class VenaRemoteServiceImpl implements VenaRemoteService {
    private static final String SLASH = "/";

    private static final String INTERFACE_SERVER_TIME = "/api/server_time";

    private static final String INTERFACE_APPLY_ENERGY = "/api/signature/apply_energy";

    private static final String INTERFACE_CALCULATION_OF_COST = "/api/signature/calculation_of_cost";

    private static final String INTERFACE_QUERY_PRICE = "/api/signature/query_price";

    private static final String INTERFACE_LEFT_RESOURCE = "/api/signature/left_resource";

    private static final String INTERFACE_REFUND = "/api/signature/refund";

    private static final String KEY_VENA_ACCESS_SIGN = "VENA-ACCESS-SIGN";

    private static final String KEY_VENA_ACCESS_TIMESTAMP = "VENA-ACCESS-TIMESTAMP";

    private static final String KEY_VENA_ACCESS_KEY = "VENA-ACCESS-KEY";

    private static final String URL_PARAM_SPLIT = "?";

    @Value("${vena.access.key}")
    private String venaAccessKey;

    @Value("${vena.private.key}")
    private String privateKey;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${vena.remote.url}")
    private String venaRemoteUrl;

    private String remoteGetCall(String method, Object params) {
        UriComponents uriComponents = buildUri(venaRemoteUrl + method, params);
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        String responseStr = restTemplate.getForObject(uriComponents.toUriString(), String.class);
        return responseStr;
    }

    private HttpHeaders buildHeader(String orgData, String timestamp) {
        String signature = SignUtils.sign(privateKey, orgData);
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.set(KEY_VENA_ACCESS_SIGN, signature);
        headers.set(KEY_VENA_ACCESS_TIMESTAMP, timestamp);
        headers.set(KEY_VENA_ACCESS_KEY, venaAccessKey);
        return headers;
    }

    private UriComponents buildUri(String url, Object params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if (null != params) {
            ReflectionUtils.doWithFields(params.getClass(), (Field field) -> {
                ReflectionUtils.makeAccessible(field);
                Object value = ReflectionUtils.getField(field, params);
                if (value != null) {
                    builder.queryParam(field.getName(), value);
                }
            });
        }
        return builder.build();
    }

    private String remoteSignGetCall(String method, Object params) {
        UriComponents uriComponents = buildUri(venaRemoteUrl + method, params);
        String timestamp = getServerTime().getServer_time().toString();
        String orgData = timestamp + method + (uriComponents.getQuery() != null ? uriComponents.getQuery() : "");
        log.info("orgData: {}", orgData);

        HttpHeaders headers = buildHeader(orgData, timestamp);

        HttpEntity<String> entity = new HttpEntity<String>("", headers);
        log.info("http request : {}", entity.toString());
        String responseStr;
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(uriComponents.toUriString(), HttpMethod.GET, entity, String.class);
            responseStr = response.getBody();
        } catch (HttpClientErrorException e) {
            responseStr = e.getResponseBodyAsString();
        }

        log.info("http response : {}", responseStr);
        return responseStr;
    }

    private String remoteSignPostCall(String method, Object params) {
        String requestJson = JSONObject.toJSONString(params);
        String timestamp = getServerTime().getServer_time().toString();
        String orgData = timestamp + method + requestJson;
        HttpHeaders headers = buildHeader(orgData, timestamp);
        HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
        log.info("http request : {}", entity.toString());
        String responseStr;

        responseStr = restTemplate.postForObject(venaRemoteUrl + method, entity, String.class);
        log.info("http method : {}, http response : {}", method, responseStr);
        return responseStr;
    }

    private void checkResponse(CommonResponse commonResponse) {
        if (null == commonResponse) {
            throw new BizException("调用VENA远程服务失败，返回response为空！");
        }

        if (!commonResponse.isSuccess()) {
            throw new BizException("调用VENA远程服务失败，失败原因：" + commonResponse.getResponse_msg());
        }
    }

    @Override
    public ServerTimeContent getServerTime() {
        String responseStr = remoteGetCall(INTERFACE_SERVER_TIME, null);

        CommonResponse<ServerTimeContent> commonResponse = JSONObject.parseObject(responseStr,
                new TypeReference<CommonResponse<ServerTimeContent>>() {
                });

        checkResponse(commonResponse);

        log.info("server time: {}", responseStr);
        log.info("sys time: {}", System.currentTimeMillis());

        return commonResponse.getContent();
    }

    @Override
    public CalculateCostContent calculateCost(CalculateCostRequest request) {
        //FIXME 测试用
        //String responseStr = "{\"response_code\":\"00\",\"response_msg\":\"success\",\"content\":{\"freeze_amount\":89733,\"trx_price\":\"3.3\",\"trx_amount\":\"1511(double，保留小数点后一位)\",\"vena_amount\":\"5995\",\"price_id\":\"15451318410980279692\",\"effective_time\":1545819763007,\"expiration_time\":1645819763007,\"vena_price\":\"4.4\"}}";
        String responseStr = remoteSignGetCall(INTERFACE_CALCULATION_OF_COST, request);
        CommonResponse<CalculateCostContent> commonResponse = JSONObject.parseObject(responseStr,
                new TypeReference<CommonResponse<CalculateCostContent>>() {
                });

        checkResponse(commonResponse);

        return commonResponse.getContent();
    }

    @Override
    public ApplyEnergyContent applyEnergy(ApplyEnergyRequest request) {
        String responseStr = remoteSignPostCall(INTERFACE_APPLY_ENERGY, request);
        CommonResponse<ApplyEnergyContent> commonResponse = JSONObject.parseObject(responseStr,
                new TypeReference<CommonResponse<ApplyEnergyContent>>() {
                });

        checkResponse(commonResponse);

        return commonResponse.getContent();
    }

    @Override
    public QueryPriceContent queryPrice(QueryPriceRequest request) {
        // FIXME: 2019/1/2
        //String responseStr = "{\"response_code\":\"00\",\"response_msg\":\"success\",\"content\":{\"trx_price\":\"3.3\",\"effective_time\":1546429075679,\"expiration_time\":1646429075679,\"vena_price\":\"4.4\"}}";
        String responseStr = remoteSignGetCall(INTERFACE_QUERY_PRICE, request);
        CommonResponse<QueryPriceContent> commonResponse = JSONObject.parseObject(responseStr,
                new TypeReference<CommonResponse<QueryPriceContent>>() {
                });

        checkResponse(commonResponse);

        return commonResponse.getContent();
    }

    @Override
    public LeftResourceContent leftResource() {
        //FIXME 测试用
        //String responseStr = "{\"response_code\":\"00\",\"response_msg\":\"success\",\"content\":{\"left_trx\":39506,\"timestamp\":1545819357561}}";
        String responseStr = remoteSignGetCall(INTERFACE_LEFT_RESOURCE, null);
        CommonResponse<LeftResourceContent> commonResponse = JSONObject.parseObject(responseStr,
                new TypeReference<CommonResponse<LeftResourceContent>>() {
                });

        checkResponse(commonResponse);

        return commonResponse.getContent();
    }

    @Override
    public RefundContent refund(RefundRequest request) {
        String responseStr = remoteSignPostCall(INTERFACE_REFUND, request);
        CommonResponse<RefundContent> commonResponse = JSONObject.parseObject(responseStr,
                new TypeReference<CommonResponse<RefundContent>>() {
                });

        checkResponse(commonResponse);

        return commonResponse.getContent();
    }

}
