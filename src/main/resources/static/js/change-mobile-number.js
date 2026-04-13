const basicUser = basicUrl + '/api/v1/users';

$("#changeMobileNumberForm").validate({
    rules: {
        mobileNumber: {
            required: true, pattern: /^01\d{9}$/
        }, newMobileNumber: {
            required: true, pattern: /^01\d{9}$/,
            notEqualTo: "#mobileNumber"
        }, confirmNewMobileNumber: {
            required: true, equalTo: "#newMobileNumber"
        }, otp: {
            required: true
        }
    },
    messages: {
        mobileNumber: {
            required: "Please enter current mobile number", pattern: "Please enter current mobile number",
        }, newMobileNumber: {
            required: "Please enter new mobile number", pattern: "Please enter new mobile number",
            notEqualTo: "New mobile number cannot be the same as the current mobile number",
        }, confirmNewMobileNumber: {
            required: "Please confirm new mobile number", equalTo: "New mobile numbers do not match",
        }, otp: {
            required: "Please enter OTP"
        }
    },
    ...commonValidationOptions,
    submitHandler: function (form) {
        Swal.fire({
            title: 'Change Mobile Number?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: 'Change',
            confirmButtonColor: '#3085d6',
            cancelButtonText: 'Cancel',
            cancelButtonColor: '#d33',
            showLoaderOnConfirm: true,
            preConfirm: () => {
                return changeMobileNumber()
                    .then(function (res) {
                        if (res.statusCode === 200) {
                            Swal.fire({
                                icon: 'success', title: 'Success', text: res.message,
                            }).then(() => {
                                window.location.reload();
                            });
                        } else {
                            let errorMsg = getErrorMessage(res.responseJSON);
                            showErrorSwal(errorMsg);
                        }
                    })
                    .catch(function (error) {
                        let errorMsg = getErrorMessage(error.responseJSON);
                        showErrorSwal(errorMsg);
                    });
            },
        });
    }
});

function changeMobileNumber() {
    return new Promise(function (resolve, reject) {
        const mobileNumber = $('#mobileNumber').val();
        const newMobileNumber = $('#newMobileNumber').val();
        const confirmNewMobileNumber = $('#confirmNewMobileNumber').val();
        const otp = $('#otp').val();

        const api = basicUser + '/changeMobileNumber';
        const payload = {mobileNumber, newMobileNumber, confirmNewMobileNumber, otp};

        $.ajax({
            type: "PUT",
            url: api,
            data: JSON.stringify(payload),
            contentType: 'application/json',
            error: function (error) {
                reject(error);
            },
            success: function (res) {
                resolve(res);
            }
        });
    });
}

$('#sendOTPButton').on('click', function () {
    let isValid = true;
    const validator = $("#changeMobileNumberForm").validate();

    // Validate mobile number
    const mobileNumber = $("#mobileNumber").val();
    if (!/^01\d{9}$/.test(mobileNumber)) {
        validator.showErrors({
            "mobileNumber": "Please enter current mobile number"
        });
        isValid = false;
    }

    // Validate new mobile number
    const newMobileNumber = $("#newMobileNumber").val();
    if (!/^01\d{9}$/.test(newMobileNumber)) {
        validator.showErrors({
            "newMobileNumber": "Please enter new mobile number"
        });
        isValid = false;
    }

    if (!isValid) {
        return;
    }

    const api = basicUser + '/changeMobileNumberOtpRequest';
    const payload = {mobileNumber, newMobileNumber};

    const updateUiAfterOtp = () => {
        $(this).attr('disabled', true);
        $('#otpMessage').text("\tAn OTP has been sent to your mobile number " + mobileNumber + '.');
        $('#OTPinput').removeAttr('hidden').show();
        $('#confirmNewMobileNumberInput').removeAttr('hidden').show();
        $('#changePhoneNumberButton').removeAttr('hidden').show();

        let countdownTime = 180; // 3 minutes in seconds
        const countdownElement = $('#timer');

        countdownElement.text(`Valid for: ${formatTime(countdownTime)}.`);

        const countdownInterval = setInterval(function () {
            countdownTime--;
            countdownElement.text(`Valid for: ${formatTime(countdownTime)}.`);

            if (countdownTime <= 0) {
                clearInterval(countdownInterval);
                $('#otpMessage').text('');
                countdownElement.text("\tTime's up! Please request a new OTP.");
                $('#sendOTPButton').attr('disabled', false);
            }
        }, 1000);
    }

    $.ajax({
        type: "PUT",
        url: api,
        data: JSON.stringify(payload),
        contentType: 'application/json',
        error: function (error) {
            let errorMsg = getErrorMessage(error.responseJSON);
            showErrorSwal(errorMsg);
        },
        success: function (res) {
            updateUiAfterOtp();
        }
    });
});

function formatTime(secondsOnly) {
    const minutes = Math.floor(secondsOnly / 60);
    const seconds = secondsOnly % 60;
    return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
}

function getChangeMobileNumberLogs() {
    const api = basicUser + '/changeMobileNumberLog';

    makeAjaxRequest(api, function (response) {
        const logs = response.content || [];

        const logMessages = logs.length > 0
            ? logs.map(log => {
                const date = new Date(log.changedDateTime);
                const formattedDate = `${('0' + date.getDate()).slice(-2)}-${date.toLocaleString('default', {month: 'short'})}-${date.getFullYear()}`;
                return `<li class="list-group-item">On <span class="text-primary">${formattedDate}</span>, the mobile number was changed from <strong>${log.oldMobileNo}</strong> to <strong>${log.newMobileNo}</strong>.</li>`;
            }).join('')
            : '<li>No change logs found.</li>';

        $('#changeLogList').html(logMessages);
    });
}


function makeAjaxRequest(api, successCallback, errorCallback) {
    $.ajax({
        type: 'GET',
        url: api,
        contentType: 'application/json',
        success: successCallback,
        error: function (res) {
            const errMsg = getErrorMessage(res.responseJSON);
            showErrorSwal(errMsg);
            if (errorCallback) errorCallback();
        },
        complete: function () {
            $(".overlay").hide();
        }
    });
}
