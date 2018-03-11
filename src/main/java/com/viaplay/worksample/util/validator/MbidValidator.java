package com.viaplay.worksample.util.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MbidValidator implements ConstraintValidator<ValidMBID, String> {

    private String pattern;

    @Override
    public void initialize(ValidMBID constraintAnnotation) {
        this.pattern = constraintAnnotation.pattern();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null || s.isEmpty()) {
            return false;
        }

        Matcher m = Pattern.compile(pattern).matcher(s);
        return m.matches();
    }
}
