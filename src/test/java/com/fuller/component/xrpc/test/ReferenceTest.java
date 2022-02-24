package com.fuller.component.xrpc.test;

import com.fuller.component.xrpc.annotation.XRPC;
import com.fuller.component.xrpc.annotation.XRPCReference;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Allen Huang on 2022/2/23
 */
@SpringBootTest(classes = ReferenceTest.Config.class)
public class ReferenceTest {

    @Autowired
    private ReferenceService referenceService;

    @Test
    public void genericsMethodTest() {
        Student student = new Student();
        student.setAge(1);
        student.setName("Java");
        Computer computer = new Computer();
        computer.setName("computer");
        computer.setCoreSize(48);
        referenceService.demoService.genericsMethod(student, computer);
    }

    @Test
    public void multipleTest() {
        referenceService.demoService.multiple2("args1", "args2");
        referenceService.demoService.multiple3("args1", "args2", "args3");
        referenceService.demoService.multiple4("args1", "args2", "args3", "args4");
        referenceService.demoService.multiple5("args1", "args2", "args3", "args4", "args5");
    }

    @Test
    public void injectTest() {
        System.out.println(referenceService.demoService.normal("Java"));
        referenceService.demoService.onlyParameter("Java");
        System.out.println(referenceService.demoService.onlyReturn());
        referenceService.demoService.allVoid();
    }

    @Test
    public void booleanTest() {
        Assertions.assertThat(referenceService.booleanService.boolMethod1(Boolean.TRUE)).isTrue();
        Assertions.assertThat(referenceService.booleanService.boolMethod1(Boolean.FALSE)).isFalse();
        Assertions.assertThat(referenceService.booleanService.boolMethod1(null)).isNull();
        Assertions.assertThat(referenceService.booleanService.boolMethod2(true)).isTrue();
        Assertions.assertThat(referenceService.booleanService.boolMethod2(false)).isFalse();
    }

    @Test
    public void numberTest() {
        Assertions.assertThat(referenceService.numberService.integerMethod(1)).isEqualTo(1);
        Assertions.assertThat(referenceService.numberService.integerMethod(null)).isNull();
        Assertions.assertThat(referenceService.numberService.intMethod(1)).isEqualTo(1);
        Assertions.assertThat(referenceService.numberService.longMethod(1L)).isEqualTo(1L);
        Assertions.assertThat(referenceService.numberService.longMethod(null)).isNull();
        Assertions.assertThat(referenceService.numberService.long1Method(1L)).isEqualTo(1L);
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config {

        @Bean
        public ReferenceService referenceService() {
            return new ReferenceService();
        }

        @Bean
        public DemoService demoService() {
            return new DemoServiceImpl();
        }

        @Bean
        public BooleanService booleanService() {
            return new BooleanServiceImpl();
        }

        @Bean
        public NumberService numberService() {
            return new NumberServiceImpl();
        }

    }

    public static class ReferenceService {

        @XRPCReference
        private DemoService demoService;

        @XRPCReference
        private BooleanService booleanService;

        @XRPCReference
        private NumberService numberService;

    }

    @XRPC(hostname = "localhost")
    public interface NumberService {
        Integer integerMethod(Integer value);

        int intMethod(int value);

        Long longMethod(Long value);

        long long1Method(long value);
    }

    @Slf4j
    public static class NumberServiceImpl implements NumberService {

        @Override
        public Integer integerMethod(Integer value) {
            log.info("[server]调用了Integer方法.值:{}", value);
            return value;
        }

        @Override
        public int intMethod(int value) {
            log.info("[server]调用了int方法.值:{}", value);
            return value;
        }

        @Override
        public Long longMethod(Long value) {
            log.info("[server]调用了Long方法.值:{}", value);
            return value;
        }

        @Override
        public long long1Method(long value) {
            log.info("[server]调用了long方法.值:{}", value);
            return value;
        }
    }

    @XRPC(hostname = "localhost")
    public interface BooleanService {
        Boolean boolMethod1(Boolean value);

        boolean boolMethod2(boolean value);
    }

    @Slf4j
    public static class BooleanServiceImpl implements BooleanService {

        @Override
        public Boolean boolMethod1(Boolean value) {
            log.info("[server]调用了Boolean方法.值:{}", value);
            return value;
        }

        @Override
        public boolean boolMethod2(boolean value) {
            log.info("[server]调用了boolean方法.值:{}", value);
            return value;
        }
    }

    @XRPC(hostname = "localhost")
    public interface DemoService {
        //有参数有返回值
        String normal(String name);

        //有参数无返回值
        void onlyParameter(String name);

        //无参数有返回值
        String onlyReturn();

        //无参数无返回值
        void allVoid();

        //多参数

        void multiple2(String s1, String s2);

        void multiple3(String s1, String s2, String s3);

        void multiple4(String s1, String s2, String s3, String s4);

        void multiple5(String s1, String s2, String s3, String s4, String s5);

        //测试泛型
        void genericsMethod(Student student, Computer computer);
    }

    @Slf4j
    public static class DemoServiceImpl implements DemoService {

        @Override
        public String normal(String name) {
            log.info("[server]{}调用了正常的方法", name);
            return "normal";
        }

        @Override
        public void onlyParameter(String name) {
            log.info("[server]{}调用了只有参数的方法", name);
        }

        @Override
        public String onlyReturn() {
            log.info("[server]调用了只有返回值的方法");
            return "onlyReturn";
        }

        @Override
        public void allVoid() {
            log.info("[server]调用了全部都为空的方法");
        }

        @Override
        public void multiple2(String s1, String s2) {
            log.info("[server]执行了多参数的方法.s1={},s2={}", s1, s2);
        }

        @Override
        public void multiple3(String s1, String s2, String s3) {
            log.info("[server]执行了多参数的方法.s1={},s2={},s3={}", s1, s2, s3);
        }

        @Override
        public void multiple4(String s1, String s2, String s3, String s4) {
            log.info("[server]执行了多参数的方法.s1={},s2={},s3={},s4={}", s1, s2, s3, s4);
        }

        @Override
        public void multiple5(String s1, String s2, String s3, String s4, String s5) {
            log.info("[server]执行了多参数的方法.s1={},s2={},s3={},s4={},s5={}", s1, s2, s3, s4, s5);
        }

        @Override
        public void genericsMethod(Student student, Computer computer) {
            log.info("[server]执行了泛型方法.student={},computer={}", student, computer);
        }

    }

    @Data
    public static class Student {
        private String name;
        private int age;
    }

    @Data
    public static class Computer {
        private String name;
        private int coreSize;
    }

}
