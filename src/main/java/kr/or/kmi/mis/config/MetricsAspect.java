package kr.or.kmi.mis.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * packageName    : kmi.api.config
 * fileName       : DBMetricsAspect
 * author         : KMI_DI
 * date           : 2024-09-11
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-09-11        KMI_DI       the first create
 */
@Aspect
@Component
public class MetricsAspect {
    private final MeterRegistry meterRegistry;
    private final Counter controllerExceptionCounter;
    private final Counter serviceExceptionCounter;

    public MetricsAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.controllerExceptionCounter = meterRegistry.counter("controller_exceptions_total");
        this.serviceExceptionCounter = meterRegistry.counter("service_exceptions_total");
    }

    // DB쿼리 시간 측정
    @Around("execution(* kr.or.kmi.mis.api.mapper..*(..))")
    public Object exception(ProceedingJoinPoint joinPoint) throws Throwable {
        String fullClassName = joinPoint.getSignature().getDeclaringTypeName();
        String className = (fullClassName != null) ? fullClassName.substring(fullClassName.lastIndexOf(".") + 1) : "UnknownClass";
        String methodName = joinPoint.getSignature().getName() != null ? joinPoint.getSignature().getName() : "UnknownMethod";

        Timer timer = Timer.builder("db.query.response.time")
                .description("Time taken to execute DB query")
                .tag("class", className)
                .tag("method", methodName)
                .register(meterRegistry);

        try {
            return timer.record(() -> {
                try {
                    return joinPoint.proceed();  // proceed() 호출
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);  // 예외 변환 및 재던짐
                }
            });
        } catch (Throwable e) {
            meterRegistry.counter("db.query.exceptions", "class", className, "method", methodName).increment();
            throw e;
        }
    }

    // API 요청 시간 및 예외 모니터링
    @Around("execution(*  kr.or.kmi.mis.api.controller..*(..))")
    public Object measureApiRequestTimeAndTrackControllerExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
        String fullClassName = joinPoint.getSignature().getDeclaringTypeName();
        String className = (fullClassName != null) ? fullClassName.substring(fullClassName.lastIndexOf(".") + 1) : "UnknownClass";
        String methodName = joinPoint.getSignature().getName() != null ? joinPoint.getSignature().getName() : "UnknownMethod";


        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            return joinPoint.proceed();  // API 요청 처리
        } catch (Throwable throwable) {
            Counter counter = meterRegistry.counter("controllers_exceptions_total"
                    ,"class", className
                    ,"method", methodName);
            counter.increment();

            throw throwable;
        } finally {
            // 타이머 종료 후 기록
            Timer timer = Timer.builder("api.requests.response.time")
                    .description("Time taken to process API request")
                    .tag("class", className)
                    .tag("method", methodName)
                    .register(meterRegistry);

            sample.stop(timer);  // 타이머 종료 및 기록
        }
    }

    // 서비스 레이어 예외 모니터링
    @Around("execution(* kr.or.kmi.mis.api.serviceimpl..*(..))")
    public Object trackServiceExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
        String fullClassName = joinPoint.getSignature().getDeclaringTypeName();
        String className = (fullClassName != null) ? fullClassName.substring(fullClassName.lastIndexOf(".") + 1) : "UnknownClass";
        String methodName = joinPoint.getSignature().getName() != null ? joinPoint.getSignature().getName() : "UnknownMethod";

        try {
            return joinPoint.proceed();  // 서비스 로직 처리
        } catch (Throwable throwable) {
            // 예외 발생 시 메트릭 카운터를 클래스명과 메소드명으로 라벨링하여 기록
            Counter counter = meterRegistry.counter("service_impl_exceptions_total"
                    ,"class", className
                    ,"method", methodName);
            counter.increment();

            throw throwable;  // 예외 재던짐
        }
    }
}
