package cn.swiftchain.integration;

import cn.swiftchain.integration.vo.*;

public interface VenaRemoteService {

    ServerTimeContent getServerTime();

    CalculateCostContent calculateCost(CalculateCostRequest request);

    ApplyEnergyContent applyEnergy(ApplyEnergyRequest request);

    QueryPriceContent queryPrice(QueryPriceRequest request);

    LeftResourceContent leftResource();

    RefundContent refund(RefundRequest request);
}
