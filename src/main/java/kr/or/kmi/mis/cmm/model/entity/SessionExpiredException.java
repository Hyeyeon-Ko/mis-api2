package kr.or.kmi.mis.cmm.model.entity;

public class SessionExpiredException extends RuntimeException {
    public SessionExpiredException(String message) {
        super(message);
    }
}
