package com.pay.cardpaysimulator.constants;

/**
 * Constants for Card API response examples used in Swagger/OpenAPI documentation.
 * This class centralizes all card-related response examples to keep controllers clean.
 * All values are final constants ready to use in annotations.
 */
public class ApiResponseCards {

    // Common error responses - 400 Bad Request
    public static final String ERROR_400_INVALID_CARD_DETAILS = "{\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid card details\"\n}";
    public static final String ERROR_400_INVALID_STATUS = "{\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid status\"\n}";
    public static final String ERROR_400_INVALID_BALANCE = "{\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid balance\"\n}";
    public static final String ERROR_400_INVALID_BRAND = "{\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid brand\"\n}";
    public static final String ERROR_400_INVALID_DAILY_LIMITS = "{\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid daily limits\"\n}";
    public static final String ERROR_400_INVALID_CREDIT_LIMITS = "{\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid credit limits\"\n}";
    public static final String ERROR_400_INVALID_AVS = "{\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid AVS information\"\n}";
    public static final String ERROR_400_INVALID_NOTES = "{\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid notes\"\n}";
    public static final String ERROR_400_INVALID_PROCESSING_RULE_REASON = "{\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid processing rule reason\"\n}";
    public static final String ERROR_400_INVALID_BRAND_VALUES = "{\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid brand. Valid values are: VISA, MASTERCARD, AMEX, DISCOVER\"\n}";

    // Common error responses - 404 Not Found
    public static final String ERROR_404_NOT_FOUND = "{\n  \"status\": 404,\n  \"error\": \"Not Found\",\n  \"message\": \"Card not found\"\n}";

    // Common error responses - 500 Server Error
    public static final String ERROR_500_CREATING_CARD = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while creating card\"\n}";
    public static final String ERROR_500_FETCHING_CARDS = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while fetching cards\"\n}";
    public static final String ERROR_500_FETCHING_CARD = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while fetching card\"\n}";
    public static final String ERROR_500_UPDATING_CARD = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while updating card\"\n}";
    public static final String ERROR_500_UPDATING_STATUS = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while updating status\"\n}";
    public static final String ERROR_500_UPDATING_BALANCE = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while updating balance\"\n}";
    public static final String ERROR_500_DELETING_CARD = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while deleting card\"\n}";
    public static final String ERROR_500_FETCHING_BY_STATUS = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while fetching cards by status\"\n}";
    public static final String ERROR_500_FETCHING_BY_BRAND = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while fetching cards by brand\"\n}";
    public static final String ERROR_500_BLOCKING_CARD = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while blocking card\"\n}";
    public static final String ERROR_500_UNBLOCKING_CARD = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while unblocking card\"\n}";
    public static final String ERROR_500_FETCHING_BY_BIN = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while fetching cards by BIN\"\n}";
    public static final String ERROR_500_FETCHING_BY_LAST4 = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while fetching cards by last4\"\n}";
    public static final String ERROR_500_FETCHING_BY_COUNTRY = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while fetching cards by country\"\n}";
    public static final String ERROR_500_FETCHING_BY_CURRENCY = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while fetching cards by currency\"\n}";
    public static final String ERROR_500_SEARCHING_CARDS = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while searching cards\"\n}";
    public static final String ERROR_500_UPDATING_DAILY_LIMITS = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while updating daily limits\"\n}";
    public static final String ERROR_500_UPDATING_CREDIT_LIMITS = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while updating credit limits\"\n}";
    public static final String ERROR_500_UPDATING_AVS = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while updating AVS information\"\n}";
    public static final String ERROR_500_UPDATING_NOTES = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while updating notes\"\n}";
    public static final String ERROR_500_UPDATING_PROCESSING_RULE_REASON = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while updating processing rule reason\"\n}";
    public static final String ERROR_500_INCREMENTING_CVV = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while incrementing CVV attempts\"\n}";
    public static final String ERROR_500_RESETTING_CVV = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while resetting CVV attempts\"\n}";
    public static final String ERROR_500_UPDATING_LAST_USED = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while updating last used timestamp\"\n}";

    // Success responses
    public static final String SUCCESS_200_DELETE = "{\n  \"status\": 200,\n  \"message\": \"Card deleted successfully\",\n  \"cardNumber\": \"4242424242424242\"\n}";

