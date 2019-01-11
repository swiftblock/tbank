package cn.swiftchain.biz.scheduler;

import cn.swiftchain.biz.model.enums.OrderStatusEnum;
import cn.swiftchain.dao.OrderDao;
import cn.swiftchain.dao.po.OrderPO;
import cn.swiftchain.integration.TronStationService;
import cn.swiftchain.integration.VenaRemoteService;
import cn.swiftchain.integration.vo.*;
import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class TransactionScheduler {
    private static final long MAX_TX_COUNT = 100;

    @Value("${scheduler.detectPaidTx.loop}")
    private int detectLoopTimes;

    @Value("${scheduler.refund.loop}")
    private int refundLoopTimes;


    @Value("${scheduler.detectTimeoutTx.limit}")
    private Long timeLimit;

    @Autowired
    OrderDao orderDao;

    @Autowired
    TronStationService tronStationService;

    @Autowired
    VenaRemoteService venaRemoteService;

    @Autowired
    TransactionTemplate txRequired;

    @Scheduled(fixedRateString = "${scheduler.detectPaidTx.interval}")
    public void detectPaidTx() {
        Long startOrderNo = null;
        for (int i = 0; i < detectLoopTimes; i++) {
            List<OrderPO> orders = orderDao.queryByStatus(startOrderNo, ImmutableSet.of(OrderStatusEnum.TIMEOUT.getCode(), OrderStatusEnum.WAIT.getCode()), MAX_TX_COUNT);

            if (CollectionUtils.isEmpty(orders)) {
                break;
            }

            startOrderNo = orders.get(orders.size() - 1).getOrderNo();

            for (OrderPO order : orders) {
                GetTransactionRequest request = new GetTransactionRequest();
                request.setValue(order.getTxHash());
                GetTransactionResponse response = tronStationService.getTransaction(request);
                //交易已上链
                if (null != response && StringUtils.equals(order.getTxHash(), response.getTxID())) {
                    //调用Vena申请资源
                    ApplyEnergyRequest applyEnergyRequest = new ApplyEnergyRequest();
                    applyEnergyRequest.setFreeze_amount(order.getFreezeAmount().longValue());
                    applyEnergyRequest.setLoan_days(order.getFreezeInterval());
                    applyEnergyRequest.setPayment_coin_type(order.getCurrency());
                    applyEnergyRequest.setPayment_address(order.getUserAddress());
                    applyEnergyRequest.setTransaction_hash(order.getTxHash());
                    ApplyEnergyContent content = venaRemoteService.applyEnergy(applyEnergyRequest);

                    if (StringUtils.isBlank(content.getFreeze_id())) {
                        log.error("订单[{}]调用冻结接口没有freeze_id", order.getOrderNo());
                        continue;
                    }

                    //更新数据
                    int count = orderDao.updateInfo(null, null, content.getFreeze_id(), null, order.getOrderNo(), order.getStatus(), OrderStatusEnum.PAID.getCode());

                    if (0 == count) {
                        log.error("检测支付交易时订单更新失败，订单{}的状态为{}", order.getOrderNo(), order.getStatus());
                    }
                }
            }
        }
    }

    @Scheduled(fixedRateString = "${scheduler.detectTimeoutTx.interval}")
    public void detectTimeoutTx() {
        Long startOrderNo = null;
        for (int i = 0; i < detectLoopTimes; i++) {
            List<OrderPO> orders = orderDao.queryByStatus(startOrderNo, ImmutableSet.of(OrderStatusEnum.WAIT.getCode()), MAX_TX_COUNT);

            if (CollectionUtils.isEmpty(orders)) {
                break;
            }

            startOrderNo = orders.get(orders.size() - 1).getOrderNo();

            for (OrderPO order : orders) {
                if (System.currentTimeMillis() - order.getGmtCreate().getTime() > timeLimit) {
                    //超时
                    int count = orderDao.updateStatus(order.getOrderNo(), OrderStatusEnum.WAIT.getCode(), OrderStatusEnum.TIMEOUT.getCode());

                    if (0 == count) {
                        log.error("检测超时交易时订单更新失败，订单{}的状态为{}", order.getOrderNo(), order.getStatus());
                    }
                }
            }
        }
    }

    @Scheduled(fixedRateString = "${scheduler.refund.interval}")
    public void refund() {
        Long startOrderNo = null;
        for (int i = 0; i < refundLoopTimes; i++) {
            List<OrderPO> orders = orderDao.queryByStatus(startOrderNo, ImmutableSet.of(OrderStatusEnum.FAILED.getCode()), MAX_TX_COUNT);

            if (CollectionUtils.isEmpty(orders)) {
                break;
            }

            startOrderNo = orders.get(orders.size() - 1).getOrderNo();

            for (OrderPO order : orders) {
                if (StringUtils.isNotBlank(order.getFreezeId())) {
                    RefundRequest refundRequest = new RefundRequest();
                    refundRequest.setFreeze_id(order.getFreezeId());
                    RefundContent content = venaRemoteService.refund(refundRequest);

                    //退款
                    int count = orderDao.updateInfo(null, content.getRefund_tx_hash(), null, null, order.getOrderNo(), OrderStatusEnum.FAILED.getCode(), OrderStatusEnum.REFUND.getCode());

                    if (0 == count) {
                        log.error("退款时订单更新失败，订单{}的状态为{}", order.getOrderNo(), order.getStatus());
                    }
                } else {
                    log.error("订单[{}]退款失败，freezeId为空", order.getOrderNo());
                }
            }
        }
    }
}
