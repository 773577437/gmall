package com.test.gmall.admin.aop;

import com.test.gmall.to.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

/**
 * JSR303校验切面的编写
 * 1)、导入aop依赖，加 @Aspect注解
 * 2）、编写切面方法，带 ProceedingJoinPoint 类型参数
 * 3）、加@Around 注解和切面表达式 ("execution(* com.test.gmall.admin..*Controller.*(..))")
 */
@Slf4j
@Component
@Aspect
public class DataValidatedAspect {

    /**
     * 目标方法的异常，如果不需要处理，一般抛出
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.test.gmall.admin..*Controller.*(..))")
    public Object ValidatedAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object proceed = null;

        //获取方法参数
        Object[] args = joinPoint.getArgs();
        //遍历方法参数
        for(Object obj : args){
            //判断参数类型是否是 BindingResult 类型
            if(obj instanceof BindingResult){
                //强转 BindingResult 类型
                BindingResult result = (BindingResult) obj;
                //判断当前校验错误数量是否大于0
                if(result.getErrorCount() > 0){
                    //获取所有错误信息
                    List<FieldError> fieldErrors = result.getFieldErrors();
                    //遍历错误信息
                    for(FieldError fieldError : fieldErrors){
                        //控制台打印错误信息
                        log.debug("属性：{}，传来的值：{}，校验出错提示：{}",
                                fieldError.getField(), fieldError.getRejectedValue(),fieldError.getDefaultMessage());
                    }
                    return new CommonResult().validateFailed(result);
                }
            }
        }
            //获取执行方法
            proceed = joinPoint.proceed(args);
        return proceed;
    }

}
