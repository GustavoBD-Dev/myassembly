
import java.util.regex.*;

public class pruebaExpresiones {
    public static void main(final String[] args) {
       String input = "   case 1 :";
       String regex = "\\s*case\\s*([0-9]+)\\s*:";
       boolean matches = Pattern.matches(regex, input);
       System.out.println(matches);
    }
}
