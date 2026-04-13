const $currentStepInput = $(`#cust-info-part, #otp-part, #verify-part`).filter(':visible');
const stepper = new Stepper($('.bs-stepper')[0]);

$(document).ready(function () {
    updateActiveStep();
    $('.btn-next').on('click', function (e) {
        e.preventDefault();
        if (isMismatchSteps()) {
            updateActiveStep();
        } else {
            isActivePartValid();
            if (isActivePartValid()) {
                let activePartID = $(".bs-stepper-content").find(".active").attr("id");
                if (activePartID === 'cust-info-part') {
                    if ($('#start-reg').length) {
                        buttonLoadingToggle('#start-reg', 'loading');
                        validateCustomer();
                    } else if ($('#start-recov').length) {
                        buttonLoadingToggle('#start-recov', 'loading');
                        forgotPassReq();
                    }

                } else if (activePartID === 'otp-part') {
                    buttonLoadingToggle('#otp-submit', 'loading');
                    verifyOTP();
                } else {
                    if ($('#forgotBtn').length) {
                        changePassword();
                    }
                }
            }
        }
    });
    var signUpForgotCommonValidation = {
        rules: {
            custCode: {
                required: true
            },
            forgotCust: {
                required: true
            },
            mobileNo: {
                required: true,
                pattern: /^[0][1]\d{9}$/,
            },
            otp: {
                required: true,
                maxlength: 10
            },

            password: {
                required: true,
                minlength: 8,
                maxlength: 100,
                pattern: /^(?=.*[A-Za-z])(?=.*\d).{8,}$/
            },
            password2: {
                required: true,
                equalTo: "#password"
            },
            captcha_form: {
                required: true,
                equalToCaptcha: true
            }
        },
        messages: {
            custCode: {
                required: "Please enter customer code"
            },
            forgotCust: {
                required: "Please enter customer code"
            },
            mobileNo: {
                required: "Please enter mobile number",
                pattern: "Mobile number must start with 01 and contain 11 digits",
            },
            otp: {
                required: "Please enter OTP"
            },
            password: {
                required: "Please enter password",
                minlength: "Password must be at least 8 characters",
                maxlength: "Password can't exceed 100 characters",
                pattern: "Password must contain a letter and a digit"
            },
            password2: {
                required: "Please confirm your password",
                equalTo: "Passwords do not match"
            },
            captcha_form: {
                required: "Please enter captcha",
            }
        },
        ...commonValidationOptions
    }
    $("#signUpForm").validate(signUpForgotCommonValidation);
    $("#forgotPassForm").validate(signUpForgotCommonValidation);
});

function makeAjaxRequest(options) {
    const {
        type,
        url,
        data,
        contentType,
        errorSelector,
        successCallback,
        successSelector,
        errorMessageSelector,
        loadingButtonSelector,
        loadingButtonClass,
    } = options;

    $.ajax({
        type: type,
        url: url,
        data: JSON.stringify(data),
        contentType: contentType,
        error: function (res) {
            initCaptcha();
            signUpErrMsg(errorMessageSelector, res);
            buttonLoadingToggle(loadingButtonSelector, 'error');
        },
        success: function (res) {
            if (res.statusCode === 201 || res.statusCode === 200) {
                $(successSelector).text(getErrorMessage(res));
                successCallback(res);
            } else {
                initCaptcha();
                signUpErrMsg(errorMessageSelector, res);
                buttonLoadingToggle(loadingButtonSelector, loadingButtonClass);
            }
        }
    });
}

function validateCustomer() {
    const customerCode = $('#custCode').val();
    const phoneNo = $('#mobileNo').val();
    const api = signupUrl + '/validate';
    const payload = {customerCode, phoneNo};

    makeAjaxRequest({
        type: "POST",
        url: api,
        data: payload,
        contentType: 'application/json',
        errorSelector: '#custErrMsg',
        successCallback: function (res) {
            startOTPTimer(initialOTPTimerDuration);
            updateActiveStep();
        },
        successSelector: '#otpMsg',
        errorMessageSelector: '#custErrMsg',
        loadingButtonSelector: '#start-reg',
        loadingButtonClass: 'error',
    });
}

