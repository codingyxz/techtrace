package com.yxz.sboot.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.SourceLocation;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Slf4j
@Component
@Aspect
public class ServiceAspects {

    /**
     * @Pointcut ：切入点声明，即切入到哪些目标方法。value 属性指定切入点表达式，默认为 ""。
     * 用于被下面的通知注解引用，这样通知注解只需要关联此切入点声明即可，无需再重复写切入点表达式
     * <p>
     * 切入点表达式常用格式举例如下：
     * - * com.xxx.aspect.xxxService.*(..))：表示 com.xxx.aspect.xxxService 类中的任意方法
     * - * com.xxx.aspect.*.*(..))：表示 com.xxx.aspect 包(不含子包)下任意类中的任意方法
     * - * com.xxx.aspect..*.*(..))：表示 com.xxx.aspect 包及其子包下任意类中的任意方法
     * </p>
     * value 的 execution 可以有多个，使用 || 隔开.
     */
    @Pointcut("execution(* com.yxz.sboot.service.aop..*.*(..))")
    public void aspectPointcut() {
    }


    /**
     * 前置通知：目标方法执行之前执行以下方法体的内容。
     * value：绑定通知的切入点表达式。可以关联切入点声明，也可以直接设置切入点表达式
     * <br/>
     * * @param joinPoint：提供对连接点处可用状态和有关它的静态信息的反射访问<br/> <p>
     * * * Object[] getArgs()：返回此连接点处（目标方法）的参数，目标方法无参数时，返回空数组
     * * * Signature getSignature()：返回连接点处的签名。
     * * * Object getTarget()：返回目标对象
     * * * Object getThis()：返回当前正在执行的对象
     * * * StaticPart getStaticPart()：返回一个封装此连接点的静态部分的对象。
     * * * SourceLocation getSourceLocation()：返回与连接点对应的源位置
     * * * String toLongString()：返回连接点的扩展字符串表示形式。
     * * * String toShortString()：返回连接点的缩写字符串表示形式。
     * * * String getKind()：返回表示连接点类型的字符串
     * * * </p>
     */
    @Before(value = "aspectPointcut()")
    public void aspectBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Signature signature = joinPoint.getSignature();
        Object target = joinPoint.getTarget();
        Object aThis = joinPoint.getThis();
        JoinPoint.StaticPart staticPart = joinPoint.getStaticPart();
        SourceLocation sourceLocation = joinPoint.getSourceLocation();
        String longString = joinPoint.toLongString();
        String shortString = joinPoint.toShortString();

        log.info("【前置通知】" +
                        "args={},signature={},target={},aThis={},staticPart={}," +
                        "sourceLocation={},longString={},shortString={}"
                , Arrays.asList(args), signature, target, aThis, staticPart, sourceLocation, longString, shortString);
    }

    /**
     * 后置通知：目标方法执行之后执行以下方法体的内容，不管目标方法是否发生异常。
     * value：绑定通知的切入点表达式。可以关联切入点声明，也可以直接设置切入点表达式
     */
    @After(value = "aspectPointcut()")
    public void aspectAfter(JoinPoint joinPoint) {
        log.info("【后置通知】kind={}", joinPoint.getKind());
    }

    /**
     * 返回通知：目标方法返回后执行以下代码
     * value 属性：绑定通知的切入点表达式。可以关联切入点声明，也可以直接设置切入点表达式
     * pointcut 属性：绑定通知的切入点表达式，优先级高于 value，默认为 ""
     * returning 属性：通知签名中要将返回值绑定到的参数的名称，默认为 ""
     *
     * @param joinPoint ：提供对连接点处可用状态和有关它的静态信息的反射访问
     * @param result    ：目标方法返回的值，参数名称与 returning 属性值一致。无返回值时，这里 result 会为 null.
     */
    @AfterReturning(pointcut = "aspectPointcut()", returning = "result")
    public void aspectAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("【返回通知】,shortString={},result=", joinPoint.toShortString(), result);
    }

    /**
     * 异常通知：目标方法发生异常的时候执行以下代码，此时返回通知不会再触发
     * value 属性：绑定通知的切入点表达式。可以关联切入点声明，也可以直接设置切入点表达式
     * pointcut 属性：绑定通知的切入点表达式，优先级高于 value，默认为 ""
     * throwing 属性：与方法中的异常参数名称一致，
     *
     * @param ex：捕获的异常对象，名称与 throwing 属性值一致
     */
    @AfterThrowing(pointcut = "aspectPointcut()", throwing = "ex")
    public void aspectAfterThrowing(JoinPoint jp, Exception ex) {
        String methodName = jp.getSignature().getName();
        if (ex instanceof ArithmeticException) {
            log.error("【异常通知】" + methodName + "方法算术异常（ArithmeticException）：" + ex.getMessage());
        } else {
            log.error("【异常通知】" + methodName + "方法异常：" + ex.getMessage());
        }
    }

    /**
     * 环绕通知
     * 1、@Around 的 value 属性：绑定通知的切入点表达式。可以关联切入点声明，也可以直接设置切入点表达式
     * 2、Object ProceedingJoinPoint.proceed(Object[] args) 方法：继续下一个通知或目标方法调用，返回处理结果，如果目标方法发生异常，则 proceed 会抛异常.
     * 3、假如目标方法是控制层接口，则本方法的异常捕获与否都不会影响目标方法的事务回滚
     * 4、假如目标方法是控制层接口，本方法 try-catch 了异常后没有继续往外抛，则全局异常处理 @RestControllerAdvice 中不会再触发
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value = "aspectPointcut()")
    public Object handleControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {

        log.info("【环绕通知】执行接口开始，方法={}，参数={} ", joinPoint.getSignature(), Arrays.asList(joinPoint.getArgs()).toString());
        //继续下一个通知或目标方法调用，返回处理结果，如果目标方法发生异常，则 proceed 会抛异常.
        //如果在调用目标方法或者下一个切面通知前抛出异常，则不会再继续往后走.
        Object proceed = joinPoint.proceed(joinPoint.getArgs());

        log.info("【环绕通知】执行接口结束，方法={}, 返回值={}", joinPoint.getSignature(), proceed);
        return proceed;
    }

}
