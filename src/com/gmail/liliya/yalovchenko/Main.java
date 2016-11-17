package com.gmail.liliya.yalovchenko;

/**
 * change file path in Windows
 * input filePath in Windows = "resources\\input.txt"
 * csv result finePath in Windows = "resources\\DecodeResult.csv"
 *
 * Last four string of code in input.txt file
 * has wrong format, to show how program handle wrong inputs
 */
public class Main {

    public static void main(String[] args) {
        FileUtil fileUtil = new FileUtil();
        fileUtil.setCsvFilePath("resources/DecodeResult.csv");
        fileUtil.setInputFilePath("resources/input.txt");
        fileUtil.decodeFile();

        /*
         * The following code represent another way of decoding data from file.
         * It is more responsive way, because you can modify decoded data before
         * writing it to file or send through the network by any protocol etc.
         * List<String> encodedData = fileUtil.getEncodedData();
         * List<String> decodedData = encodedData.stream().map(s -> Decoder.decode(s).toString()).collect(Collectors.toList());
         * fileUtil.writeToFile(decodedData);
         */

    }
}
