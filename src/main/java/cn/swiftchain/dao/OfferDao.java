package cn.swiftchain.dao;

import cn.swiftchain.dao.po.OfferPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface OfferDao extends BaseDao<OfferPO> {
    /**
     *
     * @param offerNoSet
     * @return
     */
    List<OfferPO> queryByOfferNo(@Param("offerNoSet") Set<String> offerNoSet);


    /**
     * @param offerNo
     * @param from
     * @param to
     * @return
     */
    int updateStatus(@Param("offerNo") String offerNo, @Param("from") String from, @Param("to") String to);
}
