let dropDownCountries;
let listState;
$(document).ready(function () {
    dropDownCountries = $('#country');
    listState = $('#listState');

    dropDownCountries.on("change", function () {
        loadStatesForCountry();
        $('#state').val('').focus();
    })
})

function loadStatesForCountry() {
    let selectedCountry = $('#country').val();
    let url = contextPath + "settings/list_by_country/" + selectedCountry;

    $.get(url, function (data) {
        listState.empty();

        $.each(data, function (index, state) {
            listState.append('<option value="' + state.name + '">' + state.name + '</option>');
        });
    });
}

function isConfirmPasswordMatchPassword(confirmPassword) {
    if (confirmPassword.value !== $('#password').val()) {
        confirmPassword.setCustomValidity('Passwords do not match');
    } else {
        confirmPassword.setCustomValidity('');
    }
}
