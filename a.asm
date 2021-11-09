.model small 
.stack 
.data
    v1 db 10,13,"Hola Mundo",36
    v2 db 10,13,"Hello World" + '$'
    v2 db 0
.code
    mov ax, @data
    mov ds,ax
    
    ;imprimir mensaje 
    mov ah, 5
    lea dx,v1
    int 21h
    ;devolver control al sistema 
    mov ah,4ch
    int 21h
    int 21h
end
;

;


