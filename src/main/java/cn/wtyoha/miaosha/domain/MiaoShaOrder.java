package cn.wtyoha.miaosha.domain;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "")
public class MiaoShaOrder {
    @Id
    Long id;
    Long userId;
    Long oderId;
    Long goodsId;
}
