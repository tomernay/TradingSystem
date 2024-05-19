package src.main.java.Utilities;

public class Response<T> {
    private final boolean success;
    private final String message;
    private T data;

    public Response(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public static <T> Response<T> success(String message, T data) {
        return new Response<>(true, message, data);
    }

    public static <T> Response<T> error(String message, T data) {
        return new Response<>(false, message, data);
    }

}