    // Specific error messages
    public static final String MSG_INVALID_CARD_DETAILS = "Invalid card details";
    public static final String MSG_INVALID_STATUS = "Invalid status";
    public static final String MSG_INVALID_BALANCE = "Invalid balance";
    public static final String MSG_INVALID_BRAND = "Invalid brand";
    public static final String MSG_INVALID_DAILY_LIMITS = "Invalid daily limits";
    public static final String MSG_INVALID_CREDIT_LIMITS = "Invalid credit limits";
    public static final String MSG_INVALID_AVS = "Invalid AVS information";
    public static final String MSG_INVALID_NOTES = "Invalid notes";
    public static final String MSG_INVALID_PROCESSING_RULE_REASON = "Invalid processing rule reason";
    public static final String MSG_INVALID_BRAND_VALUES = "Invalid brand. Valid values are: VISA, MASTERCARD, AMEX, DISCOVER";

    // Server error messages
    public static final String MSG_ERROR_CREATING_CARD = "Unexpected error while creating card";
    public static final String MSG_ERROR_FETCHING_CARDS = "Unexpected error while fetching cards";
    public static final String MSG_ERROR_FETCHING_CARD = "Unexpected error while fetching card";
    public static final String MSG_ERROR_UPDATING_CARD = "Unexpected error while updating card";
    public static final String MSG_ERROR_UPDATING_STATUS = "Unexpected error while updating status";
    public static final String MSG_ERROR_UPDATING_BALANCE = "Unexpected error while updating balance";
    public static final String MSG_ERROR_DELETING_CARD = "Unexpected error while deleting card";
    public static final String MSG_ERROR_FETCHING_BY_STATUS = "Unexpected error while fetching cards by status";
    public static final String MSG_ERROR_FETCHING_BY_BRAND = "Unexpected error while fetching cards by brand";
    public static final String MSG_ERROR_BLOCKING_CARD = "Unexpected error while blocking card";
    public static final String MSG_ERROR_UNBLOCKING_CARD = "Unexpected error while unblocking card";
    public static final String MSG_ERROR_FETCHING_BY_BIN = "Unexpected error while fetching cards by BIN";
    public static final String MSG_ERROR_FETCHING_BY_LAST4 = "Unexpected error while fetching cards by last4";
    public static final String MSG_ERROR_FETCHING_BY_COUNTRY = "Unexpected error while fetching cards by country";
    public static final String MSG_ERROR_FETCHING_BY_CURRENCY = "Unexpected error while fetching cards by currency";
    public static final String MSG_ERROR_SEARCHING_CARDS = "Unexpected error while searching cards";
    public static final String MSG_ERROR_UPDATING_DAILY_LIMITS = "Unexpected error while updating daily limits";
    public static final String MSG_ERROR_UPDATING_CREDIT_LIMITS = "Unexpected error while updating credit limits";
    public static final String MSG_ERROR_UPDATING_AVS = "Unexpected error while updating AVS information";
    public static final String MSG_ERROR_UPDATING_NOTES = "Unexpected error while updating notes";
    public static final String MSG_ERROR_UPDATING_PROCESSING_RULE_REASON = "Unexpected error while updating processing rule reason";
    public static final String MSG_ERROR_INCREMENTING_CVV = "Unexpected error while incrementing CVV attempts";
    public static final String MSG_ERROR_RESETTING_CVV = "Unexpected error while resetting CVV attempts";
    public static final String MSG_ERROR_UPDATING_LAST_USED = "Unexpected error while updating last used timestamp";

    // No cards found messages
    public static final String MSG_NO_CARDS_BIN = "No cards found with BIN: %s";
    public static final String MSG_NO_CARDS_LAST4 = "No cards found with last4: %s";
    public static final String MSG_NO_CARDS_CURRENCY = "No cards found with currency: %s";

    // Example objects for Swagger
    public static final String EXAMPLE_CARDS_FOUND = "[\n  {\n    \"id\": 1,\n    \"cardNumber\": \"4242424242424242\",\n    \"bin\": \"424242\"\n  }\n]";
    public static final String EXAMPLE_NO_CARDS_BIN = "{\n  \"status\": 200,\n  \"message\": \"No cards found with BIN: 424242\"\n}";
    public static final String EXAMPLE_NO_CARDS_LAST4 = "{\n  \"status\": 200,\n  \"message\": \"No cards found with last4: 4242\"\n}";
    public static final String EXAMPLE_NO_CARDS_CURRENCY = "{\n  \"status\": 200,\n  \"message\": \"No cards found with currency: USD\"\n}";

    private ApiResponseCards() {
        // Utility class - prevent instantiation
    }
}

