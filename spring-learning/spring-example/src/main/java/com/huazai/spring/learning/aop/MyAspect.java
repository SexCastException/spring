package com.huazai.spring.learning.aop;

import com.huazai.spring.learning.aop.dao.CityDao;
import com.huazai.spring.learning.aop.dao.Dao;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @author pyh
 * @date 2020/3/22 12:45
 */
@Aspect
@Component
public class MyAspect {

    /**
     * introduction ——引入
     */
    @DeclareParents(value = "com.huazai.spring.learning.aop.dao.*", defaultImpl = CityDao.class)
    public Dao dao;

    /**
     * execution表达式：粒度可以精确到修饰符，返回值，方法名和形参列表
     */
    @Pointcut("execution(public * com.huazai.spring.learning.aop.dao.CityDao.*(..))")
    public void executionPointCut() {
    }

    /**
     * within表达式：粒度只能具体到某个类
     */
    @Pointcut("within(com.huazai.spring.learning.aop.dao.*)")
    public void withinPointCut() {
    }

    @Pointcut("@annotation(com.huazai.spring.learning.aop.MyAspectAnnotation)")
    public void atAnnotationPointCut() {
    }

    /**
     * this：代理对象，表示切点代理对象是否为指定的对象子类
     */
    @Pointcut("this(com.huazai.spring.learning.aop.dao.Dao)")
    public void thisPointCut() {
    }

    /**
     * target：目标对象，即被代理的对象，
     */
    @Pointcut("target(com.huazai.spring.learning.aop.dao.Dao)")
    public void targetPointCut() {
    }

    /**
     * args表达式：不关心具体的报名类型和修饰符，粒度更大，只关心具体的方法的参数列表
     */
    @Pointcut("args(java.lang.String)")
    public void argsPointCut() {
    }

    /**
     * 通知包含两部分：位置、逻辑
     *
     * @param joinPoint
     */
    @Before(value = "executionPointCut()&&argsPointCut()&&withinPointCut()")
    public void before(JoinPoint joinPoint) {
        System.out.println("before...");
        System.out.println("target:" + joinPoint.getTarget().getClass().toString());
        System.out.println("this:" + joinPoint.getThis().getClass().toString());
    }

    @Before(value = "atAnnotationPointCut()")
    public void before1() {
        System.out.println("before11...");
    }

    @After(value = "argsPointCut()")
    public void after() {
        System.out.println("after..");
    }

    //    @Around(value = "executionPointCut()")
    public void around(ProceedingJoinPoint joinPoint) {
        System.out.println("around..");
    }
}
