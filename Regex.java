import java.util.regex.*;
import java.io.*;
import java.util.*;
public class Regex {
    private static Scanner input;
    public static void main(String[] args) {

        ArrayList<Integer> lineErrors = new ArrayList<>();
        String fileName = "file.asm";
        lineErrors = listSintaxError(fileName);
                    if (lineErrors.isEmpty()  ) { // si no hay errores 
                        System.out.println("genera tabla tokens");
                    } else { // hay errores 
                        for (Integer integer : lineErrors) { // imprime numero de linea
                            System.out.println("Error en la linea: " + integer);
                        }
                    } 
    } // fin de main

    public static ArrayList<Integer> listSintaxError ( String fileName ){
        ArrayList<Integer> Sintax_Error = new ArrayList<>();
        ArrayList<Integer> SegmentosPrograma = new ArrayList<>();
        ArrayList<Integer> lineaSegmentoPrograma = new ArrayList<>();
        String segmentos [] = {".model",".stack",".data",".code","end"};
        String patternVar = "\\s+[a-zA-Z]+[0-9]*.\\s+db.*[^ ]\\$'\\s*+$";
        String patternSegments[] = {
            "^.model\\s+small\\s*", // segmento 1
            "^.stack\\s*[0-9]*\\s*",// segmento 2
            "^.data\\s*",           // segmento 3
            "^.code\\s*",           // segmento 4
            "\\s*end\\s*"
        };
        String patternCode[][] = {
            {"mov", "\\s+mov\\s+([^0-9][a-zA-Z]+)\\s*,\\s*[^ ](')*(@*[0-9]*[a-zA-Z]*[0-9]*)(')*\\s*"},
            {"lea", "\\s+lea\\s+([^0-9][a-zA-Z]+)\\s*,\\s*([0-9]*[a-zA-Z]+[0-9]*)\\s*"},
            {"sub", "\\s+sub*\\s+([0-9]*[a-zA-Z]*).,\\s*.([0-9]*[a-zA-Z]*)\\s*"},
            {"add", "\\s+add*\\s+([0-9]*[a-zA-Z]*).,\\s*.([0-9]*[a-zA-Z]*)\\s*"},
            {"int", "\\s+int\\s+([0-9]+h)\\s*"},
            {"mul","\\s+mul*\\s+([a-zA-Z]+[0-9]*)*\\s*"},
            {"div","\\s+div*\\s+([a-zA-Z]+[0-9]*)*\\s*"},
            {"cmp","\\s+cmp*\\s+([0-9]*[a-zA-Z]*).,\\s*.([0-9]*[a-zA-Z]*)\\s*"},
            {"loop","\\s+loop*\\s+([a-zA-Z]+[0-9]*)*\\s*"},
            {"inc","\\s+inc*\\s+([a-zA-Z]+[0-9]*)+\\s*"},
            {"dec","\\s+dec*\\s+([a-zA-Z]+[0-9]*)+\\s*"},
        };
        
        for (int i = 0; i < segmentos.length; i++) {
            boolean segmentoEncontrado = false;
            try {
                input = new Scanner(new File(fileName));
            } // end try
            catch (FileNotFoundException fileNotFoundException) {
                System.err.println("Error al abrir el archivo.");
                System.exit(1);
            }
            try { // read records from file using Scanner object
                int contadorLineaActual = 1;
                while (input.hasNext()){
                    String linea = input.nextLine(); // lee la linea del archivo
                    if (linea.startsWith(segmentos[i]) ){ // li la linea comienza con los segmentos
                        if (Pattern.matches(patternSegments[i], linea)) {
                            SegmentosPrograma.add(contadorLineaActual); // lo añadimos a la lista de errores
                        segmentoEncontrado = true;
                        }
                            
                    }
                    if (!segmentoEncontrado){
                        if (linea.indexOf(segmentos[i]) != -1) {
                            lineaSegmentoPrograma.add(contadorLineaActual);
                        }
                    }
                    contadorLineaActual++;
                } // fin de while 
            } catch (Exception e) {
                System.err.println("Formato de archivo incorrecto.");
                input.close();
                System.exit(1);
            } finally{
                if (input != null) {
                    input.close(); // cierra el archivo
                } 
            }
            if (!segmentoEncontrado){
                System.out.println("no se encuentra en el archivo el segmento " + segmentos[i]);
                for (Integer indiceLinea : lineaSegmentoPrograma) {
                    System.out.println("Posible error en linea " + indiceLinea);
                }
                System.exit(1);
            }
            
           
        } // fin de for 
        //System.out.println(SegmentosPrograma.size());
        if (buscarLineasNextEnd(fileName)){
            System.out.println("Se han encontrado lineas despues de 'end'");
        }
        
        if (SegmentosPrograma.size() == 5) {
            System.out.println("Segmentos de codigo completos");
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
                boolean datoAgreagdo = false;
                /** Segmentos del Programa  
                 *  1 -> .model
                 *  2 -> .stack
                 *  3 -> .data
                 *  4 -> .code
                 *  5 -> end
                 */
                while (input.hasNext()){
                    //System.out.println("segmento porgrama actual " + segmentoPrograma);
                    String linea = input.nextLine(); // lee la linea del archivo
                    // si es un comentario, se omite la linea 
                    if ( linea.trim().startsWith(";")) {
                        contadorLineaActual++; // aumento de numero de linea que se omite 
                        continue;
                    }    
                    // si la linea esta vacia, se omite la linea 
                    if (linea.trim().isEmpty()) {
                        contadorLineaActual++; // aumento de numero de linea que se omite 
                        continue;
                    }
                    // si la linea contiene un comentario 
                    if (linea.indexOf(";") != 0){
                        String valores[] = linea.split(";");
                        linea = valores[0]; //solo se considera el String antes de ;
                    }

                    if ( segmentoPrograma == 1) { // modelo del programa 
                        if ( !Pattern.matches(patternSegments[0], linea)) {
                            Sintax_Error.add(contadorLineaActual);
                        }
                        segmentoPrograma++; // pasa a segmento de pila
                        contadorLineaActual++; // aumento de numero de linea 
                        continue; // siguiente linea
                    }
                    if ( segmentoPrograma == 2) { // pila del programa 
                        if ( !Pattern.matches(patternSegments[1], linea)) {
                            Sintax_Error.add(contadorLineaActual);
                        }
                        segmentoPrograma++; // pasa a segmento de datos
                        contadorLineaActual++; // aumento de numero de linea 
                        continue; // siguiente linea
                    }
                    if ( segmentoPrograma == 3 ) { // segmento de datos 
                        if ( !Pattern.matches(patternSegments[2], linea)) { // si no esta bien 
                            Sintax_Error.add(contadorLineaActual);
                        } else { // si esta bien el nombre del segmento
                            // leer el contenido antes del inicio de seg de codido (.code)
                            while (input.hasNext() && (!linea.contains(".code") || !linea.startsWith(".code"))) {
                                linea = input.nextLine();
                                contadorLineaActual++;
                                if ( linea.trim().equals(".code") || linea.trim().contains(".code")) { // si lo encuentra, termina el seg de datos
                                    segmentoPrograma++; // pasa al segmento de codigo 
                                    break;
                                } else { // si no es .code 
                                    // si es un comentario, se omite la linea 
                                    if ( linea.trim().startsWith(";")) {
                                        continue;
                                    }  
                                    // si la linea esta vacia, se omite la linea 
                                    if (linea.trim().isEmpty()) {
                                        continue;
                                    }
                                    // si la linea contiene un comentario 
                                    if (linea.indexOf(";") != 0){
                                        String valores[] = linea.split(";");
                                        linea = valores[0]; //solo se considera el String antes de ;
                                    }

                                    if ( !Pattern.matches(patternVar, linea) ) { // termina con '$'
                                        if ( linea.endsWith("36") ) { // termina con numero 36h = '$'
                                            if ( !Pattern.matches("\\s*[a-zA-Z]+[0-9]*.\\s+db.*[^ ].\\,36\\s*", linea) ) {
                                                Sintax_Error.add(contadorLineaActual);
                                            }
                                        } 
                                        if (!linea.endsWith("36") || !linea.endsWith("$'") ) { // si no termina con 36 o con $
                                            if ( !Pattern.matches("\\s*[a-zA-Z]+[0-9]*.\\s+db.+([0-9])+\\s*", linea) ) {
                                                Sintax_Error.add(contadorLineaActual);
                                            } 
                                        }
                                    }
                                } // fin de else 
                            } // fin de while 
                        } // fin de else 
                    } // fin de analisis de segmento de datos
                    if ( segmentoPrograma == 4 ) { // segmento de codigo 
                        if ( !Pattern.matches(patternSegments[3], linea)) { // si es incorrecto 
                            Sintax_Error.add(contadorLineaActual);
                        }  else { // esta bien el nombre del segmento 
                            // leer el contenido hasta antes de el seg end
                            while ( input.hasNext() && (!linea.startsWith("end") || !linea.contains("end")) ) {
                                datoAgreagdo = false;

                                linea = input.nextLine();
                                contadorLineaActual++;
                                // si la linea en lectura es 'end' termina de leer 
                                if ( linea.trim().startsWith("end") ) {
                                    segmentoPrograma = 5;
                                    break; // termina el ciclo de lectura 
                                } else {
                                    // si es un comentario, se omite la linea 
                                    if ( linea.trim().startsWith(";")) {
                                        continue;
                                    }  
                                    // si la linea esta vacia, se omite la linea 
                                    if (linea.isEmpty()) {
                                        continue;
                                    }
                                    // si la linea contiene un comentario 
                                    if (linea.indexOf(";") != 0){
                                        String valores[] = linea.split(";");
                                        linea = valores[0]; //solo se considera el String antes de ;
                                    }

                                    for (int i = 0; i < patternCode.length; i++) {
                                        if ( linea.trim().startsWith(patternCode[i][0])) {
                                            datoAgreagdo = true;
                                        }
                                    }
                                    if ( !datoAgreagdo ) {
                                        Sintax_Error.add(contadorLineaActual);
                                        continue;
                                    }

                                    // recorremos las columas de la matriz, corresponden a el tipo de patron                                    
                                    for (int i = 0; i < patternCode.length; i++) {
                                        // si la linea empieza con alguno de los elementos 
                                        if ( linea.trim().startsWith(patternCode[i][0]) ) { 
                                            // si no corresponde al patron, agregamos a los errores
                                            if ( !Pattern.matches(patternCode[i][1], linea) ) {
                                                Sintax_Error.add(contadorLineaActual);
                                            } 
                                        }
                                    }  // fin de ciclo for 
                                } // fin de else 
                            } // fin de while para lectura de segmento de codigo 
                        } // fin de end
                        segmentoPrograma++; // pasa a fin de programa 
                        continue; // termina de leer el codigo 
                    } // fin de while para lectura de archivo 
                    if ( segmentoPrograma == 5 ) {
                        if ( !Pattern.matches(patternSegments[4], linea)) {
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
            } finally{
                if (input != null) {
                    input.close(); // cierra el archivo
                } 
            }
        } else {
            System.out.println("segmentos de codigo incompletos");
        } // fin de else 
        return Sintax_Error;
    }


    private static boolean buscarLineasNextEnd(String fileName){
        try {
            input = new Scanner(new File(fileName));
        } // end try
        catch (FileNotFoundException fileNotFoundException) {
            System.err.println("Error al abrir el archivo.");
            System.exit(1);
        }
        try { // read records from file using Scanner object
            //int contadorLineaActual = 1;
            while (input.hasNext()){
                String linea = input.nextLine(); // lee la linea del archivo
                if ( linea.equals("end")) {
                    while (input.hasNext()) {
                        linea  = input.nextLine();
                        //System.out.println(linea);
                        // si es un comentario, se omite la linea 
                        if ( linea.trim().startsWith(";")) {
                            continue;
                        }  
                        // si la linea esta vacia, se omite la linea 
                        if (linea.trim().isEmpty()) {
                            continue;
                        }
                        // si la linea contiene un comentario 
                        if (linea.indexOf(";") != 0){
                            String valores[] = linea.split(";");
                            linea = valores[0]; //solo se considera el String antes de ;
                        }
                        if (!linea.trim().equals("")){
                            return true;
                        }
                    }
                }

                //contadorLineaActual++;
            } // fin de while 
        } catch (Exception e) {
            System.err.println("Formato de archivo incorrecto.");
            input.close();
            System.exit(1);
        } finally{
            if (input != null) {
                input.close(); // cierra el archivo
            } 
        }
        return false;
    } // fin de metodo buscar
} // fin de la clase 
