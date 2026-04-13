const basicUrl = window.location.protocol + '//' + window.location.hostname + ':' + window.location.port;

const basicAuth = basicUrl + '/api/auth';

const auth_login = "/login";

const signupUrl = '/signup';

const auth_forgot_password = "/forgotPassword";
const set_new_password = "/setForgottenPassword";
const initialOTPTimerDuration = 180;

let buttonToggleText;
let buttonToggleIcon;

function buttonLoadingToggle(btnClass, status) {
    const $btn = $(btnClass);
    if (status === "loading") {
        buttonToggleText = $btn.text();
        buttonToggleIcon = $btn.find("i").prop("outerHTML");
        $btn.prop("disabled", true);
        $btn.html('<i class="fas fa-sync-alt fa-spin"></i> Loading...');
    } else if (status === "success") {
        buttonToggleText = $btn.text();
        $btn.html('<i class="fas fa-check"></i> Success');
    } else if (status === "error") {
        $btn.prop("disabled", false);
        $btn.html(buttonToggleIcon + buttonToggleText);
    }
}

$("input").on("input", function () {
    resetErrorMessage();
});

function resetErrorMessage() {
    $("#error_msg").text('').hide();
}

function logoutAndForwardToLogin() {
    let logoutForm = $("form[name='logoutForm']");
    logoutForm.submit();
}

function getErrorMessage(res) {
    const errors = res?.errors || res?.message || res;
    return Array.isArray(errors) ? errors.map(error => error.message + '\t').join('') : errors;
}


function showErrorSwal(message) {
    Swal.fire({
        icon: 'error',
        title: 'Error',
        text: message,
        confirmButtonColor: '#d33',
        confirmButtonText: 'Close'
    });
}

function setupCharacterCounter(textareaId, charCountId, maxLength) {
    const textarea = $('#' + textareaId);
    const charCount = $('#' + charCountId);

    textarea.on('input', function () {
        const currentLength = textarea.val().length;
        if (currentLength > maxLength) {
            textarea.val(textarea.val().substring(0, maxLength));
            charCount.addClass('text-danger');
            charCount.text(`Maximum limit of ${maxLength} characters exceeded!`);
        } else {
            charCount.removeClass('text-danger');
            charCount.text(`${currentLength}/${maxLength} characters`);
        }
    });
}

function formatNumberWithWords(number) {
    if (isNaN(number))
        return number;
    const amountInWords = numberToWords(parseFloat(number));
    return `${formatNumber(number)} Tk <span class="text-sm font-italic"> (${amountInWords} Tk)</span>`;
}

function formatNumber(number) {
    return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

function numberToWords(number) {
    if (isNaN(number))
        return number;

    const digit = ['Zero', 'One', 'Two', 'Three', 'Four', 'Five', 'Six', 'Seven', 'Eight', 'Nine'];
    const elevenSeries = ['Ten', 'Eleven', 'Twelve', 'Thirteen', 'Fourteen', 'Fifteen', 'Sixteen', 'Seventeen', 'Eighteen', 'Nineteen'];
    const countingByTens = ['Twenty', 'Thirty', 'Forty', 'Fifty', 'Sixty', 'Seventy', 'Eighty', 'Ninety'];
    const shortScale = ['', 'Thousand', 'Lac', 'Billion', 'Trillion'];

    number = number.toString().replace(/[, ]/g, '');

    // if (!parseFloat(number)) return 'not a number';

    const x = number.indexOf('.');
    const decimalPart = (x !== -1) ? ' point ' + number.substring(x + 1).split('').map(d => digit[parseInt(d)]).join(' ') : '';
    number = number.substring(0, x !== -1 ? x : number.length);

    if (number.length > 15) return 'too big';
    const n = number.split('').map(d => parseInt(d));
    let str = '';
    let sk = 0;

    for (let i = 0; i < number.length; i++) {
        if ((number.length - i) % 3 === 2) {
            if (n[i] === 1) {
                str += elevenSeries[n[i + 1]] + ' ';
                i++;
                sk = 1;
            } else if (n[i] !== 0) {
                str += countingByTens[n[i] - 2] + ' ';
                sk = 1;
            }
        } else if (n[i] !== 0) {
            str += digit[n[i]] + ' ';
            if ((number.length - i) % 3 === 0) str += 'hundred ';
            sk = 1;
        }
        if ((number.length - i) % 3 === 1) {
            if (sk) str += shortScale[(number.length - i - 1) / 3] + ' ';
            sk = 0;
        }
    }

    return str.trim() + decimalPart;
}