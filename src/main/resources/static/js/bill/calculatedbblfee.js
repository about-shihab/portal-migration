function calculateDbblFee(cardType, billAmount) {
    if (cardType == 1) {
        return 10;
    }
    if (cardType == 2 || cardType == 3 || cardType == 4 || cardType == 5) {
        return billAmount * 0.015;
    }
    return billAmount * 0.01;
}