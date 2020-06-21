package cn.wtyoha.miaosha.domain;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "miaosha_order")
public class MiaoShaOrder {
    @Id
    Long id;
    Long userId;
    Long orderId;
    Long goodsId;
}
