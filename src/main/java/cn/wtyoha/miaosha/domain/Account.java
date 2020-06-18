package cn.wtyoha.miaosha.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Component
@Table(name = "account")
public class Account {
    @Id
    private Integer id;
    private String name;
    private Double money;
}
