package co.medicamecanica.rest.login;

import java.io.Serializable;

class Login implements Serializable{
    private static final long serialVersionUID = 1L;
    private Success success;

    public Success getSuccess() {
        return success;
    }

    public void setSuccess(Success success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "Login{" +
                "succsess=" + success +
                '}';
    }

    public class Success{
        @Override
        public String toString() {
            return "Success{" +
                    "code=" + code +
                    ", token='" + token + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }

        private int code;
        private String token;
        private String message;
        private int entity;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getEntity() {
            return entity;
        }

        public void setEntity(int entity) {
            this.entity = entity;
        }
    }
}
