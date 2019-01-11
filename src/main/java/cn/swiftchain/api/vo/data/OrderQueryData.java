package cn.swiftchain.api.vo.data;

import lombok.Data;

import java.util.List;

@Data
public class OrderQueryData {
    //订单数量
    private Long count;

    //起始订单编号
    private String startOrderNo;

    //订单列表
    private List<OrderData> orderList;
}
