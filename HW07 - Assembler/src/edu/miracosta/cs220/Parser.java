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
 * - rawLine (String): The un-cleaned/un-parsed line of text from the .asm file.
 * - cleanLine (String): The rawline, after being cleaned of unnecessary characters.
 * - symbol (String): The address/label without the '@' or '('/')'.
 * - destMnemonic (String): The destination portion of the C-instruction.
 * - jumpMnemonic (String): The jump portion of the C-instruction.
 * - compMnemonic (String): The computation portion of the C-instruction.
 * - commandType (char): The type of line entered by the .asm file (A-instruction, C-instruction, label, no command).
 * - lineNumber (int): The ROM line number.
 *
 * METHODS:
 *
 * - Parser (String): Connects the program to the assembly file, terminates the program if file not found.
 * - hasMoreCommands: Determines if the .asm file has more commands.
 * - advance: Takes in the next line from the .asm file, cleans it, and parses it. Closes file if no more commands.
 * - cleanLine: Cleans the rawline of whitespace, comments, tabs, etc.
 * - parseCommandType: Determines the command type.
 * - parse: Based on the command type, determines what parsing needs to be done.
 * - parseSymbol: Replaces the symbols in A-instructions/Labels.
 * - parseDest: Parses the destination portion of the C-instruction.
 * - parseComp: Parses the computation portion of the C-instruction.
 * - parseJump: Parses the jump portion of the C-instruction.
 * - Accessor methods for all instance variables.
 *
 * @author: Matt Sheehan
 */
class Parser {

    // Constants
    private static final char NO_COMMAND = 'N'; // No command.
    private static final char  A_COMMAND = 'A'; // A-Instruction.
    private static final char  C_COMMAND = 'C'; // C-Instruction.
    private static final char  L_COMMAND = 'L'; // Label.

    // Instance Variables.
    private Scanner inputFile;   // The Scanner object for connecting with the .asm file.
    private String rawLine;      // Assembly code before being cleared of comments, tabs, whitespace, etc.
    private String cleanLine;    // Cleaned line of assembly code.
    private String symbol;       // The symbol for determining if the line entered is a computation, destination, or jump.
    private String destMnemonic; // The destination portion of the C-instruction in need of parsing into binary.
    private String compMnemonic; // The computation portion of the C-instruction in need of parsing into binary.
    private String jumpMnemonic; // The jump portion of the C-instruction in need of parsing into binary.
    private char commandType;    // The type of command (A-Instruction, C-Instruction, Label, or no command).
    private int lineNumber = 1;  // The ROM line number (Begins with zero and as command come in increments).

    /**
     * Full constructor, connects the program with the .asm file.
     *
     * @param fileName The name of the .asm file.
     */
    Parser ( String fileName ) {
        try {
            inputFile = new Scanner ( new FileInputStream ( fileName ) );
        } catch ( FileNotFoundException e ) {
            System.err.println ( fileName + " not found. Terminating program." );
            System.exit ( 0 );
        }
    }

    /**
     * Determines if the file has more commands for parsing. If false, the file is closed.
     *
     * @return True if the file has more commands for parsing, false otherwise.
     */
    boolean hasMoreCommands ( ) {
        return inputFile.hasNextLine ( );
    }

    /**
     * Takes in the raw line of assembly, cleans it of comments and whitespaces, and parses the line depending on the
     * determined command (parses dest., comp., and jump if C-instruction, parses label/address without their symbol
     * if it is an a-instruction or label). Closes file if there are no more commands.
     */
    void advance ( ) {
        if ( hasMoreCommands ( ) ) {
            destMnemonic = compMnemonic = jumpMnemonic = symbol = null; // Clean from previous lines.
            lineNumber++;
            rawLine = inputFile.nextLine ( ); // Get next line.
            cleanLine ( );                    // Clean it up.
            parse ( );                        // Parse it.
        } else
            inputFile.close ( ); // No more commands.
    }

    /**
     * Takes the raw line and removes tabs, newlines, whitespace, and comments from it and stores the result in
     * cleanLine.
     */
    private void cleanLine ( ) {
        cleanLine = rawLine.trim ( ).replaceAll ( " ", "" );
        cleanLine = ( cleanLine.contains ( "//" ) ) ? cleanLine.substring ( 0, cleanLine.indexOf ( "//" ) ) : cleanLine;
    }

