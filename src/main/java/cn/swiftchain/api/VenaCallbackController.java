package cn.swiftchain.api;

import cn.swiftchain.api.vo.CommonResult;
import cn.swiftchain.api.vo.request.OfferNotifyRequest;
import cn.swiftchain.api.vo.request.OrderNotifyRequest;
import cn.swiftchain.biz.service.OfferService;
import cn.swiftchain.biz.service.OrderService;
import cn.swiftchain.common.BizException;
import cn.swiftchain.common.util.BeanValidateResult;
import cn.swiftchain.common.util.BeanValidator;
import cn.swiftchain.common.util.SignUtils;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;

@RequestMapping("/callback")
@RestController
@Slf4j
public class VenaCallbackController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OfferService offerService;

    private static final String KEY_TBANK_ACCESS_SIGN = "TBANK-ACCESS-SIGN";

    private static final String KEY_TBANK_ACCESS_TIMESTAMP = "TBANK-ACCESS-TIMESTAMP";

    private static final String KEY_TBANK_ACCESS_KEY = "TBANK-ACCESS-KEY";

    @Value("${vena.callback.key}")
    private String callbackKey;

    private String verify(HttpServletRequest request) {

        String body = null;
        try {
            body = IOUtils.toString(new InputStreamReader(request.getInputStream()));
        } catch (IOException e) {
            throw new BizException("接收订单回调解析参数出现异常");
        }

        String sign = request.getHeader(KEY_TBANK_ACCESS_SIGN);
        String timestamp = request.getHeader(KEY_TBANK_ACCESS_TIMESTAMP);
        String accessKey = request.getHeader(KEY_TBANK_ACCESS_KEY);

        String data = timestamp + request.getRequestURI() + (StringUtils.isNoneBlank(request.getQueryString()) ? request.getQueryString() : "") + body;

        String localSign = SignUtils.sign(callbackKey, data);
        Assert.isTrue(StringUtils.equals(sign, localSign), "签名验证失败");

        return body;
    }

    private void validateParams(Object params) {
        BeanValidateResult validateResult = BeanValidator.validate(params);
        if (!validateResult.isSuccess()) {
            log.error("参数校验异常, params: {}, 错误信息: {}", params, validateResult.getFirstMsg());
            throw new BizException("参数校验出错: " + validateResult.getFirstMsg());
        }
    }

    @RequestMapping(value = "/order/notify", method = RequestMethod.POST)
    public CommonResult orderNotify(HttpServletRequest request) {
        String body = verify(request);

        log.info("uri : {} , body : {}", request.getRequestURI(), body);

        OrderNotifyRequest orderNotifyRequest = JSON.parseObject(body, OrderNotifyRequest.class);

        CommonResult result = new CommonResult<>();
        try {
            validateParams(orderNotifyRequest);
            orderService.notifyOrder(orderNotifyRequest);
            log.info("transaction[{}]回调通知成功!", orderNotifyRequest.getTransaction_hash());
        } catch (Throwable e) {
            log.error("order notify出现异常", e);
            result = CommonResult.failure(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/offer/notify", method = RequestMethod.POST)
    public CommonResult offerNotify(HttpServletRequest request) {
        String body = verify(request);

        log.info("uri : {} , body : {}", request.getRequestURI(), body);

        OfferNotifyRequest offerNotifyRequest = JSON.parseObject(body, OfferNotifyRequest.class);

        CommonResult result = new CommonResult<>();
        try {
            validateParams(offerNotifyRequest);
            offerService.notifyOffer(offerNotifyRequest);
            log.info("offer[{}]回调通知成功!", offerNotifyRequest.getDisabled_price_id());
        } catch (Throwable e) {
            log.error("offer notify出现异常", e);
            result = CommonResult.failure(e.getMessage());
        }
        return result;
    }
}
