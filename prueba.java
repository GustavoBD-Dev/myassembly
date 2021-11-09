//package prueba123;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class prueba {

    public static void main(String[] arg) throws IOException {

        System.out.println("El archivo que se abrio es "+arg[0]);
        String textoArchivo = "";
        int NumeroLineas = 0;
        int TotalLineas = 0;
        String extencion = "";
        String aux = "";
        String cadenaDos = "";
        String cadenaTres = "";
        String cadenaCuatro = "";
        int contadorAux = 0;
        int NumeroLineasVacias = 0;
        String[] indicadorBusqueda= {";", ".", "mov", "model", "stack", "data", "sub", "add", "mul", "div", "code"};
        String[] indicadorBusqueda2 = {
            "; \t         \t    \t        ",
            ". \tDirectiva \t64kb \t\t00000000",
            "mov \tInstruccion \t1bytes \t\t00000001",
            "model \tDirectiva \t1 byte \t\t00000010",
            "stack \tDirectiva \t2bytes \t\t00000100",
            "data \tDirectiva \t4bytes \t\t 00001000",
            "sub \tInstruccion \t2bytes \t\t00010000",
            "add \tInstruccion \t5bytes \t\t00100000",
            "mul \tInstruccion \t4bytes \t\t01000000",
            "div \tInstruccion \t4bytes \t\t10000000",
            "code \tDirectiva \t4bytes \t\t 11000000"
        };
        String[] Directivas = {"code", "data"};
        String cadena;
        int i = 0;
        PrintStream DDescritor = new PrintStream("TablaTokens.txt");
        String nombreArchivo = arg[0].substring(0, arg[0].lastIndexOf("."));
        PrintStream ArchivoObjeto = new PrintStream(nombreArchivo + ".obj");
        Pattern P;
        Matcher M;
        Pattern p;
        Matcher m;
        try {
            FileReader fr = new FileReader(arg[0]);

            BufferedReader br = new BufferedReader(fr);
            int index = arg[0].lastIndexOf('.');
            if (index == -1) {
                System.out.println("");
            } else {
                extencion = arg[0].substring(index + 1);
                //System.out.println("La extencion del programa es: " + extencion);
            }
            if (!"asm".equals(extencion)) {
                System.out.println("Error la extencion del archivo no corresponde");
            } else {
                while ((cadena = br.readLine()) != null) {
                    textoArchivo += "\t" + cadena + "\n";
                    NumeroLineas++;
                    if (cadena.isEmpty()) {
                        NumeroLineasVacias++;
                    }
                }
                TotalLineas= NumeroLineas - NumeroLineasVacias;
                if (TotalLineas != 0) {
                    System.out.println("El numero de lineas es: " + TotalLineas);
                    DDescritor.print("\tNombre \tTipo \t\tTamaño \t\tValor");
                    for (int a = 0; a < indicadorBusqueda.length; a++) {
                        if (textoArchivo.indexOf(indicadorBusqueda[a]) != -1) {
                            DDescritor.print("\n\t" + indicadorBusqueda2[a] + "\n");
                        }
                    }
                    FileReader Fr = new FileReader(arg[0]);
                    BufferedReader Br = new BufferedReader(Fr);
                    while ((cadenaDos = Br.readLine()) != null) {
                        p = Pattern.compile("db");
                        m = p.matcher(cadenaDos);
                        if (m.find()) {
                            aux = cadenaDos;
                            char c = aux.charAt(i);
                            i++;
                            if (aux.indexOf(c) != -1) {
                                int j = 0;
                                String va = "\tDirectiva    \t4bytes    \t10011101        \n";
                                String[] parts = aux.split(" ");
                                String part1 = parts[0];
                                String part2 = parts[1];
                                DDescritor.print("\n" + part1 + va);
                            }
                        }
                    }
                    FileReader FR = new FileReader(arg[0]);
                    BufferedReader BR = new BufferedReader(FR);
                    while ((cadenaTres = BR.readLine()) != null) {
                        cadenaCuatro = cadenaTres.replaceAll("^\\s*", "");
                        contadorAux = contadorAux + 1;
                        String p1 = "model small";
                        if (cadenaCuatro.indexOf("model") != -1) {
                            p = Pattern.compile("^." + p1);
                            m = p.matcher(cadenaCuatro);
                            if (m.find()) {
                                ArchivoObjeto.print("111111111\t");
                            } else {

                                System.out.println("Error en la linea " + contadorAux);
                            }
                        }
                        String p2 = "stack 100h";
                        if (cadenaCuatro.indexOf("stack") != -1) {
                            p = Pattern.compile("^." + p2);
                            m = p.matcher(cadenaCuatro);
                            if (m.find()) {
                                ArchivoObjeto.print("11111110\t");
                            } else {
                                System.out.println("Error en la linea " + contadorAux + "no contiene un valor determinado pra el espacio");
                                System.exit(0);
                            }
                        }
                        if (cadenaCuatro.indexOf("code") != -1) {
                            p = Pattern.compile("^.");
                            m = p.matcher(cadenaCuatro);
                            if (m.find()) {
                                ArchivoObjeto.print("11111101\t");
                            } else {
                                System.out.println("Error en la linea " + contadorAux);
                                System.exit(0);
                            }
                        }
                        if (cadenaCuatro.indexOf("add") != -1) {
                            P = Pattern.compile("[a-zA-Z], [a-zA-Z]+[0-9]|[0-9]+");
                            p = Pattern.compile("^add " + P);
                            m = p.matcher(cadenaCuatro);
                            if (m.find()) {
                                ArchivoObjeto.print("11111011\t");
                            } else {
                                System.out.println("Error en la linea" + contadorAux);
                                System.exit(0);
                            }
                        }
                        if (cadenaCuatro.indexOf("sub") != -1) {
                            P = Pattern.compile("[a-zA-Z]|[a-zA-Z]+[0-9], [0-9]+");
                            p = Pattern.compile("^sub " + P);
                            m = p.matcher(cadenaCuatro);
                            if (m.find()) {
                                ArchivoObjeto.print("11110111\t");
                            } else {
                                System.out.println("Error en la linea" + contadorAux);
                                System.exit(0);
                            }
                        }
                        if (cadenaCuatro.indexOf("mul") != -1) {
                            P = Pattern.compile("[a-zA-Z]|[a-zA-Z]+[0-9], [0-9]+h+");
                            p = Pattern.compile("^mul " + P);
                            m = p.matcher(cadenaCuatro);
                            if (m.find()) {
                                ArchivoObjeto.print("11101111\t");
                            } else {
                                System.out.println("Error en la linea" + contadorAux);
                                System.exit(0);
                            }
                        }
                        if (cadenaCuatro.indexOf("div") != -1) {
                            P = Pattern.compile("[a-zA-Z]|[a-zA-Z]+[0-9], [0-9]+h+");
                            p = Pattern.compile("^div " + P);
                            m = p.matcher(cadenaCuatro);
                            if (m.find()) {
                                ArchivoObjeto.print("11011111\t");
                            } else {
                                System.out.println("Error en la linea" + contadorAux);
                                System.exit(0);
                            }
                        }
                    }
                } else {
                    System.out.println("El archivo se encuentra vacío");
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("El archivo no se encontro dentro de la carpeta");
        }
    }
}