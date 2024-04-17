$(document).ready(function () {
    $('.linkPlus').click(function () {
        const productId = $(this).attr("pid");
        updateQuantity(productId, 1);
    });

    $('.linkMinus').click(function () {
        const productId = $(this).attr("pid");
        updateQuantity(productId, -1);
    });

    $('.link-remove').click(function (evt) {
        evt.preventDefault();
        let url = $(this).attr("href");
        removeProduct(url);
    });
});

function updateQuantity(productId, change) {
    const inputValue = $('#input' + productId);
    let quantity = parseInt(inputValue.val()) + change;
    if (quantity < 1) {
        showWarningModal('Minimum quantity is 1');
        return;
    }

    if (quantity > 5) {
        showWarningModal('Maximum quantity is 5');
        return;
    }
    updateQuantityInDb(productId, quantity);
    inputValue.val(quantity);
}

function updateQuantityInDb(productId, quantity) {
    $.ajax({
        url: contextPath + 'cart/update/' + productId + '/' + quantity,
        type: 'POST',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: {
            productId: productId,
            quantity: quantity
        }
    }).done(function (result) {
        updateSubtotal(productId, result);
        updateTotal();
    }).fail(function (result) {
        showErrorModal(result.responseText);
    });
}

function updateTotal() {
    let subtotals = $(".subtotal");
    if (subtotals.length === 0) {
        $("#cartTable").remove();
        $("#sectionEmptyCartMessage").removeClass("d-none");
    } else {
        let total = 0.0;
        subtotals.each(function (index, element) {
            total += parseFloat(clearCurrencyFormat(element.innerHTML));
        })
        let formattedNumber = formatCurrency(total);
        $('#estimatedTotal').text(formattedNumber);
    }
}

function updateSubtotal(productId, subtotal) {
    let formattedNumber = formatCurrency(subtotal);
    $('#subtotal' + productId).text(formattedNumber);
}

function removeProduct(url) {
    $.ajax({
        url: url,
        type: 'DELETE',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function (result) {
        $('#row' + result).remove();
        updateCountNumber();
        showModalDialog('Shopping Cart', 'Product removed from the cart');
        updateTotal();
    }).fail(function (result) {
        showErrorModal(result.responseText);
    });
}

function updateCountNumber() {
    $(".divCount").each(function (index, element) {
        element.innerHTML = "" + (index + 1);
    });
}

function formatCurrency(amount){
    return $.number(amount, decimalDigits, decimalPointType, thousandsPointType);
}

function clearCurrencyFormat(value){
    let result = value.replaceAll(thousandsPointType, '');
    return result.replaceAll(decimalPointType, '.');
}