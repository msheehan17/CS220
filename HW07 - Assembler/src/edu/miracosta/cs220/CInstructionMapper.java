package edu.miracosta.cs220;

import java.util.HashMap;
import java.util.Map;

/**
 * CInstructionMapper.java - Translates portions of CInstructions into binary.
 *
 * INSTANCE VARIABLES:
 *
 * - computationMnemonics (String[]): Array for holding computation mnemonics to be loaded into the computation
 *   Hash-Map.
 * - computationMnemonicsBinary (String[]): Array for holding computation (binary-from) to be loaded into the
 *   computation Hash-Map.
 * - destinationMnemonics (String[]): Array for holding destination mnemonics to be loaded into the destination
 *   Hash-Map.
 * - jumpMnemonics (String[]): Array for holding jump mnemonics to be loaded into the jump Hash-Map.
 * - destinationJumpMnemonicsBinary (String[]): Array for holding destination/jump (binary-from) to be loaded into the
 *   destination.
 * - computationMnemonicsAndBinary (Hash-Map): Stores the computation portion mnemonic of C-Instructions and their
 *   binary equivalent.
 * - destinationMnemonicsAndBinary (Hash-Map): Stores the destination portion mnemonic of C-Instructions and their
 *   binary equivalent.
 * - jumpMnemonicsAndBinary (Hash-Map): Stores the jump portion mnemonic of C-Instructions and their binary equivalent.
 *
 * METHODS:
 *
 * - CInstructionMapper: Full constructor. Declares the Hash-Maps and initializes their respective values.
 * - translateComputationMnemonicIntoBinary (String): Looks up the String mnemonic in the compCodes Hash-Map to find
 *   the mnemonic's binary representation.
 * - translateDestinationMnemonicIntoBinary (String): Looks up the String mnemonic in the destCodes Hash-Map to find
 *   the mnemonic's binary representation.
 * - translateJumpMnemonicIntoBinary (String): Looks up the String mnemonic in the jumpCodes Hash-Map to find the
 *   mnemonic's binary representation.
 *
 * Author: Matt Sheehan
 */
class CInstructionMapper {

    private static final String [] computationMnemonics = {
    "0",   "1", "-1",  "D",   "A",  "!D",  "!A",  "-D",  "-A", "D+1", "A+1", "D-1", "A-1", "D+A", "D-A", "A-D", "D&A",
    "D|A", "M", "!M", "-M", "M+1", "M-1", "D+M", "D-M", "M-D", "D&M", "D|M"
    };

    private static final String [] computationMnemonicsBinary = {
    "0101010", "0111111", "0111010", "0001100", "0110000", "0001101", "0110001", "0001111", "0110011", "0011111",
    "0110111", "0001110", "0110010", "0000010", "0010011", "0000111", "0000000", "0010101", "1110000", "1110001",
    "1110011", "1110111", "1110010", "1000010", "1010011", "1000111", "1000000", "1010101"
    };

    private static final String [] destinationMnemonics = { "null", "M", "D", "MD", "A", "AM", "AD", "AMD" };
    private static final String [] jumpMnemonics = { "null", "JGT", "JEQ", "JGE", "JLT", "JNE", "JLE", "JMP" };
    private static final String [] destinationJumpMnemonicsBinary = { "000", "001", "010", "011", "100", "101", "110", "111" };

    private static final Map <String, String> destinationMnemonicsAndBinary = new HashMap <> ();
    private static final Map <String, String> computationMnemonicsAndBinary = new HashMap <> ();
    private static final Map <String, String> jumpMnemonicsAndBinary = new HashMap <> ();

    /**
     * Full constructor; initializes all Hash-Maps with their respective values.
     */
    CInstructionMapper ( ) {
        for (int i = 0; i < computationMnemonics.length; i ++) {
            computationMnemonicsAndBinary.put(computationMnemonics[i], computationMnemonicsBinary[i]);
        }

        for (int i = 0; i < destinationMnemonics.length; i ++) {
            destinationMnemonicsAndBinary.put(destinationMnemonics[i], destinationJumpMnemonicsBinary[i]);
            jumpMnemonicsAndBinary.put(jumpMnemonics[i], destinationJumpMnemonicsBinary[i]);
        }
    }

    /**
     * Translates the computation portion of the C-instruction into binary.
     * @param computationMnemonic The computation portion of the C-instruction to be translated into binary.
     * @return The computation portion of the C-instruction in its binary form (or null if mnemonic doesn't exist).
     */
    String translateComputationMnemonicIntoBinary(String computationMnemonic) {
        return computationMnemonicsAndBinary.get(computationMnemonic);
    }

    /**
     * Translates the destination portion of the C-instruction into binary.
     * @param destinationMnemonic The destination portion of the C-instruction to be translated into binary.
     * @return The destination portion of the C-instruction in its binary form (or null if mnemonic doesn't exist).
     */
    String translateDestinationMnemonicIntoBinary(String destinationMnemonic) {
        return destinationMnemonicsAndBinary.get(destinationMnemonic);
    }

    /**
     * Translates the jump portion of the C-instruction into binary.
     * @param jumpMnemonic The jump portion of the C-instruction to be translated into binary.
     * @return The jump portion of the C-instruction in its binary form (or null if mnemonic doesn't exist).
     */
    String translateJumpMnemonicIntoBinary(String jumpMnemonic) {
        return jumpMnemonicsAndBinary.get(jumpMnemonic);
    }
}