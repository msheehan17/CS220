package edu.miracosta.cs220;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Parser.java - Takes in a line of text from assembly files and deconstructs them for their instructions.
 *
 * INSTANCE VARIABLES:
 *
 * - Constants for type of command (no command, A/C-Instruction, Label).
 * - inputFile (Scanner): The object that connects the .asm file to the program.
 * - rawLAssembly (String): The un-cleaned/un-parsed line of text from the .asm file.
 * - assemblyCleanedFromWhiteSpace (String): The raw assembly code, after being cleaned of unnecessary characters.
 * - symbolInAssemblyCode (String): The address/label without the '@' or '('/')'.
 * - assemblyDestinationPortion (String): The destination portion of the C-instruction.
 * - assemblyComputationPortion (String): The computation portion of the C-instruction.
 * - assemblyJumpPortion (String): The jump portion of the C-instruction.
 * - commandType (char): The type of line entered by the .asm file (A-instruction, C-instruction, label, no command).
 * - fileLineNumber (int): The ROM line number.
 *
 * METHODS:
 *
 * - Parser (String): Connects the program to the assembly file, terminates the program if file not found.
 * - hasMoreCommands: Determines if the .asm file has more commands.
 * - advance: Takes in the next line from the .asm file, cleans it, and parses it. Closes file if no more commands.
 * - cleanAssemblyCodeOfWhiteSpace: Cleans the raw assembly code of whitespace, comments, tabs, etc.
 * - parseCommandType: Determines the command type.
 * - parseSymbol: Replaces the symbols in A-instructions/Labels.
 * - parseAssembly: Based on the command type, determines what parsing needs to be done.
 * - parseAssemblyDestinationPortion: Parses the destination portion of the C-instruction.
 * - parseAssemblyComputationPortion: Parses the computation portion of the C-instruction.
 * - parseAssemblyJumpPortion: Parses the jump portion of the C-instruction.
 * - Accessor methods for all instance variables.
 *
 * Author: Matt Sheehan
 */
class Parser {

    private static final char NO_COMMAND = 'N';
    private static final char ADDRESS = 'A';
    private static final char C_INSTRUCTION = 'C';
    private static final char LABEL = 'L';

    private Scanner inputFile;
    private String rawAssembly;
    private String assemblyWithoutWhiteSpace;
    private String symbolInAssemblyCode;
    private String assemblyDestinationPortion;
    private String assemblyComputationPortion;
    private String assemblyJumpPortion;
    private char commandType;
    private int fileLineNumber = 1;

    /**
     * Full constructor, connects the program with the .asm file.
     * @param fileName The name of the .asm file.
     */
    Parser(String fileName) {
        try {
            inputFile = new Scanner(new FileInputStream(fileName)) ;
        } catch(FileNotFoundException e) {
            System.err.println (fileName + " not found. Terminating program.");
            System.exit(0);
        }
    }

    /**
     * Determines if the file has more commands for parsing. If false, the file is closed.
     *
     * @return True if the file has more commands for parsing, false otherwise.
     */
    boolean hasMoreCommands() {
        return inputFile.hasNextLine();
    }

    /**
     * Takes in the raw line of assembly, cleans it of comments and whitespaces, and parses the line depending on the
     * determined command (parses dest., comp., and jump if C-instruction, parses label/address without their symbol
     * if it is an a-instruction or label). Closes file if there are no more commands.
     */
    void advance() {
        if (hasMoreCommands()) {
            assemblyDestinationPortion = null;
            assemblyComputationPortion = null;
            assemblyJumpPortion = null;
            symbolInAssemblyCode = null;
            fileLineNumber++;
            rawAssembly = inputFile.nextLine();
            cleanAssemblyCodeOfWhiteSpace();
            parseAssembly();
        } else {
            inputFile.close ( );
        }
    }

    /**
     * Takes the raw line and removes tabs, newlines, whitespace, and comments from it and stores the result in
     * cleanLine.
     */
    private void cleanAssemblyCodeOfWhiteSpace() {
        assemblyWithoutWhiteSpace = rawAssembly.trim();
        assemblyWithoutWhiteSpace = assemblyWithoutWhiteSpace.replaceAll(" ", "");
        assemblyWithoutWhiteSpace = assemblyWithoutWhiteSpace.replaceAll("//.*", "");
    }

