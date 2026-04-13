function getTicketStatusList() {
    const api = basicUrl + '/api/v1/complain-infos/ticket-status-list';
    $.ajax({
        type: "GET",
        url: api,
        contentType: 'application/json',
        error: function (res) {
            alert("An expected error occurred while loading complain ticket status list. Please try again.");
            logoutAndForwardToLogin();
        },
        success: function (res) {
            if (res.statusCode === 200) {
                const dataSet = [];
                let i = 1;
                $.each(res.content, function (k, v) {
                    const arr = [];
                    arr.push(i++);
                    arr.push(v.ticketNo);
                    arr.push(DOMPurify.sanitize(v.complainDescription));
                    arr.push(v.status);
                    arr.push(v.complainDate);
                    dataSet.push(arr);
                });

                $('#myTable').empty();
                $('<table id="example2" class="table table-responsive-sm table-striped table-bordered" style="width: 100%;"></table>').appendTo('#myTable');
                dt = $('#example2').DataTable({

                    data: dataSet,
                    columns: [
                        {title: "#"},
                        {title: "Ticket No."},
                        {title: "Complain Description"},
                        {title: "Status"},
                        {title: "Complain Date"},
                    ],
                    "columnDefs": [
                        {
                            "targets": [0],
                            "visible": false,
                            "searchable": false
                        }
                    ]
                });
            } else {
                showErrorSwal(getErrorMessage(res));
            }
        }
    });
}

function getTicketReviewList() {
    const api = basicUrl + '/api/v1/complain-infos/ticket-review-list';
    $.ajax({
        type: "GET",
        url: api,
        contentType: 'application/json',
        error: function (res) {
            alert("An expected error occurred while loading complain ticket review list. Please try again.");
            logoutAndForwardToLogin();
        },
        success: function (res) {
            if (res.statusCode === 200) {
                const dataSet = [];
                let i = 1;
                $.each(res.content, function (k, v) {
                    const arr = [];
                    arr.push(i++);
                    arr.push(v.complainId);
                    arr.push(DOMPurify.sanitize(v.reviewerFeedback));
                    arr.push(DOMPurify.sanitize(v.kgdclFeedback));
                    arr.push(v.reviewDate);
                    dataSet.push(arr);
                });

                $('#myTable').empty();
                $('<table id="example2" class="table table-responsive-sm table-striped table-bordered" style="width: 100%;"></table>').appendTo('#myTable');
                dt = $('#example2').DataTable({
                    data: dataSet,
                    columns: [
                        {title: "#"},
                        {title: "Ticket No."},
                        {title: "Review Message"},
                        {title: "KGDCL Feedback"},
                        {title: "Review Date"},
                    ],
                    "columnDefs": [
                        {
                            "targets": [0],
                            "visible": false,
                            "searchable": false
                        }
                    ]
                });
            } else {
                showErrorSwal(getErrorMessage(res));
            }
        }
    });
}
