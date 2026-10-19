package com.brunch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CheckoutForm {

    @NotBlank(message = "Order type is required")
    @Pattern(regexp = "^(Pickup|Delivery)$", message = "Order type must be Pickup or Delivery")
    private String orderType = "Delivery";

    @NotBlank(message = "Payment method is required")
    @Pattern(regexp = "^(Cash on Delivery|UPI)$", message = "Payment method must be Cash on Delivery or UPI")
    private String paymentMethod = "Cash on Delivery";

    private String deliveryAddress;

    public CheckoutForm() {
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
