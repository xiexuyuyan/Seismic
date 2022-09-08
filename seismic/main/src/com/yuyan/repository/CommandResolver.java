package com.yuyan.repository;

import com.yuyan.model.Command;
import com.yuyan.model.CommandRecv;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandResolver {

    // if the code length is confirmed in json description
    // like some code both min and max string equal 000 or others
    // then we use the stable value to resolve the code

    private static String formatRegexString(final Command command) {
        String stringCode = command.commandData.stringCode;
        String type = command.commandData.commandHexCode.type;
        String code = command.commandData.commandHexCode.code;
        String minValue = command.commandData.commandHexCode.commandValue.min;
        String maxValue = command.commandData.commandHexCode.commandValue.max;
        String valueType = command.commandData.commandHexCode.commandValue.type;

        String regex;

        String regValue = "+?";

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
                regValue = "([0-9A-F][0-9A-F])" + "{" + minLength + "," + maxLength + "}";
            }
        } else if (valueType.equals("string")) {
            int minLength = minValue.length() / 2;
            int maxLength = maxValue.length() / 2;
            regValue = "([0-9A-F][0-9A-F])" + "{" + minLength + "," + maxLength + "}";
        }
        String regexPrefix = "[0-9A-F]{6}";
        String regexSuffix = regValue + "0D";

        regex = regexPrefix + type + code + regexSuffix;

        return regex;
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
            if (matchedStringShouldLen < 2) {
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

    public static String[] checkUnit(final String commandSrc, List<Command> commandList) {
        List<String> matchCommands = new ArrayList<>();

        for (Command command : commandList) {
            String regex = formatRegexString(command);
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

    public static List<CommandRecv> checkUnitRecv(final String commandSrc, List<Command> commandList) {
        List<CommandRecv> matchCommands = new ArrayList<>();

        for (Command command : commandList) {
            String regex = formatRegexString(command);
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(commandSrc);

            while (matcher.find()) {
                // todo(duplicate)
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

}
