import java.io.*;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Formatter;
import java.util.FormatterClosedException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.regex.*;

public class Archivo {
    /**
     * toLowerCase  -> convierte una cadena a minusculas 
     * toUpperCase  -> convierte una cadena a Mayusculas
     * split()       -> Separa una cadena cada que encuentre un caracter, el cual se le pasa a la funcion
                        <CadenaEnDondeSeBuscaElCaracter>.trim("<caracter a buscar>")
     * startWith()  -> devuelve true o false si la cadena empieza con la cadea que se le pasa como parametro 
                        <CadenaAVerificarSiEmpiezaConElString>.startWith("<cadenaABuscarAlInicio>")
     * endWith()    -> devuelve true o false si la cadena termina con la cadea que se le pasa como parametro 
                        <CadenaAVerificarSiTerminaConElString>.startWith("<cadenaABuscarAlFinal>")
     * equals()     -> compara dos cadenas, devuelve true si son iguales de lo contrario false 
                        <CadenaAComparar>.equals("<CadenaAComparar>")
     * indexOf()    -> busca un carater en una cadena y devuleve su posicion, si no lo encuentra devuelve -1
                        <CadenaEnDondeSeVaABuscar>.indexOf() 
     * trim()       -> Elimina los espacio de la cadena 
                        <CadenaEnDondeSeEliminanLosEspacios>.trim()
     * isEmpty()    -> Devuelve true si la linea se encuentra vacia, de lo contrario devuelve false
                    ->  <VariableAIdentificar>..esEmpty() 
     * 
     */


    // variables para lectura y escritura de archivos 
    private Scanner input;
    private Formatter salida;

    // variables para la asigacion de valores de Tokens
    private int valorAsignacion1 = 0;
    private int valorAsignacion2 = 1;
    private int valorAsignacion3 = 0;
    private int valorAsignacion4 = 1;

    ArrayList<String> listaValoresOBJ = new ArrayList<>(); // valofres OBJ de los tokens
    ArrayList<Integer> lineasConErrores = new ArrayList<>(); // lineas que contienen errores de sintaxis
    ArrayList<Tokens> listaTokens = new ArrayList<>(); // lista de Tokens

    public boolean nameFile(final String[] args) { // valida la extencion del archivo
        try {
            final String fileName = args[0];
            final String valores[] = fileName.split("\\.");
            if (valores[1].equals("asm")) {
                return true;
            } else {
                System.out.print("Extencion de archivo incorrecta");
                return false;
            }
        } catch (final Exception e) {
            System.err.print("Debe agregar nombre de archivo");
        }
        return false;
    } // fin del metodo nameFile

    public boolean SearchFile(final String fileName) { // Busca el archivo
        try {
            final File file = new File(fileName);
            final Scanner inputFile = new Scanner(file);
            inputFile.close();
            return true;
        } catch (final FileNotFoundException fileNotFoundException) {
            System.err.print("\nNo se encuentra el archivo !");
        }
        return false;
    } // fin del metodo SearchFile