    /**
     * Determines the command type based on the String being empty (no command), or the first character signifying it
     * is an A-Instruction/Label.
     */
    private void parseCommandType ( ) {
        if ( cleanLine == null || cleanLine.isEmpty ( ) )
            commandType = NO_COMMAND;
        else if ( cleanLine.charAt ( 0 ) == '@' )
            commandType = A_COMMAND;
        else if ( cleanLine.charAt ( 0 ) == '(' )
            commandType = L_COMMAND;
        else
            commandType = C_COMMAND;
    }

    /**
     * Determines the command type and then parses the cleanLine based on said command type. So if the command type is
     * C, or C-Instruction, the cleanLine is parsed for destination, jump, and computation. If it is an A-Instruction
     * or a Label, the cleanLines will have their non-vital symbols removed (i.e. '@' and parenthesis).
     */
    private void parse ( ) {
        parseCommandType ( );
        if ( commandType == C_COMMAND ) {
            parseDest ( );
            parseComp ( );
            parseJump ( );
        } else if ( commandType == A_COMMAND || commandType == L_COMMAND ) {
            parseSymbol ( );
        }
    }

    /**
     * Removes portions of the String depending on the command type. If the command is an A-Instruction, the '@' is
     * removed from the cleanLine. If it is not an A-instruction (i.e. a Label), then the parenthesis are removed.
     */
    private void parseSymbol ( ) {
        symbol = cleanLine.replaceAll ( "[()@]", "" );
    }

    /**
     * Removes the non-destination portions of the cleanLine (i.e. those after the '=') and stores the remaining
     * String in the destination mnemonic.
     */
    private void parseDest ( ) {
        destMnemonic = cleanLine.contains ( "=" ) ? cleanLine.substring ( 0, cleanLine.indexOf ( "=" ) ) : "null";
    }

    /**
     * Removes the non-computation portions of the cleanLine (i.e. those after the ';', and before the '=') and stores
     * the remaining String in the computation mnemonic.
     */
    private void parseComp ( ) {
        compMnemonic = ( cleanLine.contains ( "=" ) ? cleanLine.substring ( cleanLine.indexOf ( "=" )  + 1 ) : cleanLine );
        compMnemonic = ( cleanLine.contains ( ";" ) ? cleanLine.substring ( 0, cleanLine.indexOf ( ";" )  ) : compMnemonic );
    }

    /**
     * Removes the non-jump portions of the cleanLine (i.e. those before the ';') and stores the remaining String in
     * the jump mnemonic.
     */
    private void parseJump ( ) {
        jumpMnemonic = cleanLine.contains ( ";" ) ? cleanLine.substring ( cleanLine.indexOf ( ";" ) + 1 ) : "null";
    }


    /* Accessor Methods */

    /**
     * Returns the parser object's command type.
     *
     * @return The parser object's command type.
     */
    char getCommandType ( ) {
        return commandType;
    }

    /**
     * Returns the parser object's symbol.
     *
     * @return The parser object's symbol.
     */
    String getSymbol ( ) {
        return symbol;
    }

    /**
     * Returns the parser object's destination mnemonic.
     *
     * @return The parser object's destination mnemonic.
     */
    String getDest ( ) {
        return destMnemonic;
    }

    /**
     * Returns the parser object's computation mnemonic.
     *
     * @return The parser object's computation mnemonic.
     */
    String getComp ( ) {
        return compMnemonic;
    }

    /**
     * Returns the parser object's jump mnemonic.
     *
     * @return The parser object's jump mnemonic.
     */
    String getJump ( ) {
        return jumpMnemonic;
    }

    /**
     * Returns the parser object's raw line.
     *
     * @return The parser object's raw line.
     */
    String getRawLine ( ) {
        return rawLine;
    }

    /**
     * Returns the parser object's clean line.
     *
     * @return The parser object's clean line.
     */
    String getCleanLine ( ) {
        return cleanLine;
    }

    /**
     * Returns the parser object's line number.
     *
     * @return The parser object's line number.
     */
    int getLineNumber ( ) {
        return lineNumber;
    }
}