package margoumi.com.margoumi.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import margoumi.com.margoumi.dto.PaymentInfo;
import margoumi.com.margoumi.dto.Purchase;
import margoumi.com.margoumi.dto.PurchaseResponse;

public interface CheckoutService {

    PaymentIntent createPaymentIntent(PaymentInfo paymentInfo) throws StripeException;

    PurchaseResponse placeOrder(Purchase purchase) throws Exception;
}
