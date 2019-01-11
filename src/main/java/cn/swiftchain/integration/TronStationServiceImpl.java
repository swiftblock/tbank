package cn.swiftchain.integration;

import cn.swiftchain.integration.vo.*;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Slf4j
@Service
public class TronStationServiceImpl implements TronStationService {

    private final static String INTERFACE_ACCOUNT_RESOURCE = "/wallet/getaccountresource";

    private final static String INTERFACE_GET_TRANSACTION_BY_ID = "/wallet/gettransactionbyid";

    @Value("${tron.remote.url}")
    private String tronRemoteUrl;

    @Autowired
    private RestTemplate restTemplate;

    private String remotePostCall(String method, Object params) {
        String requestJson = JSONObject.toJSONString(params);
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
        log.info("http method : {}, http request : {}", method, entity.toString());
        String responseStr;

        responseStr = restTemplate.postForObject(tronRemoteUrl + method, entity, String.class);
        log.info("http response : {}", responseStr);
        return responseStr;
    }

    @Override
    public AccountResource getAccountResource(AccountResourceRequest request) {
        //FIXME 测试用
        String responseStr = "{\"freeNetLimit\": 5000,\"NetLimit\": 135,\"assetNetUsed\": [{\"key\": \"1000003\",\"value\": 0},{\"key\": \"1000006\",\"value\": 0},{\"key\": \"1000396\",\"value\": 0},{\"key\": \"1000491\",\"value\": 0},{\"key\": \"1000745\",\"value\": 0},{\"key\": \"1000744\",\"value\": 0},{\"key\": \"1000746\",\"value\": 0},{\"key\": \"1000532\",\"value\": 0}],\"assetNetLimit\": [{\"key\": \"1000003\",\"value\": 0},{\"key\": \"1000532\",\"value\": 0},{\"key\": \"1000006\",\"value\": 0},{\"key\": \"1000396\",\"value\": 0},{\"key\": \"1000491\",\"value\": 0},{\"key\": \"1000745\",\"value\": 0},{\"key\": \"1000744\",\"value\": 0},{\"key\": \"1000746\",\"value\": 0}],\"TotalNetLimit\": 43200000000,\"TotalNetWeight\": 7325942977,\"EnergyLimit\": 5279,\"TotalEnergyLimit\": 100000000000,\"TotalEnergyWeight\": 321997402}\n";
//        String responseStr = remotePostCall(INTERFACE_ACCOUNT_RESOURCE, request);

        return JSONObject.parseObject(responseStr,
                new TypeReference<AccountResource>() {
                });
    }

    @Override
    public GetTransactionResponse getTransaction(GetTransactionRequest request) {
        String responseStr = remotePostCall(INTERFACE_GET_TRANSACTION_BY_ID, request);

        return JSONObject.parseObject(responseStr,
                new TypeReference<GetTransactionResponse>() {
                });
    }

}
