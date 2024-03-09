function addNextDetailSection(){
    let allDivDetails = $("[id^='divDetail']");
    let divDetailCount = allDivDetails.length;

    let nextDetailId = divDetailCount + 1;
    let htmlDetailSection =
    `<div class="form-inline" id="divDetail${nextDetailId}">
        <input type="hidden" name="detailIDs" value="0"/>
        <label class="m-3">Name:</label>
        <input type="text" class="form-control w-25" name="detailNames" required="required"/>
        <label class="m-3">Value:</label>
        <input type="text" class="form-control w-25" name="detailValues" required="required"/>
    </div>`

    $("#divProductDetails").append(htmlDetailSection);

    let previousDivDetail = allDivDetails.last();
    let previousDivDetailId = previousDivDetail.attr("id");

    let htmlLinkRemove = `<a class="btn fas fa-times-circle fa-2x icon-dark float-right"
			href="javascript:removeDetailSectionById(${previousDivDetailId})" 
			title="Remove this detail"></a>`;
    previousDivDetail.append(htmlLinkRemove);

    $("#input[name='detailNames']").last().focus();
}

function removeDetailSectionById(id){
    id.remove();
}