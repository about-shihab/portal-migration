var basicBillUrl = basicUrl + '/api/v1/billInfo';
var amountWithoutFee = 0;
$(document).ready(function () {
    $('#termCondition').on('change', function () {

        if ($(this).is(':checked')) {
            $('#payNowBtn').removeClass('disabled');
        } else {
            $('#payNowBtn').addClass('disabled');
        }
    });

    getNonMeteredPaymentInfo();
});


function getNonMeteredPaymentInfo() {
    let api = basicBillUrl + '/nonMeteredPaymentInfo';
    $.ajax({
        type: "GET",
        url: api,
        contentType: 'application/json',
        error: function (res) {
            const errMsg = getErrorMessage(res.responseJSON);
            showErrorSwal(errMsg);
        },
        success: function (res) {
            if (res.statusCode === 200) {
                $('#paymentRef').html(`${res.content.paymentRef}`);
                amountWithoutFee = res.content.totalAmount;
                $('#payAmount').html(`${formatNumber(amountWithoutFee)} /-`);
                calculateTotalAmount();
            } else {
                const errMsg = getErrorMessage(res.responseJSON);
                showErrorSwal(errMsg);
            }
        }
    })
}


$('input[name="paymentMethod"]').change(function () {
    calculateTotalAmount(this);
});

function calculateTotalAmount(sectedCardType) {
    let cardType = sectedCardType ? $(sectedCardType).val() : $('input[name="paymentMethod"]:checked').val();
    let fee = calculateDbblFee(cardType, amountWithoutFee);
    $('#fee').html(fee + ' /-');
    let totalPayableAmount = amountWithoutFee + fee;
    $('#totalAmount').html(totalPayableAmount + ' /-');
    $('#payNowBtn').text('Pay ' + formatNumber(totalPayableAmount) + ' BDT');
}

$('#payNowBtn').on('click', function () {
    if ($(this).hasClass('disabled')) {
        return;
    }
    let api = basicBillUrl + '/getDbblPaymentTrId';
    let cardType = $('input[name="paymentMethod"]:checked').val();
    if (cardType == null || cardType == '') {
        return;
    }
    let payload = JSON.stringify({cardtype: Number(cardType)});
    $.ajax({
        type: "POST",
        url: api,
        data: payload,
        contentType: 'application/json',
        error: function (res) {
            const errMsg = getErrorMessage(res);
            showErrorSwal(errMsg);
        },
        success: function (res) {
            if (res.statusCode === 200) {
                let carType = res.content.cardType;
                let transactionId = res.content.transactionId;
                let gatewayUrl = res.content.gatewayUrl;
                let url = `${gatewayUrl}?card_type=${carType}&trans_id=${encodeURIComponent(transactionId)}`;
                window.location.href = url;
            } else {
                const errMsg = getErrorMessage(res);
                showErrorSwal(errMsg);
            }
        }
    })
})