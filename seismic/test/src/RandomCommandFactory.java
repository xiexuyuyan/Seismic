import com.yuyan.model.Command;
import com.yuyan.model.CommandHexCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomCommandFactory {

    public static String createRandomCommands(final List<Command> commandList, final int sum, String[] coms) {
        int[] randomIndex = formatRandomIndex(sum, commandList.size());

        Command[] commands = new Command[sum];
        for (int i = 0; i < commands.length; i++) {
            commands[i] = commandList.get(randomIndex[i]);
        }

        String[] commandStrList = new String[sum];
        for (int i = 0; i < commandStrList.length; i++) {
            commandStrList[i] = setRandomCommandValue(commands[i], false);
        }

        for (int i = 0; i < commandStrList.length; i++) {
            coms[i] = commandStrList[i];
        }

        return randomCommand(3, commandStrList);
    }


    public static List<String> toConfirm(final String[] from, final String[] findIn) {
        List<String> fromErr = new ArrayList<>();

        for (int fromIndex = 0; fromIndex < from.length; fromIndex++) {
            String fromT = from[fromIndex];

            int confirm = 0;
            for (String findInT : findIn) {
                if (findInT.equals(fromT)) {
                    confirm ++;
                }
            }
            if (confirm != 1) {
                fromErr.add(fromT);
            }
        }

        return fromErr;
    }

    public static String randomCommand(final int level, final String[] commands) {
        int _level = level;
        int[] complexIndex = new int[]{1, 3, 5, 9, 15, 30};

        if (_level <= 0) {
            _level = 1;
        }

        _level = Math.min(_level, complexIndex.length);
        int complexStringMaxTimes = complexIndex[_level];

        StringBuilder builder = new StringBuilder();
        builder.append(formatRandomOtherHexString(complexStringMaxTimes));

        for (String command : commands) {
            builder.append(command);
            builder.append(formatRandomOtherHexString(complexStringMaxTimes));
        }

        return builder.toString();
    }

    private static String formatRandomOtherHexString(final int _maxSize) {
        int maxSize = _maxSize;
        if (maxSize <= 0) {
            maxSize = 1;
        }

        StringBuilder builder = new StringBuilder();
        int prefixTimes = new Random().nextInt(maxSize) + 1;
        for (int i = 0; i < prefixTimes; i++) {
            int randomInt = new Random().nextInt(256);
            String str = Integer.toString(randomInt, 16);
            builder.append(str);
        }
        return builder.toString();
    }

    public static String setRandomCommandValue(final Command command, boolean reply) {
        CommandHexCode commandHexCode;
        if (reply) {
            commandHexCode = command.commandData.replyHexCode;
        } else {
            commandHexCode = command.commandData.commandHexCode;
        }

        String codeValueType = commandHexCode.commandValue.type;
        String id = /*"3031"; */formatRandomHexIntegerValue("01", "99");
        String type = commandHexCode.type;
        String code = commandHexCode.code;
        String min = commandHexCode.commandValue.min;
        String max = commandHexCode.commandValue.max;
        String value;
        if (codeValueType.equals("integer")) {
            value = formatRandomHexIntegerValue(min, max);
        } else {
            value = formatRandomHexStringValue(min, max);
        }

        String header = Integer.toString((0x35) + value.length() / 2, 16);
        return header + id + type + code + value + "0D";
    }


    private static int[] formatRandomIndex(final int _sum, final int _length) {
        int sum = _sum;
        int length = _length;

        if (sum <= 0) {
            sum = new Random().nextInt(length);
        }
        int[] result = new int[sum];
        Arrays.fill(result, -1);

        for (int i = 0; i < sum; i++) {
            int intT = new Random().nextInt(length);

            // fix the duplicates
            for (int j = 0; j < result.length;) {
                if (result[j] == intT) {
                    intT = new Random().nextInt(length);
                    j = 0;
                } else {
                    j++;
                }
            }

            result[i] = intT;
        }

        return result;
    }



    private static String formatRandomHexStringValue(final String minStr, final String maxStr) {
        int minLen = minStr.length() / 2;
        int maxLen = maxStr.length() / 2;

        int randomLen = new Random().nextInt(maxLen - minLen + 1) + minLen;

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < randomLen; i++) {
            int randomInt = new Random().nextInt(98);
            String randomChar = Integer.toString(randomInt + 30, 16).toUpperCase();
            builder.append(randomChar);
        }

        return builder.toString();
    }

    // when minStr looks like '000...'
    // some '0' as the prefix, we regard as it occupancy-width
    // meaning that a format value must like '30303'
    private static String formatRandomHexIntegerValue(final String minStr, final String maxStr) {
        String minStrT = minStr;
        String maxStrT = maxStr;
        if (!minStrT.matches("\\d+")) {
            minStrT = "0";
        }
        if (!maxStrT.matches("\\d+")) {
            maxStrT = "0";
        }
        int minValue = Integer.parseInt(minStrT);
        int maxValue = Integer.parseInt(maxStrT);

        if (maxValue < minValue) {
            maxValue = minValue;
        }

        int randomValue;
        if (maxValue == minValue) {
            randomValue = maxValue;
        } else {
            // [min, max+1)
            randomValue = new Random().nextInt(maxValue - minValue + 1) + minValue;
        }

        int maxLen = String.valueOf(maxValue).length();
        if (maxLen != maxStr.length()) {
            // occupancy-width
            if (maxStr.equals(minStr)) {
                maxLen = maxStr.length();
            } else if (maxStr.length() == minStr.length()) {
                maxLen = maxStr.length();
            }
        }

        int randomValueLen = String.valueOf(randomValue).length();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < maxLen; i++) {
            builder.append("3");
            int j = i - (maxLen - randomValueLen);
            if (j >= 0) {
                builder.append(String.valueOf(randomValue).charAt(j));
            } else {
                builder.append("0");
            }
        }
        return builder.toString();
    }
}
