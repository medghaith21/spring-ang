package margoumi.com.margoumi.dto;

import lombok.Data;
import margoumi.com.margoumi.models.Address;
import margoumi.com.margoumi.models.Order;
import margoumi.com.margoumi.models.OrderItem;
import margoumi.com.margoumi.models.User;

import java.util.Set;

@Data
public class Purchase {

    private User customer;
    private Address shippingAddress;
    private Address billingAddress;
    private Order order;
    private Set<OrderItem> orderItems;

}