let buttonLoad;

$(document).ready(function () {
    buttonLoad = $('.loadForCountry');
    loadCountries();

    buttonLoad.click(function () {
        loadCountries();
    });
});

function loadCountries() {
    let url = contextPath + 'countries';
    $.get(url, function (data) {
        dropDownCountry.empty();
        $.each(data, function (index, country) {
            dropDownCountry.append('<option code="' + country.code + '" + value="' + country.id + '">' + country.name + '</option>');
        });
    }).done(function () {
        buttonLoad.val("Refresh Country List");
        showToastMessage("Countries loaded successfully");
    }).fail(function () {
        showToastMessage("Error loading countries");
    });
}

function showToastMessage(message) {
    $("#toastMessage").text(message);
    $(".toast").toast('show');
}