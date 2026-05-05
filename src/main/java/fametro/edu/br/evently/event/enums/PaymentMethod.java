package fametro.edu.br.evently.event.enums;

public enum PaymentMethod {
    CARTAO_CREDITO,
    CARTAO_DEBITO,
    PIX,
    BOLETO,
    GRATUITO;

    public static String convertPaymentMethodToString(PaymentMethod paymentMethod) {
        if (paymentMethod == null) return "Desconhecido";
        switch (paymentMethod) {
            case CARTAO_CREDITO:
                return "Crédito";
            case CARTAO_DEBITO:
                return "Débito";
            case PIX:
                return "Pix";
            case BOLETO:
                return "Boleto";
            case GRATUITO:
                return "Gratuito";
            default:
                return "Desconhecido";
        }
    }
}
