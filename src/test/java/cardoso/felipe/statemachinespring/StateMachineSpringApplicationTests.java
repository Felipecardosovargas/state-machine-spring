package cardoso.felipe.statemachinespring;

import cardoso.felipe.statemachinespring.entities.Payment;
import cardoso.felipe.statemachinespring.events.PaymentEvent;
import cardoso.felipe.statemachinespring.service.PaymentService;
import cardoso.felipe.statemachinespring.states.PaymentState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StateMachineSpringApplicationTests {

    @Autowired
    private StateMachineFactory<PaymentState, PaymentEvent> factory;

    @Autowired
    private PaymentService paymentService;

    private StateMachine<PaymentState, PaymentEvent> machine;

    @BeforeEach
    void setUp() {
        machine = factory.getStateMachine();
        machine.start();
    }

    @Test
    void shouldGoToCompleted_whenPayEventIsSent() {
        machine.sendEvent(Mono.just(MessageBuilder.withPayload(PaymentEvent.PAY).build())).blockLast();
        assertThat(machine.getState().getId()).isEqualTo(PaymentState.COMPLETED);
    }

    @Test
    void shouldGoToFailed_whenCancelEventIsSent() {
        machine.sendEvent(Mono.just(MessageBuilder.withPayload(PaymentEvent.CANCEL).build())).blockLast();
        assertThat(machine.getState().getId()).isEqualTo(PaymentState.FAILED);
    }

    @Test
    void givenPaymentInFailedState_whenRetryEventSent_thenStateShouldBecomePending() {
        Payment payment = new Payment(1L, "order123", PaymentState.FAILED);

        paymentService.processPayment(payment, PaymentEvent.RETRY)
                .doOnNext(result -> {
                    assertNotNull(result, "Resultado não deve ser nulo");
                    assertEquals(PaymentState.PENDING, result.getState(), "Estado deve ser PENDING após RETRY");
                })
                .block();
    }

    @Test
    void shouldGoToPendingFromCompleted_whenResetEventIsSent() {
        machine.sendEvent(Mono.just(MessageBuilder.withPayload(PaymentEvent.PAY).build())).blockLast();

        await().atMost(2, TimeUnit.SECONDS)
                .until(() -> machine.getState().getId() == PaymentState.COMPLETED);

        System.out.println("Estado após transição para COMPLETED: " + machine.getState().getId());
        assertEquals(PaymentState.COMPLETED, machine.getState().getId(), "A máquina deve estar no estado COMPLETED após o evento PAY");

        boolean accepted = machine.sendEvent(Mono.just(MessageBuilder.withPayload(PaymentEvent.RESET).build())).blockLast() != null;
        assertTrue(accepted, "O evento RESET deve ser aceito para processamento");
        System.out.println("Evento RESET aceito? " + accepted);

        await().atMost(2, TimeUnit.SECONDS)
                .until(() -> machine.getState().getId() == PaymentState.PENDING);

        System.out.println("Estado final: " + machine.getState().getId());
        assertEquals(PaymentState.PENDING, machine.getState().getId(), "O estado final deve ser PENDING");
    }
}