var basicBillUrl = basicUrl + '/api/v1/billInfo';
$(document).ready(function () {
    if (isNonMeterCustomer) {
        nonMeterUnpaidBill();
    } else {
        meterUnpaidBill();
        meterUnpaidBillDetails();
    }

});


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

function meterUnpaidBill() {
    const api = basicBillUrl + '/meteredBill';
    makeAjaxRequest(api, function (res) {
        const result = JSON.parse(res);
        if (result.statusCode === 200) {
            loadCurrentBillInfo(result.content[0], '#descriptionList', '#pay-regular', 'metered-regular');
        } else {
            const errMsg = getErrorMessage(result);
            showErrorSwal(errMsg);
        }
    });
}

function meterPartialBill() {
    const api = basicBillUrl + '/meteredPartialBill';
    makeAjaxRequest(api, function (res) {
        const result = JSON.parse(res);
        if (result.statusCode === 200) {
            loadCurrentBillInfo(result.content[0], '#partialDesc', '#pay-partial', 'metered-partial');
        } else {
            const errMsg = getErrorMessage(result);
            $('#partialDesc').html(`<h5 class="text-success text-center text-bold"><i class="far fa-check-square"></i> ${errMsg} </h5>`);
        }
    });
}

function nonMeterUnpaidBill() {
    const api = basicBillUrl + '/nonMeteredBill';
    makeAjaxRequest(api, function (res) {
        const result = JSON.parse(res);
        if (result.statusCode === 200) {
            loadCurrentBillInfo(result.content[0], '#descriptionList', '#pay-non-meter', 'non-metered');
        } else {
            const errMsg = getErrorMessage(result);
            showErrorSwal(errMsg);
        }
    });
}

function loadCurrentBillInfo(content, dynamicHtmlId, paymentBtnId, type) {
    const {labelText, fValue} = getLabelTextAndFValue(content, type);
    let dynamicHTML = '';
    labelText.forEach((text, index) => {
        dynamicHTML += `<dt class="col-md-4">${text}</dt>
                        <dd class="col-md-8" id="dd_id_${index}">${fValue[index]}</dd>`;
    });

    $(dynamicHtmlId).html(dynamicHTML);
    if (type == 'non-metered') {
        const totalBillAmount = content['Current Total(Surch Incl.)'];
        if (totalBillAmount > 0)
            $(paymentBtnId).html(`<a href="/pay-bill" class="btn btn-block bg-gradient-warning btn-lg paymentBtn">
                <i class="far fa-paper-plane"></i> Pay ${formatNumber(totalBillAmount)} BDT</a>`);
        else {
            $(paymentBtnId).html(`<button class="btn btn-block bg-gradient-success btn-lg paymentBtn">
                                  <i class="far fa-check-circle"></i> Bill already paid</button>`);
        }
    }
}


function meterUnpaidBillDetails() {
    const api = basicBillUrl + '/unpaidList';
    makeAjaxRequest(api, function (res) {
        res.content.sort((a, b) => new Date(a.lastDateOfPayment) - new Date(b.lastDateOfPayment));
        const dataSet = res.content.map((v, index) => {
            const downloadLink = `/api/v1/billInfo/download?billMonth=${new Date(Date.parse(v.billMonth + " 1, 2012")).getMonth() + 1}&billYear=${v.billYear}`;

            return [
                index + 1,
                v.billMonth + " - " + v.billYear,
                v.billAmount,
                v.meterRent,
                v.previousSurcharge,
                v.surcharge,
                v.currentTotal,
                v.status,
                v.lastDateOfPayment,
                downloadLink
            ];
        });
        const columns = [
            "#",
            "Bill Month - Year",
            "Bill Amount",
            "Meter Rent",
            "Previous Surcharge",
            "Surcharge",
            "Current Total",
            "Status",
            "Last Date Of Payment",
            "Download Bill"
        ];
        populateDataTable(columns, '#billDetailsTable', dataSet)
    });
}

function meterPartialBillDetails() {
    const api = basicBillUrl + '/meteredPartialDetails';
    makeAjaxRequest(api, function (res) {
        res.content.sort((a, b) => new Date(a.lastDateOfPayment) - new Date(b.lastDateOfPayment));
        const dataSet = res.content.map((v, index) => [
            index + 1,
            formatMonthYear(v.billMonth, v.billYear),
            v.installmentNo,
            v.status,
            v.partialAmount,
            v.lastDateOfPayment
        ]);
        const columns = [
            "#",
            "Bill Month - Year",
            "Installment No",
            "Status",
            "Installment Amount",
            "Last Date Of Payment"
        ];
        populateDataTable(columns, '#partialDetailsTable', dataSet);
    });
}

