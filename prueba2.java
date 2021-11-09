
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class prueba2 {
    // lista de Tokens
    public static ArrayList<String> listaTokens = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException, IOException {

        int contador4 = 0;
        PrintStream Escribir = new PrintStream("Codigo.obj");
        if (args.length > 1) { // si hay más de 1 parámetro
            System.out.println("Hay demasiados parámetros. Debe escribir: crearASM \"nombre\".asm");
        } else if (args.length == 0) { // si no hay parámetros
            System.out.println("Falta el nombre del archivo");
        } else {// si la entrada es correcta
            File archivo = new File(args[0]);
            if (archivo.exists()) { // si el archivo existe
                System.out.println("\tARCHIVO ENCONTRADO\n");
                System.out.println("nombre: " + args[0] + "\nTamaño: " + archivo.length() + " bytes");
                // Si hay contenido en el archivo
                if (archivo.length() > 0) {
                    try {
                        // se cuentan el numero de filas con texto del archivo
                        Scanner iterador = new Scanner(archivo);
                        int lineas = 0;
                        while (iterador.hasNextLine()) {
                            String linea = iterador.nextLine();
                            if (!linea.trim().isEmpty()) {
                                lineas++;
                            }
                        }
                        System.out.println("Numero de lineas del archivo: " + lineas);
                        // TABLA DE TOKENS-------------------------------------
                        System.out.println("\n\tCREANDO TABLA DE TOKENS\n");
                        String[][] tablaToken = { // Simbolo Nombre Tamaño Direccion
                                { ".", "Directiva", "1 byte", "1001 1001" },
                                { "model", "Directiva", "1 byte", "1111 1001" },
                                { "small", "Directiva", "1 byte", "1011 1001" },
                                { "stack", "Directiva", "2 byte", "1001 1101" },
                                { "data", "Directiva", "4 byte", "1001 1111" },
                                { "code", "Directiva", "3 byte", "0001 0001" },
                                { "v1", "Directiva", "1 byte", "0001 1111" },
                                { "mov", "Instruccion", "3 bytes", "1111 0000" },
                                { ";", "Comentario", "-------", "---------" },
                                { "lea", "Instruccion", "2 bytes", "0011 1100" },
                                { "int", "Instruccion", "2 bytes", "0101 1010" },
                                { ":", "Etiqueta", "2 bytes", "0110 0011" },
                                { "add", "Instruccion", "3 bytes", "0101 1111" },
                                { "sub", "Instruccion", "3 bytes", "0111 1011" },
                                { "mul", "Instruccion", "3 bytes", "0001 0001" },
                                { "div", "Instruccion", "3 bytes", "0101 0000" },
                                { "call", "Instruccion", "4 bytes", "0000 0000" },
                                { "ret", "Instruccion", "3 bytes", "0101 0111" },
                                { "jmp", "Instruccion", "5 bytes", "0011 1100" },
                                { "cmp", "Instruccion", "4 bytes", "0011 0011" },
                                { "pot", "Instruccion", "7 bytes", "1100 1100" },
                                { "aaa", "Instruccion", "3 bytes", "1010 0101" },
                                { "je", "Instruccion", "2 bytes", "0101 1010" },
                                { "jle", "Instruccion", "3 bytes", "0101 1011" },
                                { "jg", "Instruccion", "3 bytes", "0101 1001" },
                                { "jge", "Instruccion", "3 bytes", "0101 0001" },
                                { "jg", "Instruccion", "3 bytes", "0101 0011" }, };
                        // Se crea el objeto tipo File "archivo.asm"
                        // File archivo = new File("archivo.asm");
                        // Se crea un objeto Scanner para leer el contenido del archivo
                        Scanner input = new Scanner(archivo);
                        Scanner entrada = new Scanner(archivo);
                        String token = null;
                        String token2;
                        // Se muestra el contenido del archivo
                        while (entrada.hasNextLine()) {
                            System.out.println(entrada.nextLine());
                        }
                        entrada.close();
                        // Se crea la tabla de token para el archivo
                        System.out.println("\nSimbolo\t\tNombre\t\tTamaño\t\tDireccion");
                        while (input.hasNextLine()) {
                            // Se obtiene el token del archivo
                            token = input.next();
                            token2 = "";
                            // para ignorar lo que este entre comillas dobles
                            if (token.charAt(0) == '"') {
                                while (token.charAt(token.length() - 1) != '"') {
                                    token = input.next();
                                }
                            }
                            // en caso de ser comentario
                            if (token.contains(";")) {
                                token = ";";
                                // saltarse a la siguiente linea de texto
                                input.nextLine();
                            } else if (token.charAt(0) == '.') {// separar el punto de otra palabra
                                token2 = token.substring(1);
                                token = ".";
                            } else if (token.charAt(token.length() - 1) == ':') {
                                token2 = token.substring(0, token.length() - 1);
                                token = ":";
                            }

                            for (String[] tablaToken1 : tablaToken) {
                                if (token.equals(tablaToken1[0])) {
                                    // Se muestran los datos
                                    System.out.println(tablaToken1[0] + "\t\t" + tablaToken1[1] + "\t" + tablaToken1[2]
                                            + "\t\t" + tablaToken1[3]);
                                    break;
                                }
                            }
                            for (String[] tablaToken1 : tablaToken) {
                                if (token2.equals(tablaToken1[0])) {
                                    // Se muestran los datos
                                    System.out.println(tablaToken1[0] + "\t\t" + tablaToken1[1] + "\t" + tablaToken1[2]
                                            + "\t\t" + tablaToken1[3]);
                                    break;
                                }
                            }
                        }
                        input.close();
                        /////////////////////////////////////////////////////////////////////////////////////////////////
                        FileReader FR = new FileReader(args[0]);
                        String Cadena4 = "";
                        BufferedReader BR = new BufferedReader(FR);
                        String Cadena3 = "";
                        int contador2 = 0;
                        while ((Cadena3 = BR.readLine()) != null) {
                            Cadena4 = Cadena3.replaceAll("^\\s*", "");
                            contador2 = contador2 + 1;
                            if (lineas == contador2) {
                                // System.out.println("la cadena es "+Cadena3);
                                if (Cadena4.indexOf("end") != -1) {
                                    Pattern p;
                                    Matcher m;
                                    p = Pattern.compile("^end");
                                    m = p.matcher(Cadena4);
                                    if (m.find()) {
                                        listaTokens.add("10101101");// System.out.println("error");
                                    } else {
                                        contador4++;
                                        System.out.println("Error en la linea: " + contador2
                                                + " No debe haber nada despues del end");
                                    }
                                }
                                System.exit(0);
                            }
                            String p1 = "model small";
                            if (Cadena4.indexOf("model") != -1) {
                                Pattern p;
                                Matcher m;
                                p = Pattern.compile("^." + p1);
                                m = p.matcher(Cadena4);
                                if (m.find()) {
                                    listaTokens.add("1111 1001\t");// System.out.println("error");
                                } else {
                                    contador4++;
                                    System.out.println("Error en la linea: " + contador2);
                                    System.exit(0);
                                }
                            }
                            String p2 = "stack ";
                            if (Cadena4.indexOf("stack") != -1) {
                                Pattern p;
                                Matcher m;
                                Pattern P;
                                Matcher M;
                                P = Pattern.compile("[0-9]+h");
                                p = Pattern.compile("^." + p2 + P);
                                m = p.matcher(Cadena4);
                                if (m.find()) {
                                    listaTokens.add("1001 1101\t");// System.out.println("error");
                                } else {
                                    contador4++;
                                    System.out.println("Error en la linea: " + contador2);
                                    System.exit(0);
                                }
                            }
                            if (Cadena4.indexOf("data") != -1) {
                                Pattern p;
                                Matcher m;
                                p = Pattern.compile("^.data|^[mov]");
                                m = p.matcher(Cadena4);
                                if (m.find()) {
                                    listaTokens.add("1001 1111\t");// System.out.println("error");
                                } else {
                                    contador4++;
                                    System.out.println("Error en la linea: " + contador2);
                                    System.exit(0);
                                }
                            }
                            if (Cadena4.indexOf("code") != -1) {
                                Pattern p;
                                Matcher m;
                                p = Pattern.compile("^.");
                                m = p.matcher(Cadena4);
                                if (m.find()) {
                                    listaTokens.add("0001 0001\t");// System.out.println("error");
                                } else {
                                    contador4++;
                                    System.out.println("Error en la linea: " + contador2);
                                    System.exit(0);
                                }
                            }
                            if (Cadena4.indexOf("mov") != -1) {
                                Pattern P;
                                Matcher M;
                                P = Pattern.compile("[a-zA-Z]|[a-zA-Z]+[0-9], [a-z0-9]+");
                                Pattern p;
                                Matcher m;
                                p = Pattern.compile("^mov " + P);
                                m = p.matcher(Cadena4);
                                if (m.find()) {
                                    listaTokens.add("1111 0000\t");// System.out.println("error");
                                } else {
                                    contador4++;
                                    System.out.println("Error en la linea: " + contador2);
                                    System.exit(0);
                                }
                            }
                            if (Cadena4.indexOf("add") != -1) {
                                Pattern P;
                                Matcher M;
                                P = Pattern.compile("[a-zA-Z]+, [a-zA-Z]|[0-9]");
                                Pattern p;
                                Matcher m;
                                p = Pattern.compile("^add " + P);
                                m = p.matcher(Cadena4);
                                if (m.find()) {
                                    listaTokens.add("0101 1111\t");// System.out.println("error");
                                } else {
                                    contador4++;
                                    System.out.println("Error en la linea: " + contador2);
                                    System.exit(0);
                                }
                            }
                            if (Cadena4.indexOf("sub") != -1) {
                                Pattern P;
                                Matcher M;
                                P = Pattern.compile("[a-zA-Z]+, [a-zA-Z]|[0-9]");
                                Pattern p;
                                Matcher m;
                                p = Pattern.compile("^sub " + P);
                                m = p.matcher(Cadena4);
                                if (m.find()) {
                                    listaTokens.add("0111 1011\t");// System.out.println("error");
                                } else {
                                    contador4++;
                                    System.out.println("Error en la linea: " + contador2);
                                    System.exit(0);
                                }
                            }
                            if (Cadena4.indexOf("mul") != -1) {
                                Pattern P;
                                Matcher M;
                                P = Pattern.compile("[a-zA-Z]+[0-9]|[a-zA-Z] ");
                                Pattern p;
                                Matcher m;
                                p = Pattern.compile("^mul " + P);
                                m = p.matcher(Cadena4);
                                if (m.find()) {
                                    listaTokens.add("0001 0001\t");// System.out.println("error");
                                } else {
                                    contador4++;
                                    System.out.println("Error en la linea: " + contador2);
                                    System.exit(0);
                                }
                            }
                            if (Cadena4.indexOf("div") != -1) {
                                Pattern P;
                                Matcher M;
                                P = Pattern.compile("[a-zA-Z]+, [a-zA-Z]|[0-9]");
                                Pattern p;
                                Matcher m;
                                p = Pattern.compile("^div " + P);
                                m = p.matcher(Cadena4);
                                if (m.find()) {
                                    listaTokens.add("0101 0000\t");// System.out.println("error");
                                } else {
                                    contador4++;
                                    System.out.println("Error en la linea: " + contador2);
                                    System.exit(0);
                                }
                            }
                            if (Cadena4.indexOf("call") != -1) {
                                Pattern P;
                                Matcher M;
                                P = Pattern.compile("[a-zA-Z]");
                                Pattern p;
                                Matcher m;
                                p = Pattern.compile("^call " + P);
                                m = p.matcher(Cadena4);
                                if (m.find()) {
                                    listaTokens.add("0000 0000\t");// System.out.println("error");
                                } else {
                                    contador4++;
                                    System.out.println("Error en la linea: " + contador2);
                                    System.exit(0);
                                }
                            }
                            if (Cadena4.indexOf("cmp") != -1) {
                                Pattern P;
                                Matcher M;
                                P = Pattern.compile("[a-zA-Z], [0-9]||[a-zA-Z]+[0-9]||[a-zA-Z]");
                                Pattern p;
                                Matcher m;
                                p = Pattern.compile("^cmp " + P);
                                m = p.matcher(Cadena4);
                                if (m.find()) {
                                    listaTokens.add("0011 0011\t");// System.out.println("error");
                                } else {
                                    contador4++;
                                    System.out.println("Error en la linea: " + contador2);
                                    System.exit(0);
                                }
                            }
                            if (Cadena4.indexOf("jmp") != -1) {
                                Pattern P;
                                Matcher M;
                                P = Pattern.compile("[a-zA-Z]|[a-zA-Z]+[0-9]");
                                Pattern p;
                                Matcher m;
                                p = Pattern.compile("^jmp " + P);
                                m = p.matcher(Cadena4);
                                if (m.find()) {
                                    listaTokens.add("0011 1111\t");// System.out.println("error");
                                } else {
                                    contador4++;
                                    System.out.println("Error en la linea: " + contador2);
                                    System.exit(0);
                                }
                            }
                            if (Cadena4.indexOf(":") != -1) {
                                String p3 = ":";
                                Pattern p;
                                Matcher m;
                                p = Pattern.compile("^[a-zA-Z]|[a-zA-Z]+[0-9] " + p3);
                                m = p.matcher(Cadena4);
                                if (m.find()) {
                                    listaTokens.add("0110 0011\t");// System.out.println("error");
                                } else {
                                    contador4++;
                                    System.out.println("Error en la linea: " + contador2);
                                    System.exit(0);
                                }
                            }
                            /////////////////////////////////////////////////////////////////////////////////////////////////
                        }

                        for (String strings : listaTokens) {
                            System.out.println(strings);
                        }

                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(prueba2.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {// si el archivo no existe
                System.out.println("\tError: El archivo no existe\n");
            }
        }
    }// Fin de main

    
}