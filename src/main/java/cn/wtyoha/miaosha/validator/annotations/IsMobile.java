package cn.wtyoha.miaosha.validator.annotations;


import cn.wtyoha.miaosha.validator.MobileNumValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {MobileNumValidator.class})
public @interface IsMobile {
    boolean required() default false;
    String message() default "手机号码格式错误";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
