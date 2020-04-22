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
 * @author: Matt Sheehan
 */
public class Assembler {

    public static void main ( String [ ] args ) {
        runAssembler ( );
    }

    /**
     * Wrapper method for running the assembler, translating the assembly code into binary.
     */
    private static void runAssembler ( ) {
        SymbolTable st = new SymbolTable ( ); // Manage symbols.
        Scanner     sc = new Scanner ( System.in ); // Accept user input.

        System.out.print ( "Please enter the file name: " );

        String inputFileName  = sc.nextLine ( );

        String outputFileName =  inputFileName.replace ( "asm", "hack" ); // Make output file (.hack ext.)

        PrintWriter output = null;
        try {
            output = new PrintWriter ( new FileOutputStream ( outputFileName ) );
        } catch ( FileNotFoundException e ) {
            System.err.println ( "Could not write to output file: " + outputFileName );
        }

        firstPass  ( inputFileName, st );
        secondPass ( inputFileName, st, output );

        output.close ( );
    }

    /**
     * Coverts a decimal number to binary (String version).
     *
     * @param decimal The number to be converted.
     *
     * @return The binary representation of the decimal number.
     */
    private static String decimalToBinary ( int decimal ) {
        StringBuilder binaryString =  new StringBuilder ( Integer.toBinaryString ( decimal ) );
        return "0".repeat ( 16 - binaryString.length ( ) ) + binaryString.toString ( );
    }

    /**
     * Initial pass through the .asm file, determines the need for creating variables/labels within the symbol table.
     *
     * @param inputFileName The .asm file name.
     * @param symbolTable The table which will store the newly added variables/labels within the .asm file.
     */
    private static void firstPass ( String inputFileName, SymbolTable symbolTable ) {
        Parser parser = new Parser ( inputFileName );
        int rom = 0;

        while ( parser.hasMoreCommands ( ) ) {
            parser.advance ( );
            rom = ( parser.getCommandType ( ) == 'A' || parser.getCommandType ( ) == 'C' ) ? ++rom : rom;
            if ( parser.getCommandType ( ) == 'L' )
                symbolTable.addEntry ( parser.getSymbol ( ), rom );
        }
    }

    /**
     * Translates the assembly code into its binary representation and prints it to the output file.
     *
     * @param assemblyFileName The assembly input file.
     * @param symbolTable The symbol table for generating the address of the symbol given.
     * @param output The hack output file.
     */
    private static void secondPass ( String assemblyFileName, SymbolTable symbolTable, PrintWriter output ) {
        Parser parser = new Parser ( assemblyFileName );
        CInstructionMapper cHelp = new CInstructionMapper ( );
        int ram = 16;

        while ( parser.hasMoreCommands ( ) ) {
            parser.advance ( );
            if ( parser.getCommandType ( ) == 'A' ) {
                StringBuilder a = new StringBuilder ( );
                // Address
                if ( Character.isDigit ( parser.getSymbol ( ).charAt ( 0 ) ) ) {
                    a.append ( decimalToBinary ( Integer.parseInt ( parser.getSymbol ( ) ) ) );
                    output.println ( a.toString ( ) );
                // Label/Variable
                } else {
                    if ( ! symbolTable.contains ( parser.getSymbol ( ) ) ) {
                        symbolTable.addEntry ( parser.getSymbol ( ), ram );
                        ++ram;
                    }

                    a.append ( decimalToBinary ( symbolTable.getAddress ( parser.getSymbol ( ) ) ) );
                    output.println ( a.toString ( ) );
                }
            } else if ( parser.getCommandType ( ) == 'C' ) {
                String c =  ( "111" + cHelp.comp ( parser.getComp ( ) )
                                    + cHelp.dest ( parser.getDest ( ) )
                                    + cHelp.jump ( parser.getJump ( ) ) );
                output.println ( c );
            }
        }
    }
}