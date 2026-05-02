package fametro.edu.br.evently.event.enums;

public enum PaymentMethod {
    CARTAO_CREDITO,
    CARTAO_DEBITO,
    PIX,
    BOLETO;

    public static String convertPaymentMethodToString(PaymentMethod paymentMethod) {
        switch (paymentMethod) {
            case CARTAO_CREDITO:
                return "Crédito";
            case CARTAO_DEBITO:
                return "Débito";
            case PIX:
                return "Pix";
            case BOLETO:
                return "Boleto";
            default:
                return "Desconhecido";
        }
    }
}
