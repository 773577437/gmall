package com.test.gmall.admin.aop;

import com.test.gmall.to.CommonResult;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.NullArgumentException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一处理所有异常，给前端返回500的json
 * 注意：环绕通知切面的方法，一定要将异常抛出，否则目标方法的异常将捕捉不到
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {ArithmeticException.class})
    public Object handlerException01(Exception e){
        log.debug("系统异常信息：{}",e.getMessage());
        return new CommonResult().validateFailed("数学异常。。。");
    }

    @ExceptionHandler(value = {NullArgumentException.class})
    public Object handlerException02(Exception e){
        log.debug("系统异常信息：{}",e.getMessage());
        return new CommonResult().validateFailed("空指针异常。。。");
    }
}
