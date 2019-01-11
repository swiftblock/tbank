package cn.swiftchain.biz.service;

import cn.swiftchain.api.vo.data.OrderData;
import cn.swiftchain.api.vo.data.OrderQueryData;
import cn.swiftchain.api.vo.enums.OrderQueryStatusEnum;
import cn.swiftchain.api.vo.enums.ResponseCode;
import cn.swiftchain.api.vo.request.OrderDeleteRequest;
import cn.swiftchain.api.vo.request.OrderNotifyRequest;
import cn.swiftchain.api.vo.request.OrderQueryRequest;
import cn.swiftchain.biz.model.enums.OfferStatusEnum;
import cn.swiftchain.biz.model.enums.OrderStatusEnum;
import cn.swiftchain.common.BizException;
import cn.swiftchain.dao.OfferDao;
import cn.swiftchain.dao.OrderDao;
import cn.swiftchain.dao.po.OfferPO;
import cn.swiftchain.dao.po.OrderPO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.math.RoundingMode;
import java.util.*;

@Service
@Slf4j
public class OrderService {
    private static final long MAX_COUNT = 30;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OfferDao offerDao;

    public void deleteOrder(OrderDeleteRequest request) {
        /**
         * 查询order
         */
        OrderPO orderPO = orderDao.queryById(request.getOrderNo());

        if (null == orderPO) {
            throw new BizException(ResponseCode.ORDER_NOT_EXIST);
        }

        Assert.isTrue(StringUtils.equals(orderPO.getUserAddress(), request.getUserAddress()), "订单userAddress与传入的不一致");

        OrderStatusEnum status = OrderStatusEnum.getBycode(orderPO.getStatus());

        if (null == orderPO) {
            throw new BizException(ResponseCode.ORDER_STATUS_INVALID);
        }

        switch (status) {
            case WAIT:
            case APPLIED:
            case PAID:
            case REMOVED:
            case DONE:
            case FAILED:
                log.error("订单{}的状态为{},不能被删除", orderPO.getOrderNo(), orderPO.getStatus());
                throw new BizException(ResponseCode.ORDER_CANT_REMOVE);
            default:
                break;
        }

        int count = orderDao.updateStatus(orderPO.getOrderNo(), orderPO.getStatus(), OrderStatusEnum.REMOVED.getCode());

        if (count == 0) {
            throw new BizException(ResponseCode.ORDER_REMOVE_FAILED);
        }
    }

