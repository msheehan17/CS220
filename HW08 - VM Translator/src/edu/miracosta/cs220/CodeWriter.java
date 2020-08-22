package edu.miracosta.cs220;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * INSTANCE VARIABLES:
 * - codeWriter (PrintWriter): The object responsible for writing all ASM commands to the .asm file.
 * - jumpsMade (int): For keeping track of the jumps made in order to better facilitate writing the jumps in ASM.
 *
 * METHODS:
 * - Full Constructor: Connects with the .asm to be written to by the program.
 * - writeArithmetic (String): Based on the arithmetic command given, writes the respective VM code in ASM.
 * - writePushPop (String, String, int): Based on the push/pop command given, writes the respective VM code in ASM.
 * - close: Closes the PrintWriter object's stream.
 *
 * Author: Matt Sheehan
 */
class CodeWriter {

    private PrintWriter codeWriter;
    private int jumpsMade = 0;

    /**
     * Full constructor. Connects the program with the output file.
     * @param outputFileName The output file name.
     */
    CodeWriter (String outputFileName) {
        try {
            codeWriter = new PrintWriter(new FileOutputStream(outputFileName));
        } catch(FileNotFoundException e) {
            System.out.println("Cannot write to file; terminating program.");
            System.exit(0);
        }
    }

    /**
     * Based on the arithmetic command given, produces the respective ASM code (based on switch expression).
     * @param command The arithmetic command.
     */
    void writeArithmetic(String command) {
        String arithmeticBoilerPlate = "@SP\nAM=M-1\nD=M\nM=0\nA=A-1\n"; // For and, or, not, neg.
        String comparisonBoilerPlatePartOne = "@SP\nAM=M-1\nD=M\nA=A-1\nD=M-D\n@FALSE" + jumpsMade + "\nD;"; // gt, lt, eq.
        String comparisonBoilerPlatePartTwo = "\n@SP\nA=M-1\nM=-1\n@CONTINUE" + jumpsMade + "\n0;JMP\n(FALSE" + jumpsMade
                                            + ")\n@SP\nA=M-1\nM=0\n(CONTINUE" + jumpsMade + ")\n"; // gt, lt, eq.

        String asmCode = switch (command) {
            case "add": yield arithmeticBoilerPlate + "M=M+D";
            case "sub": yield arithmeticBoilerPlate + "M=M-D";
            case "and": yield arithmeticBoilerPlate + "M=M&D";
            case "or" : yield arithmeticBoilerPlate + "M=M|D";
            case "not": yield "@SP\nA=M-1\nM=!M";
            case "neg": yield "@SP\nA=M-1\nM=-M";
            case "gt" : { jumpsMade++; yield comparisonBoilerPlatePartOne + "JLE" + comparisonBoilerPlatePartTwo; }
            case "lt" : { jumpsMade++; yield comparisonBoilerPlatePartOne + "JGE" + comparisonBoilerPlatePartTwo; }
            case "eq" : { jumpsMade++; yield comparisonBoilerPlatePartOne + "JNE" + comparisonBoilerPlatePartTwo; }
            default: yield null;
        };
        codeWriter.println(asmCode);
    }

    /**
     * Based on the push/pop command given, produces the respective ASM code (based on switch expression).
     * @param pushOrPopCommand Determines if the command is a push or pop command.
     * @param segment The memory segment to be pushed to/popped from.
     * @param index The index of the memory segment.
     */
    void writePushPop(String pushOrPopCommand, String segment, int index ) {
        String pushOrPopASM;
        String staticPointerPushOrPopASM;
        String translatedASM;

        if (pushOrPopCommand.equals("push")) {
            pushOrPopASM ="\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n";
            staticPointerPushOrPopASM = "\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n";
        } else {
            pushOrPopASM = "\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n";
            staticPointerPushOrPopASM = "\nD=A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n";
        }

        translatedASM = switch(segment) {
            case "constant": yield "@" + index + "\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n";
            case "local" :   yield "@LCL\nD=M\n@"  + index + pushOrPopASM;
            case "argument": yield "@ARG\nD=M\n@"  + index + pushOrPopASM;
            case "this":     yield "@THIS\nD=M\n@" + index + pushOrPopASM;
            case "that":     yield "@THAT\nD=M\n@" + index + pushOrPopASM;
            case "temp":     yield "@R5\nD=M\n@"   + (index + 5) + pushOrPopASM;
            case "static":   yield "@" + (index + 16) + staticPointerPushOrPopASM;
            case "pointer":  yield "@" + ((index == 0) ? "THIS" : "THAT") + staticPointerPushOrPopASM;
            default: yield null;
        };
        codeWriter.println(translatedASM);
    }

    /**
     * Closes the output file.
     */
    void close() {
        codeWriter.close();
    }
}
