let dt;
const basicUser = basicUrl + '/api/v1/users';
$(document).ready(function () {
    const api = basicUser + '/info';
    loadProfile(api);
});

function loadProfile(api) {
    $.ajax({
        type: "GET",
        url: api,
        contentType: 'application/json',
        error: function (res) {
            showErrorSwal("Unexpected error occurred while loading profile. Please try again.");
        },
        success: function (res) {
            $('#custName').html(res.content.name);
            $('#custType').html(res.content.customerType);
            $('#zone').html(res.content.zone);
            $('#custCode').html(res.content.customerCode);
            $('#oldCode').html(res.content.oldCode);
            $('#phoneNo').html(res.content.mobileNo);
            $('#billingAdd').html(res.content.address);
            $('#connStatus').html(res.content.connectionStatus);
            if (res.content.applianceInfoList && res.content.applianceInfoList.length > 0) {
                displayApplianceInfo(res.content.applianceInfoList);
            } else {
                displayMeterInfoTable(res.content.meterInfoList);
            }
        }
    });
}

function displayApplianceInfo(applianceInfoList) {
    $('#appliances-info-title').text('Appliances Information');
    createDataTable('#appliance-info', applianceInfoList, [
        {title: "Appliance Name", data: "applianceName"},
        {title: "Quantity", data: "quantityOfAppliance"}
    ]);
}

function displayMeterInfoTable(meterInfoList) {
    $('#appliance-info-title').text('Meter Information');
    createDataTable('#meter-info', meterInfoList, [
        {title: "Meter No.", data: "meterNo"},
        {title: "Meter Group", data: "meterGroup"},
        {title: "Meter Type", data: "meterType"},
        {title: "Meter Status", data: "meterStatus"}
    ]);
}

function createDataTable(containerId, data, columns) {
    $(containerId).empty();
    $('<table class="table table-sm table-striped table-bordered" style="width: 100%;"></table>').appendTo(containerId);
    $(containerId + ' table').DataTable({
        data: data,
        columns: columns,
        columnDefs: [
            {
                targets: '_all',
                className: 'text-center'
            }
        ],
        bDestroy: true,
        bSort: false,
        searching: false,
        lengthChange: false
    });
}