    public OrderQueryData queryOrderList(OrderQueryRequest request) {
        /**
         * 查询order
         */
        long count = request.getCount() > MAX_COUNT ? MAX_COUNT : request.getCount();
        OrderQueryStatusEnum queryStatus = OrderQueryStatusEnum.getBycode(request.getStatus());
        Set<String> statusSet = new HashSet<String>();
        switch (queryStatus) {
            case WAIT:
                statusSet.add(OrderStatusEnum.WAIT.getCode());
                break;
            case PAID:
                statusSet.add(OrderStatusEnum.PAID.getCode());
                statusSet.add(OrderStatusEnum.APPLIED.getCode());
                statusSet.add(OrderStatusEnum.DONE.getCode());
                break;
            case ALL:
                statusSet.add(OrderStatusEnum.WAIT.getCode());
                statusSet.add(OrderStatusEnum.PAID.getCode());
                statusSet.add(OrderStatusEnum.APPLIED.getCode());
                statusSet.add(OrderStatusEnum.DONE.getCode());
                statusSet.add(OrderStatusEnum.TIMEOUT.getCode());
                statusSet.add(OrderStatusEnum.FAILED.getCode());
                break;
        }
        List<OrderPO> orderPOS = orderDao.queryByAddrAndStatus(request.getUserAddress(), request.getStartOrderNo(), statusSet, count);

        OrderQueryData orderQueryData = new OrderQueryData();
        orderQueryData.setCount(0L);
        if (!CollectionUtils.isEmpty(orderPOS)) {

            /**
             * 查询offer
             */
            Set<String> offerNos = Sets.newHashSet();
            for (OrderPO orderPO : orderPOS) {
                offerNos.add(orderPO.getOfferNo());
            }
            List<OfferPO> offerPOS = offerDao.queryByOfferNo(offerNos);
            Map<String, OfferPO> offerPOMap = Maps.newHashMap();
            for (OfferPO offerPO : offerPOS) {
                offerPOMap.put(offerPO.getOfferNo(), offerPO);
            }

            /**
             * 组装结果
             */
            orderQueryData.setStartOrderNo(orderPOS.get(orderPOS.size() - 1).getOrderNo().toString());
            orderQueryData.setCount(new Long(orderPOS.size()));
            List<OrderData> orderDatas = Lists.newArrayList();
            for (OrderPO orderPO : orderPOS) {
                OrderData orderData = new OrderData();
                orderData.setOrderNo(orderPO.getOrderNo().toString());
                orderData.setUserAddress(orderPO.getUserAddress());
                orderData.setCurrency(orderPO.getCurrency());
                orderData.setFreezeAmount(orderPO.getFreezeAmount().setScale(ResourceService.PRECISION, RoundingMode.UP).toPlainString());
                orderData.setFreezeInterval(orderPO.getFreezeInterval());
                orderData.setOfferNo(orderPO.getOfferNo());
                orderData.setPayAmount(orderPO.getPayAmount().setScale(ResourceService.PRECISION, RoundingMode.UP).toPlainString());
                orderData.setTxHash(orderPO.getTxHash());
                orderData.setRawTx(orderPO.getRawTx().toString());
                orderData.setResourceAmount(orderPO.getResourceAmount().setScale(ResourceService.PRECISION, RoundingMode.UP).toPlainString());
                orderData.setResourceType(orderPO.getResourceType());
                OrderStatusEnum status = OrderStatusEnum.getBycode(orderPO.getStatus());
                Assert.notNull(status, "订单状态为空!");
                orderData.setStatus(status.getCode());
                orderData.setStatusDict(status.getDesc());
                OfferPO offer = offerPOMap.get(orderPO.getOfferNo());
                Assert.notNull(offer, "报价为空!");
                Assert.isTrue(StringUtils.isNotBlank(offer.getStatus()), "报价状态为空");
                OfferStatusEnum offerStatus = OfferStatusEnum.getBycode(offer.getStatus());
                orderData.setIsValid(OfferStatusEnum.VALID == offerStatus);
                long restTime = offer.getEndTime().getTime() - System.currentTimeMillis();
                if (restTime < 0) {
                    restTime = 0;
                }
                orderData.setRestTime(restTime);
                orderData.setGmtCreate(orderPO.getGmtCreate().getTime());

                orderDatas.add(orderData);
            }
            orderQueryData.setOrderList(orderDatas);
        }
        return orderQueryData;
    }

    public void notifyOrder(OrderNotifyRequest request) {
        /**
         * 查询订单
         */
        OrderPO orderPO = orderDao.queryByTxHash(request.getTransaction_hash());
        if (null == orderPO) {
            throw new BizException(ResponseCode.ORDER_NOT_EXIST);
        }
        Assert.isTrue(StringUtils.equals(request.getFreeze_id(), orderPO.getFreezeId()), "冻结编号不一致");

        OrderStatusEnum status = OrderStatusEnum.getBycode(orderPO.getStatus());
        Assert.notNull(status, "查询状态不存在");

        switch (status) {
            case DONE:
            case FAILED:
            case REFUND:
                //已经处理过
                return;
            case WAIT:
            case APPLIED:
                throw new BizException("回调通知时，订单状态还未支付");
            case PAID:
                //正常
                break;
            default:
                log.error("回调通知时，订单状态为{}[{}]", status.getDesc(), status.getCode());
                throw new BizException("回调通知时，订单状态不合法");
        }

        int count = 0;
        if (request.success()) {
            //冻结成功
            count = orderDao.updateInfo(request.getFreeze_hash(), null, null, request.getResponse_msg(), orderPO.getOrderNo(), status.getCode(), OrderStatusEnum.DONE.getCode());
        } else {
            //冻结失败
            log.info("订单[{}]冻结失败，失败原因为:{}", orderPO.getOrderNo(), request.getResponse_msg());
            count = orderDao.updateInfo(null, null, null, request.getResponse_msg(), orderPO.getOrderNo(), status.getCode(), OrderStatusEnum.FAILED.getCode());
        }

        if (0 == count) {
            throw new BizException("订单状态更新失败，可能有并发");
        }
    }
}
