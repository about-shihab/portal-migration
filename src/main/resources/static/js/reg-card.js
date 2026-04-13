function requestDuplicateCard() {
    $.ajax({
        url: "/api/v1/registration-card/issue-request",
        type: "POST",
        success: function (response) {
            $("#otp-section").show();
            OTP_COUNTDOWN();
            $("#reg-msg").text(response.message);
        },
        error: function (error) {
            let errorMsg = getErrorMessage(error.responseJSON);
            showErrorSwal(errorMsg);
        }
    });
}

function validateOtpAndDownload() {
    const otp = $('#otp-input').val();
    $.ajax({
        url: "/api/v1/registration-card/validate-otp",
        type: "POST",
        contentType: "application/json",
        data: otp,
        success: function () {
            window.location.href = `/api/v1/registration-card/download?otp=${otp}`;
        },
        error: function (xhr) {
            showErrorSwal(xhr.responseText);
        }
    });
}


const OTP_COUNTDOWN = () => {
    $('#issue-button').attr('disabled', true);

    let countdownTime = 180; // 3 minutes in seconds
    const countdownElement = $('#otp-timer');

    countdownElement.text(`Valid for: ${formatTime(countdownTime)}.`);

    const countdownInterval = setInterval(function () {
        countdownTime--;
        countdownElement.text(`Valid for: ${formatTime(countdownTime)} Minutes.`);

        if (countdownTime <= 0) {
            clearInterval(countdownInterval);
            $('#otp-timer').text('');
            countdownElement.text("\tTime's up! Please request a new OTP.");
            $('#issue-button').attr('disabled', false);
        }
    }, 1000);
}