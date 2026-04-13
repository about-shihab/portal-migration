let url = '/api/v1/billInfo/billHistory';
let monthrange;

function initializeMonthPicker(selector) {
    $(selector).datepicker({
        dateFormat: "MM-yy",
        changeMonth: true,
        changeYear: true,
        showAnim: 'slide',
        yearRange: "2009:" + new Date().getFullYear(),
        onClose: function (dateText, inst) {
            var month = inst.selectedMonth + 1;
            var year = inst.selectedYear;
            $(this).val($.datepicker.formatDate('M-yy', new Date(year, month - 1, 1)));
            $(this).data('value', `${year}-${month < 10 ? '0' + month : month}`);
        }
    }).focus(function () {
        $(".ui-datepicker-calendar").hide();
    }).on("keydown", function (e) {
        e.preventDefault();
    });
}

function formatMonthToYearMonth(monthString) {
    const monthMap = {
        'Jan': '01', 'Feb': '02', 'Mar': '03', 'Apr': '04', 'May': '05', 'Jun': '06',
        'Jul': '07', 'Aug': '08', 'Sep': '09', 'Oct': '10', 'Nov': '11', 'Dec': '12'
    };

    let [monthName, year] = monthString.split('-');
    if (!monthMap[monthName] || !year) return null;
    return `${year}-${monthMap[monthName]}`;
}

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

function getMonthRange(startDate, endDate) {
    const start = new Date(startDate);
    const end = new Date(endDate);
    const startMonth = start.toLocaleString('default', {month: 'short'});
    const endMonth = end.toLocaleString('default', {month: 'short'});
    return `${startMonth}-${start.getFullYear()} to ${endMonth}-${end.getFullYear()}`;
}

