@SCREEN
D=A
@i
M=D
(LOOP)
@24576
D=A
@i
D=M-D
@ENDLOOP
D;JGT

@i
A=M+1
M=-1

@i
M=M+1
@LOOP
0;JMP

(ENDLOOP)
@ENDLOOP
0;JMP
