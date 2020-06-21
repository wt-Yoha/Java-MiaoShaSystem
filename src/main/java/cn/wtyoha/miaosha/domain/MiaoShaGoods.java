package cn.wtyoha.miaosha.domain;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "miaosha_goods")
public class MiaoShaGoods {
    @Id
    @GeneratedValue(generator = "JDBC")
    Long id;
    Long goodsId;
    BigDecimal miaoshaPrice;
    Integer stockCount;
    Date startDate;
    Date endDate;
}