function meterUnpaidBillDetails(url) {
    makeAjaxRequest(url, function (res) {
        if (res.statusCode === 404) {
            $('#billHistoryTable').empty();
            showErrorSwal(res.message);
        } else {
            const dataSet = res.content.map((v, index) => {
                const rowData = [
                    index + 1,
                    v.billMonth.substring(0, 3) + "-" + v.billYear,
                    v.billAmount,
                    v.meterRent,
                    v.previousSurcharge,
                    v.surcharge,
                    v.currentTotal,
                    v.status,
                    v.lastDateOfPayment
                ];

                if (v.isNonMetered === false) {
                    const downloadLink = `/api/v1/billInfo/download?billMonth=${new Date(Date.parse(v.billMonth + " 1, 2012")).getMonth() + 1}&billYear=${v.billYear}`;
                    rowData.push(downloadLink);
                } else {
                    rowData.push('');
                }
                return rowData;
            });
            const logoData = 'data:image/png;base64,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';
            const companyName = 'Karnaphuli Gas Distribution Company Limited';

            const headerContent = {
                columns: [
                    {},
                    {image: logoData, width: 80, height: 75, alignment: 'center'},
                    {
                        text: [
                            {text: companyName, fontSize: 18, bold: true},
                            {
                                text: `\nBill History of ${$('#username').text().split(/\s*\|\s*/).slice(-1)[0].trim()}\n`,
                                fontSize: 14
                            },
                            {text: `Date Range: ${monthrange}`, fontSize: 14}
                        ],
                        alignment: 'center',
                        width: 450,
                        height: 80,
                        margin: [0, 10, 0, 0]
                    },
                    {}
                ],
                margin: [0, 0, 0, 20],
            };

            $('#billHistoryTable').empty();
            $('<table id="dataTable" class="table table-sm table-view table-bordered table-responsive-sm table-striped table-hover" style="width: 100%"></table>').appendTo('#billHistoryTable');
            const dt = $('#dataTable').DataTable({
                data: dataSet,
                dom: '<"row mb-3"<"col-sm-12 col-md-4"l><"col-sm-12 col-md-4 align-items-center"B><"col-sm-12 col-md-4"f>>' +
                    '<"row"<"col-md-12"tr>>' +
                    '<"row mt-2"<"col-sm-12 col-md-6"i><"col-sm-12 col-md-6"p>>',
                serverSide: false,
                buttons: [
                    {
                        extend: 'colvis',
                        text: '<i class="fas fa-eye"></i> Column Visibility',
                        className: 'btn-pill btn-success',
                        titleAttr: 'Select/deselect to make columns visible/invisible'
                    },
                    {
                        extend: 'pdf',
                        text: '<i class="fas fa-file-pdf"></i> PDF',
                        className: 'btn-pill btn-danger',
                        titleAttr: 'Download as PDF',
                        pageSize: 'A4',
                        orientation: 'landscape',
                        filename: `KGDCL_Bill_History_${$('#username').text().split(/\s*\|\s*/).slice(-1)[0].trim()}_${monthrange}`,
                        exportOptions: {
                            columns: [0, 1, 2, 3, 4, 5, 6, 7, 8],
                            stripHtml: false,
                            format: {
                                body: function (data, row, column, node) {
                                    return column === 8 ? data : node.textContent;
                                }
                            }
                        },
                        customize: function (doc) {
                            let columnWidths = ['4%', '12%', '13%', '12%', '12%', '12%', '12%', '10%', '15%'];
                            let columnAlignments = ['right', 'center', 'right', 'right', 'right', 'right', 'right', 'center', 'center'];
                            doc.content[0] = headerContent;
                            doc.content[1].table.widths = columnWidths;
                            doc.content[1].table.body.forEach(function (row) {
                                for (let i = 0; i < row.length; i++) {
                                    row[i].alignment = columnAlignments[i];
                                }
                            });
                            doc.content[1].table.body.forEach(function (row, rowIndex) {
                                row.forEach(function (cell, cellIndex) {
                                    if (rowIndex === 0) {
                                        cell.border = [true, true, true, true];
                                    } else {
                                        cell.border = [false, true, true, true];
                                    }
                                });
                            });
                            doc['footer'] = function (page, pages) {
                                return {
                                    columns: [
                                        {
                                            alignment: 'left',
                                            text: [new Date().toLocaleString('en-GB', {
                                                timeZone: 'Asia/Dhaka',
                                                hour12: true
                                            }).toUpperCase()],
                                            fontSize: 10
                                        },
                                        {
                                            alignment: 'center',
                                            text: ['System generated report from ', {
                                                text: 'KGDCL Customer Billing Portal',
                                                link: 'https://billing.kgdcl.gov.bd/'
                                            }],
                                            fontSize: 10,
                                            width: 300
                                        },
                                        {
                                            alignment: 'right',
                                            text: ['Page ', {text: page.toString()}, ' of ', {text: pages.toString()}],
                                            fontSize: 10
                                        }
                                    ],
                                    margin: [20, 20],
                                };
                            };
                        }
                    }
                ],
                columns: [
                    {title: "#", orderable: true, searchable: false},
                    {title: "Bill Month"},
                    {title: "Bill Amount"},
                    {
                        title: "Meter Rent", render: function (data) {
                            return data ? data : '-';
                        }
                    },
                    {title: "Previous Surcharge"},
                    {title: "Surcharge"},
                    {title: "Current Total"},
                    {
                        title: "Status", render: function (data) {
                            return data.toLowerCase() === 'paid' ? `<span class="badge badge-success">${data}</span>` : `<span class="badge badge-danger">${data}</span>`;
                        }
                    },
                    {title: "Last Date of Payment"},
                    {
                        title: "Action", render: function (data) {
                            console.log(data);
                            return data ? `<a href=${data} target="_blank"><i class="fas fa-download"></i></a>` : 'N/A';
                        }
                    }
                ],
                bDestroy: true,
                bSort: false,

                columnDefs: [
                    {targets: [0, 7, 8], className: 'text-center align-middle'},
                    {targets: [2, 3, 4, 5, 6], className: 'text-right'},
                    {targets: [9], className: 'text-center align-middle', orderable: false, searchable: false}
                ],
                paging: true,
                scrollY: "400px",
                scrollCollapse: true,
                lengthMenu: [
                    [10, 25, 50, -1],
                    [10, 25, 50, "All"]
                ],
                headerCallback: function (thead) {
                    $(thead).find('th').addClass('bg-gradient-dark text-center');
                },
            });
        }
    });
}

$(document).ready(function () {
    initializeMonthPicker("#startMonth");
    initializeMonthPicker("#endMonth");

    $('#billHistoryForm').submit(function (e) {
        e.preventDefault();

        let startMonth = $('#startMonth').val();
        let endMonth = $('#endMonth').val();

        if (!startMonth || !endMonth) {
            alert("Please select start and end month");
            return;
        }

        let startFormatted = formatMonthToYearMonth(startMonth);
        let endFormatted = formatMonthToYearMonth(endMonth);

        if (!startFormatted || !endFormatted) {
            alert("Invalid date format");
            return;
        }

        monthrange = getMonthRange(startFormatted, endFormatted);

        meterUnpaidBillDetails(`${url}?start=${startFormatted}&end=${endFormatted}`);
    });
});
