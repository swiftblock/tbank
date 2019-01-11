package cn.swiftchain.dao;

import cn.swiftchain.dao.po.OrderPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface OrderDao extends BaseDao<OrderPO> {
    /**
     * @param statusSet
     * @param startOrderNo
     * @return
     */
    List<OrderPO> queryByAddrAndStatus(@Param("userAddress") String userAddress,
                                       @Param("startOrderNo") String startOrderNo,
                                       @Param("statusSet") Set<String> statusSet,
                                       @Param("count") Long count);

    /**
     * @param statusSet
     * @param startOrderNo
     * @return
     */
    List<OrderPO> queryByStatus(@Param("startOrderNo") Long startOrderNo,
                                @Param("statusSet") Set<String> statusSet,
                                @Param("count") Long count);

    /**
     * @param orderNo
     * @param from
     * @param to
     * @return
     */
    int updateStatus(@Param("orderNo") Long orderNo, @Param("from") String from, @Param("to") String to);


    /**
     * @param resourceTxHash
     * @param refundTxHash
     * @param freezeId
     * @param orderNo
     * @param from
     * @param to
     * @return
     */
    int updateInfo(@Param("resourceTxHash") String resourceTxHash, @Param("refundTxHash") String refundTxHash, @Param("freezeId") String freezeId, @Param("freezeMsg") String freezeMsg, @Param("orderNo") Long orderNo, @Param("from") String from, @Param("to") String to);

    /**
     * @param txHash
     * @return
     */
    OrderPO queryByTxHash(@Param("txHash") String txHash);
}
