package me.tiezhu.websocket;

public interface Constants {

    interface Header {
        String INPUT_USER = "X-HTTP-USER";
        String WEBSOCKET_USER = "simpUser";
    }

    interface Attribute {
        String PRINCIPAL = "HttpPrincipal";
    }

    interface SubscribePath {
        String PREFIX_PERSONAL = "/personal";
        String PREFIX_CLASSROOM = "/classroom";
        String PREFIX_PUBLIC = "/public";

        String PERSONAL_MSG = PREFIX_PERSONAL + "/msg";
        String CLASSROOM_INFO = PREFIX_CLASSROOM + "/sync";
        String PUBLIC_MSG = PREFIX_PUBLIC + "/msg";
    }
}
