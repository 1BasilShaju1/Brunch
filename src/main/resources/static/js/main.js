function togglePasswordVisibility(fieldId) {
    const field = document.getElementById(fieldId);
    if (!field) return;

    if (field.type === "password") {
        field.type = "text";
    } else {
        field.type = "password";
    }
}

function toggleAddressField() {
    const deliveryRadio = document.getElementById("typeDelivery");
    const addressGroup = document.getElementById("deliveryAddressGroup");
    const feeSpan = document.getElementById("summaryDeliveryFee");
    const finalAmountSpan = document.getElementById("summaryFinalAmount");

    if (!deliveryRadio || !addressGroup || !finalAmountSpan) return;

    const subtotal = parseFloat(finalAmountSpan.getAttribute("data-subtotal")) || 0.0;

    if (deliveryRadio.checked) {
        addressGroup.style.display = "block";
        if (feeSpan) feeSpan.textContent = "₹30.00";
        finalAmountSpan.innerHTML = "₹" + (subtotal + 30.0).toFixed(2);
    } else {
        addressGroup.style.display = "none";
        if (feeSpan) feeSpan.textContent = "₹0.00";
        finalAmountSpan.innerHTML = "₹" + subtotal.toFixed(2);
    }
}

function confirmDelete(message) {
    return confirm(message || "Are you sure you want to proceed with deletion?");
}

document.addEventListener("DOMContentLoaded", function () {
    const buttons = document.querySelectorAll(".btn");
    buttons.forEach((btn) => {
        btn.addEventListener("click", function (e) {
            const ripple = document.createElement("span");
            ripple.classList.add("ripple-effect");
            const rect = btn.getBoundingClientRect();
            ripple.style.left = e.clientX - rect.left + "px";
            ripple.style.top = e.clientY - rect.top + "px";
            btn.appendChild(ripple);
            setTimeout(() => {
                ripple.remove();
            }, 600);
        });
    });

    toggleAddressField();
});
