package edu.miracosta.cs220;

import java.util.HashMap;
import java.util.Map;
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
 * @author Matt Sheehan
 */
class SymbolTable {

    private Map < String, Integer > symbolTable = new HashMap < > (  );

    private static String [ ] reservedSymbols = { "SP", "LCL", "ARG", "THIS", "THAT", "SCREEN", "KBD" };

    private static int [ ] reservedSymbolRegisters = { 0, 1, 2, 3, 4, 16384, 24576 };

    /**
     * Full constructor, initializes the symbol table hash-map with all pre-defined symbols.
     */
    SymbolTable ( ) {
        for ( int i = 0; i < 16; i++ )
            symbolTable.put ( ( "R" + i ), i );

        for ( int i = 0; i < reservedSymbols.length; i++ )
            symbolTable.put ( reservedSymbols [ i ], reservedSymbolRegisters [ i ] );
    }

    /**
     * Adds a symbol/address pair to the symbol table hash-map (if not already defined, and symbol has valid name).
     *
     * @param symbol The symbol to be added.
     * @param address The register number.
     *
     * @return True if the symbol/address pair was added, false if not.
     */
    boolean addEntry ( String symbol, int address ) {
        Integer v; // If symbol added to hash-map, returns null.
        if ( isValidName ( symbol ) ) {
            v = symbolTable.putIfAbsent ( symbol, address );
            return ( v == null );
        } else
            return false; // Invalid name.
    }

    /**
     * Determines if the symbol exists in the symbol table hash-map.
     *
     * @param symbol The symbol to be checked.
     *
     * @return True if the symbol is contained within the hash-map, false if not.
     */
    boolean contains ( String symbol ) {
        return symbolTable.containsKey ( symbol );
    }

    /**
     * Returns the address linked with the symbol argument.
     *
     * @param symbol The symbol who's address is to be returned.
     *
     * @return The address of the symbl argument.
     */
    int getAddress ( String symbol ) {
        return symbolTable.getOrDefault ( symbol, null );
    }

    /**
     * Determines if the symbol has a valid name. The initial character can be any letter (upper or lowercase), and the
     * symbols ( . _ $ ). All following characters follow the same rules as the initial character, but can additionally
     * be any number 0-9.
     *
     * @param symbol The symbol to be checked for name validity.
     *
     * @return True if the name is valid, false otherwise.
     */
    private boolean isValidName ( String symbol ) {
        return Pattern.compile ( "[a-zA-Z_.$][a-zA-Z0-9_.$]*" ).matcher ( symbol ).matches ( );
    }
}
