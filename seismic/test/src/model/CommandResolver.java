package model;

import com.yuyan.utils.Log;
import org.yuyan.command.model.Command;
import org.yuyan.command.model.CommandHexCode;
import org.yuyan.command.model.CommandRecv;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandResolver {
    private static final String TAG = "CommandResolver";

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

    /**
     * For a given string which contains "00"..."FF"
     * Parse it witch command rule write in {@link Command}
     * @param commandSrc A string is made up of "00"..."FF"
     * @param commandList Command rule auto generate by json
     * @param reply Command is described as to mode Send and Reply
     * @return List&lt;{@link CommandRecv}&gt; matched command with source string
     */
    public static List<CommandRecv> parse(final String commandSrc, final List<Command> commandList, final boolean reply) {
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

            String commandDescription = command.description;
            // Log.i(TAG, "[Coder Wu] parse: commandDescription = \n" + commandDescription);
            // Log.i(TAG, "[Coder Wu] parse: regex = \n" + regex);

            while (matcher.find()) {
                // todo(20220906-101950: duplicate)
                String matchT = matcher.group();
                // Log.i(TAG, "[Coder Wu] parse: matchT = " + matchT);
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