    public int totalLineas(final String fileName) { // Regresa el total de linea del archivo
        int tLineas = 0;
        try {
            input = new Scanner(new File(fileName));
        } // end try
        catch (final FileNotFoundException fileNotFoundException) {
            System.err.println("Error al abrir el archivo.");
            System.exit(1);
        }
        try { // read records from file using Scanner object
            while (input.hasNext()) {
                final String linea = input.nextLine();
                try {
                    // System.out.println(linea);
                    tLineas++;
                    // System.out.println(linea);
                } catch (final InputMismatchException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (final NoSuchElementException elementException) {
            System.err.println("Formato de archivo incorrecto.");
            input.close();
            System.exit(1);
        } catch (final IllegalStateException stateException) {
            System.err.println("Error al leer de archivo.");
            System.exit(1);
        } // end catch
        if (input != null) {
            input.close(); // cierra el archivo
        }
        return tLineas; // retorna el total de lineas del archivo 
    }

    public void sizeFile(final String fileName) { // Imprime el tamaño del archivo en bytes
        final float longitud = new File(fileName).length();
        System.out.println(
                "\tSize      : " + ((longitud > 1024) ? 
                    longitud / 1024 + " Kb\n\n" : longitud + " bytes\n\t"));
    }

    public void tableTokens(final String fileName) {
        final String[] identificadores = { ".", "mov", "endm", "add", "sub", "mul", "div", "loop", // "macro","endm","proc","endp",
                "aam", "aaa", "jmp", "je", "jle", "cmp", "call", "ret", "lea", "int", ";" }; // Tokens
        listaTokens = new ArrayList<>(); // inicializamos la lista de tokens
        try { // intentamos abrir el archivo
            input = new Scanner(new File(fileName)); // Archivo del usuario
        } // end try
        catch (final FileNotFoundException fileNotFoundException) { // si no podemos abrir el archivo 
            System.err.println("Error al abrir el archivo.");
            System.exit(1); // salimos del programa 
        }
        // leemos el contenido del archivo, el archivo txt asigna una marca de fin de entradas del archivo
        while (input.hasNext()) { // miesntras tenga entradas 
            String linea = input.nextLine(); // lee un renglon del archivo 
            // si la linea contiene comentarios
            // indexOf retorna la posicion en la cadena donde se encuentra el carater, si no se encuentra retorna -1 
            if (linea.indexOf(";") != -1) {  // si la posicion del caracter no es -1, es decir que si se encuentra
                // si la linea empieza con ';' 
                if (linea.trim().startsWith(";")) {
                    // si no se ha agregado a la tabla 
                    if (!TokenInTable(";")) {
                        // añadimos el token a la lista 
                        listaTokens.add(
                                new Tokens(1, ";", tipoToken(";"), tamanioToken(";"), valorToken(tamanioToken(";"))));
                    }
                    continue; // continuamos con la siguiente linea del archivo 
                } else { // la linea contiene ';' pero no es la principio
                    final String valores[] = linea.split(";"); 
                    linea = valores[0]; // solo se considera el String sntes de ';' pues despues de ';' el compilador lo ignora 
                }
                continue; // continuamos a la siguiente linea 
            }

            // buscamos el segmento final del programa
            
            if (linea.toLowerCase().trim().equals("end")) {
                if (TokenInTable("end")) {
                    System.out.println("ya se ha agregado a la lista: \t\t" + "end"
                            + (("end".trim().length() > 7) ? "\t✖" : "\t\t✖"));
                } else {
                    listaTokens.add(new Tokens(1, "end", tipoToken("end"), tamanioToken("end"),
                            valorToken(tamanioToken("end"))));
                    System.out.println("se agregado un nuevo token: \t\t" + "end"
                            + (("end".trim().length() > 7) ? "\t✓" : "\t\t✓"));
                }
            }
            // System.out.println(">>>>"+linea);
            for (int i = 0; i < identificadores.length; i++) { // buscamos en los identificadores
                // la cadena se convierte en minusculas, se eliminan los primeros espacios
                // si la cadena empieza con algunos de los identificadores
                if (linea.toLowerCase().trim().startsWith(identificadores[i])) {
                    // Buscamos en la listaTokens si ya se ha encontrado
                    if (TokenInTable(identificadores[i])) { // si si encontro
                        System.out.println("ya se ha agregado a la lista: \t\t" + identificadores[i]
                                + ((identificadores[i].trim().length() > 7) ? "\t✖" : "\t\t✖"));
                    } else { // si no se encontro lo añadimos al ArrayList
                        listaTokens.add(new Tokens(1, identificadores[i], tipoToken(identificadores[i]),
                                tamanioToken(identificadores[i]), valorToken(tamanioToken(identificadores[i]))));
                        System.out.println("se agregado un nuevo token: \t\t" + identificadores[i]
                                + ((identificadores[i].trim().length() > 7) ? "\t✓" : "\t\t✓"));
                    }
                }
            } // fin de ciclo for
            if (linea.toLowerCase().trim().endsWith(":") && (!linea.startsWith(";"))) { // si la cadena termina con : es
                                                                                        // una etiqueta
                // String [] valores = linea.toLowerCase().trim().split("\\;");
                if (TokenInTable(linea.toLowerCase().trim())) { // si ya se encuentra en el ArrayList
                    System.out.println("ya se ha agregado a la lista: \t\t" + linea.toLowerCase().trim()
                            + ((linea.trim().length() > 7) ? "\t✖" : "\t\t✖"));
                } else { // No se encuentra en el ArrayList
                    final String sToken = linea.toLowerCase().trim().replace(":", " ");// = valores[0];
                    System.out.println("se agregado un nuevo token: \t\t" + sToken
                            + ((sToken.trim().length() > 7) ? "\t✓" : "\t\t✓"));
                    listaTokens.add(new Tokens(1, sToken.trim(), tipoToken(sToken + ":"), tamanioToken(sToken + ":"),
                            valorToken(tamanioToken(sToken + ":"))));
                }
            }

            // si la cadena contiene "macro" y no empieza con ";" es una macro
            if ((linea.indexOf(" macro ") != -1)) {
                final String valores[] = linea.split(" ");
                if (TokenInTable(valores[1])) { // Se busca "macro" en el ArrayList
                    System.out.println("ya se ha agregado a la lista: \t\t" + valores[1]
                            + ((valores[1].trim().length() > 7) ? "\t✖" : "\t\t✖"));
                } else {
                    // agregamos macro si no se encuentra en la tabla
                    System.out.println("se agregado un nuevo token: \t\t" + valores[1]
                            + ((valores[1].length() > 7) ? "\t✓" : "\t\t✓"));
                    listaTokens.add(new Tokens(1, valores[1], tipoToken(valores[1]), tamanioToken(valores[1]),
                            valorToken(tamanioToken(valores[1]))));
                }
                if (TokenInTable(valores[0])) {
                    System.out.println("ya se ha agregado a la lista: \t\t" + valores[0]
                            + ((valores[0].trim().length() > 7) ? "\t✖" : "\t\t✖"));
                } else {
                    // agregamos macro si no se encuentra en la tabla
                    System.out.println("se agregado un nuevo token: \t\t" + valores[0]
                            + ((valores[0].length() > 7) ? "\t✓" : "\t\t✓"));
                    listaTokens.add(new Tokens(1, valores[0], "etiqueta", "1", valorToken("1")));
                }
            }
            // si no se encuentra en ninguna de las anteriores puede ser una variable
            // si la cadena contiene es <name> DB <values>
            if (linea.indexOf("db") != -1) {
                final String valores[] = linea.trim().split("\\ ");
                if (valores[1].equals("db")) {
                    if (TokenInTable(valores[0])) {
                        System.out.println("ya se ha agregado a la lista: \t\t" + valores[0].toLowerCase().trim()
                                + ((valores[0].trim().length() > 7) ? "\t✖" : "\t\t✖"));
                    } else { // para distinguirlo de los demas, agregamos un ' db' al final
                        listaTokens.add(new Tokens(1, valores[0], tipoToken(valores[0] + " db"),
                                tamanioToken(valores[0] + " db"), valorToken(tamanioToken(valores[0] + " db"))));
                        // las funciones reconoceran la cadena al final
                        System.out.println("se agregado un nuevo token: \t\t" + valores[0]
                                + ((valores[0].trim().length() > 7) ? "\t✓" : "\t\t✓"));
                    }
                }
            }
        } // fin de while, aqui se termina de leer el archivo

        if (input != null) {
            input.close(); // cierra el archivo
        }
        System.out.println("\n\nCant\tnombreID\tTipo\t\tTamaño\t\tValor");
        for (final Tokens lista : listaTokens) {
            System.out.println(lista.toString());
        }
    } // fin del metodo tablaTokens

    public boolean TokenInTable(final String ident) { // busca el token en ArrayList
        for (final Tokens aux : listaTokens) {
            if (aux.getNombreID().equals(ident)) {
                aux.setCantidad(1); // incrementa la cantidad
                return true; // si se encuentra
            }
        }
        return false; // no se encunetra en el ArrayList
    } // fin de metodo TokenInTable

    public String tipoToken(final String token) { // devuelve el tipo de token
        String tipo = "undefined";
        final String[] directivas = { ".", "v1", "v2", "end" };
        final String[] intrucciones = { "int", "endm", "mov", "add", "aam", "sub", "mul", "loop", "div", "loop", "jmp", "je",
                "jle", "cmp", "call", "ret", "lea" };
        // buscamos en las directivas
        for (int i = 0; i < directivas.length; i++) {
            if (directivas[i].equals(token))
                tipo = "directiva";
        }
        if (token.endsWith(" db")) { // si contiene la cadena al final es variable
            tipo = "directiva";
        }
        // buscamos en instrucciones
        for (int i = 0; i < intrucciones.length; i++) {
            if (intrucciones[i].equals(token))
                tipo = "instruccion";
        }
        // buscamos en las etiquetas
        if (token.toLowerCase().trim().endsWith(":")) {
            tipo = "etiqueta";
        }
        // si es un ; es un comentario
        if (token.equals(";")) {
            tipo = "comentario";
        }
        // si es una macro
        if (token.trim().equals("macro")) {
            tipo = "instruccion";
        }
        if (token.startsWith("macro")) {
            tipo = "etiqueta";
        }

        return tipo;
    } // fin del metodo tipoToken

    public String tamanioToken(final String Token) { // define el tamanio de token
        String tamanio = "undefined"; // el tamaño se define en Bytes (8 bits)
        if (Token.equals(".")) {
            tamanio = "2";
        } else if (Token.equals(";")) {
            tamanio = "------";
        } else if (tipoToken(Token).equals("etiqueta")) {
            tamanio = "1";
        } else if (tipoToken(Token).equals("directiva") && !Token.equals(".")) {
            tamanio = "1";
        } else if (tipoToken(Token).equals("instruccion")) {
            tamanio = "2";
        }
        return tamanio;
    }

    public String valorToken(final String tamanio) { // valor segun tamaño
        if (tamanio.equals("------")) {
            return tamanio;
        }
        if (tamanio.equals("2")) {
            if (valorAsignacion1 > 15 || valorAsignacion2 > 15) {
                valorAsignacion1 = 0;
                valorAsignacion2 = 1;
            }
            final String str1 = Integer.toHexString(valorAsignacion1); // hexadecimal
            final String str2 = Integer.toHexString(valorAsignacion2); // hexadecimal
            final String str3 = Integer.toHexString(valorAsignacion3); // hexadecimal
            final String str4 = Integer.toHexString(valorAsignacion4); // hexadecimal
            valorAsignacion3++;
            valorAsignacion4++;
            return String.format("%2s%s%s%sH", str4, str3, str2, str1).replace(' ', '0');
        }
        if (tamanio.equals("1")) {
            if (valorAsignacion1 > 15 && valorAsignacion2 > 15) {
                valorAsignacion1 = 0;
                valorAsignacion2 = 1;
            }
            final String str1 = Integer.toHexString(valorAsignacion1); // hexadecimal
            final String str2 = Integer.toHexString(valorAsignacion2); // hexadecimal
            valorAsignacion1++;
            valorAsignacion2++;
            return String.format("%4s%sH", str2, str1).replace(' ', '0');
        }
        return "undefined";
    }

    public ArrayList<Integer> listSintaxError(final String fileName) {
        final ArrayList<Integer> Sintax_Error = new ArrayList<>(); // lista de errores del programa
        final ArrayList<Integer> SegmentosPrograma = new ArrayList<>(); // numero de linea de segmentos del programa
        final ArrayList<Integer> lineaSegmentoPrograma = new ArrayList<>();
        final String segmentos[] = { ".model small", ".stack", ".data", ".code", "end" }; // segmentos de programa
        final String patternVar = "\\s+[a-zA-Z]+[0-9]*.\\s+db.*[^ ]\\$'\\s*+$"; // patron para variables
        final String patternSegments[] = { 
                "^.model\\s+small\\s*",     // segmento 1
                "^.stack\\s+([0-9])*\\s*",  // segmento 2
                "^.data\\s*",               // segmento 3
                "^.code\\s*",               // segmento 4
                "\\s*end\\s*"               // segmento 5
        };

        final String patternCode[][] = { // patones de busqueda de las instrucciones
                { "mov", "\\s+mov\\s+([^0-9][a-zA-Z]+)\\s*,\\s*[^ ](')*(@*[0-9]*[a-zA-Z]*[0-9]*)(')*\\s*" },
                { "lea", "\\s+lea\\s+([^0-9][a-zA-Z]+)\\s*,\\s*([0-9]*[a-zA-Z]+[0-9]*)\\s*" },
                { "sub", "\\s+sub*\\s+([0-9]*[a-zA-Z]*).,\\s*.([0-9]*[a-zA-Z]*)\\s*" },
                { "add", "\\s+add*\\s+([0-9]*[a-zA-Z]*).,\\s*.([0-9]*[a-zA-Z]*)\\s*" },
                { "int", "\\s+int\\s+([0-9]+h*H*)\\s*" }, 
                { "mul", "\\s+mul*\\s+([^0-9][a-zA-Z]*[0-9]*)*\\s*" },
                { "div", "\\s+div*\\s+([^0-9][a-zA-Z]*[0-9]*)*\\s*" },
                { "cmp", "\\s+cmp*\\s+([0-9]*[a-zA-Z]*).,\\s*.([0-9]*[a-zA-Z]*)\\s*" },
                { "loop", "\\s+loop*\\s+([a-zA-Z]+[0-9]*)*\\s*" }, 
                { "inc", "\\s+inc*\\s+([a-zA-Z]+[0-9]*)+\\s*" },
                { "dec", "\\s+dec*\\s+([a-zA-Z]+[0-9]*)+\\s*" } };
        // buscamos los segmentos del programa en el archivo
        for (int i = 0; i < segmentos.length; i++) {
            boolean segmentoEncontrado = false;
            try {
                input = new Scanner(new File(fileName));
            } // end try
            catch (final FileNotFoundException fileNotFoundException) {
                System.err.println("Error al abrir el archivo.");
                System.exit(1);
            }
            try { // read records from file using Scanner object
                int contadorLineaActual = 1;
                while (input.hasNext()) {
                    final String linea = input.nextLine(); // lee la linea del archivo
                    if (linea.trim().startsWith(segmentos[i])) { // si la linea comienza con los segmentos
                        if (Pattern.matches(patternSegments[i], linea)) {
                            SegmentosPrograma.add(contadorLineaActual); // lo añadimos a la lista de errores
                            segmentoEncontrado = true;
                        }
                    }
                    if (!segmentoEncontrado) {
                        if (linea.indexOf(segmentos[i]) != -1) {
                            lineaSegmentoPrograma.add(contadorLineaActual);
                        }
                    }
                    contadorLineaActual++; // contador de lineas actual
                } // fin de while
            } catch (final Exception e) {
                System.err.println("Formato de archivo incorrecto.");
                input.close();
                System.exit(1);
            } finally {
                if (input != null) {
                    input.close(); // cierra el archivo
                }
            }
            if (!segmentoEncontrado) {
                System.out.println("no se encuentra en el " + "archivo el segmento '" + segmentos[i] + "' !!");
                for (final Integer indiceLinea : lineaSegmentoPrograma) {
                    System.out.println("Posible error en linea " + indiceLinea);
                }
                System.exit(1);
            }
        } // fin de for

        if (buscarLineasNextEnd(fileName)) {
            System.out.println("Se han encontrado lineas despues de 'end'");
            System.exit(1);
        }

        if (SegmentosPrograma.size() == 5) {
            System.out.println("Segmentos de codigo completos");
            try {
                input = new Scanner(new File(fileName));
            } // end try
            catch (final FileNotFoundException fileNotFoundException) {
                System.err.println("Error al abrir el archivo.");
                System.exit(1);
            }
            try { // read records from file using Scanner object
                int contadorLineaActual = 1;
                int segmentoPrograma = 1;
                /**
                 * Segmentos del Programa 1 -> .model 2 -> .stack 3 -> .data 4 -> .code 5 -> end
                 */
                while (input.hasNext()) {
                    String linea = input.nextLine(); // lee la linea del archivo
                    // si es un comentario, se omite la linea
                    if (linea.trim().startsWith(";")) {
                        contadorLineaActual++; // aumento de numero de linea que se omite
                        continue;
                    }
                    // si la linea esta vacia, se omite la linea
                    if (linea.trim().isEmpty()) {
                        contadorLineaActual++; // aumento de numero de linea que se omite
                        continue;
                    }
                    // si la linea contiene un comentario
                    if (linea.indexOf(";") != 0) {
                        final String valores[] = linea.split(";");
                        linea = valores[0]; // solo se considera el String antes de ;
                    }
                    if (segmentoPrograma == 1) { // modelo del programa
                        if (!Pattern.matches(patternSegments[0], linea)) {
                            Sintax_Error.add(contadorLineaActual);
                        }
                        segmentoPrograma++; // pasa a segmento de pila
                        contadorLineaActual++; // aumento de numero de linea
                        continue; // siguiente linea
                    }
                    if (segmentoPrograma == 2) { // pila del programa
                        if (!Pattern.matches(patternSegments[1], linea)) {
                            Sintax_Error.add(contadorLineaActual);
                        }
                        segmentoPrograma++; // pasa a segmento de datos
                        contadorLineaActual++; // aumento de numero de linea
                        continue; // siguiente linea
                    }
                    if (segmentoPrograma == 3) { // segmento de datos
                        if (!Pattern.matches(patternSegments[2], linea)) { // si no esta bien
                            Sintax_Error.add(contadorLineaActual);
                        } else { // si esta bien el nombre del segmento
                            // leer el contenido antes del inicio de seg de codido (.code)
                            while (input.hasNext() && (!linea.contains(".code") || !linea.startsWith(".code"))) {
                                linea = input.nextLine();
                                contadorLineaActual++;
                                if (linea.trim().equals(".code") || linea.trim().contains(".code")) { // si lo
                                                                                                        // encuentra,
                                                                                                        // termina el seg
                                                                                                        // de datos
                                    segmentoPrograma++; // pasa al segmento de codigo
                                    break;
                                } else { // si no es .code
                                    // si es un comentario, se omite la linea
                                    if (linea.trim().startsWith(";")) {
                                        continue;
                                    }
                                    // si la linea esta vacia, se omite la linea
                                    if (linea.trim().isEmpty()) {
                                        continue;
                                    }
                                    // si la linea contiene un comentario
                                    if (linea.indexOf(";") != 0) {
                                        final String valores[] = linea.split(";");
                                        linea = valores[0]; // solo se considera el String antes de ;
                                    }
                                    if (!Pattern.matches(patternVar, linea)) { // termina con '$'
                                        if (linea.endsWith("36")) { // termina con numero 36h = '$'
                                            if (!Pattern.matches("\\s*[a-zA-Z]+[0-9]*.\\s+db.*[^ ].\\,36\\s*", linea)) {
                                                Sintax_Error.add(contadorLineaActual);
                                            }
                                        }
                                        if (!linea.endsWith("36") || !linea.endsWith("$'")) { // si no termina con 36 o
                                                                                                // con $
                                            if (!Pattern.matches("\\s*[a-zA-Z]+[0-9]*.\\s+db.+([0-9])+\\s*", linea)) {
                                                Sintax_Error.add(contadorLineaActual);
                                            }
                                        }
                                    }
                                } // fin de else
                            } // fin de while
                        } // fin de else
                    } // fin de analisis de segmento de datos
                    if (segmentoPrograma == 4) { // segmento de codigo
                        if (!Pattern.matches(patternSegments[3], linea)) { // si es incorrecto
                            Sintax_Error.add(contadorLineaActual);
                        } else { // esta bien el nombre del segmento
                            // leer el contenido hasta antes de el seg end
                            while (input.hasNext() && (!linea.startsWith("end") || !linea.contains("end"))) {
                                boolean datoAgregado = false;
                                linea = input.nextLine();
                                contadorLineaActual++;
                                // si la linea en lectura es 'end' termina de leer
                                if (linea.trim().startsWith("end")) {
                                    segmentoPrograma = 5;
                                    break; // termina el ciclo de lectura
                                } else {
                                    // si es un comentario, se omite la linea
                                    if (linea.trim().startsWith(";")) {
                                        continue;
                                    }
                                    // si la linea esta vacia, se omite la linea
                                    if (linea.isEmpty()) {
                                        continue;
                                    }
                                    // si la linea contiene un comentario
                                    if (linea.indexOf(";") != 0) {
                                        final String valores[] = linea.split(";");
                                        linea = valores[0]; // solo se considera el String antes de ;
                                    }
                                    // si no se recoce la linea
                                    for (int i = 0; i < patternCode.length; i++) {
                                        if (linea.trim().startsWith(patternCode[i][0])) {
                                            datoAgregado = true;
                                        }
                                    }
                                    if (!datoAgregado) { // agregamos la linea
                                        Sintax_Error.add(contadorLineaActual);
                                        continue;
                                    }
                                    // recorremos las columas de la matriz, corresponden a el tipo de patron
                                    for (int i = 0; i < patternCode.length; i++) {
                                        // si la linea empieza con alguno de los elementos
                                        if (linea.trim().startsWith(patternCode[i][0])) {
                                            // si no corresponde al patron, agregamos a los errores
                                            if (!Pattern.matches(patternCode[i][1], linea)) {
                                                Sintax_Error.add(contadorLineaActual);
                                            }
                                        }
                                    } // fin de ciclo for

                                } // fin de else
                            } // fin de while para lectura de segmento de codigo
                        } // fin de end
                        segmentoPrograma++; // pasa a fin de programa
                        continue; // termina de leer el codigo
                    } // fin de while para lectura de archivo
                    if (segmentoPrograma == 5) {
                        if (!Pattern.matches(patternSegments[4], linea)) {
                            Sintax_Error.add(contadorLineaActual);
                        }
                        segmentoPrograma++; // pasa a segmento de datos
                        contadorLineaActual++; // aumento de numero de linea
                        continue; // siguiente linea
                    }
                    contadorLineaActual++; // numero de linea
                } // fin de while
            } catch (final Exception e) {
                System.err.println("Formato de archivo incorrecto.");
                input.close();
                System.exit(1);
            } finally {
                if (input != null) {
                    input.close(); // cierra el archivo
                }
            }
        } else {
            System.err.println("¡¡Segmentos del programa incompletos!!");
            System.out.println("Verificar que segmentos de programa se ecuentren bien escritos");
            System.exit(1);
        } // fin de else
        return Sintax_Error;
    } // fin del metodo listSintaxError

    private void obtenerListaValoresOBJ(final String fileName) {
        try {
            input = new Scanner(new File(fileName));
        } // end try
        catch (final FileNotFoundException fileNotFoundException) {
            System.err.println("Error al abrir el archivo.");
            System.exit(1);
        }
        try { // read records from file using Scanner object
            while (input.hasNext()) {
                final String linea = input.nextLine(); // read the line of file 
                for (final Tokens var : listaTokens) {
                    if (linea.trim().startsWith(var.getNombreID())) {
                        if (var.getNombreID().equals(";")){
                            continue;
                        } else {
                            listaValoresOBJ.add(var.getValor());
                        }
                    }
                }
            } // fin de while
        } catch (final Exception e) {
            System.err.println("Formato de archivo incorrecto.");
            input.close();
            System.exit(1);
        } finally {
            if (input != null) {
                input.close(); // cierra el archivo
            }
        }
    } // fin de obtenerValoresListaOBJ

    public void generarOBJ(final String fileName) {
        obtenerListaValoresOBJ(fileName);
        final String nombreArchivoTXT = fileName.replace("asm", "obj");
        try {
            salida = new Formatter("C:\\Users\\pc\\Documents\\UAEMex\\Ensambladores\\myassembly\\" + nombreArchivoTXT);
        } // end try
        catch (final SecurityException securityException) {
            System.err.println("No tienes permisos de escritura.");
            System.exit(1);
        } // end catch
        catch (final FileNotFoundException filesNotFoundException) {
            System.err.println("Error al crear el archivo.");
            System.exit(1);
        } // end catch
        try {
            /*********** empieza escritura del archivo ******************/
            for (final String aux : listaValoresOBJ) {
                salida.format("%s\n", aux);
            }
            /*********** termina escritura del archivo ********************/
        } // end try
        catch (final FormatterClosedException formatterClosedException) {
            System.err.println("Error al escribir el archivo.");
            return;
        } // end catch
        catch (final NoSuchElementException elementException) {
            System.err.println("Entrada no valida. Intente nuevamente.");
            input.nextLine();
        } // end catch
        if (salida != null) {
            salida.close();
        }
    } // fin de generarOBJ

    private boolean buscarLineasNextEnd(final String fileName) {
        try {
            input = new Scanner(new File(fileName));
        } // end try
        catch (final FileNotFoundException fileNotFoundException) {
            System.err.println("Error al abrir el archivo.");
            System.exit(1);
        }
        try { // read records from file using Scanner object
                // int contadorLineaActual = 1;
            while (input.hasNext()) {
                String linea = input.nextLine(); // lee la linea del archivo
                if (linea.equals("end")) {
                    while (input.hasNext()) {
                        linea = input.nextLine();
                        // System.out.println(linea);
                        // si es un comentario, se omite la linea
                        if (linea.trim().startsWith(";")) {
                            continue;
                        }
                        // si la linea esta vacia, se omite la linea
                        if (linea.trim().isEmpty()) {
                            continue;
                        }
                        // si la linea contiene un comentario
                        if (linea.indexOf(";") != 0) {
                            final String valores[] = linea.split(";");
                            linea = valores[0]; // solo se considera el String antes de ;
                        }
                        if (!linea.trim().equals("")) {
                            return true;
                        }
                    }
                }

                // contadorLineaActual++;
            } // fin de while
        } catch (final Exception e) {
            System.err.println("Formato de archivo incorrecto.");
            input.close();
            System.exit(1);
        } finally {
            if (input != null) {
                input.close(); // cierra el archivo
            }
        }
        return false;
    } // fin de metodo buscar

} // fin de la clase main