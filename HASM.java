import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.FormatterClosedException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class HASM {
    public static final String outputOBJ = "";// si no se agrega ruta, se crea en la carpeta del proyecto
    // variables para lectura y escritura de archivos
    public static Scanner input;
    public static Formatter salida;

    // variables para la asigacion de valores de Tokens
    public static int valorAsignacion1 = 0;
    public static int valorAsignacion2 = 1;
    public static int valorAsignacion3 = 0;
    public static int valorAsignacion4 = 1;

    // valofres OBJ de los tokens
    public static ArrayList<String> listaValoresOBJ = new ArrayList<>();
    // lineas que contienen errores de sintaxis
    public static ArrayList<Integer> lineasConErrores = new ArrayList<>();
    // lista de Tokens
    public static ArrayList<Tokens> listaTokens = new ArrayList<>();
    // ArrayList que guarda las lineas con errores
    public static ArrayList<Integer> Sintax_Error = new ArrayList<>();

    public static void main(String[] args) {
        try { // verifica que se pasa nombre del archivo como argumento
              // si marca error la siguiente instruccion si no se ha pasado
              // ningun parametro, se ejcuta catch
            String inputFile = args[0]; // obtenemos el nombre del archivo.
            // se pudo ejecutar la intruccion anterior, no pasa a catch
            if (nameFile(inputFile)) { // validamos extencion del archivo
                if (searchFile(inputFile)) { // buscamo el archivo
                    System.out.println("File:   " + inputFile);
                    System.out.println("Size:   " + sizeFile(inputFile));
                    System.out.println("Lines:  "
                            + ((totalLines(inputFile) > 0) ? totalLines(inputFile) : "El archivo se encuentra vacio"));
                    if (totalLines(inputFile) > 0) { // si el archivo contiene lineas
                        // buscamos los errores en el archivo
                        listSintaxError(inputFile);
                        // si la lista esta vacia no hay errores
                        if (Sintax_Error.isEmpty()) {
                            System.out.println("\n\tNo se han encontrado errores");
                            tableTokens(inputFile);
                            System.out.println("\n\tSe ha generado archivo OBJ en: " 
                                + outputOBJ + generarOBJ(inputFile));
                        } else {
                            System.err.println("\n\tSe han encontrado errores");
                            for (Integer integer : Sintax_Error) { // imprime numero de linea
                                System.out.println("Error en la linea: " + integer);
                            }
                        }
                    } else { // si el archivo no contine lineas termina el programa
                        System.out.println("Fin del programa...");
                        System.exit(1); // sale del programa
                    }
                } else {
                    System.err.println("No se encuentra el archivo !!");
                }
            } else {
                System.err.println("Extencion de archivo incorrecta !!");
            }
        } catch (Exception e) {
            System.out.println("\tDebe agregar nombre de archivo!");
            System.exit(1); // sale del programa
        }

    }

    public static boolean nameFile(String nameFile) {
        // si la cadena termina com '.asm' retorna true
        if (nameFile.endsWith(".asm")) {
            return true;
        }
        return false;
    }

    public static boolean searchFile(String nameFile) {
        File myFile = new File(nameFile); // crea un objeto archivo
        if (myFile.exists()) { // si el objeto archivo existe
            return true;
        }
        return false;
    }

    public static int totalLines(String nameFile) {
        int totalLines = 0;
        try {
            input = new Scanner(new File(nameFile));
        } catch (FileNotFoundException e) {
            System.err.println("Error al abrir el archivo !");
            System.exit(1); // sale del programa
        }
        try {
            while (input.hasNext()) {
                String line = input.nextLine();
                totalLines++;
                //System.out.println(totalLines + " " + line);
            }
        } catch (NoSuchElementException e) {
            System.err.println("Formato de archivo incorrecto!");
            input.close(); // cierra el archivo
            System.exit(1); // sale del programa
        } catch (IllegalStateException e) {
            System.out.println("Error al leer el archivo!");
            System.exit(1); // sale del programa
        }
        if (input != null) {
            input.close();
        }
        return totalLines;
    }

    public static String sizeFile(String fileName) {
        float longitud = new File(fileName).length();
        if (longitud > 24) {
            return longitud / 24 + " Kb";
        }
        return longitud + " bytes";
    }

    public static void listSintaxError(String fileName) {
        // numero de linea de segmentos del programa
        ArrayList<Integer> SegmentosPrograma = new ArrayList<>();
        ArrayList<Integer> lineaSegmentoPrograma = new ArrayList<>();
        // segmentos de programa
        String segmentos[] = { ".model small", ".stack", ".data", ".code", "end" };
        // patron para variables
        String patternVar = "\\s+[a-zA-Z]+[0-9]*.\\s+db.*[^ ]\\$'\\s*+$";
        String patternSegments[] = { "^.model\\s+small\\s*", // segmento 1
                "^.stack\\s+([0-9])*\\s*", // segmento 2
                "^.data\\s*", // segmento 3
                "^.code\\s*", // segmento 4
                "\\s*end\\s*" // segmento 5
        };
        // patones de busqueda de las instrucciones
        String patternCode[][] = {
                { "mov", "\\s*mov\\s+([^0-9][a-zA-Z]+)\\s*,\\s*[^ ](')*(@*[0-9]*[a-zA-Z]*[0-9]*)(')*\\s*" },
                { "lea", "\\s*lea\\s+([^0-9][a-zA-Z]+)\\s*,\\s*([0-9]*[a-zA-Z]+[0-9]*)\\s*" },
                { "sub", "\\s*sub\\s+([0-9]*[a-zA-Z]*).,\\s*.([0-9]*[a-zA-Z]*)\\s*" },
                { "add", "\\s*add*\\s+([0-9]*[a-zA-Z]*).,\\s*.([0-9]*[a-zA-Z]*)\\s*" },
                { "int", "\\s*int\\s+([0-9]+h*H*)\\s*" }, 
                { "mul", "\\s*mul*\\s+([^0-9][a-zA-Z]*[0-9]*)*\\s*" },
                { "div", "\\s*div*\\s+([^0-9][a-zA-Z]*[0-9]*)*\\s*" },
                { "cmp", "\\s*cmp*\\s+([0-9]*[a-zA-Z]*).,\\s*.([0-9]*[a-zA-Z]*)\\s*" },
                { "loop", "\\s*loop*\\s+([a-zA-Z]+[0-9]*)*\\s*" }, 
                { "inc", "\\s*inc*\\s+([a-zA-Z]+[0-9]*)+\\s*" },
                { "dec", "\\s*dec*\\s+([a-zA-Z]+[0-9]*)+\\s*" } };
        // buscamos los segmentos del programa en el archivo
        for (int i = 0; i < segmentos.length; i++) {
            boolean segmentoEncontrado = false;
            try {
                input = new Scanner(new File(fileName));
            } catch (FileNotFoundException fileNotFoundException) {
                System.err.println("Error al abrir el archivo.");
                System.exit(1);
            }
            try { // read records from file using Scanner object
                int contadorLineaActual = 1;
                while (input.hasNext()) {
                    String linea = input.nextLine(); // lee la linea del archivo
                    // si la linea comienza con los segmentos
                    if (linea.trim().startsWith(segmentos[i])) {
                        if (Pattern.matches(patternSegments[i], linea)) {
                            // lo añadimos a la lista de errores
                            SegmentosPrograma.add(contadorLineaActual);
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
            } catch (Exception e) {
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
                for (Integer indiceLinea : lineaSegmentoPrograma) {
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
            System.out.println("\n\tSegmentos de codigo completos");
            try {
                input = new Scanner(new File(fileName));
            } // end try
            catch (FileNotFoundException fileNotFoundException) {
                System.err.println("Error al abrir el archivo.");
                System.exit(1);
            }
            try { // read records from file using Scanner object
                int contadorLineaActual = 1;
                int segmentoPrograma = 1;
                while (input.hasNext()) {
                    // lee la linea del archivo
                    String linea = input.nextLine();
                    // si es un comentario, se omite la linea
                    if (linea.trim().startsWith(";")) {
                        // aumento de numero de linea que se omite
                        contadorLineaActual++;
                        continue;
                    }
                    // si la linea esta vacia, se omite la linea
                    if (linea.trim().isEmpty()) {
                        // aumento de numero de linea que se omite
                        contadorLineaActual++;
                        continue;
                    }
                    // si la linea contiene un comentario
                    if (linea.indexOf(";") != 0) {
                        String valores[] = linea.split(";");
                        // solo se considera el String antes de ;
                        linea = valores[0];
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
                                if (linea.trim().equals(".code") || linea.trim().contains(".code")) {
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
                                        String valores[] = linea.split(";");
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
                                        String valores[] = linea.split(";");
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
            } catch (Exception e) {
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
          // return Sintax_Error;
    } // fin del metodo listSintaxError

    private static boolean buscarLineasNextEnd(String fileName) {
        try {
            input = new Scanner(new File(fileName));
        } // end try
        catch (FileNotFoundException fileNotFoundException) {
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
                            String valores[] = linea.split(";");
                            linea = valores[0]; // solo se considera el String antes de ;
                        }
                        if (!linea.trim().equals("")) {
                            return true;
                        }
                    }
                }

                // contadorLineaActual++;
            } // fin de while
        } catch (Exception e) {
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

    public static void tableTokens(String fileName) {
        String[] identificadores = { ".", "mov", "endm", "add", "sub", "mul", "div", "loop", "aam", "aaa", "jmp", "je",
                "jle", "cmp", "call", "ret", "lea", "int", ";" }; // Tokens
        listaTokens = new ArrayList<>(); // inicializamos la lista de tokens
        try { // intentamos abrir el archivo
            input = new Scanner(new File(fileName)); // Archivo del usuario
        } // end try
        catch (FileNotFoundException fileNotFoundException) {
            // si no podemos abrir el archivo
            System.err.println("Error al abrir el archivo.");
            System.exit(1); // salimos del programa
        }
        // leemos el contenido del archivo, el archivo txt asigna
        // una marca de fin de entradas del archivo
        while (input.hasNext()) { // miesntras tenga entradas
            String linea = input.nextLine(); // lee una linea
            // si la linea contiene comentarios
            // indexOf retorna la posicion en la cadena donde
            // se encuentra el carater, si no se encuentra retorna -1
            if (linea.indexOf(";") != -1) { // si la posicion del caracter no es -1, es decir que si se encuentra
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
                    String valores[] = linea.split(";");
                    linea = valores[0]; // solo se considera el String sntes de ';' pues despues de ';' el compilador lo
                                        // ignora
                }
                continue; // continuamos a la siguiente linea
            }

            // buscamos el segmento final del programa
            if (linea.toLowerCase().trim().equals("end")) {
                if (!TokenInTable("end")) {
                    listaTokens.add(new Tokens(1, "end", tipoToken("end"), tamanioToken("end"),
                            valorToken(tamanioToken("end"))));
                }
            }
            for (int i = 0; i < identificadores.length; i++) { // buscamos en los identificadores
                // la cadena se convierte en minusculas, se eliminan los primeros espacios
                // si la cadena empieza con algunos de los identificadores
                if (linea.toLowerCase().trim().startsWith(identificadores[i])) {
                    // Buscamos en la listaTokens si ya se ha encontrado
                    if (!TokenInTable(identificadores[i])) { // si si encontro
                        listaTokens.add(new Tokens(1, identificadores[i], tipoToken(identificadores[i]),
                                tamanioToken(identificadores[i]), valorToken(tamanioToken(identificadores[i]))));
                    }
                }
            } // fin de ciclo for
            if (linea.toLowerCase().trim().endsWith(":") && (!linea.startsWith(";"))) { // si la cadena termina con : es
                                                                                        // una etiqueta
                // String [] valores = linea.toLowerCase().trim().split("\\;");
                if (!TokenInTable(linea.toLowerCase().trim())) { // si ya se encuentra en el ArrayList
                    String sToken = linea.toLowerCase().trim().replace(":", " ");// = valores[0];
                    listaTokens.add(new Tokens(1, sToken.trim(), tipoToken(sToken + ":"), tamanioToken(sToken + ":"),
                            valorToken(tamanioToken(sToken + ":"))));
                }
            }

            // si la cadena contiene "macro" y no empieza con ";" es una macro
            if ((linea.indexOf(" macro ") != -1)) {
                String valores[] = linea.split(" ");
                if (!TokenInTable(valores[1])) { // Se busca "macro" en el ArrayList
                    listaTokens.add(new Tokens(1, valores[1], tipoToken(valores[1]), tamanioToken(valores[1]),
                            valorToken(tamanioToken(valores[1]))));
                }
                if (!TokenInTable(valores[0])) {
                    listaTokens.add(new Tokens(1, valores[0], "etiqueta", "1", valorToken("1")));
                }
            }
            // si no se encuentra en ninguna de las anteriores puede ser una variable
            // si la cadena contiene es <name> DB <values>
            if (linea.indexOf("db") != -1) {
                String valores[] = linea.trim().split("\\ ");
                if (valores[1].equals("db")) {
                    if (!TokenInTable(valores[0])) {
                        listaTokens.add(new Tokens(1, valores[0], tipoToken(valores[0] + " db"),
                                tamanioToken(valores[0] + " db"), valorToken(tamanioToken(valores[0] + " db"))));
                    }
                }
            }
        } // fin de while, aqui se termina de leer el archivo

        if (input != null) {
            input.close(); // cierra el archivo
        }
        System.out.println("\n\nCant\tnombreID\tTipo\t\tTamaño\t\tValor");
        for (Tokens lista : listaTokens) {
            System.out.println(lista.toString());
        }
    } // fin del metodo tablaTokens

    public static boolean TokenInTable(String ident) { // busca el token en ArrayList
        for (Tokens aux : listaTokens) {
            if (aux.getNombreID().equals(ident)) {
                aux.setCantidad(1); // incrementa la cantidad
                return true; // si se encuentra
            }
        }
        return false; // no se encunetra en el ArrayList
    } // fin de metodo TokenInTable

    public static String tipoToken(String token) { // devuelve el tipo de token
        String tipo = "undefined";
        String[] directivas = { ".", "v1", "v2", "end" };
        String[] intrucciones = { "int", "endm", "mov", "add", "aam", "sub", "mul", "loop", "div", "loop", "jmp", "je",
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

    public static String tamanioToken(String Token) { // define el tamanio de token
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

    public static String valorToken(String tamanio) { // valor segun tamaño
        if (tamanio.equals("------")) {
            return tamanio;
        }
        if (tamanio.equals("2")) {
            if (valorAsignacion1 > 15 || valorAsignacion2 > 15) {
                valorAsignacion1 = 0;
                valorAsignacion2 = 1;
            }
            String str1 = Integer.toHexString(valorAsignacion1); // hexadecimal
            String str2 = Integer.toHexString(valorAsignacion2); // hexadecimal
            String str3 = Integer.toHexString(valorAsignacion3); // hexadecimal
            String str4 = Integer.toHexString(valorAsignacion4); // hexadecimal
            valorAsignacion3++;
            valorAsignacion4++;
            return String.format("%2s%s%s%sH", str4, str3, str2, str1).replace(' ', '0');
        }
        if (tamanio.equals("1")) {
            if (valorAsignacion1 > 15 && valorAsignacion2 > 15) {
                valorAsignacion1 = 0;
                valorAsignacion2 = 1;
            }
            String str1 = Integer.toHexString(valorAsignacion1); // hexadecimal
            String str2 = Integer.toHexString(valorAsignacion2); // hexadecimal
            valorAsignacion1++;
            valorAsignacion2++;
            return String.format("%4s%sH", str2, str1).replace(' ', '0');
        }
        return "undefined";
    }

    private static void obtenerListaValoresOBJ(String fileName) {
        try {
            input = new Scanner(new File(fileName));
        } // end try
        catch (FileNotFoundException fileNotFoundException) {
            System.err.println("Error al abrir el archivo.");
            System.exit(1);
        }
        try { // read records from file using Scanner object
            while (input.hasNext()) {
                String linea = input.nextLine(); // lee la linea del archivo
                for (Tokens var : listaTokens) {
                    if (linea.trim().startsWith(var.getNombreID())) {
                        if (var.getNombreID().equals(";")) {
                            continue;
                        } else {
                            listaValoresOBJ.add(var.getValor());
                        }
                    }
                }
            } // fin de while
        } catch (Exception e) {
            System.err.println("Formato de archivo incorrecto.");
            input.close();
            System.exit(1);
        } finally {
            if (input != null) {
                input.close(); // cierra el archivo
            }
        }
    } // fin de obtenerValoresListaOBJ

    public static String generarOBJ(String fileName) {
        obtenerListaValoresOBJ(fileName);
        String nombreArchivoASM = fileName.replace("asm", "obj");
        try {
            salida = new Formatter(outputOBJ + nombreArchivoASM);
        } catch (SecurityException securityException) {
            System.err.println("No tienes permisos de escritura.");
            System.exit(1);
        } catch (FileNotFoundException filesNotFoundException) {
            System.err.println("Error al crear el archivo.");
            System.exit(1);
        } // end catch
        try {
            /*********** empieza escritura del archivo ******************/
            for (String aux : listaValoresOBJ) {
                salida.format("%s\n", aux);
            }
            /*********** termina escritura del archivo ********************/
        } catch (FormatterClosedException formatterClosedException) {
            System.err.println("Error al escribir el archivo.");
            
        } // end catch
        catch (NoSuchElementException elementException) {
            System.err.println("Entrada no valida. Intente nuevamente.");
            input.nextLine();
        } // end catch
        if (salida != null) {
            salida.close();
        }
        return nombreArchivoASM;
    } // fin de generarOBJ

}