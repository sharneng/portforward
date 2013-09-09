package com.sharneng.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;

/**
 * Default annotation to set package-level {@link javax.annotation.Nonnull Nonnull} for null check analysis. Apply this
 * annotation to package definition makes every field, parameter and method return value of any classes in the package
 * {@link javax.annotation.Nonnull Nonnull} unless otherwise annotated with {@link javax.annotation.CheckForNull
 * CheckForNull}.
 */
@Documented
@Nonnull
@TypeQualifierDefault({ ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NonnullByDefault {
}
