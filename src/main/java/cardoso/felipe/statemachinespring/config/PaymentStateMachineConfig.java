package cardoso.felipe.statemachinespring.config;

import cardoso.felipe.statemachinespring.events.PaymentEvent;
import cardoso.felipe.statemachinespring.states.PaymentState;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Configuration
@EnableStateMachineFactory
public class PaymentStateMachineConfig extends EnumStateMachineConfigurerAdapter<PaymentState, PaymentEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        states
                .withStates()
                .initial(PaymentState.PENDING)
                .state(PaymentState.PENDING, onPending(), onPendingExit())
                .state(PaymentState.COMPLETED, onCompleted(), onCompletedExit())
                .state(PaymentState.FAILED, onFailed(), onFailedExit())
                .end(PaymentState.FAILED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(PaymentState.PENDING).target(PaymentState.COMPLETED).event(PaymentEvent.PAY)
                .and()
                .withExternal()
                .source(PaymentState.PENDING).target(PaymentState.FAILED).event(PaymentEvent.CANCEL)
                .and()
                .withExternal()
                .source(PaymentState.FAILED).target(PaymentState.PENDING).event(PaymentEvent.RETRY)
                .and()
                .withExternal()
                .source(PaymentState.COMPLETED).target(PaymentState.PENDING).event(PaymentEvent.RESET)
                .action(onReset());
    }

    private Action<PaymentState, PaymentEvent> onReset() {
        return context -> System.out.println("→ Executando ação de RESET e indo para PENDING");
    }

    private Action<PaymentState, PaymentEvent> onPending() {
        return context -> System.out.println("→ Entering PENDING state");
    }

    private Action<PaymentState, PaymentEvent> onPendingExit() {
        return context -> System.out.println("← Exiting PENDING state");
    }

    private Action<PaymentState, PaymentEvent> onCompleted() {
        return context -> System.out.println("→ Entering COMPLETED state");
    }

    private Action<PaymentState, PaymentEvent> onCompletedExit() {
        return context -> System.out.println("← Exiting COMPLETED state");
    }

    private Action<PaymentState, PaymentEvent> onFailed() {
        return context -> System.out.println("→ Entering FAILED state");
    }

    private Action<PaymentState, PaymentEvent> onFailedExit() {
        return context -> System.out.println("← Exiting FAILED state");
    }
}
