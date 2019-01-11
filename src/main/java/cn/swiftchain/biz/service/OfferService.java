package cn.swiftchain.biz.service;

import cn.swiftchain.api.vo.request.OfferNotifyRequest;
import cn.swiftchain.biz.model.enums.OfferStatusEnum;
import cn.swiftchain.common.BizException;
import cn.swiftchain.dao.OfferDao;
import cn.swiftchain.dao.po.OfferPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OfferService {
    @Autowired
    private OfferDao offerDao;

    public void notifyOffer(OfferNotifyRequest request) {
        /**
         * 查询报价
         */
        OfferPO offerPO = offerDao.queryById(request.getDisabled_price_id());
        if (null == offerPO) {
            //不存在该报价
            return;
        }

        OfferStatusEnum status = OfferStatusEnum.getBycode(offerPO.getStatus());

        if (OfferStatusEnum.INVALID == status) {
            //已通知过
            return;
        }

        int count = offerDao.updateStatus(offerPO.getOfferNo(), status.getCode(), OfferStatusEnum.INVALID.getCode());

        if (0 == count) {
            log.error("报价过期通知时，更新报价失败，报价{}的状态为{}", offerPO.getOfferNo(), offerPO.getStatus());
            throw new BizException("报价过期通知时，更新报价失败");
        }

        log.info("报价{}已过期", offerPO.getOfferNo());
    }

}
