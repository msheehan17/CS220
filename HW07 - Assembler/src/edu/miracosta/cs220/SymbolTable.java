package edu.miracosta.cs220;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SymbolTable.java - The class for managing symbols and their respective address.
 *
 * INSTANCE VARIABLES:
 *
 * - symbolTable (hash-map): Stores the predefined symbols (and their addresses), as well as any added symbols.
 * - reservedSymbols (String[]): An array holding predefined symbols to be loaded into the symbolTable hash-map.
 * - reservedSymbolRegisters (int[]): An array holding the register locations of predefined symbols to be loaded
 *   into the symbolTable hash-map.
 *
 * METHODS:
 *
 * - Full constructor: Initializes the predefined symbols/registers into the symbolTable hash-map.
 * - addEntry: Adds entry if not already defined and returns true, false otherwise (symbol exists/has invalid name).
 * - contains: Returns true if the symbol is defined within the symbolTable hash-map, false otherwise.
 * - getAddress: Returns the address corresponding to the symbol argument given.
 * - isValidName: Returns true if the symbol argument has a valid name, false otherwise.
 *
 * Author: Matt Sheehan
 */
class SymbolTable {

    private static final Map <String, Integer> symbolTable = new HashMap <> ();
    private static final String [] reservedSymbols = { "SP", "LCL", "ARG", "THIS", "THAT", "SCREEN", "KBD" };
    private static final int [] reservedSymbolRegisters = { 0, 1, 2, 3, 4, 16384, 24576 };

    /**
     * Full constructor, initializes the symbol table hash-map with all pre-defined symbols.
     */
    SymbolTable () {
        for (int i = 0; i < 16; i++)
            symbolTable.put (("R" + i), i);

        for (int i = 0; i < reservedSymbols.length; i++)
            symbolTable.put (reservedSymbols [i], reservedSymbolRegisters [i]);
    }

    /**
     * Adds a symbol/address pair to the symbol table hash-map (if not already defined, and symbol has valid name).
     * @param symbolToBeAdded The symbol to be added.
     * @param indexToAddTheSymbolAt The register number.
     */
    void addSymbol(String symbolToBeAdded, int indexToAddTheSymbolAt) {
        if (isValidSymbolName(symbolToBeAdded)) {
            symbolTable.put(symbolToBeAdded, indexToAddTheSymbolAt);
        }
    }

    /**
     * Determines if the symbol argument is located in the symbol table.
     * @param symbolToBeChecked The symbol to be checked for existence.
     * @return True if the symbol is in the symbol table, false otherwise.
     */
    boolean contains(String symbolToBeChecked) {
        return symbolTable.containsKey(symbolToBeChecked);
    }

    /**
     * Returns the address of valid symbol arguments.
     * @param symbolToBeCheckedForAddress The symbol to be checked for an address.
     * @return Returns the symbol argument's address if it is in the symbol table, null otherwise.
     */
    int getAddress(String symbolToBeCheckedForAddress) {
        return symbolTable.getOrDefault(symbolToBeCheckedForAddress, null);
    }

    /**
     * Determines if the symbol argument has a valid name. Valid names will begin with the choice of lower/uppercase
     * letters, period, underscore, or dollar sign. Subsequent characters follow the same rule with the addition of
     * numbers.
     * @param symbolToBeCheckedForValidName The symbol to be checked for name validity.
     * @return True if the symbol has a valid name, false otherwise.
     */
    private static boolean isValidSymbolName(String symbolToBeCheckedForValidName) {
        Pattern validNamePattern = Pattern.compile("[a-zA-Z_.$][a-zA-Z0-9_.$]*");
        Matcher validNameChecker = validNamePattern.matcher(symbolToBeCheckedForValidName);
        return validNameChecker.matches();
    }
}
