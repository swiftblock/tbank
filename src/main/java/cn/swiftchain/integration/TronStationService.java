package cn.swiftchain.integration;

import cn.swiftchain.integration.vo.AccountResource;
import cn.swiftchain.integration.vo.AccountResourceRequest;
import cn.swiftchain.integration.vo.GetTransactionRequest;
import cn.swiftchain.integration.vo.GetTransactionResponse;

public interface TronStationService {
    AccountResource getAccountResource(AccountResourceRequest request);

    GetTransactionResponse getTransaction(GetTransactionRequest request);
}
