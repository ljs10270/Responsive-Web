package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseUtil {//DB와 연동하는 클래스
	
	public static Connection getConnection() {//DB와 연결 자체를 관리
		//static인 이유: 다른 클래스에서 이 함수를 사용하기 위해
		try {
			String dbURL = "jdbc:mysql://localhost:3306/Hanseo"; //MYSQL에 접속하기 위한 명령어
			String dbID = "root";
			String dbPassword = "~~~~~";
			Class.forName("com.mysql.jdbc.Driver"); //JDBC드라이버 사용하겠다고 정의
			return DriverManager.getConnection(dbURL, dbID, dbPassword); //MYSQL에 접속한 상태를 반환
		} catch (Exception e) {
			e.printStackTrace(); //오류가 잡히면 출력
		}
		return null; //DB연동 오류가 발생하면 널 반환
	}
}
