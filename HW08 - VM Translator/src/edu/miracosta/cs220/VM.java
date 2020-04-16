package edu.miracosta.cs220;

/**
 * Algorithm:
 *
 * 1. The program will accept input from the command line of all files to trasnlate from VM to ASM code.
 *
 * 2. A Parser and CodeWriter object will be used for this translation process. The Parser will read in a line of
 * VM code, clean it off comments, and determine the type of VM command being given (add, sub, push, pop, etc.)
 *
 * 3. Whether the command given is an arithmetic command (add, sub, neg, gt, lt, eq, and, or, not), or a push/pop
 * command, the CodeWriter will write specific ASM code that will carry out the VM's manipulation of registers, in
 * the form of ASM. Note: Lines that are solely comments will be skipped over.
 *
 * 4. Once the program has moved through all lines in the VM file, the contents will be placed in a file of the same
 * name with a .asm extension.
 *
 * @author Matt Sheehan
 */
public class VM {
    public static void main ( String ... files ) {
        for ( String file : files ) {
            Parser parse  = new Parser ( file );
            CodeWriter cw = new CodeWriter ( file.replaceAll ( "vm", "asm" ) );

            while ( parse.hasMoreCommands ( ) ) {
                parse.advance ( );
                if ( parse.getCommandType ( ).matches ( "add|sub|neg|gt|lt|eq|and|or|not" ) )
                    cw.writeArithmetic ( parse.getCommandType ( ) );
                else if ( parse.getCommandType ( ).matches ( "push|pop" ) )
                    cw.writePushPop ( parse.getCommandType ( ), parse.getMemorySegment ( ), parse.getMemoryIndex ( ) );
                else if ( parse.getCommandType ( ).equals ( "Comment" ) ) { } // Do nothing.
                }
            cw.close ( ); // Move contents from buffer.
       }
    } // End main.
}
