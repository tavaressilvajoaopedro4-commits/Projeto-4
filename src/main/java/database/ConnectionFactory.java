package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    // Altere os dados de acordo com o seu banco local
    private static final String URL = "jdbc:postgresql://localhost:5432/seu_banco";
    private static final String USER = "postgres";
    private static final String PASSWORD = "sua_senha";

    public static Connection getConnection() throws SQLException {
        try {
            // Garante o carregamento do driver em projetos legados
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC não encontrado.", e);
        }
    }
}
