package com.gmail.liliya.yalovchenko;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * The class {@code FileUtil} provide methods for performing reading from and writing to file
 * {@code inputFilePath} - path to file from where encoded data are reading
 * {@code csvFilePath} - path to file where decoded data are writing
 * **/
public class FileUtil {

    private String inputFilePath;
    private String csvFilePath;

    public FileUtil() {}

    public FileUtil(String inputFilePath, String csvFilePath) {
        this.inputFilePath = inputFilePath;
        this.csvFilePath = csvFilePath;
    }

    /**
     * Reads the encoded string from input file {@code inputFilePath},
     * decodes it and then write to csv file {@code csvFilePath}
     * Decode all strings in input file and write all of them to output file
     */
    public void decodeFile() {

        try (BufferedReader reader = new BufferedReader(new FileReader(this.inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(this.csvFilePath) )) {

            String line;

            while ((line = reader.readLine()) != null) {
                String str = Decoder.decode(line).toString();
                writer.write(str);
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Reads all encoded strings from input file {@code inputFilePath},
     * and return it
     *
     * @return list of read strings from file
     */
    public List<String> getEncodedData() {
        List<String> allEncodedString = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(this.inputFilePath))) {
            stream.forEach(allEncodedString::add);
        } catch (IOException e) {
            System.out.println("Error while reading input file");

            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                System.out.println(stackTraceElement);
            }
        }
        return allEncodedString;
    }

    /**
     * Write list of strings to output file
     */
    public void writeToFile(List<String> allDecodedString) {

        try {
            Files.write(Paths.get(this.csvFilePath), allDecodedString);
        } catch (IOException e) {
            System.out.println("Error while writing to file");

            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                System.out.println(stackTraceElement);
            }
        }
    }

    public String getCsvFilePath() {
        return csvFilePath;
    }

    public void setCsvFilePath(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    public String getInputFilePath() {
        return inputFilePath;
    }

    public void setInputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

}
