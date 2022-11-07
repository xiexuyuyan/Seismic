function print_success() {
    console.log("success");
}

const availableCommands = new Array(
        "GET_TEST_BACKLIGHT_VALUE"
        , "SET_TEST_BACKLIGHT_VALUE"
        , "GET_TEST_VOLUME"
        , "SET_TEST_VOLUME"
        , "SET_TEST_HOTSPOT_NAME"
        , "GET_TEST_MAC_ADDRESS"
        , "BURN_HDCP_KEY"

//        , "IS_TOUCHLOCK_ENABLE"
//        , "SET_TOUCHLOCK_ENABLE"
//        , "IS_IMAGE_FREEZED"
//        , "SET_IMAGE_FREEZED"
//        , "IS_R_S232_ENABLE"
//        , "SET_R_S232_ENABLE"
//        , "IS_W_O_L_ENABLE"
//        , "SET_W_O_L_ENABLE"
//        , "GET_BAUD_RATE"
//        , "SET_BAUD_RATE"
        , "SET_R_T_C_SHUTDOWN_TIME"
        , "GET_R_T_C_SHUTDOWN_TIME"
        , "SET_R_T_C_BOOT_TIME"
        , "CANCEL_R_T_C_BOOT_TIME"
        , "GET_R_T_C_BOOT_TIME"
        , "SET_R_T_C_TIME"
        , "GET_R_T_C_TIME"
        , "GET_BOOT_MODE"
        , "SET_BOOT_MODE"
        , "FACTORY_RESET"
        , "GET_L_A_N_STATUS"
        , "SET_L_A_N"
        , "GET_AUTO_SWICH_WHEN_NO_SIGNAL_STATUS"
        , "SET_AUTO_SWICH_WHEN_NO_SIGNAL"
        , "GET_WAKE_UP_REASON"
        , "GET_MAC_ADDRESS"
        , "SET_MAC_ADDRESS"
        , "GET_SERIAL_NUMBER"
        , "SET_SERIAL_NUMBER"
        , "GET_BOOT_TIME_AMOUNT"
        , "SET_BOOT_TIME_AMOUNT"
        , "GET_TOUCH_VERSION"
        , "GET_DEVICE_NAME"
        , "SET_DEVICE_NAME"
        , "GET_DEVICE_MODEL"
        , "GET_TOUCH_DRIVE_VERSION"
        , "SET_ENERGY_WARNING_STATUS"
        , "GET_ENERGY_WARNING_STATUS"
        , "SET_ENERGY_WARNING_LEVEL"
        , "GET_ENERGY_WARNING_LEVEL"
        , "GET_WAKE_UP_SOURCE"
        , "SET_WAKE_UP_SOURCE"
        , "GET_COMMON_BOARD_SCREEN_INFO"
        , "SET_COMMON_BOARD_SCREEN_INFO"
        , "GET_USB_UPGRADE"
        , "SET_USB_UPGRADE"
        , "GET_K_S_D_A_NUMBER"
        , "SET_COMMON_BOARD_MOTHERBOARD_INFO"
        , "GET_COMMON_BOARD_MOTHERBOARD_INFO"
        , "SET_COMMON_BOARD_CUSTOM_INFO"
        , "GET_COMMON_BOARD_CUSTOM_INFO"
        , "GET_PANEL_NAME"
        , "GET_BOARD_VERSION"
        , "GET_NO_OPERATION_STANDBY_TIME"
        , "SET_NO_OPERATION_STANDBY_TIME"
        , "GET_AUD_SPK_CONF"
        , "SET_AUD_SPK_CONF"
        , "GET_AUD_SUB_CONF"
        , "SET_AUD_SUB_CONF"
        , "IS_W62_B_BOARD"
        , "GET_SCREEN_TRANSLATION"
        , "SET_SCREEN_TRANSLATION"
        , "GET_SO_VERSION"
        , "SET_LOGO_CONFIG"
        , "GET_LOGO_DISPLAY"
        , "SET_LOGO_DISPLAY"
        , "CHANGE_LOGO"
        , "SET_OTG_PORT_CHANGE"
        , "SET_LED_STATUS"
        , "SET_POWER_SAVE_MODE"
        , "GET_POWER_SAVE_MODE"
        , "REBOOT_SYSTEM"
        , "SET_USB_FOLLOW_MODE"
        , "GET_USB_FOLLOW_MODE"
        , "SET_SLEEP_MODE_ENABLE"
        , "IS_SLEEP_MODE_ENABLED"
        , "GET_BURN_TIME_AMOUNT"
        , "SET_BURN_TIME_AMOUNT"
//        , "SET_H_D_C_P_KEY"
        , "GET_H_D_C_P_KEY_EXISTS"
//        , "ANDROID_O_P_S_CONTACT_OPEN"
//        , "ANDROID_O_P_S_CONTACT_CLOSE"
        , "GET_BOARD_TYPEC_VERSION"
        , "IS_FILE_EXIST"
        , "GET_ETH_IO_STATUS"
        , "SET_UART0_SWITCH_FUNC"
        , "SET_ETHERNET_RESET"
        , "CHANGE_R_S232_DEBUG"
        , "GET_CLOCK_DATE_FORMAT"
        , "SET_CLOCK_DATE_FORMAT"
        , "SET_MIC_MIXER_INPUT_AMPLIFY"
        , "GET_ETH_MAC_ADDRESS"
        , "SET_ETH_MAC_ADDRESS"
        , "SET_ICMP_STATUS"
        , "GET_ICMP_STATUS"
        , "REBOOT_SYSTEM_AFTER_DELAY"
        , "GET_TOP_ACTIVITY"
        , "GET_EBSW_DEVICE_MODEL"
        , "SET_DEVICE_MODEL"
        , "SET_PANEL_NAME"
        );

