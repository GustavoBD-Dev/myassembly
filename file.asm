.model small
.stack
.data
    v1 db 0,36


.code
    0
    mov dl, 'char'
    mov ah, ax
    int 21h
end

