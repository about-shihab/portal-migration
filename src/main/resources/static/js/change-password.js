const basicUser = basicUrl + '/api/v1/users';

$("#changePasswordForm").validate({
    rules: {
        password: {
            required: true, maxlength: 100,
        }, newPassword: {
            required: true, minlength: 8, maxlength: 100, pattern: /^(?=.*[A-Za-z])(?=.*\d).{8,}$/
        }, password2: {
            required: true, equalTo: "#newPassword"
        }
    },
    messages: {
        password: {
            required: "Please enter current password", maxlength: "Password can't exceed 100 characters",
        }, newPassword: {
            required: "Please enter new password",
            minlength: "Password must be at least 8 characters",
            maxlength: "Password can't exceed 100 characters",
            pattern: "Password must contain a letter and a digit"
        }, password2: {
            required: "Please confirm your password", equalTo: "Passwords do not match"
        }
    },
    ...commonValidationOptions,
    submitHandler: function (form) {
        Swal.fire({
            title: 'Change Password?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: 'Change',
            confirmButtonColor: '#3085d6',
            cancelButtonText: 'Cancel',
            cancelButtonColor: '#d33',
            showLoaderOnConfirm: true,
            preConfirm: () => {
                return changePassword()
                    .then(function (res) {
                        if (res.statusCode === 200) {
                            Swal.fire({
                                icon: 'success', title: 'Success', text: res.message,
                            }).then(() => {
                                window.location.reload();
                            });
                        } else {
                            let errorMsg = getErrorMessage(res);
                            showErrorSwal(errorMsg);
                        }
                    })
                    .catch(function (error) {
                        let errorMsg = getErrorMessage(error);
                        showErrorSwal(errorMsg);
                    });
            },
        });
    }
});

function changePassword() {
    return new Promise(function (resolve, reject) {
        const password = $('#password').val();
        const newPassword = $('#newPassword').val();
        const confirmPassword = $('#password2').val();

        const api = basicUser + '/changePassword';
        const payload = {password: password, newPassword: newPassword, confirmPassword: confirmPassword};

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