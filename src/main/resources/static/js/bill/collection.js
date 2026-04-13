$(document).ready(function () {
    const currentDate = new Date().toLocaleDateString('en-GB').replaceAll('/', '-');
    $('#startDate, #endDate').datepicker({
        dateFormat: 'dd-mm-yy',
        yearRange: "2009:" + new Date().getFullYear(),
        onSelect: function (dateText) {
            $('#formattedDate').val(dateText);
        },
        changeMonth: true,
        changeYear: true,
        showAnim: 'slide',
        maxDate: '+0d'
    });
    $('#startDate, #endDate').datepicker('option', 'minDate', '01-01-2009');
    $('#endDate').datepicker('option', 'maxDate', currentDate).datepicker('setDate', currentDate);
    $('#billCollectionForm').submit(function (e) {
        e.preventDefault();
        const start = moment($('#startDate').val(), 'DD-MM-YYYY').format('YYYY-MM-DD');
        const end = moment($('#endDate').val(), 'DD-MM-YYYY').format('YYYY-MM-DD');
        generateReport(start, end);

    });
    $(document).ajaxStart(function () {
        $('#overlay').show();

    });
    $(document).ajaxStop(function () {
        $('#overlay').hide();
    });
});

function generateReport(start, end) {
    let url = '/api/v1/billInfo/billCollectionReport';
    const params = new URLSearchParams();
    params.set('start', start);
    params.set('end', end);
    url += '?' + params.toString();
    $.ajax({
        url: url,
        method: 'GET',
        xhrFields: {
            responseType: 'arraybuffer'
        },
        headers: {
            'Accept': 'application/pdf'
        },
        success: function (data, status, xhr) {
            let blob = new Blob([data], {type: 'application/pdf'});
            let blobUrl = URL.createObjectURL(blob);
            $('#pdfContainer').show();
            $('#pdfViewer').attr('src', blobUrl);
        },
        error: function (err) {
            showErrorSwal("An error occurred while generating report. Please try again later.")
        },
    });
}