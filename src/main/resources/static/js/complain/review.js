function createComplainReview() {
    Swal.fire({
        title: 'Confirmation',
        text: 'Are you sure to submit this review?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes, submit it!',
        cancelButtonText: 'Cancel'
    }).then((result) => {
        if (result.isConfirmed) {
            submitComplainReviewForm();
        }
    });
}


function submitComplainReviewForm() {
    const ticketNo = $('#ticketNo').val();
    const feedback = $('#reviewerFeedback').val();
    const api = basicUrl + '/api/v1/complain-infos/review?ticketNo=' + ticketNo + '&feedback=' + feedback;

    $.ajax({
        type: "POST",
        url: api,
        contentType: 'application/json',
        error: function (res) {
            alert("An expected error occurred while creating complain review. Please try again.");
            logoutAndForwardToLogin();
        },
        success: function (res) {
            if (res.statusCode === 200) {
                const successMessage = `Your feedback has been submitted successfully! Thank you for your valuable feedback`;
                if (res.content.status === 'SuccessFullySaved') {
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
                    showErrorSwal(res.content.status)
                }
            } else {
                showErrorSwal(getErrorMessage(res));
            }
        }
    });

}

$('#reviewForm').validate({
    rules: {
        ticketNo: {
            required: true
        },
        reviewerFeedback: {
            required: true,
            maxlength: 500,
        }
    },
    messages: {
        ticketNo: {
            required: "Please enter a Ticket No."
        },
        reviewerFeedback: {
            required: "Please enter a Review Message.",
            maxlength: "Review Message cannot exceed 500 characters.",
        }
    },
    ...commonValidationOptions,
    submitHandler: function (form) {
        createComplainReview();
        return false;
    }
});

setupCharacterCounter('reviewerFeedback', 'reviewCounter', 500);
