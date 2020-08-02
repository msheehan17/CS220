package edu.miracosta.cs220;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Assembler.java - Translates assembly code into binary.
 *
 * Algorithm:
 *
 * 1. The program will prompt the user for the name of the .asm file to be translated.
 *
 * 2. The program will sweep through the file, finding labels within the assembly code (labels are identified within
 * the parenthesis) and adding them to the symbol hash-map so that any a-instructions that point to the label
 * (@LABEL) can point to the ROM line number that label was stored at.
 *
 * 3. After identifying symbols, the "second pass" of the program will begin translation, line by line. If the line is
 * an a-instruction (and the symbol portion is a number), it will be directly translated into binary. If it is a
 * reference to a label, the ROM line of the label will be translated into binary. C-instructions will be translated
 * based on their contents (dest., comp., jump). Each translated line will be written to .hack file.
 *
 * Author: Matt Sheehan
 */
public class Assembler {

    public static void main(String [] args) {
        runAssembler();
    }

    /**
     * Wrapper method for running the assembler, translating the assembly code into binary.
     */
    private static void runAssembler() {
        String inputFileName;
        String outputFileName;
        Scanner userInputFromKeyboard = new Scanner(System.in);
        SymbolTable assemblyCodeSymbolManager = new SymbolTable();
        PrintWriter binaryCodeWriter;

        System.out.print("Please enter the file name: ");
        inputFileName = userInputFromKeyboard.nextLine();
        outputFileName = inputFileName.replace ("asm", "hack");

        try {
            binaryCodeWriter = new PrintWriter(new FileOutputStream(outputFileName));
            addSymbolsToTheSymbolTable(inputFileName, assemblyCodeSymbolManager);
            translateAssembly(inputFileName, assemblyCodeSymbolManager, binaryCodeWriter);
            binaryCodeWriter.close();
        } catch (FileNotFoundException e) {
            System.err.println("Could not write to output file: " + outputFileName);
        }
    }

    /**
     * Coverts a decimal number to binary (String version).
     * @param numberToConvertToBinary The number to be converted.
     * @return The binary representation of the decimal number.
     */
    private static String decimalToBinary(int numberToConvertToBinary) {
        StringBuilder binaryString = new StringBuilder (Integer.toBinaryString(numberToConvertToBinary));
        return "0".repeat(16 - binaryString.length()) + binaryString.toString (); // Make 16 digits.
    }

    /**
     * Initial pass through the .asm file, determines the need for creating variables/labels within the symbol table.
     * @param inputFileName The .asm file name.
     * @param symbolTable The table which will store the newly added variables/labels within the .asm file.
     */
    private static void addSymbolsToTheSymbolTable(String inputFileName, SymbolTable symbolTable) {
        int romLine = 0;
        Parser assemblyCodeParser = new Parser(inputFileName);

        while (assemblyCodeParser.hasMoreCommands()) {
            assemblyCodeParser.advance();

            if (assemblyCodeParser.getCommandType () == 'A' || assemblyCodeParser.getCommandType() == 'C') {
                ++romLine;
            } else if ( assemblyCodeParser.getCommandType() == 'L') {
                symbolTable.addSymbol(assemblyCodeParser.getSymbolInAssemblyCode(), romLine);
            }
        }
    }

    /**
     * Translates the assembly code into its binary representation and prints it to the output file.
     * @param assemblyFileName The assembly input file.
     * @param symbolTable The symbol table for generating the address of the symbol given.
     * @param output The hack output file.
     */
    private static void translateAssembly(String assemblyFileName, SymbolTable symbolTable, PrintWriter output) {
        int ramLineNumber = 16;
        Parser assemblyCodeParser = new Parser(assemblyFileName);
        CInstructionMapper cInstructionMapper = new CInstructionMapper();

        while (assemblyCodeParser.hasMoreCommands()) {
            assemblyCodeParser.advance();
            if (assemblyCodeParser.getCommandType() == 'A') {
                StringBuilder address = new StringBuilder ( );
                if (Character.isDigit(assemblyCodeParser.getSymbolInAssemblyCode().charAt(0))) {
                    address.append(decimalToBinary(Integer.parseInt(assemblyCodeParser.getSymbolInAssemblyCode())));
                } else {
                    if (!symbolTable.contains(assemblyCodeParser.getSymbolInAssemblyCode())) {
                        symbolTable.addSymbol(assemblyCodeParser.getSymbolInAssemblyCode(), ramLineNumber);
                        ++ramLineNumber;
                    }
                    address.append(decimalToBinary(symbolTable.getAddress(assemblyCodeParser.getSymbolInAssemblyCode())));
                }
                output.println(address.toString());
            } else if (assemblyCodeParser.getCommandType() == 'C') {
                String cInstruction = "111";
                cInstruction += cInstructionMapper.translateComputationMnemonicIntoBinary(assemblyCodeParser.getAssemblyComputationPortion());
                cInstruction += cInstructionMapper.translateDestinationMnemonicIntoBinary(assemblyCodeParser.getAssemblyDestinationPortion());
                cInstruction += cInstructionMapper.translateJumpMnemonicIntoBinary(assemblyCodeParser.getAssemblyJumpPortion());
                output.println (cInstruction);
            }
        }
    }
}