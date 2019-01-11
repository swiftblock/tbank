package cn.swiftchain.api;

import cn.swiftchain.api.vo.CommonResult;
import cn.swiftchain.api.vo.TestRes;
import cn.swiftchain.api.vo.data.ResourceInfoData;
import cn.swiftchain.api.vo.request.TakeOrderRequest;
import cn.swiftchain.biz.service.ResourceService;
import cn.swiftchain.integration.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/resource")
@RestController
@Slf4j
public class ResourceController {
    @Autowired
    ResourceService resourceService;

    @Value("${resource.receive.address}")
    private String receiveAddress;

    @RequestMapping("/test")
    public TestRes greeting(@RequestParam(value = "id", defaultValue = "1") int id) {
        TestRes testRes = resourceService.getData(id);
        if (null == testRes) {
            return new TestRes(2,
                    "hello first time");
        }
        return testRes;
    }

    @RequestMapping("/time")
    public ServerTimeContent time() {

        return resourceService.getServerTime();
    }

    @RequestMapping("/calculate")
    public CalculateCostContent calculateCost() {
        CalculateCostRequest request = new CalculateCostRequest();
        request.setLoan_days(3L);
        request.setFreeze_amount("1");

        return resourceService.calculateCost(request);
    }

    @RequestMapping("/apply")
    public ApplyEnergyContent applyEnergy() {
        ApplyEnergyRequest request = new ApplyEnergyRequest();
        request.setPayment_address("TMqVRgoCbKvcZcSmNaUADFsnKpyPRxHnEm");
        request.setTransaction_hash("5d104627aa3571252a0a9f08b1419f45032413bb977787a782e7e0535d220f62");
        request.setPayment_coin_type("TRX");
        request.setFreeze_amount(10000L);
        request.setLoan_days(3L);
        request.setRemark("123aaa");

        return resourceService.applyEnergy(request);
    }

    @RequestMapping("/query/price")
    public QueryPriceContent queryPrice() {
        QueryPriceRequest request = new QueryPriceRequest();
        request.setPrice_id("123");

        return resourceService.queryPrice(request);
    }

    @RequestMapping("/left")
    public LeftResourceContent left() {
        return resourceService.leftResource();
    }

    @RequestMapping("/account")
    public AccountResource account() {
        AccountResourceRequest request = new AccountResourceRequest();
        request.setAddress(receiveAddress);
        return resourceService.accountResource(request);
    }

    @RequestMapping(value = "/info", method = RequestMethod.POST)
    public CommonResult info() {
        CommonResult<ResourceInfoData> result = new CommonResult<>();
        try {
            ResourceInfoData resourceInfoData = resourceService.getResourceInfo();
            result.setData(resourceInfoData);
        } catch (Throwable e) {
            log.error("info出现异常", e);
            result = CommonResult.failure(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/take", method = RequestMethod.POST)
    public Object take(@RequestBody @Valid TakeOrderRequest request) {
        CommonResult result = new CommonResult<>();
        try {
            resourceService.takeResource(request);
        } catch (Throwable e) {
            log.error("take出现异常", e);
            result = CommonResult.failure(e.getMessage());
        }
        return result;
    }
}
