package cn.wtyoha.miaosha.domain;

import cn.wtyoha.miaosha.validator.annotations.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Component;
import javax.validation.constraints.NotNull;

import javax.persistence.Table;
import java.util.Date;

@Data
@Component
@Table(name = "miaosha_user")
public class MiaoShaUser {
    @NotNull
    @IsMobile(required = false)
    Long id;

    @NotNull
    @Length(min = 6)
    String nickname;

    @NotNull
    String password;
    String salt;
    String head;
    Date registerDate;
    Date lastLoginDate;
    Integer loginCount;
}
