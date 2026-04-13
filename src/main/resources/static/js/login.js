$("#loginForm").validate({
    rules: {
        custCode: {
            required: true
        },
        password: {
            required: true,
            maxlength: 100
        },
        captcha_form: {
            required: true,
            equalToCaptcha: true
        }
    },
    messages: {
        custCode: {
            required: "Please enter your customer code",
        },
        password: {
            required: "Please enter your password",
            maxlength: "Password can't exceed 100 characters"
        },
        captcha_form: {
            required: "Please enter captcha",
        }
    },
    ...commonValidationOptions,
    submitHandler: function (form) {
        buttonLoadingToggle('.sign-in-btn', 'loading');
        form.submit();
    }
});

