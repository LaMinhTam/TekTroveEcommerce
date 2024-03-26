let dropDownCountryForState;
let dropDownStates;
let buttonAddState;
let buttonUpdateState;
let buttonDeleteState;
let labelStateName;
let fieldStateName;

$(document).ready(function () {
    dropDownCountryForState = $('#dropDownCountriesForState');
    dropDownStates = $('#dropDownStates');
    buttonAddState = $('#buttonAddState');
    buttonUpdateState = $('#buttonUpdateState');
    buttonDeleteState = $('#buttonDeleteState');
    labelStateName = $('#labelStateName');
    fieldStateName = $('#fieldStateName');
    dropDownCountryForState.change(function () {
        loadState();
    })

    dropDownStates.click(function () {
        changeFormStateToSelectState();
    })

    buttonAddState.click(function () {
        if (buttonAddState.val() == "Add") {
            addState();
        } else {
            changeFormStateToNewState()
        }
    });

    buttonUpdateState.click(function () {
        updateState();
    });

    buttonDeleteState.click(function () {
        deleteState();
    });
});

function loadState() {
    let selectedCountryId = dropDownCountryForState.val();
    let url = contextPath + "states/list_by_country/" + selectedCountryId;

    $.get(url, function (responseJSON) {
        dropDownStates.empty();

        $.each(responseJSON, function (index, state) {
            dropDownStates.append('<option value="' + state.id + '">' + state.name + '</option>');
        })
    })
}

function changeFormStateToNewState() {
    buttonAddState.val("Add");
    labelStateName.text("State/Province Name:")
    buttonUpdateState.prop('disabled', true);
    buttonDeleteState.prop('disabled', true);

    fieldStateName.val("").focus();
}

function changeFormStateToSelectState() {
    buttonAddState.val("New");
    buttonUpdateState.prop('disabled', false);
    buttonDeleteState.prop('disabled', false);

    labelStateName.text("Selected State/Province:");
    let selectedState = $('#dropDownStates option:selected');
    fieldStateName.val(selectedState.text());
}

function addState() {
    let url = contextPath + "states/save";
    let stateName = fieldStateName.val();
    let countryId = dropDownCountryForState.val();
    let state = {
        name: stateName,
        country: {
            id: countryId
        }
    };
    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        contentType: 'application/json',
        data: JSON.stringify(state)
    }).done(function (stateId) {
        $("#dropDownStates").append('<option value="' + stateId + '">' + stateName + '</option>');
        fieldStateName.val("");
        showToastMessage("Newly added state id: " + stateId);
    }).fail(function () {
        showToastMessage("Error adding state");
    })
}

function updateState() {
    let url = contextPath + "states/save";
    let stateId = dropDownStates.val();
    let stateName = fieldStateName.val();
    let countryId = dropDownCountryForState.val();
    let state = {
        id: stateId,
        name: stateName,
        country: {
            id: countryId
        }
    };
    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        contentType: 'application/json',
        data: JSON.stringify(state)
    }).done(function () {
        $('#dropDownStates option:selected').text(stateName);
        showToastMessage("State updated successfully");
    }).fail(function () {
        showToastMessage("Error updating state");
    })
}

function deleteState() {
    let stateId = dropDownStates.val();
    let url = contextPath + "states/" + stateId;
    $.ajax({
        type: 'DELETE',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function () {
        $('#dropDownStates option:selected').remove();
        changeFormStateToNewState();
        showToastMessage("State deleted successfully");
    }).fail(function () {
        showToastMessage("Error deleting state");
    })
}