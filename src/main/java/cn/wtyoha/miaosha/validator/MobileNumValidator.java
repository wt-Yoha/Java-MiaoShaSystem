package cn.wtyoha.miaosha.validator;

import cn.wtyoha.miaosha.validator.annotations.IsMobile;
import cn.wtyoha.miaosha.validator.utils.IsMobileValidatorUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MobileNumValidator implements ConstraintValidator<IsMobile, Long> {
    boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (required) {
            return IsMobileValidatorUtil.isMobile(String.valueOf(value));
        } else {
            if (value != null) {
                return IsMobileValidatorUtil.isMobile(String.valueOf(value));
            } else {
                return false;
            }
        }
    }
}
