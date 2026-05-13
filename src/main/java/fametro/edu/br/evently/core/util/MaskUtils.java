package fametro.edu.br.evently.core.util;

public class MaskUtils {

    public static String formatCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) return cpf;
        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    public static String formatPhone(String phone) {
        if (phone == null) return phone;
        if (phone.length() == 11) {
            return phone.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        } else if (phone.length() == 10) {
            return phone.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        }
        return phone;
    }
    
    public static String unmask(String value) {
        if (value == null) return null;
        return value.replaceAll("\\D", "");
    }
}
