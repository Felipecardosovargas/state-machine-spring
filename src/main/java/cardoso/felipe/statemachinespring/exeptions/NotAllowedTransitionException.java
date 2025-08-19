package cardoso.felipe.statemachinespring.exeptions;

import cardoso.felipe.statemachinespring.events.PaymentEvent;
import cardoso.felipe.statemachinespring.states.PaymentState;

public class NotAllowedTransitionException extends RuntimeException {
    private static final String MESSAGE = "This transition is not allowed from the current state '%s' to '%s'.";

    public NotAllowedTransitionException(PaymentState currentStatus, PaymentEvent nextStatus) {
        super(String.format(MESSAGE, currentStatus, nextStatus));
    }
}
