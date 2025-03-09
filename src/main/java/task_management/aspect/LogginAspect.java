//package task_management.aspect;
//
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.AfterReturning;
//import org.aspectj.lang.annotation.AfterThrowing;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Aspect
//@Component
//public class LogginAspect {
//
//    @Pointcut("execution(* task_management.controller.TaskController.*(..))")
//    public void taskControllerMethods() {
//    }
//
//    @Before("taskControllerMethods()")
//    public void logBefore(JoinPoint joinPoint) {
//        log.info("Запуск метода {} начался...", joinPoint.getSignature());
//    }
//
//    @AfterThrowing(pointcut = "taskControllerMethods()", throwing = "exception")
//    public void logAfterThrowing(Exception exception) {
//        log.error("Произошла ошибка: {}", exception.getMessage());
//    }
//
//    @AfterReturning(pointcut = "taskControllerMethods()", returning = "result")
//    public void logAfterReturning(Object result) {
//        log.info("Метод выполнен успешно, результат: {}", result);
//    }
//
//    @Around("taskControllerMethods()")
//    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
//        long startTime = System.currentTimeMillis();
//        Object result = joinPoint.proceed();
//        long endTime = System.currentTimeMillis();
//        log.info("Метод {} выполнен за {} ms", joinPoint.getSignature(), (endTime - startTime));
//        return result;
//    }
//
//
//}
