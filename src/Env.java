public enum Env {
    DB_USR("sustech_admin"),
    DB_PASSWORD("oIIzEuVdcu7Sc5v7"),
    DB_SERVER_URL("jdbc:mysql://mysql2.sqlpub.com:3307/xiangqi"),
    DEFAULT_BOARD("111719231523191711000000000000000000002100000000002100130013001300130013000000000000000000000000000000000000120012001200120012002000000000002000000000000000000000101618221422181610");

    private final String value;
    private Env(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}