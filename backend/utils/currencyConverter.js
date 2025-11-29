const conversionRates = {
  USD: 110,    // 1 USD = 110 BDT
  BDT: 1,      // Base
  COIN: 10     // 1 COIN = 10 BDT
};

const convertCurrency = (amount, fromCurrency, toCurrency) => {
  if (!conversionRates[fromCurrency] || !conversionRates[toCurrency]) {
    throw new Error("Invalid currency type");
  }

  // Step 1: Convert `from` currency to BDT
  const amountInBDT = amount * conversionRates[fromCurrency];

  // Step 2: Convert BDT → target currency
  return amountInBDT / conversionRates[toCurrency];
};

module.exports = { convertCurrency, conversionRates };
