
var gateway_host = "andromeda-70.ics.uci.edu";
var gateway_port = 7180;
var path = "/api/g";

var url = "http://" + gateway_host + ":" + gateway_port + path;

// REGISTER USER
var USER_REGISTERED = 110;
var INVALID_PASSWORD_LEN = 12;
var INVALID_PASSWORD = 13;
var EMAIL_ALREADY_USED = 16;

// LOGIN USER
var PASSWORD_MISMATCH = 11;
var USER_NOT_FOUND = 14;
var LOGIN_SUCCESS = 120;

// MOVIE SEARCH
var MOVIES_FOUND = 210;
var NO_MOVIES_FOUND = 211;
var QUICK_SEARCH = false;
var DETAIL_GET = false;
var LETTER_SEARCH = false;

// RATING
var RATING_SUCCESS = 250;
var RATING_FAIL = 251;

// CART INSERT
var INVALID_QUANTITY = 33;
var DUPLICATE_INSERTION = 311;
var ITEM_INSERTED = 3100;

// CART RETRIEVE
var ITEM_DOES_NOT_EXIST = 312;
var CART_RETRIEVED = 3130;
var CART_PRICE_GET = 3555;
var CART_GET = false;

// DELETE FROM CART
var CART_DELETE_SUCCESS = 3120;
var CART_CLEARED = 3140;

// UPDATE CART
var CART_UPDATED = 3110;

// ORDER PLACE
var CREATE_PAYMENT_FAIL = 342;
var ORDER_PLACED = 3400;

var PAYMENT_INCOMPLETE = 3422;
var PAYMENT_COMPLETE = 3420;

// INSERT CUSTOMER/CC
var CC_INSERT_SUCCESS = 3200;
var DUP_CC = 325;
var CUSTOMER_INSERT_SUCCESS = 3300;
var DUP_CUSTOMER = 333;

// ORDER RETRIEVE
var ORDERS_RETRIEVED = 3410;
var ITEM_GET = false;

// GET GENRES
var GOT_GENRES = 219;