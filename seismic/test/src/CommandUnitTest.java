import com.yuyan.model.Command;
import com.yuyan.repository.CommandHelper;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandUnitTest {

    private static final int SEND_TIMES = 10000;
    private static final int ONE_TIME_MAX_COMMAND_SUM = 20;
    private static final int OTHER_HEX_COMPLEX_LEVEL = 5;

    public static void main(String[] args) throws IOException {
        CommandHelper.init("seismic/test/res/A311D2_medium_auto_test_command.json");
        List<Command> commandList = CommandHelper.INSTANCE.commandList.commands;
        int commandListSize = commandList.size();

        Map<String, String[]> testSamples = new HashMap<>();

        for (int i = 0; i < SEND_TIMES; i++) {
            int randomCommandSum = new Random().nextInt(ONE_TIME_MAX_COMMAND_SUM) + 1;
            int[] randomIndex = formatRandomIndex(randomCommandSum, commandListSize);

            String[] commands = new String[randomCommandSum];

            for (int j = 0; j < commands.length; j++) {
                Command command = commandList.get(randomIndex[j]);
                String commandString = command.commandData.stringCode;
                commands[j] = commandString.replace('X', '0');
            }

            testSamples.put(randomCommand(OTHER_HEX_COMPLEX_LEVEL, commands), commands);
        }

        checkFunc(testSamples, commandList);
    }
    public static String[] checkUnit(final String commandSrc, List<Command> commandList) {
        List<String> matchCommands = new ArrayList<>();

        for (Command command : commandList) {
            String stringCode = command.commandData.stringCode;
            String type = command.commandData.commandHexCode.type;
            String code = command.commandData.commandHexCode.code;
            String minValue = command.commandData.commandHexCode.commandValue.min;
            String maxValue = command.commandData.commandHexCode.commandValue.max;
            String valueType = command.commandData.commandHexCode.commandValue.type;

            String regex;
            boolean lengthConfirmed = false;
            if (minValue.equals(maxValue)) {
                regex = stringCode;
                lengthConfirmed = true;
            } else {
                String regValue = "+?";
                if (valueType.equals("integer")) {
                    int minLength = minValue.length();
                    int maxLength = maxValue.length();
                    regValue = "{" + minLength + "," + maxLength + "}";
                    lengthConfirmed = false;
                } else if (valueType.equals("string")) {
                    int minLength = minValue.length() / 2;
                    int maxLength = maxValue.length() / 2;
                    regValue = "{" + minLength + "," + maxLength + "}";
                    lengthConfirmed = false;
                }
                String regexPrefix = "[0-9A-F]{6}";
                String regexSuffix = "([0-9A-F][0-9A-F])" + regValue + "0D";

                regex = regexPrefix + type + code + regexSuffix;
            }

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(commandSrc);

            // System.out.println("----------" + regex);
            while (matcher.find()) {
                // todo(duplicate)
                boolean confirmToAdd = true;

                String matchT = matcher.group();
                if (!lengthConfirmed) {
                    // look from head to tail until suitable CR
                    String lenInHeadST = matchT.substring(0, 2);
                    int lenInHead = Integer.parseInt(lenInHeadST, 16) - 0x30;
                    int matchedStringShouldLen = ((lenInHead + 1) * 2);

                    if (matchT.length() < matchedStringShouldLen) {
                        confirmToAdd = false;
                    } else {
                        if (matchedStringShouldLen < 2) {
                            confirmToAdd = false;
                        } else {
                            String shouldBeCR = matchT.substring(
                                    matchedStringShouldLen - 2, matchedStringShouldLen);// [ , )
                            if (shouldBeCR.equals("0D")) {
                                matchT = matchT.substring(0, matchedStringShouldLen);
                                confirmToAdd = true;
                            }
                        }
                    }
                }

                if (confirmToAdd) {
                    matchCommands.add(matchT);
                }
            }
        }

        final String[] re = new String[matchCommands.size()];
        for (int i = 0; i < matchCommands.size(); i++) {
            re[i] = matchCommands.get(i);
        }

        return re;
    }

    private static List<String> toConfirm(final String[] from, final String[] findIn) {
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

    private static void checkFunc(Map<String, String[]> testSamples, List<Command> commandList) {
        String[] randomHexCodes = new String[testSamples.size()];
        int index = 0;
        for (String s : testSamples.keySet()) {
            randomHexCodes[index++] = s;
        }
        for (String randomHexCode : randomHexCodes) {
            // System.out.println("randomHexCode = " + randomHexCode);
            String[] originCommands = testSamples.get(randomHexCode);
            String[] matchedCommands = checkUnit(randomHexCode, commandList);

            List<String> checkA = toConfirm(matchedCommands, originCommands);
            List<String> checkB = toConfirm(originCommands, matchedCommands);
            // System.out.println(randomHexCode);
            if (checkA.size() != 0 || checkB.size() != 0) {
                System.out.println(Arrays.toString(matchedCommands));
                System.out.println(Arrays.toString(originCommands));
                System.out.println(checkA);
                System.out.println(checkB);
                System.out.println(randomHexCode);
                System.out.println("notmatch");
                System.out.println();
            }
        }
    }



    private static int[] formatRandomIndex(int sum, int length) {
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


    private static String randomCommand(int level, String[] commands) {
        int _level = level;
        int[] complexIndex = new int[]{1, 3, 5, 9, 15, 30};

        if (_level <= 0) {
            _level = 1;
        }

        _level = Math.min(_level, complexIndex.length);
        int complexStringMaxTimes = complexIndex[_level];

        StringBuilder builder = new StringBuilder();
        builder.append(formatRandomHexString(complexStringMaxTimes));

        for (String command : commands) {
            builder.append(command);
            builder.append(formatRandomHexString(complexStringMaxTimes));
        }

        return builder.toString();
    }

    private static String formatRandomHexString(int maxSize) {
        if (maxSize <= 0) {
            maxSize = 1;
        }

        StringBuilder builder = new StringBuilder();
        int prefixTimes = new Random().nextInt(maxSize) + 1;
        for (int i = 0; i < prefixTimes; i++) {
            int randomInt = new Random().nextInt(256);
            String str = outputStringFormat(receiveFormat(sendFormat(inputFormat(randomInt))));
            builder.append(str);
        }
        return builder.toString();
    }









    // input-format     String["00", "FF"] | char-int:[00, FF]
    // send-format      int:[0xFF_FF_FF_00, 0xFF_FF_FF_FF] 32bit
    //                  int:[-0x80, 0x7F]
    //                  int[-0x80, 0x7F] == byte[-0x80, 0x7F]
    // transport-format byte:[-0x80, 0x7F]
    //                  byte[-0x80, 0x7F] == int[-0x80, 0x7F]
    //                  int:[-0x80, 0x7F]
    // receive-format   int:[0xFF_FF_FF_00, 0xFF_FF_FF_FF] 32bit
    // output-format    String["00", "FF"] | char-int:[00, FF]

    private static int inputFormat(int d) {
        return d & 0x00_00_00_FF;
    }

    private static int inputFormat(String s) {
        if (s.matches("\\d{1,2}")) {
            return (Integer.parseInt(s) & 0x00_00_00_FF);
        } else {
            return 0x00_00_00_00;
        }
    }

    private static byte sendFormat(int d) {
        return (byte) ((0x00_00_00_FF & d) - 0x80);
    }

    private static int receiveFormat(byte b) {
        return (b + 0x80) & 0x00_00_00_FF;
    }

    private static int outputIntFormat(int d) {
        return (0x00_00_FF_FF & d);
    }

    private static String outputStringFormat(int d) {
        int high = (0x00_00_00_F0 & d) >> 4;
        int low = 0x00_00_00_0F & d;

        char[] HEX_CODE = new char[]{
                  '0', '1', '2', '3', '4', '5', '6', '7'
                , '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        return HEX_CODE[high] + "" + HEX_CODE[low];
    }

}
