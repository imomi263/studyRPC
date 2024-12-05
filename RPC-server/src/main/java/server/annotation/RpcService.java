package server.annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service // 并提供了service注解的功能
public @interface RpcService {
    /**
     * 暴露服务接口类型
     */
    Class<?> interfaceType() default Object.class;

    /**
     * 服务版本
     */
    String version() default "1.0";
}
