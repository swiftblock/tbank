package cn.swiftchain.api;

import cn.swiftchain.api.vo.CommonResult;
import cn.swiftchain.api.vo.data.OrderQueryData;
import cn.swiftchain.api.vo.request.OrderDeleteRequest;
import cn.swiftchain.api.vo.request.OrderQueryRequest;
import cn.swiftchain.biz.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @RequestMapping("/history")
    public Object history() {
        return "order/history";
    }

    @ResponseBody
    @RequestMapping("/delete")
    public Object delete(@RequestBody @Valid OrderDeleteRequest param) {
        CommonResult result = new CommonResult<>();
        try {
            orderService.deleteOrder(param);
        } catch (Throwable e) {
            log.error("order/delete出现异常", e);
            result = CommonResult.failure(e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public CommonResult list(@RequestBody @Valid OrderQueryRequest request) {
        CommonResult<OrderQueryData> result = new CommonResult<>();
        try {
            OrderQueryData orderQueryData = orderService.queryOrderList(request);
            result.setData(orderQueryData);
        } catch (Throwable e) {
            log.error("order/list出现异常", e);
            result = CommonResult.failure(e.getMessage());
        }
        return result;
    }
}
