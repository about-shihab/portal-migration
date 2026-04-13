var basicBillUrl = basicUrl + '/api/v1/billInfo';
$(document).ready(function () {

    var transactionId = localStorage.getItem("dbblTransactionId");
    if (transactionId === null || transactionId === undefined || transactionId === "" || transactionId.length !== 28) {
        window.location = '/bill-details';
        return;
    }
    const api = basicBillUrl + '/getDbblPaymentResult';

    const payload = {transid: transactionId};
    $.ajax({
        type: "PUT",
        url: api,
        data: JSON.stringify(payload),
        contentType: 'application/json',
        error: function (res) {
            handlePaymentError(res);
        },
        success: function (res) {
            if (res.statusCode === 200) {
                handlePaymentSuccess(res);
            } else {
                handlePaymentError(res);
            }
        }
    })
});

function handlePaymentSuccess(res) {
    $('#payment-confirm-msg').text('Payment Successful');
    $('.payment-icon i').removeClass('fas fa-check-circle').addClass('fas fa-check-circle text-success');
    $('#payment-details').empty();
    var detailsHTML = '';
    detailsHTML += '<div class="col-md-offset-4">';
    detailsHTML += '<ul class="nav flex-column">';

    $.each(res.content, function (key, value) {
        detailsHTML += '<li class="nav-item">';
        detailsHTML += '<div class="nav-link text-primary">';
        detailsHTML += key + ':';
        detailsHTML += '<span class="float-right text-dark text-bold">' + value + '</span>';
        detailsHTML += '</div>';
        detailsHTML += '</li>';
    });

    detailsHTML += '</ul>';
    detailsHTML += '</div>';

    $('#payment-details').append(detailsHTML);
}

function handlePaymentError(res) {
    const errMsg = getErrorMessage(res);
    $('.payment-icon i').removeClass('fas fa-check-circle').addClass('fas fa-times-circle text-danger');
    $('#payment-confirm-msg').text('Payment Failed');
    $('#payment-details').html(`<h5 class="text-center text-danger text-hov-white">${errMsg}</h5>`);

}