    /**
     * Determines the command type based on the String being empty (no command), or the first character signifying it
     * is an A-Instruction/Label.
     */
    private void parseCommandType() {
        if (assemblyWithoutWhiteSpace == null || assemblyWithoutWhiteSpace.isEmpty()) {
            commandType = NO_COMMAND;
        } else if (assemblyWithoutWhiteSpace.charAt(0) == '@') {
            commandType = ADDRESS;
        } else if (assemblyWithoutWhiteSpace.charAt(0) == '(') {
            commandType = LABEL;
        } else {
            commandType = C_INSTRUCTION;
        }
    }

    /**
     * Removes portions of the String depending on the command type. If the command is an A-Instruction, the '@' is
     * removed from the cleanLine. If it is not an A-instruction (i.e. a Label), then the parenthesis are removed.
     */
    private void parseSymbol() {
        symbolInAssemblyCode = assemblyWithoutWhiteSpace.replaceAll("[()@]", "");
    }

    /**
     * Determines the command type and then parses the cleanLine based on said command type. So if the command type is
     * C, or C-Instruction, the cleanLine is parsed for destination, jump, and computation. If it is an A-Instruction
     * or a Label, the cleanLines will have their non-vital symbols removed (i.e. '@' and parenthesis).
     */
    private void parseAssembly() {
        parseCommandType();
        if (commandType == C_INSTRUCTION) {
            parseDestinationPortion();
            parseComputationPortion();
            parseJumpPortion();
        } else if (commandType == ADDRESS || commandType == LABEL) {
            parseSymbol();
        }
    }

    /**
     * Removes the non-destination portions of the cleanLine (i.e. those after the '=') and stores the remaining
     * String in the destination mnemonic.
     */
    private void parseDestinationPortion() {
        if (assemblyWithoutWhiteSpace.contains("=")) {
            assemblyDestinationPortion = assemblyWithoutWhiteSpace.replaceAll("=.+", "");
        } else {
            assemblyDestinationPortion = "null";
        }
    }

    /**
     * Removes the non-jump portions of the cleanLine (i.e. those before the ';') and stores the remaining String in
     * the jump mnemonic.
     */
    private void parseJumpPortion() {
        if (assemblyWithoutWhiteSpace.contains(";")){
            assemblyJumpPortion = assemblyWithoutWhiteSpace.replaceAll(".+;", "");
        } else {
            assemblyJumpPortion = "null";
        }
    }

    /**
     * Removes the non-computation portions of the cleanLine (i.e. those after the ';', and before the '=') and stores
     * the remaining String in the computation mnemonic.
     */
    private void parseComputationPortion() {
        assemblyComputationPortion = assemblyWithoutWhiteSpace;
        assemblyComputationPortion = assemblyComputationPortion.replaceAll(".+;", "");
        assemblyComputationPortion = assemblyComputationPortion.replaceAll("=.+", "");
    }

    /**
     * Returns the parser object's command type.
     *
     * @return The parser object's command type.
     */
    char getCommandType() {
        return commandType;
    }

    /**
     * Returns the parser object's symbol.
     * @return The parser object's symbol.
     */
    String getSymbolInAssemblyCode() {
        return symbolInAssemblyCode;
    }

    /**
     * Returns the parser object's destination mnemonic.
     * @return The parser object's destination mnemonic.
     */
    String getAssemblyDestinationPortion() {
        return assemblyDestinationPortion;
    }

    /**
     * Returns the parser object's jump mnemonic.
     * @return The parser object's jump mnemonic.
     */
    String getAssemblyJumpPortion() {
        return assemblyJumpPortion;
    }

    /**
     * Returns the parser object's computation mnemonic.
     * @return The parser object's computation mnemonic.
     */
    String getAssemblyComputationPortion() {
        return assemblyComputationPortion;
    }

    /**
     * Returns the parser object's raw line.
     * @return The parser object's raw line.
     */
    String getRawAssembly() {
        return rawAssembly;
    }

    /**
     * Returns the parser object's clean line.
     * @return The parser object's clean line.
     */
    String getAssemblyWithoutWhiteSpace() {
        return assemblyWithoutWhiteSpace;
    }

    /**
     * Returns the parser object's line number.
     * @return The parser object's line number.
     */
    int getFileLineNumber() {
        return fileLineNumber;
    }
}