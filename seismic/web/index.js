function print_success() {
    console.log("success");
}

var gJsonData = new Map();


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

function requestSendCommand(commandObject, value, type){
    $.ajax({
        type: "POST",
        url: "http://localhost:6699/com.yuyan.seismic/HANDLE_POST_COMMAND",
        data:{
            'type': type,
            'command_data_name': commandObject.commandData.name
        },
        success: function (result) {
            console.log(result)
        },
        error: function (result) {
            console.log(result)
        }
    });
}

function formatTrString(commandObject) {
    var trString = `
        <td colspan="5"><input type="text" style="width: calc(100% - 8px)"></td>
    `;

    var trSubmit = `
        <td><button id="id_value_${commandObject.commandData.name}" style="width: 100%"
                onclick="onSubmitStableInteger(this.id)">send</button></td>
    `;

    return trString + trSubmit;
}

function changeCommandValue(commandButtonId, op) {
    var commandDataName = commandButtonId.split('id_bt_')[1];
    var commandObject = gJsonData.get(commandDataName);

    var minString = commandObject.commandData.commandHexCode.commandValue.min;
    var maxString = commandObject.commandData.commandHexCode.commandValue.max;
    var minValue = parseInt(minString);
    var maxValue = parseInt(maxString);

    var valueObject = $("#" + "id_value_" + commandObject.commandData.name);
    var progressObject = $("#" + "id_progress_" + commandObject.commandData.name);
    var currentValueText = valueObject.text();
    var currentValue = Number(currentValueText);
    var setTo = currentValue + Number(op);
    if (setTo < minValue) {
        setTo = minValue;
    }
    if (setTo > maxValue) {
        setTo = maxValue;
    }
    valueObject.text(setTo);
    progressObject.val(setTo);
}

function onSubmitStableInteger(commandSubmitValueId) {
    var commandDataName = commandSubmitValueId.split('id_value_')[1];
    var commandObject = gJsonData.get(commandDataName);
    var valueObject = $("#" + "id_value_" + commandObject.commandData.name)
    console.log(commandDataName);
    requestSendCommand(commandObject, commandDataName, "STABLE_INTEGER");
}

function formatTrStableInteger(commandObject) {
    var trString = `
        <td colspan="5"></td>
    `;

    var trSubmit = `
        <td><button id="id_value_${commandObject.commandData.name}" style="width: 100%"
                onclick="onSubmitStableInteger(this.id)">send</button></td>
    `;

    return trString + trSubmit;
}

function onSubmitInteger(commandSubmitValueId) {
    var commandDataName = commandSubmitValueId.split('id_value_')[1];
    var commandObject = gJsonData.get(commandDataName);
    var valueObject = $("#" + "id_value_" + commandObject.commandData.name);
    var currentValueText = valueObject.text();
    var currentValue = Number(currentValueText);
    console.log(currentValue);
}

function formatTrInteger(commandObject) {
    var commandDataName = commandObject.commandData.name;
    var minString = commandObject.commandData.commandHexCode.commandValue.min;
    var maxString = commandObject.commandData.commandHexCode.commandValue.max;
    var minValue = parseInt(minString);
    var maxValue = parseInt(maxString);

    if (minValue == maxValue) {
        return formatTrStableInteger(commandObject);
    }

    var valueRandom = Math.random() * (maxValue - minValue) + minValue;
    valueRandom = parseInt(valueRandom);
    var trInteger = `
        <td><button id="id_bt_${commandDataName}"
                    onclick="changeCommandValue(this.id, -10)">-10</button></td>

        <td><button id="id_bt_${commandDataName}"
                    onclick="changeCommandValue(this.id, -1)">-1</button></td>

        <td><progress id="id_progress_${commandDataName}"
                    value="${valueRandom}"
                    min="${minValue}" max="${maxValue}"
                    style="max-width: 100px"></progress></td>

        <td><button id="id_bt_${commandDataName}"
                    onclick="changeCommandValue(this.id, +1)">+1</button></td>


        <td><button id="id_bt_${commandDataName}"
                    onclick="changeCommandValue(this.id, +10)">+10</button></td>

    `;


    var trSubmit = `
        <td><button id="id_value_${commandObject.commandData.name}" style="width: 100%"
                onclick="onSubmitInteger(this.id)">${valueRandom}</button></td>
    `;

    return trInteger + trSubmit;
}

function appendOnline(commandObject) {
    var trPrefix = "<tr>";
    var trSuffix = "</tr>";
    var trMain = `
        <td>...</td>
        <td>${commandObject.commandData.name}</br>${commandObject.description}</td>
        <td>${commandObject.commandData.stringCode}</td>
    `;

    var trValue = "";
    var valueType = commandObject.commandData.commandHexCode.commandValue.type;
    if (valueType == "string") {
        trValue = formatTrString(commandObject);
    } else if (valueType == "integer"){
        trValue = formatTrInteger(commandObject);
    }

    var trLine = trPrefix + trMain + trValue + trSuffix;

    $("#commandTable").append(trLine);
}

function parseJsonData(jsonStr) {
    var jsonObject = JSON.parse(jsonStr);
    console.log(jsonObject.size);
    jsonObject.commands.forEach(function (command) {
        appendOnline(command);
        gJsonData.set(command.commandData.name, command)
    });
}