function attrCommandDisabledCheck(commandDataName) {
    for (let i = 0; i < availableCommands.length; i++) {
        if (availableCommands[i] != commandDataName) {
            continue;
        } else {
            return "";
        }
    }
    return "disabled";
}

const gJsonData = new Map();

function requestSerialportSwitch(status){
    const reData = {"size": 10};
    $.ajax({
        type: "POST",
        url: "http://localhost:6699/com.yuyan.seismic/POST_SWITCH_SERIALPORT",
        data:{
            'status': status,
        },
        success: function (result) {
            console.log("success: " + result);
        },
        error : function() {
            console.log("error");
        }
    });
}

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
    var postTo = "POST_COMMAND";

    var commandDataName = commandObject.commandData.name;
    if (commandDataName === "BURN_HDCP_KEY") {
        postTo = "POST_COMMAND_BURN_HDCP_KEY";
    }

    var isSerialport = $("#SwitchSerialport").is(':checked');
    var isLan = $("#SwitchLan").is(':checked');

    if (isSerialport) {
        postTo = postTo + "_LOCAL";
    } else if (isLan) {
        postTo = postTo + "_REMOTE";
    } else {
        return;
    }

    $.ajax({
        type: "POST",
        url: "http://localhost:6699/com.yuyan.seismic/" + postTo,
        data:{
            'command_data_name': commandObject.commandData.name,
            'value': value
        },
        success: function (result) {
            console.log("success: " + result);
            echoExecute(result);
        },
        error: function (result) {
            console.log("failed: " + result);
        }
    });
}

function echoExecute(rawResultStr) {
    const rawResult = JSON.parse(rawResultStr);
    const commandDataName = rawResult.name;
    const commandObject = gJsonData.get(commandDataName);
    const commandValue = rawResult.value;

    const echoObject = $("#" + "id_exec_echo_" + commandObject.commandData.name);
    echoObject.text(commandValue);
}

function inputStringBlur(inputStringId) {
    const commandDataName = inputStringId.split('id_string_input_')[1];
    const commandObject = gJsonData.get(commandDataName);

    const inputObject = $("#" + inputStringId);
    const inputString = inputObject.val();
    const codeString = valueWrapper(commandObject, inputString);
    const commandStringObject = $("#" + "id_command_string_" + commandDataName);
    commandStringObject.text(codeString);
    const stringValueObject = $("#" + "id_string_value_" + commandDataName);
    stringValueObject.text(inputString);
    console.log("commandDataName = " + commandDataName + ", inputObject = " + inputString + ", codeString = " + codeString);
}

function onSubmitString(commandSubmitId) {
    const commandDataName = commandSubmitId.split('id_submit_')[1];
    const commandObject = gJsonData.get(commandDataName);

    const commandStringObject = $("#" + "id_command_string_" + commandDataName);
    const currentCodeString = commandStringObject.text();
    console.log("currentCodeString = " + currentCodeString);
    requestSendCommand(commandObject, currentCodeString);
}

