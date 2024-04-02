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
    if(!validateFormCountry($("#formCountry"))) return;
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
        }).done(function (country) {
        showToastMessage("Newly added country id: " + country.id);
        selectNewlyAddedCountry(country);
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

function selectNewlyAddedCountry(country) {
    console.log(country)
    dropDownCountry.append('<option code="' + country.code + '" value="' + country.id + '">' + country.name + '</option>');

    $("#dropDownCountries option[value='" + country.id + "']").prop('selected', true);
    fieldCountryName.val("").focus();
    fieldCountryCode.val("");
}