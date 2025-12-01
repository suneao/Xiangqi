public enum Env {
    DB_USR("sustech_admin"),
    DB_PASSWORD("oIIzEuVdcu7Sc5v7"),
    DB_SERVER_URL("jdbc:mysql://mysql2.sqlpub.com:3307/xiangqi"),
    DEFAULT_BOARD("defauard");

    private final String value;
    private Env(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
