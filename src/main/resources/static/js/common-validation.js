const commonValidationOptions = {
    errorElement: 'span',
    errorPlacement: function (error, element) {
        error.addClass('invalid-feedback');
        if (element.closest('.form-group').length) {
            element.closest('.form-group').append(error);
        } else if (element.closest('.input-group').length) {
            element.closest('.input-group').append(error);
        } else {
            element.after(error);
        }
    },
    highlight: function (element, errorClass, validClass) {
        $(element).addClass('is-invalid');
    },
    unhighlight: function (element, errorClass, validClass) {
        $(element).removeClass('is-invalid');
    }
};

jQuery.validator.addMethod("equalToCaptcha", function (value, element) {
    return this.optional(element) || parseInt(value) === captchaResult;
}, "Captcha is not correct");