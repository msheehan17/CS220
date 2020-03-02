@16  // Move to register 16.
D=A  // Store the value of 16 in the D-register.
@R0  // Move to register 0.
M=D  // Store the value of 16 in register 0's memory.
@3   // Move to register 3.
D=A  // Store the value of 3 in the D-register.
@R1  // Move to register 1.
M=D  // Store the value of 3 in the register 1's memory.
@R0  // Move to register 0.
D=M  // Store the value of 16 in the D-register.

// Now the loop will perform division through iterative subtraction.

(LOOP)
@R1 // Move to register 3.

// Subtract 3 (the contents of register 1's memory register) from 16 (the value
// in the D-register).
D=D-M
@END // The end label where the jump will be made if the condition is met.

// The condition for the jump. If the difference becomes negative, the quotient
// is at max value.
D; JLT
@R2 // Move to register 2.

// The memory register of register two will store the quotient, and will
// increment by one each iteration that the difference is not negative.
M=M+1
@LOOP // The label for the loop to continue.

// This jump is unconditional, it will iterate until the difference becomes
// negative.
0;JMP

(END)
@END   // The label for the end of the loop.
0; JMP // Continue to cycle on end to prevent further input.