function populateDataTable(columns, tableId, dataSet) {
    const table = $(tableId);
    table.empty();
    const dt = table.DataTable({
        data: dataSet,
        columns: columns.map((column, index) => {
            if (column == 'Status') {
                return {
                    title: "Status",
                    render: function (data) {
                        if (data.toLowerCase() === 'unpaid') {
                            return '<span class="badge badge-danger">' + data + '</span>';
                        } else {
                            return '<span class="badge badge-success">' + data + '</span>';
                        }
                    }
                };
            } else {
                return {title: column};
            }
        }),
        "columnDefs": [
            {
                "className": "text-right",
                "targets": [columns.indexOf('Bill Amount'),
                    columns.indexOf('Meter Rent'), columns.indexOf('Previous Surcharge'),
                    columns.indexOf('Surcharge'), columns.indexOf('Current Total'), columns.indexOf('Installment Amount')]
            },
            {
                "className": "text-center", "targets":
                    columns.reduce((indices, col, index) => {
                        if (typeof col === 'string' || typeof col === 'date') {
                            indices.push(index);
                        }
                        return indices;
                    }, [])
            },
            {
                "className": "text-center",
                "targets": columns.indexOf('Download Bill'),
                "render": function (data, type, row) {
                    return `<a href="${data}" target="_blank" class="btn btn-primary btn-sm"><i class="fas fa-download"></i></a>`;
                }
            }
        ],
        "bDestroy": true,
        "bSort": false,
        "width": "100%",
        "paging": true,
        "headerCallback": function (thead) {
            $(thead).find('th').addClass('bg-cyan text-white');
        }
    });

    if (tableId == '#partialDetailsTable') {
        const totalInstallmentAmount = dataSet.reduce((total, item) => total + item[columns.indexOf('Installment Amount')], 0);
        table.append('<tfoot class="text-bold border-0"><tr><td class=" text-right" colspan="4">Total Installment Amount :</td><td colspan="2" class="text-center">= ' + formatNumber(totalInstallmentAmount) + '</td></tr></tfoot>');
    }

}

function getLabelTextAndFValue(content, meterType) {
    let labelText = [];
    let fValue = [];
    const govText = content['Is Govt.'] ? 'Yes' : 'No';
    if (meterType == 'non-metered') {
        labelText = [
            'Customer Name :',
            'Billing Months :',
            'Surcharge :',
            'Current Total (Surch Incl.) :',
            'Last Payment Date :',
            'Is Govt. :',
        ];
        fValue = [
            content['NAME'],
            content['Billing Months'],
            formatNumberWithWords(content['Surcharge Amount']),
            formatNumberWithWords(content['Current Total(Surch Incl.)']),
            content['Last Payment Date'],
            govText
        ];
    } else if (meterType == 'metered-regular') {
        labelText = [
            'Customer Name :',
            'Billing Month - Year :',
            'Gas Bill :',
            'Surcharge :',
            'Meter Rent :',
            'Total Bill Amount :',
            'Last Payment Date :',
            'Is Govt. :',
        ];
        fValue = [
            content['Customer Name'],
            content['Bill Month'] + " - " + content['Bill Year'],
            formatNumberWithWords(content['Gas Bill']),
            formatNumberWithWords(content['Surcharge']),
            formatNumberWithWords(content['Meter Charge']),
            formatNumberWithWords(content['Total Bill Amount']),
            content['Last Payment Date'],
            govText
        ];
    } else if (meterType == 'metered-partial') {
        labelText = [
            'Customer Name :',
            'Installment No :',
            'Installment Amount  :',
            'Surcharge :',
            'Total Bill Amount :',
            'Last Payment Date :',
            'Is Govt. :',
        ];
        fValue = [
            content['Customer Name'],
            content['Installment No'],
            formatNumberWithWords(content['Installment Amount']),
            formatNumberWithWords(content['Surcharge']),
            formatNumberWithWords(content['Total Bill Amount']),
            content['Last Payment Date'],
            govText
        ];
    }

    return {labelText, fValue};
}

function formatMonthYear(month, year) {
    const monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
    const formattedMonth = monthNames[month - 1]; // Month is 1-indexed
    return formattedMonth + ' - ' + year;
}




