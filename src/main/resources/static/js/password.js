$(".toggle-password").on("click", function () {
    const passwordInput = $(this).closest(".input-group").find(".input_pass");
    const passwordFieldType = passwordInput.attr("type");

    if (passwordFieldType === "password") {
        passwordInput.attr("type", "text");
        $(this).find("i").removeClass("fa-eye").addClass("fa-eye-slash");
    } else {
        passwordInput.attr("type", "password");
        $(this).find("i").removeClass("fa-eye-slash").addClass("fa-eye");
    }
});
