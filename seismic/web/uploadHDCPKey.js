function onUploadHDCPKey(obj) {
    var formData = new FormData($('#uploadHDCPKeyForm')[0]);
    $.ajax({
        type: "POST",
        url: "http://localhost:6699/com.yuyan.seismic/POST_UPLOAD_HDCP_KEY",
        data: formData,
        cache: false,
        processData: false,
        contentType: false,
        success: function (result) {
            console.log("success: " + result);
        },
        error : function() {
            console.log("error");
        }
    });
}
