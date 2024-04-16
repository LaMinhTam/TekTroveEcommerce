let quantityInput;
$(document).ready(function () {
    quantityInput = $('#input' + productId)

    $('.linkPlus').click(function () {
        incrementQuantity();
    });

    $('.linkMinus').click(function () {
        decrementQuantity();
    });
});

function incrementQuantity() {
    let quantity = parseInt(quantityInput.val());
    console.log(quantityInput)
    if (quantity < 5) {
        quantityInput.val(quantity + 1);
    } else {
        showWarningModal('Maximum quantity is 5');
    }
}

function decrementQuantity() {
    let quantity = parseInt(quantityInput.val());
    if (quantity > 1) {
        quantityInput.val(quantity - 1);
    } else {
        showWarningModal('Minimum quantity is 1');
    }
}