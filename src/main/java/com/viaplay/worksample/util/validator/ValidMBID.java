package com.viaplay.worksample.util.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * The annotation of the MBID validator according to JSR-303 validator standard
 */
@Constraint(validatedBy = MbidValidator.class)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMBID {

    String message() default "MBID must follow UUID format! See https://en.wikipedia.org/wiki/Universally_unique_identifier";

    String pattern() default "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
