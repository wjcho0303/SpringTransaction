package springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Slf4j
class MemberServiceTest {
    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired LogRepository logRepository;


    /**
     * memberService        @Transactional: OFF
     * memberRepository     @Transactional: ON
     * logRepository        @Transactional: ON
     */
    @Test
    void outerTxOff_success() {
        String username = "outerTxOff_success";

        memberService.joinV1(username);

        assertThat(memberRepository.findByUsername(username)).isNotEmpty();
        assertThat(logRepository.findByMessage(username)).isNotEmpty();
    }


    /**
     * memberService        @Transactional: OFF
     * memberRepository     @Transactional: ON
     * logRepository        @Transactional: ON
     */
    @Test
    void outerTxOff_fail() {
        String username = "로그예외_outerTxOff_fail";

        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        assertThat(memberRepository.findByUsername(username)).isNotEmpty();
        assertThat(logRepository.findByMessage(username)).isEmpty();
    }


    /**
     * memberService        @Transactional: ON
     * memberRepository     @Transactional: OFF
     * logRepository        @Transactional: OFF
     */
    @Test
    void singleTx() {
        String username = "singleTx";

        memberService.joinV1(username);

        assertThat(memberRepository.findByUsername(username)).isNotEmpty();
        assertThat(logRepository.findByMessage(username)).isNotEmpty();
    }


    /**
     * memberService        @Transactional: ON
     * memberRepository     @Transactional: ON
     * logRepository        @Transactional: ON
     */
    @Test
    void outerTxON_success() {
        String username = "outerTxON_success";

        memberService.joinV1(username);

        assertThat(memberRepository.findByUsername(username)).isNotEmpty();
        assertThat(logRepository.findByMessage(username)).isNotEmpty();
    }
    @Test
    void outerTxON_fail() {
        String username = "로그예외_outerTxON_fail";

        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        assertThat(memberRepository.findByUsername(username)).isEmpty();
        assertThat(logRepository.findByMessage(username)).isEmpty();
    }
    @Test
    void recoverException_fail() {
        String username = "로그예외_recoverException_fail";

        // memberService.joinV2(username);
        assertThatThrownBy(() -> memberService.joinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);

        // assertThat(memberRepository.findByUsername(username)).isNotEmpty();
        assertThat(memberRepository.findByUsername(username)).isEmpty();
        assertThat(logRepository.findByMessage(username)).isEmpty();
    }

}