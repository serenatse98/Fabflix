package edu.uci.ics.sctse.service.movies.utilities;

public class ResultCodes {
    // General error codes
    public static final int INTERNAL_SERVER_ERROR = -1;
    public static final int JSON_MAPPING_EXCEPTION = -2;
    public static final int JSON_PARSE_EXCEPTION = -3;

    // Data validation failure codes - for when data fails "plausible correctness" checks
    public static final int EMAIL_INVALID_LENGTH = -10;
    public static final int EMAIL_INVALID_FORMAT = -11;
    public static final int PASSWORD_INVALID_LENGTH = -12;
    public static final int TOKEN_INVALID_LENGTH = -13;
    public static final int PRIVILEGE_LEVEL_OUT_OF_RANGE = -14;
    public static final int USER_ID_OUT_OF_RANGE = -15;
    public static final int EMAIL_NOT_PROVIDED = -16;
    public static final int SESSION_NOT_PROVIDED = -17;

    // "Success" codes - the request had proper syntax, and the data was plausibly correct
    public static final int PASSWORD_MISMATCH = 11;
    public static final int PASSWORD_INSUFFICIENT_LENGTH = 12;
    public static final int PASSWORD_INSUFFICIENT_CHARS = 13;
    public static final int USER_NOT_FOUND = 14;
    public static final int EMAIL_ALREADY_IN_USE = 16;

    // api/idm/user/register
    public static final int REGISTRATION_SUCCSSFUL = 110;

    // api/idm/user/login
    public static final int LOGIN_SUCCESSFUL = 120;

    // api/idm/session
    public static final int SESSION_ACTIVE = 130;
    public static final int SESSION_EXPIRED = 131;
    public static final int SESSION_CLOSED = 132;
    public static final int SESSION_REVOKED = 133;
    public static final int SESSION_NOT_FOUND = 134;
    public static final int SESSION_TIMEOUT = 135;

    // api/idm/user/privilege
    public static final int USER_PRIVILEGE_GOOD = 140;
    public static final int USER_PRIVILEGE_BAD = 141;

    // api/idm/user/password
    public static final int PASSWORD_UPDATED = 150;

    // api/idm/user/get
    public static final int USER_RETRIEVED = 160;

    // api/idm/user/create
    public static final int USER_CREATED = 171;
    public static final int CANNOT_CREATE_ROOT_USER = 172;

    // api/idm/user/update
    public static final int USER_UPDATED = 180;
    public static final int CANNOT_ELEVATE_USER_TO_ROOT = 181;

    // api/movies/search -- get/{movieid}
    public static final int MOVIES_FOUND = 210;
    public static final int NO_MOVIES_FOUND = 211;

    // api/movies/add
    public static final int MOVIE_ADDED = 214;
    public static final int CANNOT_ADD_MOVIE = 215;
    public static final int MOVIE_ALREADY_EXISTS = 216;

    // api/movies/genre
    public static final int GENRE_ADDED = 217;
    public static final int CANNOT_ADD_GENRE = 218;
    public static final int GENRES_RETRIEVED = 219;

    // api/movies/delete/{movieid}
    public static final int MOVIE_REMOVED = 240;
    public static final int CANNOT_REMOVE_MOVIE = 241;
    public static final int MOVIE_ALREADY_REMOVED = 242;

    // api/movies/star
    public static final int STARS_FOUND = 212;
    public static final int NO_STARS_FOUND = 213;
    public static final int STAR_ADDED = 220;
    public static final int CANNOT_ADD_STAR = 221;
    public static final int STAR_ALREADY_EXISTS = 222;
    public static final int STARIN_ADDED = 230;
    public static final int CANNOT_ADD_STARIN = 231;
    public static final int STARIN_ALREADY_EXISTS = 232;

    // api/movies/rating
    public static final int RATING_UPDATED = 250;
    public static final int CANNOT_UPDATE_RATING = 251;


    public static final int[] resultCodes = {
            INTERNAL_SERVER_ERROR, JSON_MAPPING_EXCEPTION, JSON_PARSE_EXCEPTION, EMAIL_INVALID_LENGTH,
            EMAIL_INVALID_FORMAT, PASSWORD_INVALID_LENGTH, TOKEN_INVALID_LENGTH, PRIVILEGE_LEVEL_OUT_OF_RANGE,
            USER_ID_OUT_OF_RANGE, PASSWORD_MISMATCH, PASSWORD_INSUFFICIENT_LENGTH, PASSWORD_INSUFFICIENT_CHARS,
            USER_NOT_FOUND, SESSION_NOT_FOUND, EMAIL_ALREADY_IN_USE, REGISTRATION_SUCCSSFUL, LOGIN_SUCCESSFUL,
            SESSION_ACTIVE, SESSION_EXPIRED, SESSION_REVOKED, SESSION_TIMEOUT, USER_PRIVILEGE_GOOD, USER_PRIVILEGE_BAD,
            PASSWORD_UPDATED, USER_RETRIEVED, USER_CREATED, CANNOT_CREATE_ROOT_USER, USER_UPDATED,
            CANNOT_ELEVATE_USER_TO_ROOT, MOVIES_FOUND, NO_MOVIES_FOUND, MOVIE_ADDED, CANNOT_ADD_MOVIE, MOVIE_ALREADY_EXISTS,
            CANNOT_REMOVE_MOVIE, MOVIE_REMOVED, MOVIE_ALREADY_REMOVED, GENRES_RETRIEVED, GENRE_ADDED, CANNOT_ADD_GENRE,
            STARS_FOUND, NO_STARS_FOUND, STAR_ADDED, CANNOT_ADD_STAR, STAR_ALREADY_EXISTS, STARIN_ADDED,
            CANNOT_ADD_STARIN, STARIN_ALREADY_EXISTS, RATING_UPDATED, CANNOT_UPDATE_RATING
    };

