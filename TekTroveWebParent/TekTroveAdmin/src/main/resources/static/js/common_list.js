function showDeleteModal(link, entityName){
    let entityId = link.attr("entityId")

    $("#deleteBtn").attr("href", link.attr("href"))
    $("#confirmText").text("are you sure to delete this " + entityName + " ID #" + entityId + " ?")
}

function showDetailModal(link, product) {
    let id = link.attr("href").split("/").pop();
    let url = moduleUrl + "/detail/" + id
    console.log(url)
    $.get(url, function (data) {
        $("#detailModal .modal-content").html(data);
        $("#detailModal").modal("show");
    });
}

$(".link-delete").on("click", function (e) {
    e.preventDefault();
    showDeleteModal($(this), 'customer');
})

$(".link-detail").on("click", function (e) {
    e.preventDefault();
    showDetailModal($(this), 'product');
})