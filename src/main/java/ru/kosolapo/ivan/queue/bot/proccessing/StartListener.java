package ru.kosolapo.ivan.queue.bot.proccessing;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.kosolapo.ivan.queue.bot.bot.DiscordBot;
import ru.kosolapo.ivan.queue.bot.data.Request;
import ru.kosolapo.ivan.queue.bot.data.RequestMetaData;
import ru.kosolapo.ivan.queue.bot.proccessing.annotation.Command;
import ru.kosolapo.ivan.queue.bot.proccessing.annotation.DiscordController;
import ru.kosolapo.ivan.queue.bot.validation.Validator;
import ru.kosolapo.ivan.queue.bot.validation.annotation.ValidationAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

@Component
public class StartListener {
    public Map<RequestMetaData, Function<Request, Mono<?>>> map = new HashMap<>();

    private final ConfigurableListableBeanFactory factory;

    public StartListener(ConfigurableListableBeanFactory factory) {
        this.factory = factory;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        var context = (ConfigurableApplicationContext) event.getApplicationContext();
        var beans = context.getBeanNamesForAnnotation(DiscordController.class);
        var x = factory.getBean(DiscordBot.class);
        for (var i : beans) {
            var bean = factory.getBean(i);
            var clazz = bean.getClass();
            Method[] m = clazz.getMethods();
            for (Method method : m) {
                Command annotation = method.getAnnotation(Command.class);
                String s;
                if (annotation == null) {
                    continue;
                }
                s = annotation.value();
                Parameter[] parameters = method.getParameters();
                int len = parameters.length;
                List<Validator> validators;
                if (parameters.length == 0) {
                    validators = validators(method.getAnnotations());
                } else {
                    validators = validators(method.getAnnotations(), parameters[0].getAnnotations());
                }

                Validator validator = validators.stream()
                        .filter(Objects::nonNull)
                        .reduce(val -> Mono.empty(),
                                (f1, f2) -> val -> f1.apply(val).then(f2.apply(val)));

                int count = annotation.count();
                count = count >= 0 ? count : -1;
                map.put(new RequestMetaData(s, count), (value) -> {
                    try {
                        Object[] args = new Object[len];
                        if (len > 0) {
                            args[0] = value;
                        }
                        return validator.apply(value)
                                .onErrorStop()
                                .then((Mono<?>) method.invoke(bean, args))
                                .log();
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        return Mono.empty();
                    }
                });
            }
        }
        x.run(map);
    }

    private List<Validator> validators(Annotation[]... annotations) {
        return validatorsByAnnotation(Arrays.stream(annotations).flatMap(Arrays::stream));
    }

    private List<Validator> validatorsByAnnotation(Stream<Annotation> annotations) {
        return validators(annotations(annotations));
    }

    private List<Validator> validators(Stream<Class<? extends Validator>> annotated) {
        return annotated.map(x -> (Validator) factory.getBean(x)).distinct().toList();
    }

    private Stream<Class<? extends Validator>> annotations(Stream<Annotation> annotations) {
        return annotations.flatMap(annotation -> {
            if (annotation instanceof ValidationAnnotation validationAnnotation) {
                return Stream.of(validationAnnotation.value());
            }
            //looks bad, needs to be fixed
            if (annotation.annotationType().getName().startsWith("ru.kosolapo.ivan")) {
                return annotations(Arrays.stream(annotation.annotationType().getAnnotations()));
            }
            return Stream.empty();
        });
    }
}
