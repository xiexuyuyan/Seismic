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

function requestAllHDCPKeyList(parseJsonDataFunc){
    $.ajax({
        url: "http://localhost:6699/com.yuyan.seismic/GET_HDCP_KEY_LIST",
        success: function (result) {
            console.log("success");
            parseJsonDataFunc(result);
        },
        error : function() {
            console.log("error");
        }
    });
}

function appendHDCPKeyFileOnline(fileName) {
    const trPrefix = "<tr>";
    const trSuffix = "</tr>";
    const trMain = `
        <td>${fileName}</td>
    `;
    const trLine = trPrefix + trMain + trSuffix;

    $("#HDCPKeyFileListTable").append(trLine);
}


function parseHDCPKeyFileList(jsonStr) {
    $("#HDCPKeyFileListTable").html("");
    const jsonObject = JSON.parse(jsonStr);
    jsonObject.value.forEach(function (fileName) {
        appendHDCPKeyFileOnline(fileName);
    });
}
