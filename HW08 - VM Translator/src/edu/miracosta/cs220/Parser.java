package edu.miracosta.cs220;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * INSTANCE VARIABLES:
 * - inputFile (Scanner): The Scanner object responsible for connecting the .vm file with the program.
 * - commandType (String): The type of command (arithmetic or push/pop) given by the VM code.
 * - argOne (String): The memory segment the VM command (constant, static, local, etc).
 * - argTwo (String): The index of the memory segment to pop from/push onto.
 *
 * METHODS:
 * - Full constructor: Connects the .vm file with the program, exits the program if connection couldn't be made.
 * - hasMoreCommands: Determines if the file has more commands to be parsed.
 * - advance: Takes in a line of VM code, cleans it, and determines the VM command type.
 * - Accessor methods for all instance variables (except inputFile).
 *
 * @author Matt Sheehan
 */
class Parser {

    private Scanner inputFile;   // The file the class will be parsing.
    private String commandType;  // The command (push, pop, call, return, etc.)
    private String memorySegment; // The memory segment for non-arithmetic commands (or the arithmetic command alone)
    private int memoryIndex;     // The index of the memory segment (N/A for arithmetic commands).

    /**
     * Full constructor, connects the file with the class.
     */
    Parser ( String fileName ) {
        try {
            inputFile = new Scanner ( new FileInputStream ( fileName ) );
        } catch ( FileNotFoundException e ) {
            System.out.println ( fileName + " file not found; terminating program." );
            System.exit ( 0 );
        }
    }

    /**
     * Returns the state of the file having more commands.
     * @return True if the file has more commands for parsing, false if not.
     */
    boolean hasMoreCommands ( ) {
        return inputFile.hasNext ( );
    }

    /**
     * Captures the next line of VM code, cleans it of comments, the separates the code into a command, and potentially
     * a memory segment and index.
     */
    void advance ( ) {
        if ( hasMoreCommands ( ) ) {
            commandType = memorySegment = null; // Clear out for next command.
            memoryIndex = 0;

            String vmLine = inputFile.nextLine ( );
            vmLine = vmLine.contains ( "//" ) ? vmLine.substring ( 0, vmLine.indexOf ( "//" ) ) : vmLine; // Clean comments.

            if ( vmLine.isEmpty ( ) ) {
                commandType = "Comment";
                return;
            }

            String [ ] segments  = vmLine.split ( " " ); // Split VM code.

            switch ( segments [ 0 ] ) {
                case "add", "sub", "neg", "gt", "lt", "eq", "and", "or", "not", "return" ->
                    commandType = memorySegment = segments[ 0 ];
                default -> {
                    commandType   = segments [ 0 ];
                    memorySegment = segments [ 1 ];
                    if ( segments [ 0 ].matches ( "(?i)push|pop|call|function" ) )
                        memoryIndex = Integer.parseInt ( segments [ 2 ] );
                }
            }
        } else
            inputFile.close ( );
    }

    /**
     * Accessor for command type.
     * @return The command type.
     */
    String getCommandType ( ) {
        return commandType;
    }

    /**
     * Accessor for memory segment of the command, or arithmetic command.
     * @return The first portion of the command, either memory segment or arithmetic (null if command is return.
     */
    String getMemorySegment ( ) {
        return commandType.equals ( "return" )  ? null : memorySegment;
    }

    /**
     * Accessor for the index of the memory segment the VM command is working with.
     * @return The index of the memory command, or null if the command type isn't push, pop, call, or function.
     */
    int getMemoryIndex ( ) {
        return commandType.matches ( "(?i)push|pop|call|function" )  ? memoryIndex : -1;
    }
}
