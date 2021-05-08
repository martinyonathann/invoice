package main;


import connection.DatabaseConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"rest_resource"})
public class main {
    public static void main(String[] args) {
        DatabaseConnection db =  new DatabaseConnection();
        db.getConnection();
        SpringApplication.run(main.class, args);
    }
}
