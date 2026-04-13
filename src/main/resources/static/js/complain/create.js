function create() {
    Swal.fire({
        title: 'Confirmation',
        text: 'Are you sure to submit this complaint?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes, submit it!',
        cancelButtonText: 'Cancel'
    }).then((result) => {
        if (result.isConfirmed) {
            submitComplainForm();
        }
    });
}

function submitComplainForm() {
    const api = basicUrl + '/api/v1/complain-infos/create';
    const causeIdVal = $('#complainOn').val();
    const description = $('#description').val();
    const payload = {causeId: causeIdVal, description: description};

    $.ajax({
        type: "POST",
        url: api,
        data: JSON.stringify(payload),
        contentType: 'application/json',
        error: function (res) {
            alert("An expected error occurred while creating complain. Please try again.");
            logoutAndForwardToLogin();
        },
        success: function (res) {
            if (res.statusCode === 200) {
                $('#errorMsg').fadeOut();
                const ticketNo = res.content.status;
                const successMessage = `Your complaint has been submitted successfully! Your e-Ticket no is: <span class="badge-primary badge-pill text-bold">${ticketNo}</span>. Please preserve it for future use.`;
                Swal.fire({
                    title: 'Success',
                    html: successMessage,
                    icon: 'success',
                    confirmButtonColor: '#3085d6',
                    confirmButtonText: 'OK'
                }).then(() => {
                    window.location.reload();
                });
            } else {
                showErrorSwal(getErrorMessage(res));
            }
        }
    });
}

$("#createComplainForm").validate({
    rules: {
        complainOn: {
            required: true
        },
        description: {
            required: true,
            maxlength: 2000
        }
    },
    messages: {
        complainOn: {
            required: "Please select a Complain On option."
        },
        description: {
            required: "Please enter a complain description.",
            maxlength: "Description cannot exceed 2000 characters."
        }
    },
    ...commonValidationOptions,
    submitHandler: function (form) {
        create();
        return false;
    }
});

$(document).ready(function () {
    const api = basicUrl + '/api/v1/complain-infos/cause-list';
    $.ajax({
        type: "GET",
        url: api,
        contentType: 'application/json',
        error: function (res) {
            alert("An expected error occurred while loading complain cause list. Please try again.");
            logoutAndForwardToLogin();
        },
        success: function (res) {
            if (res.statusCode === 200) {
                let htmlOptions = '<option value="" disabled selected>--Select One--</option>';
                $.each(res.content, function (index, item) {
                    htmlOptions += '<option value="' + item.id + '">' + item.about + '</option>';
                });
                $('#complainOn').html(htmlOptions);

            } else {
                showErrorSwal(getErrorMessage(res));
            }
        }
    });
});

setupCharacterCounter('description', 'descriptionCounter', 2000);
