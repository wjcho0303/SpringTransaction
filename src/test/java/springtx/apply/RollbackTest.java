package springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Slf4j
public class RollbackTest {

    static class MyException extends Exception {
    }

    @TestConfiguration
    static class RollbackTestConfig {
        @Bean
        RollbackService rollbackService() {
            return new RollbackService();
        }
    }

    static class RollbackService {
        // 런타임 예외 발생: 롤백
        @Transactional
        public void runtimeException() {
            log.info("call runtimeException");
            throw new RuntimeException();
        }

        // 체크 예외 발생: 커밋
        @Transactional
        public void checkedException() throws MyException {
            log.info("call checkedException");
            throw new MyException();
        }

        // 체크 예외 rollbackFor 처리하기\
        @Transactional(rollbackFor = MyException.class)
        public void rollbackFor() throws MyException {
            log.info("call rollbackFor checkedException");
            throw new MyException();
        }
    }

    @Autowired RollbackService rollbackService;

    @Test
    void runtimeExceptionTest() {
        assertThatThrownBy(() -> rollbackService.runtimeException())
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void checkedException() throws MyException {
        assertThatThrownBy(() -> rollbackService.checkedException())
                .isInstanceOf(MyException.class);
    }

    @Test
    void rollbackFor() throws MyException {
        assertThatThrownBy(() -> rollbackService.rollbackFor())
                .isInstanceOf(MyException.class);
    }

}
