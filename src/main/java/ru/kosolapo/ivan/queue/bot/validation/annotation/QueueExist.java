package ru.kosolapo.ivan.queue.bot.validation.annotation;

import ru.kosolapo.ivan.queue.bot.validation.QueueExistValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.TYPE})
@ValidationAnnotation(QueueExistValidator.class)
public @interface QueueExist {
}