    public static String setMessage(int code) {
        switch (code) {
            case JSON_MAPPING_EXCEPTION:
                return "JSON mapping exception.";
            case JSON_PARSE_EXCEPTION:
                return "JSON parse exception.";
            case EMAIL_INVALID_LENGTH:
                return "Email address has invalid length.";
            case EMAIL_INVALID_FORMAT:
                return "Email address has invalid format.";
            case PASSWORD_INVALID_LENGTH:
                return "Password has invalid length.";
            case TOKEN_INVALID_LENGTH:
                return "Token has invalid length.";
            case PRIVILEGE_LEVEL_OUT_OF_RANGE:
                return "Privilege level is not within valid range.";
            case USER_ID_OUT_OF_RANGE:
                return "User ID is not within a valid range.";
            case EMAIL_NOT_PROVIDED:
                return "Email not provided in request header.";
            case SESSION_NOT_PROVIDED:
                return "SessionID not provided in request header";
            case PASSWORD_MISMATCH:
                return "Passwords do not match.";
            case PASSWORD_INSUFFICIENT_LENGTH:
                return "Password does not meet length requirements.";
            case PASSWORD_INSUFFICIENT_CHARS:
                return "Password does not meet character requirements.";
            case USER_NOT_FOUND:
                return "User not found.";
            case EMAIL_ALREADY_IN_USE:
                return "Email already in use.";
            case REGISTRATION_SUCCSSFUL:
                return "User registered successfully .";
            case LOGIN_SUCCESSFUL:
                return "User successfully logged in.";
            case SESSION_NOT_FOUND:
                return "Session not found.";
            case SESSION_ACTIVE:
                return "Session is active.";
            case SESSION_CLOSED:
                return "Session is closed.";
            case SESSION_EXPIRED:
                return "Session is expired.";
            case SESSION_REVOKED:
                return "Session is revoked.";
            case SESSION_TIMEOUT:
                return "Session is timed out.";
            case USER_PRIVILEGE_GOOD:
                return "User has sufficient privilege.";
            case USER_PRIVILEGE_BAD:
                return "User has insufficient privilege.";
            case PASSWORD_UPDATED:
                return "User password successfully updated.";
            case USER_RETRIEVED:
                return "User successfully retrieved.";
            case USER_CREATED:
                return "User successfully created.";
            case CANNOT_CREATE_ROOT_USER:
                return "Creating user with ROOT privilege is not allowed.";
            case USER_UPDATED:
                return "User successfully updated.";
            case CANNOT_ELEVATE_USER_TO_ROOT:
                return "Elevating user to ROOT privilege is not allowed.";
            case MOVIES_FOUND:
                return "Found movies with search parameters.";
            case NO_MOVIES_FOUND:
                return "No movies found with search parameters.";
            case MOVIE_ADDED:
                return "Movie successfully added.";
            case CANNOT_ADD_MOVIE:
                return "Could not add movie.";
            case MOVIE_ALREADY_EXISTS:
                return "Movie already exists.";
            case MOVIE_REMOVED:
                return "Movie successfully removed.";
            case CANNOT_REMOVE_MOVIE:
                return "Could not remove movie.";
            case MOVIE_ALREADY_REMOVED:
                return "Movie has been already removed.";
            case GENRES_RETRIEVED:
                return "Genres successfully retrieved.";
            case GENRE_ADDED:
                return "Genre successfully added.";
            case CANNOT_ADD_GENRE:
                return "Genre could not be added.";
            case STARS_FOUND:
                return "Found stars with search parameters.";
            case NO_STARS_FOUND:
                return "No stars found with search parameters.";
            case STAR_ADDED:
                return "Star successfully added.";
            case CANNOT_ADD_STAR:
                return "Could not add star.";
            case STAR_ALREADY_EXISTS:
                return "Star already exists.";
            case STARIN_ADDED:
                return "Star successfully added to movie.";
            case CANNOT_ADD_STARIN:
                return "Could not add star to movie.";
            case STARIN_ALREADY_EXISTS:
                return "Star already exists in movie.";
            case RATING_UPDATED:
                return "Rating successfully updated.";
            case CANNOT_UPDATE_RATING:
                return "Could not update rating.";
            case INTERNAL_SERVER_ERROR:

            default:
                return "Internal server error.";
        }
    }
}
