function showDeleteModal(link, entityName){
    entityId = link.attr("entityId")

    $("#deleteBtn").attr("href", link.attr("href"))
    $("#confirmText").text("are you sure to delete this " + entityName + " ID #" + entityId + " ?")
}