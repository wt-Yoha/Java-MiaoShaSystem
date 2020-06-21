package cn.wtyoha.miaosha.domain;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "order_info")
public class OrderInfo {
    @Id
    @GeneratedValue(generator = "JDBC")
    Long id;
    Long userId;
    Long goodsId;
    Long deliveryAddrId;
    String goodsName;
    Integer goodsCount;
    BigDecimal goodsPrice;
    Integer orderChannel;
    Integer status;
    Date createDate;
    Date payDate;
}