function forgotPassReq() {
    const customerCode = $('#forgotCust').val();
    const api = basicAuth + auth_forgot_password;
    const payload = {username: customerCode};

    makeAjaxRequest({
        type: "POST",
        url: api,
        data: payload,
        contentType: 'application/json',
        errorSelector: '#custErrMsg',
        successCallback: function (res) {
            startOTPTimer(initialOTPTimerDuration);
            updateActiveStep();
        },
        successSelector: '#otpMsg',
        errorMessageSelector: '#custErrMsg',
        loadingButtonSelector: '#start-recov',
        loadingButtonClass: 'error',
    });
}

function changePassword() {
    const api = basicAuth + set_new_password;
    const payload = {
        newPassword: $('#password').val(),
        confirmPassword: $('#password2').val()
    };

    makeAjaxRequest({
        type: "PUT",
        url: api,
        data: payload,
        contentType: 'application/json',
        errorSelector: '#custErrMsg',
        successCallback: function (res) {
            redirectLogin(getErrorMessage(res));
            updateActiveStep();
        },
        successSelector: '#otpMsg',
        errorMessageSelector: '#custErrMsg',
        loadingButtonSelector: '#start-recov',
        loadingButtonClass: 'error',
    });
}

function verifyOTP() {
    const otp = $('#otp').val();
    const api = signupUrl + '/otp?otp=' + otp; // Use query parameter

    makeAjaxRequest({
        type: "POST",
        url: api,
        contentType: 'application/json',
        errorSelector: '#otpMsg',
        successCallback: function (res) {
            updateActiveStep();
        },
        successSelector: '#otpMsg',
        errorMessageSelector: '#otpMsg',
        loadingButtonSelector: '#otp-submit',
        loadingButtonClass: 'error',
    });
}


function signUpErrMsg(htmlId, err) {
    $(htmlId).text(getErrorMessage(err)).addClass("text-danger");
}


function isActivePartValid() {
    let isValid = true;
    $currentStepInput.find(':input').each(function () {
        if (!$(this).valid()) {
            isValid = false;
        }
    });
    return isValid;
}

function updateActiveStep() {
    let registrationSteps = parseInt(getCookie("registrationSteps"), 10);
    if (registrationSteps) {
        stepper.to(registrationSteps);
    } else {
        stepper.to(1);
    }
}

function isMismatchSteps() {
    return getCookie("registrationSteps") != null && stepper._currentIndex + 1 !== parseInt(getCookie("registrationSteps"), 10);
}

function getCookie(name) {
    const cookies = document.cookie.split(';');
    for (let i = 0; i < cookies.length; i++) {
        const cookie = cookies[i].trim();
        const cookieParts = cookie.split('=');
        if (cookieParts[0] === name) {
            return cookieParts[1];
        }
    }
    return null;
}

function startOTPTimer(duration) {
    let timer = duration;
    const otpTimer = $("#otpTimer");

    const timerInterval = setInterval(function () {
        const minutes = Math.floor(timer / 60);
        const seconds = timer % 60;

        otpTimer.text(`${minutes}:${seconds < 10 ? '0' : ''}${seconds}`);

        if (--timer < 0) {
            $("#otpTimerMsg").html(` <p class="text-danger">Times up! Refresh the page or <a href="/signup">click here</a> to refresh manually.</p>`);
            clearInterval(timerInterval);
        }
    }, 1000);
}

function redirectLogin(msg) {
    let redirectDiv = `<p class="text-center text-success">${msg}</p>
                             <div class="row justify-content-center">
                            <h6>Redirecting to <a href="/login">Login </a> Page within &nbsp;</h6>
                            <h6 id="countdown"></h6>
                          </div>`
    $('#forgotPassForm').html(redirectDiv).fadeIn();
    var countdown = 5;
    var countdownDisplay = $('#countdown');

    var countdownInterval = setInterval(function () {
        countdownDisplay.text(` ${countdown} seconds`);
        countdown--;

        if (countdown < 0) {
            clearInterval(countdownInterval);
            window.location.href = auth_login;
        }
    }, 1000);

}
