$(document).ready(function () {
    $("#fileImage").change(function () {
        var file = this.files[0];
        var maxSize = 1024 * 1024;

        if (file.size > maxSize) {
            this.setCustomValidity('File size exceeds the maximum allowed size (1MB). Please choose a smaller file.');
            this.reportValidity();
            $("#thumbnail").attr("src", "");
        } else {
            this.setCustomValidity("");
            showImageThumbnail(this);
        }
    });
});

function showImageThumbnail(fileInput) {
    var file = fileInput.files[0];
    var reader = new FileReader();
    reader.onload = function (e) {
        $("#thumbnail").attr("src", e.target.result);
    }
    reader.readAsDataURL(file);
}