function formatTrString(commandObject) {
    const sample = "ABC123";
    let codeString = valueWrapper(commandObject, sample);
    const commandDataName = commandObject.commandData.name;

    const trString = `
        <td colspan="5" style="max-width: 300px">
            <input id="id_string_input_${commandDataName}" onBlur="inputStringBlur(this.id)" type="text" style="width: calc(100% - 8px)">
            </br>
            <span id="id_string_value_${commandDataName}">${sample}</span>
            &brvbar;
            <span id="id_command_string_${commandDataName}">${codeString}</span>
        </td>
    `;
    const attrDisabled = attrCommandDisabledCheck(commandDataName);
    const trSubmit = `
        <td><button ${attrDisabled} id="id_submit_${commandDataName}" style="width: 100%"
                onclick="onSubmitString(this.id)">send</button></td>
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

    let codeString = valueWrapper(commandObject, String(setTo));
    const commandStringObject = $("#" + "id_command_string_" + commandDataName);
    commandStringObject.text(codeString);
}

function onSubmitStableInteger(commandSubmitValueId) {
    const commandDataName = commandSubmitValueId.split('id_submit_')[1];
    const commandObject = gJsonData.get(commandDataName);
    const valueObject = $("#" + "id_value_" + commandObject.commandData.name)
    console.log("click on: " + commandDataName);

    const maxString = commandObject.commandData.commandHexCode.commandValue.max;
    let value = valueWrapper(commandObject, maxString);

    requestSendCommand(commandObject, value);
}

function formatTrStableInteger(commandObject) {
    const commandDataName = commandObject.commandData.name;

    const maxString = commandObject.commandData.commandHexCode.commandValue.max;
    let codeString = valueWrapper(commandObject, maxString);

    const trString = `
        <td colspan="5" style="max-width: 300px"><span id="id_command_string_${commandDataName}">${codeString}</span></td>
    `;

    const attrDisabled = attrCommandDisabledCheck(commandDataName);
    const trSubmit = `
        <td><button ${attrDisabled} id="id_submit_${commandDataName}" style="width: 100%"
                onclick="onSubmitStableInteger(this.id)">send</button></td>
    `;

    return trString + trSubmit;
}

function valueWrapper(commandObject, valueText) {
    const valueType = commandObject.commandData.commandHexCode.commandValue.type;
    const maxString = commandObject.commandData.commandHexCode.commandValue.max;
    const minString = commandObject.commandData.commandHexCode.commandValue.min;

    let valueLength = 0;
    if (maxString.length == minString.length) {
        valueLength = maxString.length
    } else {
        valueLength = valueText.length;
        if (valueType == "string") {
            valueLength = valueLength * 2;
        }
    }


    let builder = "";
    if (valueType == "integer") {
        let offset = valueLength - valueText.length;
        for(let i = 0; i < valueLength; i++) {
            builder = builder + "3";
            if (i >= (offset)) {
                builder = builder + valueText[i - offset];
            } else {
                builder = builder + "0";
            }
        }
    } else if (valueType == "string") {
        valueLength = valueLength / 2;
        let offset = valueLength - valueText.length;
        for(let i = 0; i < valueLength; i++) {
            if (i >= (offset)) {
                builder = builder + valueText.charCodeAt(i - offset).toString(16);
            } else {
                builder = builder + "00";
            }
        }
    }

    const commandType = commandObject.commandData.commandHexCode.type;
    const commandCode = commandObject.commandData.commandHexCode.code;

    const prefixCode = "3031" + commandType + commandCode;

    let value = prefixCode + builder + "0D";
    const head = (value.length/2 + 0x30).toString(16).toUpperCase();
    value = head + value;
    const commandDataName = commandObject.commandData.name;

    return value;
}

function onSubmitInteger(commandSubmitValueId) {
    const commandDataName = commandSubmitValueId.split('id_submit_')[1];
    const commandObject = gJsonData.get(commandDataName);
    const valueObject = $("#" + "id_value_" + commandObject.commandData.name);
    const currentValueText = valueObject.text();
    const currentValue = Number(currentValueText);

    let codeString = valueWrapper(commandObject, currentValueText);

    console.log("click on: " + commandDataName + ", value = " + currentValue + ", code = " + codeString);
    requestSendCommand(commandObject, codeString);
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

    let codeString = valueWrapper(commandObject, valueRandom+"");

    const trInteger = `
        <td><button id="id_bt_${commandDataName}"
                    onclick="changeCommandValue(this.id, -10)">-10</button></td>

        <td><button id="id_bt_${commandDataName}"
                    onclick="changeCommandValue(this.id, -1)">-1</button></td>

        <td><progress id="id_progress_${commandDataName}"
                    value="${valueRandom}"
                    min="${minValue}" max="${maxValue}"
                    style="max-width: 100px"></progress>
                    <label id="id_value_${commandObject.commandData.name}"
                        for="id_progress_${commandDataName}"
                    >${valueRandom}</label></br>
                    <span id="id_command_string_${commandDataName}">${codeString}</span>
                    </td>

        <td><button id="id_bt_${commandDataName}"
                    onclick="changeCommandValue(this.id, +1)">+1</button></td>


        <td><button id="id_bt_${commandDataName}"
                    onclick="changeCommandValue(this.id, +10)">+10</button></td>

    `;

    const attrDisabled = attrCommandDisabledCheck(commandDataName);
    const trSubmit = `
        <td><button ${attrDisabled} id="id_submit_${commandDataName}" style="width: 100%"
                onclick="onSubmitInteger(this.id)">send</button></td>
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
        <td id="id_exec_echo_${commandObject.commandData.name}">...</td>
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

function checkCommandSendBy(obj) {
    var isChecked = $(obj).is(':checked');
    var clickOn = obj.id;

    if (isChecked) {
        if (clickOn === "SwitchSerialport" && $("#SwitchLan").is(':checked')) {
            $("#SwitchLan").prop("checked", false).change();
        } else if (clickOn === "SwitchLan" && $("#SwitchSerialport").is(':checked')){
            $("#SwitchSerialport").prop("checked", false).change();
        }
    }
}

function changeCommandSendBy(obj) {
    var changeOn = obj.id;
    var isChecked = $(obj).is(':checked');
    if (changeOn === "SwitchSerialport") {
        requestSerialportSwitch(isChecked);
    } else if (changeOn === "SwitchLan"){
    }
}
