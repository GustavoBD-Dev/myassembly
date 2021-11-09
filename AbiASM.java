import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AbiASM{
    // variables para lectura y escritura de archivos
    public static Scanner input;
    public static Formatter salida;

    // contador de lineas
    public static int contadorLineas = 0; 
    public static void main(String[] args) {
        try {
            String nombreArchivo = args[0]; // obtenemos el argumento 
            verificarArchivo(nombreArchivo); // mandamos llamar a la funcion
            // si el total de lineas del archivo es cero termiana el programa 
            if (contadorLineas == 0) {
                input.close();
                return;
            } else {

                try { // intentamos leer el archivo 
                    input = new Scanner(new File(nombreArchivo));
                } catch (FileNotFoundException e) {
                    System.err.println("Error al abrir el archivo !");
                    return; // sale del programa
                }
                ///////////////////////////// empieza la verificacion de sintaxis 

                // buscamos fin de programa
                /////////////
                boolean encontroEND = false;
                input = new Scanner(new File(args[0]));
                while (input.hasNext()) { // miestras se tengan lineas 
                    String linea = input.nextLine();
                    if (linea.trim().equals("end")){
                        encontroEND = true;
                        continue;
                    }
                    if (encontroEND){
                        if (linea.startsWith(";") || linea.isEmpty()){
                            continue;
                        } else {
                            System.out.println("se encuentran lineas despues de END");
                            input.close();
                            return;
                        }
                    } 
                }
                if (!encontroEND){
                    System.out.println("No se encuentra end");
                    input.close();
                    return;
                } 

                ///////////// termina de buscar fin de programa 

                /// empieza a buscar sintaxis 
                try { // intentamos leer el archivo 
                    input = new Scanner(new File(nombreArchivo));
                } catch (FileNotFoundException e) {
                    System.err.println("Error al abrir el archivo !");
                    return; // sale del programa
                }
                int lineaActual = 1;
                while (input.hasNext()) { // miestras se tengan lineas 
                    String linea = input.nextLine();
                    // si la liena es un comentario o es una linea vacia
                    if (linea.startsWith(";") || linea.isEmpty()) {
                        lineaActual++;// aumentamos el contador de lienas 
                        continue; // pasa a la siguinte linea 
                    }
        
                    // si la linea actual es 1 debe der '.model small'
                    if (lineaActual == 1) {
                        if (!linea.equals(".model small")) {
                            System.out.println("No se Encuentra .model small");
                            return; // sale del programa 
                        }
                        lineaActual++; // aumenta el contador de linea 
                        continue; // pasa a la siguiete linea 
                    }
                    // si la linea actual es 2 debe der '.stack'
                    if (lineaActual == 2) {
                        if (!linea.equals(".stack")) {
                            System.out.println("No se Encuentra .stack");
                            return; // sale del programa 
                        }
                        lineaActual++; // aumenta el contador de linea 
                        continue; // pasa a la siguiete linea
                    }
                    // si la linea actual es 3 debe der '.data'
                    if (lineaActual == 3) {
                        if (!linea.equals(".data")) {
                            System.out.println("No se Encuentra .data");
                            return; // sale del programa 
                        }
                        lineaActual++; // aumenta el contador de linea 
                        continue; // pasa a la siguiete linea
                    }
                    // si la linea actual es 4 debe ser declaracion de variables
                    if (lineaActual == 4 || lineaActual == 5 || lineaActual == 6  ) {
                        // declaramos nuestro patron de regex 
                        String patterVar = "\\s+[a-zA-Z]+[0-9]*.\\s+db.*[^ ]\\$'\\s*+$";
                        // matches guarda un valor boleeano si el patron cumple o no
                        boolean mattches = Pattern.matches(patterVar, linea);
                    /*    if (!mattches){ // si no se cumple con el patron
                            System.out.println("Error en la linea: " + lineaActual);
                            return;
                        }
                    */    lineaActual++; // aumenta el contador de linea 
                        continue; // pasa a la siguiete linea
                    }
                    // si la linea actual es 6 debe der '.code'
                    if (lineaActual == 7) {
                        if (!linea.equals(".code")) {
                            System.out.println("No se Encuentra .code");
                            return; // sale del programa 
                        }
                        lineaActual++; // aumenta el contador de linea 
                        continue; // pasa a la siguiete linea
                    }
        
                    // si la linea actual es 4 debe ser declaracion de variables
                    if (lineaActual >= 7) {
                        if (linea.indexOf("mov") != -1) {
                            //System.out.println(linea);
                            //separamos los valores de las cadenas 
                            String valores[] = linea.trim().split(" ");
                           
                            try {
                                String var1 =  valores[0];
                                String var2 =  valores[1];
                                String var3 =  valores[2];

                            } catch (Exception e) {
                                System.out.println("Error en linea: " + lineaActual);
                                return;
                            }
                            // si no se ocntienen 3 valores esta mal escrita la linea 
                            // o hace falta algun parametro 
                            if (valores.length < 2 && !linea.equals("end")) {
                                System.out.println("Error en linea: " + lineaActual);
                                return;
                            }    
                        } 
                        
                        lineaActual++; // aumenta el contador de linea
                        
                        continue;
                    }            
                }

                /// termina la verificacion de sintaxis 
            }
            // finalizamos cerrando el archivo 
            input.close();
        } catch (Exception e) {
            System.out.println("no se agrego nombre de archivo");
            System.exit(1); // sale del programa 
        }
        
    }

    public static void verificarArchivo(String nombreArchivo) {
        if (nombreArchivo.endsWith(".asm")) { // valida que el nombre del archivo termina en .asm 
            File archivoFuente = new File(nombreArchivo); // creamo un objeto archivo 
            // exist() retorna true si existe el archivo 
            if (archivoFuente.exists()) { // si existe el archivo 
                try { // intentamos leer el archivo 
                    input = new Scanner(new File(nombreArchivo));
                } catch (FileNotFoundException e) {
                    System.err.println("Error al abrir el archivo !");
                    return; // sale del programa
                }
                 
                // leemos el archivo liena a linea 
                while (input.hasNext()) { // miestras se tengan lineas 
                    String linea = input.nextLine();
                    contadorLineas++;
                }

                System.out.println("*******************************");
                System.out.println("\tNombre:" + nombreArchivo);           // nombre del archivo 
                System.out.println("\tLineas:" + contadorLineas);          // total de lienas del archivo
                System.out.println("\tTamaño:" + archivoFuente.length() + " Bytes");  // tamaño del archivo
                System.out.println("*******************************");

            } else { // no existe el archivo 
                System.out.println("No se encuentra el archivo");
            }         

        } else {
            System.out.println("Nombre de archivo incorrecto");
            return; // sale del programa 
        }
    }

    public static void verificarSintaxis(){

       
    }


} // fin de la clase principal 



