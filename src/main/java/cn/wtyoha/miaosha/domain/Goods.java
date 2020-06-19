package cn.wtyoha.miaosha.domain;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;


@Data
@Table(name = "goods")
public class Goods {
    @Id
    private Long id;
    private String name;
    private String title;
    private String detail;
    private BigDecimal price;
    private Integer stock;
    private String smallImg;
    private String middleImg;
    private String largeImg;
}
