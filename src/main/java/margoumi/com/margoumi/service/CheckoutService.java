package margoumi.com.margoumi.service;

import margoumi.com.margoumi.dto.Purchase;
import margoumi.com.margoumi.dto.PurchaseResponse;

public interface CheckoutService {

    PurchaseResponse placeOrder(Purchase purchase);
}
