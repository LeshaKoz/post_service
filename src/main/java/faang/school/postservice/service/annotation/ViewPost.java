package faang.school.postservice.service.annotation;

import faang.school.postservice.model.Post;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewPost {

    Class<?> targetValue() default Post.class;
}