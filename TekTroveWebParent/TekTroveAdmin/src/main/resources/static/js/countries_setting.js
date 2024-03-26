let dropDownCountry;
let buttonAdd;
let buttonUpdate;
let buttonDelete;
let fieldCountryName;
let fieldCountryCode;
let labelCountryName

$(document).ready(function () {
    dropDownCountry = $('.dropDownCountries');
    buttonAdd = $('#buttonAddCountry');
    buttonUpdate = $('#buttonUpdateCountry');
    buttonDelete = $('#buttonDeleteCountry');
    labelCountryName = $('#labelCountryName');
    fieldCountryName = $('#fieldCountryName');
    fieldCountryCode = $('#fieldCountryCode');

    dropDownCountry.change(function () {
        changeFormStateToSelectCountry();
    });

    buttonAdd.click(function () {
        if (buttonAdd.val() === "New") {
            changeFormStateToNew();
        } else {
            addCountry();
        }
    });

    buttonUpdate.click(function () {
        updateCountry();
    });

    buttonDelete.click(function () {
        deleteCountry();
    });
})

function changeFormStateToSelectCountry() {
    buttonAdd.prop('value', "New");
    buttonUpdate.prop('disabled', false);
    buttonDelete.prop('disabled', false);

    let selectedCountry = $('#dropDownCountries option:selected');
    fieldCountryName.val(selectedCountry.text());
    fieldCountryCode.val(selectedCountry.attr('code'));
}

function changeFormStateToNew() {
    buttonAdd.prop('value', "Save");
    buttonUpdate.prop('disabled', true);
    buttonDelete.prop('disabled', true);
    labelCountryName.val("Selected Country")
    fieldCountryName.val("");
    fieldCountryCode.val("");
    $("#dropDownCountries option:selected").prop('selected', false);
}

function addCountry() {
    let url = contextPath + 'countries/save';
    let countryName = fieldCountryName.val();
    let countryCode = fieldCountryCode.val();
    let country = {
        name: countryName,
        code: countryCode
    };

    $.ajax({
        type: 'POST',
        url: url,
        data: JSON.stringify(country),
        contentType: 'application/json',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
    }).done(function (countryId) {
        showToastMessage("Newly added country id: " + countryId);
        selectNewlyAddedCountry(countryId, countryCode, countryName);
    }).fail(function () {
        showToastMessage("Error: Could not connect to server or server encounter error. Please try again later.");
    });
}

function updateCountry() {
    let url = contextPath + 'countries/save';
    let countryId = dropDownCountry.val();
    let countryName = fieldCountryName.val();
    let countryCode = fieldCountryCode.val();
    let country = {
        id: countryId,
        name: countryName,
        code: countryCode
    };

    $.ajax({
        type: 'POST',
        url: url,
        data: JSON.stringify(country),
        contentType: 'application/json',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
    }).done(function (country) {
        $(".dropDownCountries option[value='" + countryId + "']").text(country.name);
        $(".dropDownCountries option[value='" + countryId + "']").attr('code', country.code);
        showToastMessage("Country updated successfully");
        changeFormStateToNew();
    }).fail(function () {
        showToastMessage("Error: Could not connect to server or server encounter error. Please try again later.");
    });
}

function deleteCountry() {
    let countryId = dropDownCountry.val();
    let url = contextPath + 'countries/' + countryId;

    $.ajax({
        type: 'DELETE',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
    }).done(function () {
        $(".dropDownCountries option[value='" + countryId + "']").remove();
        changeFormStateToNew();
        showToastMessage("Country deleted successfully");
    }).fail(function () {
        showToastMessage("Error: Could not connect to server or server encounter error. Please try again later.");
    });

}

function selectNewlyAddedCountry(countryId, countryCode, countryName) {
    dropDownCountry.append('<option code="' + countryCode + '" value="' + countryId + '">' + countryName + '</option>');

    $("#dropDownCountries option[value='" + countryId + "']").prop('selected', true);
    fieldCountryName.val("").focus();
    fieldCountryCode.val("");
}