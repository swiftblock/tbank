package cn.swiftchain.biz.service;

import cn.swiftchain.api.vo.*;
import cn.swiftchain.api.vo.data.PackData;
import cn.swiftchain.api.vo.data.ResourceInfoData;
import cn.swiftchain.api.vo.enums.ResponseCode;
import cn.swiftchain.api.vo.request.TakeOrderRequest;
import cn.swiftchain.biz.model.enums.CurrencyEnum;
import cn.swiftchain.biz.model.enums.OfferStatusEnum;
import cn.swiftchain.biz.model.enums.OrderStatusEnum;
import cn.swiftchain.common.BizException;
import cn.swiftchain.common.util.AddrUtils;
import cn.swiftchain.dao.ComboDao;
import cn.swiftchain.dao.DataDao;
import cn.swiftchain.dao.OfferDao;
import cn.swiftchain.dao.OrderDao;
import cn.swiftchain.dao.po.ComboPO;
import cn.swiftchain.dao.po.DataPO;

import cn.swiftchain.dao.po.OfferPO;
import cn.swiftchain.dao.po.OrderPO;
import cn.swiftchain.integration.TronStationService;
import cn.swiftchain.integration.VenaRemoteService;
import cn.swiftchain.integration.vo.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ResourceService {
    private static final int MAX_COMBO = 20;

    public static final int PRECISION = 1;


    @Autowired
    private DataDao dataDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OfferDao offerDao;

    @Autowired
    private ComboDao comboDao;

    @Autowired
    private VenaRemoteService venaRemoteService;

    @Autowired
    private TronStationService tronStationService;

    @Autowired
    private TransactionTemplate txRequired;

    @Value("${resource.trx.min}")
    private Long minFreezeAmount;

    @Value("${resource.interval.min}")
    private Long minFreezeInterval;

    @Value("${resource.interval.max}")
    private Long maxFreezeInterval;

    @Value("${resource.receive.address}")
    private String receiveAddress;

    @Value("${resource.fee.rate}")
    private BigDecimal feeRate;

    public TestRes getData(int id) {
        List<DataPO> dataPOS = dataDao.queryByList(null);
        DataPO dataPO = dataDao.queryById(id);
        if (null == dataPO) {
            return null;
        }
        TestRes TestRes = new TestRes(dataPO.getId(), dataPO.getContent());
        return TestRes;
    }

    public ServerTimeContent getServerTime() {
        return venaRemoteService.getServerTime();
    }

    public CalculateCostContent calculateCost(CalculateCostRequest request) {
        return venaRemoteService.calculateCost(request);
    }

    public ApplyEnergyContent applyEnergy(ApplyEnergyRequest request) {
        return venaRemoteService.applyEnergy(request);
    }

    public QueryPriceContent queryPrice(QueryPriceRequest request) {
        return venaRemoteService.queryPrice(request);
    }

    public LeftResourceContent leftResource() {
        return venaRemoteService.leftResource();
    }

    public AccountResource accountResource(AccountResourceRequest request) {
        return tronStationService.getAccountResource(request);
    }

    /**
     * 获取资源信息
     *
     * @return
     */
    public ResourceInfoData getResourceInfo() {
        /**
         * 查询冻结1个TRX，1天需要支付的TRX或者VENA
         */
        CalculateCostRequest calculateCostRequest = new CalculateCostRequest();
        calculateCostRequest.setFreeze_amount("500");
        calculateCostRequest.setLoan_days(3);
        CalculateCostContent calculateCostContent = venaRemoteService.calculateCost(calculateCostRequest);

        /**
         * 查询套餐列表
         */
        List<ComboPO> comboPOS = comboDao.queryByList(new ComboPO());

        /**
         * 查询Trx冻结兑换Energy的数量
         */
        AccountResourceRequest accountResourceRequest = new AccountResourceRequest();
        accountResourceRequest.setAddress(AddrUtils.base58Check2Hex(receiveAddress));
        AccountResource accountResource = tronStationService.getAccountResource(accountResourceRequest);

        /**
         * 查询剩余资金池
         */
        LeftResourceContent leftResourceContent = venaRemoteService.leftResource();

        /**
         * 组装PackVO
         */
        //冻结1TRX的单价
        Assert.isTrue(StringUtils.isNoneBlank(calculateCostContent.getTrx_price()), "TrxPrice为空!");
        Assert.isTrue(StringUtils.isNoneBlank(calculateCostContent.getVena_price()), "VenaPrice为空!");
        BigDecimal trxPrice = new BigDecimal(calculateCostContent.getTrx_price());
        BigDecimal venaPrice = new BigDecimal(calculateCostContent.getVena_price());
        //冻结1TRX换取的Energy
        Assert.notNull(accountResource.getTotalEnergyLimit(), "accountResource.TotalEnergyLimit为空!");
        Assert.notNull(accountResource.getTotalEnergyWeight(), "accountResource.TotalEnergyWeight为空!");
        BigDecimal energyFreezePrice = new BigDecimal(accountResource.getTotalEnergyLimit()).divide(new BigDecimal(accountResource.getTotalEnergyWeight()), PRECISION, BigDecimal.ROUND_DOWN);

        List<PackData> packDatas = Lists.newLinkedList();
        for (ComboPO comboPO : comboPOS) {
            PackData packData = new PackData();
            packData.setFreezeAmount(comboPO.getFreezeAmount());
            packData.setFreezeInterval(comboPO.getFreezeInterval());
            BigDecimal freezeAmount = new BigDecimal(packData.getFreezeAmount());
            BigDecimal freezeInterval = new BigDecimal(packData.getFreezeInterval());
            BigDecimal payAmount = freezeAmount.multiply(freezeInterval).multiply(feeRate).divide(trxPrice, PRECISION, BigDecimal.ROUND_CEILING);
            packData.setPayAmount(payAmount.toPlainString());
            packData.setPayCurrency(CurrencyEnum.TRX.getCode());
            BigDecimal resourceAmount = energyFreezePrice.multiply(freezeAmount);
            packData.setResourceAmount(resourceAmount.toPlainString());
            packDatas.add(packData);
        }

        /**
         * 组装resource info
         */
        ResourceInfoData resourceInfoData = new ResourceInfoData();
        Assert.notNull(leftResourceContent.getLeft_trx(), "LeftTrx为空!");
        resourceInfoData.setAvailableFunds(leftResourceContent.getLeft_trx());
        resourceInfoData.setMinTrxFreezeAmount(minFreezeAmount);
        resourceInfoData.setMaxTrxFreezeAmount(leftResourceContent.getLeft_trx());
        resourceInfoData.setMinTrxFreezeInterval(minFreezeInterval);
        resourceInfoData.setMaxTrxFreezeInterval(maxFreezeInterval);
        resourceInfoData.setOfferNo(calculateCostContent.getPrice_id());
        Long restTime = calculateCostContent.getExpiration_time() - System.currentTimeMillis();
//        Assert.isTrue(restTime > 0, "报价已超时!");
        resourceInfoData.setRestTime(restTime);
        resourceInfoData.setTrxFreezePrice(trxPrice.multiply(feeRate).toPlainString());
        resourceInfoData.setVenaFreezePrice(venaPrice.multiply(feeRate).toPlainString());
        resourceInfoData.setTrxResourcePrice(energyFreezePrice.toPlainString());
        resourceInfoData.setPackageList(packDatas);
        resourceInfoData.setReceiveAddress(receiveAddress);

        return resourceInfoData;
    }

    /**
     * 租用资源
     *
     * @param request
     * @return
     */
    public void takeResource(TakeOrderRequest request) {
        BigDecimal feeRate = new BigDecimal(this.feeRate.toPlainString());

        OfferPO queryPO = new OfferPO();
        queryPO.setOfferNo(request.getOfferNo());
        OfferPO offerPO = offerDao.queryById(queryPO);
        boolean offerNotExist = (null == offerPO);

        if (offerNotExist) {
            QueryPriceRequest queryPriceRequest = new QueryPriceRequest();
            queryPriceRequest.setPrice_id(request.getOfferNo());
            QueryPriceContent content = venaRemoteService.queryPrice(queryPriceRequest);

            offerPO = new OfferPO();
            offerPO.setOfferNo(request.getOfferNo());
            offerPO.setPriceTrx(new BigDecimal(content.getTrx_price()));
            offerPO.setPriceVena(new BigDecimal(content.getVena_price()));
            offerPO.setStartTime(new Date(content.getEffective_time()));
            offerPO.setEndTime(new Date(content.getExpiration_time()));
            offerPO.setStatus(OfferStatusEnum.VALID.getCode());
        }

        if (!StringUtils.equals(OfferStatusEnum.VALID.getCode(), offerPO.getStatus())) {
            log.error("报价{}状态为{}, 已失效", offerPO.getOfferNo(), offerPO.getStatus());
            throw new BizException(ResponseCode.OFFER_INVALID);
        }

        if (System.currentTimeMillis() > offerPO.getEndTime().getTime()) {
            log.error("交易[{}]的报价已超时, 过期时间为{}", request.getTxHash(), offerPO.getEndTime());
            throw new BizException(ResponseCode.OFFER_TIMEOUT);
        }

        BigDecimal freezeAmount = new BigDecimal(request.getTrxFreezeAmount());
        BigDecimal freezeInterval = new BigDecimal(request.getTrxFreezeInterval());
        BigDecimal payAmount = new BigDecimal(request.getPayAmount());
        //当前支付金额 = 冻结金额 * 冻结天数 * 费率 / 冻结单价(TRX)
        BigDecimal currentPayAmount = freezeAmount.multiply(freezeInterval).multiply(feeRate).divide(offerPO.getPriceTrx(), PRECISION, RoundingMode.CEILING);
        //当前成本 = 冻结金额 * 冻结天数 / 冻结单价(TRX)
        BigDecimal fixedCost = freezeAmount.multiply(freezeInterval).divide(offerPO.getPriceTrx(), PRECISION, RoundingMode.CEILING);

        if (payAmount.compareTo(currentPayAmount) < 0) {
            log.error("交易[{}]的报价已失效，当前最小支付金额为{}， 实际支付金额为{}", request.getTxHash(), currentPayAmount, payAmount);
            throw new BizException(ResponseCode.OFFER_INVALID);
        }

        if (payAmount.compareTo(fixedCost) < 0) {
            log.error("交易[{}]的报价已失效，当前成本为{}， 实际支付金额为{}", request.getTxHash(), fixedCost, payAmount);
            throw new BizException(ResponseCode.OFFER_INVALID);
        }

        BigDecimal fee = payAmount.subtract(fixedCost);

        OrderPO orderPO = new OrderPO();
        orderPO.setUserAddress(request.getUserAddress());
        orderPO.setResourceType(request.getResourceType());
        orderPO.setResourceAmount(new BigDecimal(request.getResourceAmount()));
        orderPO.setFreezeAmount(freezeAmount);
        orderPO.setFee(fee);
        orderPO.setFeeRate(feeRate);
        orderPO.setStatus(OrderStatusEnum.WAIT.getCode());
        orderPO.setTxHash(request.getTxHash());
        orderPO.setRawTx(request.getRawTx());
        orderPO.setCurrency(request.getCurrency());
        orderPO.setPayAmount(payAmount);
        orderPO.setOfferNo(request.getOfferNo());
        orderPO.setFreezeInterval(request.getTrxFreezeInterval());
        orderPO.setGmtCreate(new Date());


        OfferPO finalOfferPO = offerPO;
        txRequired.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                try {
                    orderDao.add(orderPO);
                } catch (DuplicateKeyException e) {
                    log.info("幂等，tx hash : {}", request.getTxHash());
                }

                if (offerNotExist) {
                    try {
                        offerDao.add(finalOfferPO);
                    } catch (DuplicateKeyException e) {
                        log.info("幂等，offer no : {}", request.getOfferNo());
                    }
                }
            }
        });
    }
}
