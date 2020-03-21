package edu.miracosta.cs220;

import java.util.HashMap;
import java.util.Map;

/**
 * CInstructionMapper.java - Translates portions of CInstructions into binary.
 *
 * INSTANCE VARIABLES:
 *
 * - compCodes (Hash-Map): Stores options for the computation portion of C-Instructions and their binary equivalent.
 * - destCodes (Hash-Map): Stores options for the destination portion of C-Instructions and their binary equivalent.
 * - jumpCodes (Hash-Map): Stores options for the jump portion of C-Instructions and their binary equivalent.
 * - computationMnemonics (String[]): Array for holding computation mnemonics to be loaded into the computation Hash-Map.
 * - destinationMnemonics (String[]): Array for holding destination mnemonics to be loaded into the destination Hash-Map.
 * - jumpMnemonics (String[]): Array for holding jump mnemonics to be loaded into the jump Hash-Map.
 * - computationBinary (String[]): Array for holding computation (binary-from) to be loaded into the computation Hash-Map.
 * - destinationJumpBinary (String[]): Array for holding destination/jump (binary-from) to be loaded into the destination/
 *   jump Hash-Map.
 *
 * METHODS:
 *
 * - CInstructionMapper: Full constructor. Declares the Hash-Maps and initializes their respective values.
 * - comp (String): Looks up the String mnemonic in the compCodes Hash-Map to find the mnemonic's binary representation.
 * - dest (String): Looks up the String mnemonic in the destCodes Hash-Map to find the mnemonic's binary representation.
 * - jump (String): Looks up the String mnemonic in the jumpCodes Hash-Map to find the mnemonic's binary representation.
 *
 * @author: Matt Sheehan
 */
class CInstructionMapper {

    private static String [ ] computationMnemonics = {
    "0",   "1", "-1",  "D",   "A",  "!D",  "!A",  "-D",  "-A", "D+1", "A+1", "D-1", "A-1", "D+A", "D-A", "A-D", "D&A",
    "D|A", "M", "!M", "-M", "M+1", "M-1", "D+M", "D-M", "M-D", "D&M", "D|M"
    };

    private static String [ ] computationBinary = {
    "0101010", "0111111", "0111010", "0001100", "0110000", "0001101", "0110001", "0001111", "0110011", "0011111",
    "0110111", "0001110", "0110010", "0000010", "0010011", "0000111", "0000000", "0010101", "1110000", "1110001",
    "1110011", "1110111", "1110010", "1000010", "1010011", "1000111", "1000000", "1010101"
    };

    private static String [ ] destinationMnemonics = { "null", "M", "D", "MD", "A", "AM", "AD", "AMD" };

    private static String [ ] jumpMnemonics = { "null", "JGT", "JEQ", "JGE", "JLT", "JNE", "JLE", "JMP" };

    private static String [ ] destinationJumpBinary = { "000", "001", "010", "011", "100", "101", "110", "111" };

    private Map < String, String > compCodes = new HashMap < > (  ); // Computation mnemonic/binary pairs.
    private Map < String, String > destCodes = new HashMap < > (  ); // Destination mnemonic/binary pairs.
    private Map < String, String > jumpCodes = new HashMap < > (  ); // Jump mnemonic/binary pairs.

    /**
     * Full constructor; initializes all Hash-Maps with their respective values.
     */
    CInstructionMapper ( ) {
        // Initialize the Computation, Destination, and Jump Hash-Map.
        for ( int i = 0; i < computationMnemonics.length; i ++)
            compCodes.put ( computationMnemonics [ i ], computationBinary [ i ] );

        for ( int i = 0; i < destinationMnemonics.length; i ++) {
            destCodes.put ( destinationMnemonics [ i ], destinationJumpBinary [ i ] );
            jumpCodes.put ( jumpMnemonics [ i ],        destinationJumpBinary [ i ] );
        }
    }

    /**
     * Translates the computation portion of the C-instruction into binary.
     *
     * @param mnemonic The computation portion of the C-instruction to be translated into binary.
     *
     * @return The computation portion of the C-instruction in its binary form (or null if mnemonic doesn't exist).
     */
    String comp ( String mnemonic ) {
        return compCodes.get ( mnemonic );
    }

    /**
     * Translates the destination portion of the C-instruction into binary.
     *
     * @param mnemonic The destination portion of the C-instruction to be translated into binary.
     *
     * @return The destination portion of the C-instruction in its binary form (or null if mnemonic doesn't exist).
     */
    String dest ( String mnemonic ) {
        return destCodes.get ( mnemonic );
    }

    /**
     * Translates the jump portion of the C-instruction into binary.
     *
     * @param mnemonic The jump portion of the C-instruction to be translated into binary.
     *
     * @return The jump portion of the C-instruction in its binary form (or null if mnemonic doesn't exist).
     */
    String jump ( String mnemonic ) {
        return jumpCodes.get ( mnemonic );
    }
}