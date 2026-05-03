package fametro.edu.br.evently.event.enums;

public enum AgeRange {
    CRIANCA, // até 10 anos
    JOVEM, // até 20 anos
    ADULTO, // até 59
    IDOSO; // A partir de 60

    public static String convertAgeRangeToString(AgeRange ageRange) {
        if (ageRange == null)
            return "Desconhecido";
        switch (ageRange) {
            case CRIANCA:
                return "Criança";
            case JOVEM:
                return "Jovem";
            case ADULTO:
                return "Adulto";
            case IDOSO:
                return "Idoso";
            default:
                return "Desconhecido";
        }
    }

    public String getLabel() {
        return convertAgeRangeToString(this);
    }
}
