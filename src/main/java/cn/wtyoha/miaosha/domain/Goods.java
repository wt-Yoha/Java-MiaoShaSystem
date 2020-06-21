package cn.wtyoha.miaosha.domain;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;


@Data
@Table(name = "goods")
public class Goods {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;
    private String name;
    private String title;
    private String detail;
    private BigDecimal price;
    private Integer stock;
    private String smallImg;
    private String middleImg;
    private String largeImg;

    @Transient
    private MiaoShaGoods miaoShaGoods;
}
