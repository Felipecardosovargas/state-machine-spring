package cardoso.felipe.statemachinespring.service;

import cardoso.felipe.statemachinespring.entities.Payment;
import cardoso.felipe.statemachinespring.events.PaymentEvent;
import cardoso.felipe.statemachinespring.states.PaymentState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Autowired
    private StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;

    public Mono<Payment> processPayment(Payment payment, PaymentEvent event) {
        StateMachine<PaymentState, PaymentEvent> stateMachine = stateMachineFactory.getStateMachine(payment.getId().toString());

        return Mono.from(stateMachine.stopReactively())
                .then(Mono.fromRunnable(() -> {
                    stateMachine.getStateMachineAccessor().doWithAllRegions(accessor -> {
                        accessor.resetStateMachineReactively(
                                new DefaultStateMachineContext<>(payment.getState(), null, null, null)
                        ).block();
                    });
                }))
                .then(Mono.from(stateMachine.startReactively()))
                .flatMap(started ->
                        Mono.from(stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(event).build())))
                )
                .flatMap(result -> {
                    if (result.getResultType() == StateMachineEventResult.ResultType.ACCEPTED) {
                        if (stateMachine.getState() != null) {
                            payment.setState(stateMachine.getState().getId());
                            return Mono.just(payment);
                        } else {
                            return Mono.error(new IllegalStateException("Estado inválido após transição."));
                        }
                    } else {
                        return Mono.error(new IllegalStateException("Transição não permitida para evento: " + event));
                    }
                });
    }
}

