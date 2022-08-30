function print_success() {
    console.log("success");
}

function requestAllCommandStr(parseJsonDataFunc){
    var reData = {"size":10};
    $.ajax({
        url: "http://localhost:6699/com.yuyan.seismic/GET_COMMAND_ALL",
        success: function (result) {
            console.log("success");
            parseJsonDataFunc(result);
        },
        error : function() {
            console.log("error");
        }
    });
}

function appendOnline(commandData_name, commandData_stringCode) {
    var trLine = `
        <tr>
            <td>...</td>
            <td>${commandData_name}</td>
            <td>${commandData_stringCode}</td>
            <td>...</td>
            <td>...</td>
            <td>...</td>
            <td>...</td>
            <td>...</td>
            <td>...</td>
        </tr>
    `;
    $("#setTable").append(trLine);
}

function parseJsonData(jsonStr) {
    var jsonObject = JSON.parse(jsonStr);
    console.log(jsonObject.size);
    jsonObject.commands.forEach(function (command) {
        appendOnline(command.commandData.name
            , command.commandData.stringCode);
    });
}