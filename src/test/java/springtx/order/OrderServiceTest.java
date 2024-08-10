package springtx.order;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class OrderServiceTest {
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    void complete() throws NotEnoughMoneyException {
        Order order = new Order();
        order.setOrderStatus("정상");

        orderService.order(order);

        Order foundOrder = orderRepository.findById(order.getId()).get();
        assertThat(foundOrder.getPayStatus()).isEqualTo("완료");
    }

    @Test
    void runtimeException() throws RuntimeException {
        Order order = new Order();
        order.setOrderStatus("예외");

        assertThatThrownBy(() -> orderService.order(order))
                .isInstanceOf(RuntimeException.class);

        Optional<Order> foundOrder = orderRepository.findById(order.getId());
        assertThat(foundOrder).isEmpty();
    }

    @Test
    void businessException() {
        Order order = new Order();
        order.setOrderStatus("잔고부족");

        assertThatThrownBy(() -> orderService.order(order))
                .isInstanceOf(NotEnoughMoneyException.class);

        Optional<Order> foundOrder = orderRepository.findById(order.getId());
        assertThat(foundOrder).isNotEmpty();
        assertThat(foundOrder.get().getPayStatus()).isEqualTo("대기");
    }
}