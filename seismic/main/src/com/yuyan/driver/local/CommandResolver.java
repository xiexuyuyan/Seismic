package com.yuyan.driver.local;

import com.yuyan.model.Command;
import com.yuyan.model.CommandHexCode;
import com.yuyan.model.CommandRecv;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandResolver {
    private static final String TAG = "CommandResolver";

    public static byte[] sendStringToBytes(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < s.length()-1; i+=2) {
            String s1 = s.substring(i, i+2);
            int d = Integer.parseInt(s1, 16);
            byte b1 = (byte) (d & 0x00_00_00_FF);
            b[i/2] = b1;
        }
        return b;
    }

    public static String receiveByteToString(final byte[] buff, int len) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            byte b = buff[i];
            int d = (b & 0x00_00_00_FF);
            String s = Integer.toString(d, 16);
            if (s.length() == 1) {
                s = "0" + s;
            }
            builder.append(s.toUpperCase());
        }
        return builder.toString();
    }


    // if the code length is confirmed in json description
    // like some code both min and max string equal 000 or others
    // then we use the stable value to resolve the code

    private static String formatRegexString(final CommandHexCode commandHexCode) {
        String type = commandHexCode.type;
        String code = commandHexCode.code;
        String minValue = commandHexCode.commandValue.min;
        String maxValue = commandHexCode.commandValue.max;
        String valueType = commandHexCode.commandValue.type;

        String regValue = "([0-9A-F][0-9A-F])+";

        if (valueType.equals("integer")) {
            if (minValue.equals(maxValue)) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < minValue.length(); i++) {
                    builder.append("3");
                    builder.append(minValue.charAt(i));
                }
                regValue = builder.toString();
            } else {
                int minLength = minValue.length();
                int maxLength = maxValue.length();

                // regard the max width as the finally Max occupancy-width
                maxLength = Math.max(minLength, maxLength);

                regValue = "(3[0-9])" + "{" + minLength + "," + maxLength + "}";
            }
        } else if (valueType.equals("string")) {
            if (minValue.equals(maxValue)) {
                regValue = minValue;
            } else {
                int minLength = minValue.length() / 2;
                int maxLength = maxValue.length() / 2;

                // regard the max width as the finally Max occupancy-width
                maxLength = Math.max(minLength, maxLength);

                regValue = "([0-9A-F][0-9A-F])" + "{" + minLength + "," + maxLength + "}";
            }
        }

        String regexPrefix = "[0-9A-F]{6}";
        String regexSuffix = "0D";

        return regexPrefix + type + code + regValue + regexSuffix;
    }

    private static String checkIllegalLen(String matchT) {
        boolean confirmToAdd = true;
        // look from head to tail until suitable CR
        String lenInHeadST = matchT.substring(0, 2);
        int lenInHead = Integer.parseInt(lenInHeadST, 16) - 0x30;
        int matchedStringShouldLen = ((lenInHead + 1) * 2);

        if (matchT.length() < matchedStringShouldLen) {
            confirmToAdd = false;
        } else {
            // could be a bigger data, means the length a command at least should be
            if (matchedStringShouldLen < 8) { // L:prefix[3X3031] == 6, L:suffix[0D] = 2
                confirmToAdd = false;
            } else {
                String shouldBeCR = matchT.substring(
                        matchedStringShouldLen - 2, matchedStringShouldLen);// [ , )
                if (shouldBeCR.equals("0D")) {
                    matchT = matchT.substring(0, matchedStringShouldLen);
                } else {
                    confirmToAdd = false;
                }
            }
        }

        return confirmToAdd ? matchT : null;
    }

    public static String[] checkUnit(final String commandSrc, final List<Command> commandList, final boolean reply) {
        List<String> matchCommands = new ArrayList<>();

        for (Command command : commandList) {
            CommandHexCode commandHexCode;
            if (reply) {
                commandHexCode = command.commandData.replyHexCode;
            } else {
                commandHexCode = command.commandData.commandHexCode;
            }
            String regex = formatRegexString(commandHexCode);
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(commandSrc);

            while (matcher.find()) {
                // todo(duplicate)
                String matchT = matcher.group();
                String checkT = checkIllegalLen(matchT);
                if (checkT != null) {
                    matchCommands.add(checkT);
                }
            }
        }

        final String[] re = new String[matchCommands.size()];
        for (int i = 0; i < matchCommands.size(); i++) {
            re[i] = matchCommands.get(i);
        }

        return re;
    }

    public static List<CommandRecv> checkUnitRecv(final String commandSrc, final List<Command> commandList, final boolean reply) {
        List<CommandRecv> matchCommands = new ArrayList<>();

        for (Command command : commandList) {
            CommandHexCode commandHexCode;
            if (reply) {
                commandHexCode = command.commandData.replyHexCode;
            } else {
                commandHexCode = command.commandData.commandHexCode;
            }
            String regex = formatRegexString(commandHexCode);
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(commandSrc);

            while (matcher.find()) {
                // todo(20220906-101950: duplicate)
                String matchT = matcher.group();
                String checkT = checkIllegalLen(matchT);
                if (checkT != null) {
                    CommandRecv commandRecv = new CommandRecv(checkT, command);
                    matchCommands.add(commandRecv);
                }
            }
        }

        return matchCommands;
    }

    public static String valueReplyFiller(Command command, int value) {
        CommandHexCode replyHexCode = command.commandData.replyHexCode;
        String reMinStr = replyHexCode.commandValue.min;
        String reMaxStr = replyHexCode.commandValue.max;
        return CommandResolver.formatIntegerHexValue(reMinStr, reMaxStr, value);
    }


    public static String valueReplyFiller(Command command, String value) {
        CommandHexCode replyHexCode = command.commandData.replyHexCode;
        String reMinStr = replyHexCode.commandValue.min;
        String reMaxStr = replyHexCode.commandValue.max;
        return CommandResolver.formatStringHexValue(reMinStr, reMaxStr, value);
    }

    // when minStr looks like '000...'
    // some '0' as the prefix, we regard as it occupancy-width
    // meaning that a format value must like '30303'
    private static String formatIntegerHexValue(final String minStr, final String maxStr, final int value) {
        int reValue;

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

        // regex check above confirm that,
        // the min value is 000,
        // so we just check the max value below.

        if (maxValue < minValue) {
            maxValue = minValue;
        }

        reValue = Math.min(value, maxValue);

        int maxLen = String.valueOf(maxValue).length();// 0099 ==> len=2
        if (maxLen != maxStr.length()) { // 0099 ==> len=4
            // occupancy-width
            if (maxStr.equals(minStr)) {
                // case 1: min: "001", max: "001"
                maxLen = maxStr.length();
            } else if (maxStr.length() == minStr.length()) {
                // case 2: min: "001", max: "099"
                maxLen = maxStr.length();
            }
        }

        int reValueLen = String.valueOf(reValue).length();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < maxLen; i++) {
            builder.append("3");
            int j = i - (maxLen - reValueLen);
            if (j >= 0) {
                builder.append(String.valueOf(reValue).charAt(j));
            } else {
                builder.append("0");
            }
        }
        return builder.toString();
    }

    private static String formatStringHexValue(final String minStr, final String maxStr, final String value) {
        int minLen = minStr.length();
        int maxLen = maxStr.length();

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            String s = Integer.toString(c, 16).toUpperCase();
            s = (s.length() != 2) ? "0" + s.charAt(0) : s;
            builder.append(s);
        }

        int reLen = builder.length();
        if (minLen == maxLen) {
            reLen = minLen;
        }

        reLen = Math.max(minLen, reLen);
        reLen = Math.min(maxLen, reLen);

        if (builder.length() < reLen) {
            int offset = reLen - builder.length();
            for (int i = 0; i < offset; i++) {
                builder.append("00");
            }
        }

        return builder.toString();
    }

    public static String getValueString(CommandHexCode commandHexCode, String code) {
        String valuePayload = code.substring(10, code.length()-2);
        String valueType = commandHexCode.commandValue.type;
        String valueReplyStr = "0";
        if (valueType.equals("integer")) {
            StringBuilder c = new StringBuilder();
            for (int i = 0; i < valuePayload.length()-1; i+=2) {
                c.append(valuePayload.charAt(i+1));
            }
            valueReplyStr = c.toString();
        } else if (valueType.equals("string")){
            StringBuilder c = new StringBuilder();
            for (int i = 0; i < valuePayload.length()-1; i+=2) {
                String d = valuePayload.substring(i, i+2);
                int e = Integer.parseInt(d, 16);
                char f = (char) e;
                c.append(f);
            }
            valueReplyStr = c.toString();
        }

        return valueReplyStr;
    }
}
