package cardoso.felipe.statemachinespring.entities;

import cardoso.felipe.statemachinespring.states.PaymentState;
import lombok.Data;

@Data
public class Payment {

    private Long id;
    private String orderId;
    private PaymentState state;

    public Payment(Long id, String orderId, PaymentState state) {
        this.id = id;
        this.orderId = orderId;
        this.state = state;
    }
}
