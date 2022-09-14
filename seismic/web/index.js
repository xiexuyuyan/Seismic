function print_success() {
    console.log("success");
}

const gJsonData = new Map();


function requestAllCommandStr(parseJsonDataFunc){
    const reData = {"size": 10};
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

function requestSendCommand(commandObject, value){
    $.ajax({
        type: "POST",
        url: "http://localhost:6699/com.yuyan.seismic/POST_COMMAND",
        data:{
            'command_data_name': commandObject.commandData.name,
            'value': value
        },
        success: function (result) {
            console.log("success: " + result)
        },
        error: function (result) {
            console.log("failed: " + result)
        }
    });
}

function formatTrString(commandObject) {
    const trString = `
        <td colspan="5"><input type="text" style="width: calc(100% - 8px)"></td>
    `;

    const trSubmit = `
        <td><button id="id_value_${commandObject.commandData.name}" style="width: 100%"
                onclick="onSubmitStableInteger(this.id)">send</button></td>
    `;

    return trString + trSubmit;
}

function changeCommandValue(commandButtonId, op) {
    const commandDataName = commandButtonId.split('id_bt_')[1];
    const commandObject = gJsonData.get(commandDataName);

    const minString = commandObject.commandData.commandHexCode.commandValue.min;
    const maxString = commandObject.commandData.commandHexCode.commandValue.max;
    const minValue = parseInt(minString);
    const maxValue = parseInt(maxString);

    const valueObject = $("#" + "id_value_" + commandObject.commandData.name);
    const progressObject = $("#" + "id_progress_" + commandObject.commandData.name);
    const currentValueText = valueObject.text();
    const currentValue = Number(currentValueText);
    let setTo = currentValue + Number(op);
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
    const commandDataName = commandSubmitValueId.split('id_value_')[1];
    const commandObject = gJsonData.get(commandDataName);
    const valueObject = $("#" + "id_value_" + commandObject.commandData.name)
    console.log("click on: " + commandDataName);

    const valueType = commandObject.commandData.commandHexCode.commandValue.type;
    const maxString = commandObject.commandData.commandHexCode.commandValue.max;

    if (valueType != "integer") {
        return;
    }

    let builder = "";
    for(let i = 0; i < maxString.length; i++) {
        builder = builder + "3";
        builder = builder + maxString[i];
    }

    const commandType = commandObject.commandData.commandHexCode.type;
    const commandCode = commandObject.commandData.commandHexCode.code;

    const prefixCode = "3031" + commandType + commandCode;
    let value = prefixCode + builder + "0D";
    const head = (value.length/2 + 0x30).toString(16);
    value = head + value;
    // console.log("value = " + value);
    requestSendCommand(commandObject, value);
}

function formatTrStableInteger(commandObject) {
    const trString = `
        <td colspan="5"></td>
    `;

    const trSubmit = `
        <td><button id="id_value_${commandObject.commandData.name}" style="width: 100%"
                onclick="onSubmitStableInteger(this.id)">send</button></td>
    `;

    return trString + trSubmit;
}

function onSubmitInteger(commandSubmitValueId) {
    const commandDataName = commandSubmitValueId.split('id_value_')[1];
    const commandObject = gJsonData.get(commandDataName);
    const valueObject = $("#" + "id_value_" + commandObject.commandData.name);
    const currentValueText = valueObject.text();
    const currentValue = Number(currentValueText);

    const minString = commandObject.commandData.commandHexCode.commandValue.min;
    const maxString = commandObject.commandData.commandHexCode.commandValue.max;

    const commandType = commandObject.commandData.commandHexCode.type;
    const commandCode = commandObject.commandData.commandHexCode.code;

    const prefixCode = "3031" + commandType + commandCode;
    let builder = "";
    let offset = maxString.length - currentValueText.length;
    for(let i = 0; i < maxString.length; i++) {
        builder = builder + "3";
        if (i >= (offset)) {
            builder = builder + currentValueText[i - offset];
        } else {
            builder = builder + "0";
        }
    }
    let value = prefixCode + builder + "0D";
    const head = (value.length/2 + 0x30).toString(16);
    value = head + value;
    console.log("click on: " + commandDataName + ", " + value);
    requestSendCommand(commandObject, value);
}

function formatTrInteger(commandObject) {
    const commandDataName = commandObject.commandData.name;
    const minString = commandObject.commandData.commandHexCode.commandValue.min;
    const maxString = commandObject.commandData.commandHexCode.commandValue.max;
    const minValue = parseInt(minString);
    const maxValue = parseInt(maxString);

    if (minValue == maxValue) {
        return formatTrStableInteger(commandObject);
    }

    let valueRandom = Math.random() * (maxValue - minValue) + minValue;
    valueRandom = parseInt(valueRandom);
    const trInteger = `
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


    const trSubmit = `
        <td><button id="id_value_${commandObject.commandData.name}" style="width: 100%"
                onclick="onSubmitInteger(this.id)">${valueRandom}</button></td>
    `;

    return trInteger + trSubmit;
}

function appendOnline(commandObject) {
    const trPrefix = "<tr>";
    const trSuffix = "</tr>";
    const trMain = `
        <td>...</td>
        <td class="name-width">${commandObject.commandData.name}</br>${commandObject.description}</td>
    `;
    const trExecuteEcho = `
        <td>...</td>
    `;

    let trValue = "";
    const valueType = commandObject.commandData.commandHexCode.commandValue.type;
    if (valueType == "string") {
        trValue = formatTrString(commandObject);
    } else if (valueType == "integer"){
        trValue = formatTrInteger(commandObject);
    }

    const trLine = trPrefix + trMain + trValue + trExecuteEcho + trSuffix;

    $("#commandTable").append(trLine);
}

function parseJsonData(jsonStr) {
    const jsonObject = JSON.parse(jsonStr);
    console.log(jsonObject.size);
    jsonObject.commands.forEach(function (command) {
        appendOnline(command);
        gJsonData.set(command.commandData.name, command)
    });
}