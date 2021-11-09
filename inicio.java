import java.util.ArrayList;

public class inicio {
    public static void main(String[] args) {

        Archivo file = new Archivo();
        String fileName;
        ArrayList<Integer> lineErrors = new ArrayList<>();
        // se valida el archivo con el nombre del archivo 
        if (file.nameFile(args)) { // Validamos extencion del archivo
            fileName = args[0];
            if (file.SearchFile(fileName)) { // Si se encuentra el archivo 
                System.out.println("\tFile      : " + args[0]);
                System.out.println("\tLines     : " + file.totalLineas(fileName));
                file.sizeFile(fileName);
                if (file.totalLineas(fileName) > 0) {
                    lineErrors = file.listSintaxError(fileName);
                    if (lineErrors.isEmpty()  ) { // si no hay errores 
                        file.tableTokens(fileName); // genera tabla de tokens 
                        file.generarOBJ(fileName);
                    } else { // hay errores 
                        for (Integer integer : lineErrors) { // imprime numero de linea
                            System.out.println("Error en la linea: " + integer);
                        }
                    }  
                }
            } else {
                System.out.print("\nVerificar nombre del archivo");
            }
        } else {
            System.exit(1);
        }
    }
}
